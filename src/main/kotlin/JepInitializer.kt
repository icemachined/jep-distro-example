import jep.Jep
import jep.JepConfig
import jep.MainInterpreter
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.IllegalStateException
import java.net.JarURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Configuring Jep depending on environment.
 */
object JepInitializer {
    var config = JepConfig()

    private val LOGGER = LoggerFactory.getLogger(javaClass)

    init {
        LOGGER.info("Initializing JEP")

        val classLoader = javaClass
        val packageName = "ExampleModule"
        val packageInitFile = classLoader.getResource("/$packageName/__init__.py")

        config.redirectStdErr(System.err)
        config.redirectStdout(System.out)

        if (packageInitFile?.protocol == "file") {
            LOGGER.debug(
                "Found the $packageName module using a \"file\" resource. Using python code directly."
            )
            // we can point JEP to the folder and get better debug messages with python source code
            // locations

            // we want to have the parent folder of "ExampleModule" so that we can do "import ExampleModule"
            // in python
            config.addIncludePaths(Paths.get(packageInitFile.toURI()).parent.parent.toString())
        } else {
            val targetFolder = Files.createTempDirectory("jep_python_example")
            config.addIncludePaths(targetFolder.toString())

            extractPythonPackage(packageInitFile, packageName, targetFolder)
        }

        val jepLibrary = if (System.getenv("JEP_LIBRARY_PATH") != null) {
            File(System.getenv("JEP_LIBRARY_PATH"))
        } else {
            val jepRoot = Paths.get(packageInitFile.toURI()).parent.parent.parent.resolve("jep-distro").resolve("jep")
            when (getOS()!!) {
                OS.WINDOWS -> File(jepRoot.resolve("jep.dll").toString())
                OS.LINUX -> File(jepRoot.resolve("libjep.so").toString())
                OS.MAC_ARM64 -> File(jepRoot.resolve("libjep.arm64.jnilib").toString())
                OS.MAC_X86_64 -> File(jepRoot.resolve("libjep.x86_64.jnilib").toString())
            }

        }
        if (!jepLibrary.exists()) {
            throw IllegalStateException("Can't find jep library. File $jepLibrary does not exist.")
        }
        MainInterpreter.setJepLibraryPath(jepLibrary.path)
        config.addIncludePaths(
            jepLibrary.toPath().parent.parent.toString()
        )
    }

    private fun extractPythonPackage(pyInitFile: URL?, packageName: String, targetFolder: Path) {
        // otherwise, we are probably running inside a JAR, so we try to extract our files
        // out of the jar into a temporary folder
        val jarURL = pyInitFile?.openConnection() as? JarURLConnection
        val jar = jarURL?.jarFile

        if (jar == null) {
            LOGGER.error(
                "Could not extract $packageName package out of the jar."
            )
        } else {
            LOGGER.info(
                "Using JAR connection to {} to extract files into {}",
                jar.name,
                targetFolder
            )

            val entries = jar.entries().asSequence().filter { it.name.contains(packageName) }

            entries.forEach { entry ->
                LOGGER.debug("Extracting entry: {}", entry.name)

                // resolve target files relatively to our target folder. They are already
                // prefixed with ExampleModule/
                val targetFile = targetFolder.resolve(entry.name).toFile()

                // make sure to create directories along the way
                if (entry.isDirectory) {
                    targetFile.mkdirs()
                } else {
                    // copy the contents into the temp folder
                    jar.getInputStream(entry).use { input ->
                        targetFile.outputStream().use { output -> input.copyTo(output) }
                    }
                }
            }
        }
    }

    fun getOS(): OS? {
        val os = System.getProperty("os.name").lowercase()
        if (os.contains("win")) {
            return OS.WINDOWS
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return OS.LINUX
        } else if (os.contains("mac")) {
            val arch = System.getProperty("os.arch").lowercase()
            if (arch == "aarch64")
                return OS.MAC_ARM64
            else
                return OS.MAC_X86_64
        }
        return null
    }
    enum class OS {
        WINDOWS, LINUX, MAC_ARM64, MAC_X86_64
    }
}
fun Jep.setupDebugger() {
    val debugEgg = System.getenv("DEBUG_PYTHON_EGG")
    val debugHost = System.getenv("DEBUG_PYTHON_HOST") ?: "localhost"
    val debugPort = System.getenv("DEBUG_PYTHON_PORT")?.toInt() ?: 52225
    if (debugEgg != null) {
        this.runScript(File(JepInitializer.javaClass.getResource("/setup_debug.py").file).canonicalPath)
        this.invoke("enable_debugger", debugEgg, debugHost, debugPort)
    }
}


import com.icemachined.Request
import pemja.core.PythonInterpreter
import pemja.core.PythonInterpreterConfig
import java.io.File
import java.util.*


fun main(args: Array<String>) {
    println("Initializing interpreter")

    val pythonPath = File(JepInitializer.javaClass.getResource("/example_pemja.py").file)
    val config = PythonInterpreterConfig
        .newBuilder()
        .setPythonExec("python3") // specify python exec
        .addPythonPaths(pythonPath.parent) // add path to search path
        .build()
    val interpreter = PythonInterpreter(config);
    interpreter.setupDebugger()
    interpreter.exec("import example_pemja")

    val request = Request(
        """
        def foo():
            x = source()
            if x < MAX:
                y = 2 * x
                sink(y)
        """.trimIndent()
    )
    val res = interpreter.invoke("example_pemja.process_request", request);
    print("res = $res")

}

fun PythonInterpreter.setupDebugger(){
    val debugEgg = System.getenv("DEBUG_PYTHON_EGG")
    val debugHost = System.getenv("DEBUG_PYTHON_HOST") ?: "localhost"
    val debugPort = System.getenv("DEBUG_PYTHON_PORT")?.toInt() ?: 52225
    if (debugEgg != null) {
        this.exec("import setup_debug")
        this.invoke("setup_debug.enable_debugger", debugEgg, debugHost, debugPort);
    }
}
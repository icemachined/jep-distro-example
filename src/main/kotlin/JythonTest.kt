import com.icemachined.Request
import com.icemachined.Response
import org.python.core.*
import org.python.util.PythonInterpreter
import java.io.File
import java.util.*


fun main(args: Array<String>) {
    println("Initializing interpreter")
    val properties = Properties()
    val pythonPath = File(JepInitializer.javaClass.getResource("/example.py").file).canonicalPath
    properties.setProperty("python.path", pythonPath)
    PythonInterpreter.initialize(System.getProperties(), properties, arrayOf(""))
    val interp = PythonInterpreter()
    interp.setupDebugger()
    // here can be 2 approaches to load code
    // 1. we can put our module path to sys.path or python.path in python
    // and then call import something from this module
    // 2. we can use interpreter execfile, but in this case if the file use some imports and they are not in the path
    //    there will be an error, so we need be carefil. Also jython can resolve python modules from classpath.
    interp.execfile(pythonPath)

    println("Invoking process_request")

    // there can be 2 approaches calling the function from jython
    // 1. call through pyfunction
    val pr = interp.get("process_request") as PyFunction
    val request = Request(
        "def foo():\n" +
               "    x = source()\n" +
               "    if x < MAX:\n" +
               "        y = 2 * x\n" +
               "        sink(y)\n"
    )
    // there is 2 ways to convert java object to pyobject
    // Py.java2py(request) or PyJavaType.wrapJavaObject(request)
    val result = pr.__call__(Py.java2py(request)).__tojava__(Response::class.java) as Response
    println("Response: ${result.response}")
    // 2. call through eval script with named parameters
    interp.set("myparam", request)  // global variables can lead to memory leaks
    val result1 = interp.eval("process_request(myparam)" ).__tojava__(Response::class.java) as Response
    println("Response1: ${result1.response}")
}

// was unable to stop on breakpoint inside module, owing to pydev debugger: Unable to find real location for
fun PythonInterpreter.setupDebugger() {
    val debugEgg = System.getenv("DEBUG_PYTHON_EGG")
    val debugHost = System.getenv("DEBUG_PYTHON_HOST") ?: "localhost"
    val debugPort = System.getenv("DEBUG_PYTHON_PORT")?.toInt() ?: 52225
    if (debugEgg != null) {
        this.execfile(File(JepInitializer.javaClass.getResource("/setup_debug.py").file).canonicalPath)
        this.get("enable_debugger").__call__(PyString(debugEgg), PyString(debugHost), PyInteger(debugPort))
    }
}

import com.icemachined.Response
import jep.SubInterpreter
import java.io.File


fun main(args: Array<String>) {
    println("Initializing interpreter")
    val interp = SubInterpreter(JepInitializer.config)
    interp.setupDebugger()
    interp.runScript(File(JepInitializer.javaClass.getResource("/example.py").file).canonicalPath)

    println("Invoking process_request")
    val result = interp.invoke("process_request", "Hello") as Response
    println("Response: ${result.response}")
}
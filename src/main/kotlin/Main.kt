import com.icemachined.Response
import jep.SubInterpreter


fun main(args: Array<String>) {
    println("Initializing interpreter")
    val interp = SubInterpreter(JepInitializer.config)
    interp.exec(JepInitializer.javaClass.getResource("/example.py").readText())

    println("Invoking process_request")
    val result = interp.invoke("process_request", "Hello") as Response
    println("Response: ${result.response}")
}
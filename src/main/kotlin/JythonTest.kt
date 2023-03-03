import com.icemachined.Response
import org.python.util.PythonInterpreter
import java.io.File


fun main(args: Array<String>) {
    println("Initializing interpreter")
    val interp = PythonInterpreter()
    interp.execfile(File(JepInitializer.javaClass.getResource("/example.py").file).canonicalPath)

    println("Invoking process_request")
    val result = interp.exec("process_request(\"Hello\")" ) as Response
    println("Response: ${result.response}")
}
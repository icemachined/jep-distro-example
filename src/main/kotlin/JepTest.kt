import com.icemachined.Request
import com.icemachined.Response
import jep.MainInterpreter
import jep.SharedInterpreter
import jep.SubInterpreter
import java.io.File


fun main(args: Array<String>) {
    println("Initializing interpreter")

    val interp = SubInterpreter(JepInitializer.config)

    val initScript = File(JepInitializer.javaClass.getResource("/example.py").file).canonicalPath
    interp.runScript(initScript)
    val request = Request(
        "def foo():\n" +
                "    x = source()\n" +
                "    if x < MAX:\n" +
                "        y = 2 * x\n" +
                "        sink(y)\n"
    )
    val res = interp.invoke("process_request", request)
    print("res = $res")
}
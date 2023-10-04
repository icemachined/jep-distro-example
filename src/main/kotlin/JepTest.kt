import com.icemachined.Request
import jep.SubInterpreter
import java.io.File


fun main(args: Array<String>) {
    println("Initializing interpreter")

    val interp = SubInterpreter(JepInitializer.config)

    val initScript = File(JepInitializer.javaClass.getResource("/example.py").file).canonicalPath
    interp.runScript(initScript)

    val request = Request(
        """
        def foo():
            x = source()
            if x < MAX:
                y = 2 * x
                sink(y)
        """.trimIndent()
    )
    val res = interp.invoke("process_request", request)
    print("res = $res")
}
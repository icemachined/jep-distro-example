import com.icemachined.Request
import com.icemachined.Response
import org.python.core.PyFunction
import org.python.core.PyJavaType
import org.python.util.PythonInterpreter
import java.io.File


fun main(args: Array<String>) {
    println("Initializing interpreter")
    val interp = PythonInterpreter()
    interp.execfile(File(JepInitializer.javaClass.getResource("/example.py").file).canonicalPath)

    println("Invoking process_request")

    // call through pyobject
    val pr = interp.get("process_request") as PyFunction
    val result = pr.__call__(PyJavaType.wrapJavaObject(Request("Hello"))).__tojava__(Response::class.java) as Response
    println("Response: ${result.response}")
    // call through eval script with named parameters
    interp.set("myparam", Request("Hey"))  // global variables can lead to memory leaks
    val result1 = interp.eval("process_request(myparam)" ).__tojava__(Response::class.java) as Response
    println("Response1: ${result1.response}")
}
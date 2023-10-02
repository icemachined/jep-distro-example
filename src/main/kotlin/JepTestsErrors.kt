import com.icemachined.Request
import jep.Jep
import jep.SubInterpreter
import jep.python.PyCallable
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File


class SomeBuilder {
    fun build(){
        throw RuntimeException("Error happens")
    }

    fun <T>  buildLiteral(value:T):Literal<T> {
        return Literal(value)
    }
}

class  Literal<T>(val value:T)

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JepTestError {

    lateinit var interp: Jep
    @BeforeAll
    fun setup(){
        println("Initializing interpreter")
        interp = SubInterpreter(JepInitializer.config)
        println("Initialized interpreter")
    }

    @AfterAll
    fun shutdown(){
        println("Closing interpreter")
        interp?.close()
        println("Closed interpreter")
    }

    @Test
    fun testExceptionHandling(){
        val initScript = File(JepInitializer.javaClass.getResource("/testerror.py").file).canonicalPath
        interp.runScript(initScript)
        val res = interp.invoke("process_request", SomeBuilder())
        print("res = $res")
    }

    @Test
    fun testPyObjLeakage(){
        val b = SomeBuilder()
        interp["b"] = b
        interp.eval("x = b.buildLiteral(1+5j)")
        val x = interp.getValue("x") as Literal<*>
        Thread { println(x.value) }.start()
    }

    @Test
    fun testMixedReturnType(){
        val initScript = File(JepInitializer.javaClass.getResource("/mixedtype.py").file).canonicalPath
        interp.runScript(initScript)
        val res = interp.getValue("get_some_list") as PyCallable
        print("res = ${res.call(true)}")
        print("res = ${res.call(false)}")
    }

}
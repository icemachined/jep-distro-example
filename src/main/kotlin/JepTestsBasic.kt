import com.icemachined.Request
import jep.Jep
import jep.SubInterpreter
import jep.python.PyCallable
import jep.python.PyObject
import org.junit.jupiter.api.*
import java.io.File
import java.util.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JepTestsBasic {

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
    fun helloWorld(){
        interp.exec("from java.lang import System")
        interp.exec("s = 'Hello World'")
        interp.exec("System.out.println(s)")
        interp.exec("print(s)")
        interp.exec("print(s[1:-1])")
    }

    @Test
    fun callingPythonMethods() {
        interp.exec("import ExampleModule")
        // any of the following work, these are just pseudo-examples

        val request = Request(
            """
            def foo():
                x = source()
                if x < MAX:
                    y = 2 * x
                    sink(y)
            """.trimIndent()
        )
        // using exec(String) to invoke methods
        interp.set("arg", request);
        interp.exec("x = ExampleModule.extract_function_name(arg)");
        val result1 = interp.getValue("x");
        println(result1)
        interp.exec("del arg");
        interp.exec("del x");

        // using getValue(String) to invoke methods
        val result2 = interp.getValue("ExampleModule.get_time()");
        println(result2)

        // using invoke to invoke methods
        interp.exec("foo3 = ExampleModule.extract_function_name")
        val result3 = interp.invoke("foo3", request);
        println(result3)
    }

    @Test
    fun callingJavaFromPython(){
        val initScript = JepInitializer.javaClass.getResource("/calling_java.py")?.readText()
        interp.exec(initScript)
        interp.set("javaSet", setOf("Hello", "World"));
        interp.exec("from java.util import ArrayList");
        interp.exec("javaList = ArrayList()");
        interp.exec("javaList.addAll(javaSet)");
        interp.exec("print(javaList)")
        interp.exec("print(type(javaSet))")
    }

    @Test
    fun pyCallable() {
        interp.eval(
            /* language=Python */
            """
            class Example(object):
                def __init__(self):
                    pass
                def helloWorld(self):
                    return 'Hello World'
            """.trimIndent()
        );
        interp.eval("instance = Example()")
        val pyobj: PyObject = interp.getValue("instance", PyObject::class.java)
        val pyHelloWorld: PyCallable = pyobj.getAttr("helloWorld", PyCallable::class.java)
        val result = pyHelloWorld.call() as String
        println(result)

        interp.eval("def hello(arg):\n" +
                "    return 'Hello ' +  str(arg)")
        val pyHello: PyCallable = interp.getValue("hello", PyCallable::class.java)
        val result1 = pyHello.call("World") as String
        println(result1)
    }

    @Test
    fun usingPyObject(){
        interp.exec(
            /* language=Python */
            """
            class Simple:
                def test(self):
                    return 123;
            """.trimIndent()
        )
        val simple: PyCallable = interp.getValue("Simple", PyCallable::class.java)
        val instance = simple.callAs(PyObject::class.java)
        val result = instance.getAttr("test", PyCallable::class.java).callAs(
            Number::class.java
        )
        println(result)

        val instanceJava: Simple = instance.proxy(Simple::class.java)
        val result1: Int = instanceJava.test()
        println(result1)
    }
    interface Simple {
        fun test(): Int
    }
    /**
     * Not implemented yet in JEP, planned for 4.2 release.
     */
    @Test
    @Disabled
    fun customConversion(){
        interp.set("theDate", Date())
        interp.exec("print(type(theDate))") // <class 'java.util.Date'>

        interp.exec("print(theDate.getTime())") // Current time as long

        val initScript = File(JepInitializer.javaClass.getResource("/date_converter.py").file).canonicalPath
        interp.runScript(initScript)
        interp.set("theDate", Date())
        interp.exec("print(type(theDate))") // <class 'datetime.datetime'>

        interp.set("anotherDate", Date())
        interp.exec("print(anotherDate - theDate)") // an instance of <class 'datetime.timedelta'>
    }
}
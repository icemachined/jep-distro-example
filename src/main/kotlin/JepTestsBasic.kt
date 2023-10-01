import com.icemachined.Request
import jep.Jep
import jep.SubInterpreter
import jep.python.PyCallable
import jep.python.PyObject
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance


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

        val obj = Request(
        "def foo():\n" +
            "    x = source()\n" +
            "    if x < MAX:\n" +
            "        y = 2 * x\n" +
            "        sink(y)\n"
        )
        // using exec(String) to invoke methods
        interp.set("arg", obj);
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
        val result3 = interp.invoke("foo3", obj);
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
    }

    @Test
    fun pyCallable() {
        interp.eval(
                "class Example(object):\n" +
                "    def __init__(self):\n" +
                "        pass\n" +
                "    def helloWorld(self):\n" +
                "        return 'Hello World'\n");
        interp.eval("instance = Example()")
        val pyobj: PyObject = interp.getValue("instance", PyObject::class.java)
        val pyHelloWorld: PyCallable = pyobj.getAttr("helloWorld", PyCallable::class.java)
        val result = pyHelloWorld.call() as String

        interp.eval("def hello(arg):\n" +
                "    return 'Hello ' +  str(arg)")
        val pyHello: PyCallable = interp.getValue("hello", PyCallable::class.java)
        val result1 = pyHello.call("World") as String
        println(result1)
    }
}
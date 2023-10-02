import JepInitializer.config
import jep.DirectNDArray
import jep.Jep
import jep.NDArray
import jep.SharedInterpreter
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.nio.ByteBuffer
import java.nio.FloatBuffer


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class JepTestsNumpy {
    lateinit var interp: Jep
    @BeforeAll
    fun setup() {
        println("Initializing interpreter")
        SharedInterpreter.setConfig(config)
        interp = SharedInterpreter()
        interp.eval("import jep")
        interp.eval("print(\"Is numpy enabled:\" + str(jep.JEP_NUMPY_ENABLED))")
        println("Initialized interpreter")
    }

    @AfterAll
    fun shutdown() {
        println("Closing interpreter")
        interp.close()
        println("Closed interpreter")
    }

    @Test
    fun testNDArray() {
        val f = floatArrayOf(1.0f, 2.1f, 3.3f, 4.5f, 5.6f, 6.7f)
        val nd: NDArray<*> = NDArray<Any?>(f, 3, 2)
        interp["x"] = nd
        interp.eval("print(x)")
    }

    @Test
    fun directMemorySupport(){
        val data: FloatBuffer = ByteBuffer.allocateDirect(6 * 4).asFloatBuffer()
        val nd = DirectNDArray(data, 6)
        interp["x"] = nd
        interp.exec("x[1] = 700")
        // val will 700 since we set it in python
        // val will 700 since we set it in python
        val `val` = data[1]
        data.put(4, `val` + 100)
        // prints 800 since we set in java
        // prints 800 since we set in java
        interp.exec("print(x[4])")
    }

    @Test
    fun testNumpyWOSupport(){
        interp.exec("import numpy as np")
        interp.exec("a = np.array([[1, 2], [3, 4]])")
        interp.exec("print(a)")
        interp.exec("b = a.transpose()")
        interp.exec("print(b)")
        interp.exec("c = a@b")
        interp.exec("print(c)")
        interp.exec("d = np.linalg.det(c)")
        interp.exec("print(d)")
        val d = interp.getValue("d")
        println("d=$d")
    }
}
import jep.MainInterpreter
import jep.SharedInterpreter
import java.io.File


fun main(args: Array<String>) {
    println("Initializing interpreter")

    MainInterpreter.setSharedModulesArgv("",
        "--dataset=shakespeare",
        "--n_layer=4",
        "--n_head=4",
        "--n_embd=64",
        "--device=cpu",
        "--compile=False",
        "--eval_iters=1",
        "--block_size=64",
        "--batch_size=8")
    val relativeGPTmasterPath = "target/python/nanoGPT-master"
    val gptDir = File(relativeGPTmasterPath).canonicalPath
    JepInitializer.config.addIncludePaths(gptDir)
    JepInitializer.config.addIncludePaths("$gptDir/data/shakespeare")
    SharedInterpreter.setConfig(JepInitializer.config)
    val interp = SharedInterpreter()
    interp.eval("import os")
    interp.eval("os.chdir('$relativeGPTmasterPath/data/shakespeare')")
    interp.set("__file__", "prepare.py")
    interp.runScript("prepare.py")
    interp.eval("os.chdir('../..')")
    interp.runScript("train.py")
}
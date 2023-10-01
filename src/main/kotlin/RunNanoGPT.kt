import jep.MainInterpreter
import jep.SharedInterpreter
import java.io.File


fun main(args: Array<String>) {
    println("Initializing interpreter")

    MainInterpreter.setSharedModulesArgv(
        "",
        "config/train_shakespeare_char.py",
        "--log_interval=1",
        "--n_layer=4",
        "--n_head=4",
        "--n_embd=128",
        "--device=cpu",
        "--compile=False",
        "--eval_iters=20",
        "--block_size=64",
        "--batch_size=12",
        "--max_iters=2000",
        "--lr_decay_iters=2000",
        "--dropout=0.0"
    )
    val relativeGPTmasterPath = "target/python/nanoGPT-master"
    val gptDir = File(relativeGPTmasterPath).canonicalPath
    JepInitializer.config.addIncludePaths(gptDir)
    SharedInterpreter.setConfig(JepInitializer.config)
    val interp = SharedInterpreter()
    interp.eval("import os")
    interp.eval("os.chdir('$relativeGPTmasterPath')")
    interp.eval("import os");
    System.out.println(interp.getValue("os.getcwd()"));
    val prepFile = "data/shakespeare_char/prepare.py"
    interp.set("__file__", prepFile);
    interp.runScript(prepFile);
    interp.runScript("train.py")
}
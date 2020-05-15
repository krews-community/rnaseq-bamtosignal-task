package testutil

import java.security.MessageDigest
import java.nio.file.*
import java.io.File
import util.CmdRunner
import util.*

val cmdRunner = TestCmdRunner()

class TestCmdRunner : CmdRunner {
    override fun run(cmd: String) = exec("docker", "exec", "rnaseq-bamtosignal-base", "sh", "-c", cmd)
    override fun runCommand(cmd: String): String? = getCommandOutput("docker", "exec", "rnaseq-bamtosignal-base", "sh", "-c", cmd)
}

fun copyDirectory(fromDir: Path, toDir: Path) {
    Files.walk(fromDir).forEach { fromFile ->
        if (Files.isRegularFile(fromFile)) {
            val toFile = toDir.resolve(fromDir.relativize(fromFile))
            Files.createDirectories(toFile.parent)
            Files.copy(fromFile, toFile)
        }
    }
}

fun setupTest() {
    exec(
        "docker", "run", "--name", "rnaseq-bamtosignal-base", "--rm", "-i",
        "-t", "-d", "-v", "${testInputResourcesDir}:${testInputResourcesDir}",
        "-v", "${testDir}:${testDir}", "genomealmanac/rnaseq-bamtosignal-base"
    )
}

fun cleanupTest() {
    testDir.toFile().deleteRecursively()
}

fun File.md5(): String {
    return MessageDigest.getInstance("MD5").digest(this.readBytes()).joinToString("") { "%02x".format(it) }
}

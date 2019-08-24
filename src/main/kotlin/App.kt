import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.*
import step.*
import util.*
import java.nio.file.*
import util.CmdRunner


fun main(args: Array<String>) = Cli().main(args)

class Cli : CliktCommand() {
    private val bamFile: Path by option("-bamFile", help = "path to bam file")
            .path(exists = true).required()

    private val chromSizes:Path by option("-chromSizes", help = "path to chrom sizes file")
            .path(exists = true).required()

    private val strandedness:String by option("-strandedness",help = "strandedness").choice("Stranded", "Unstranded").required()
    private val outputPrefix: String by option("-outputPrefix", help = "output file name prefix; defaults to 'output'").default("output")
    private val outDir by option("-outputDir", help = "path to output Directory")
        .path().required()

    override fun run() {
        val cmdRunner = DefaultCmdRunner()
        cmdRunner.runTask(bamFile,chromSizes,strandedness,outDir,outputPrefix)
    }
}

/**
 * Runs pre-processing and bwa for raw input files
 *
 * @param taFiles pooledTa Input
 * @param outDir Output Path
 */
fun CmdRunner.runTask(bamFile:Path,chromSizes:Path,strandedness:String, outDir:Path,outputPrefix:String) {

    bamtosignal(bamFile,chromSizes,strandedness,outDir,outputPrefix)
}
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.*
import step.*
import util.*
import java.nio.file.*
import util.CmdRunner

fun main(args: Array<String>) = Cli().main(args)

class Cli : CliktCommand() {

    private val bam: Path by option("--bam", help = "path to BAM file to convert")
        .path(exists = true).required()
    private val chromosomeSizes: Path by option("--chromosome-sizes", help = "path to a TSV file containing chromosome names and sizes")
        .path(exists = true).required()
    private val stranded: Boolean by option("--stranded",help = "strandedness").flag()

    private val outputPrefix: String by option("--output-prefix", help = "output file name prefix; defaults to 'output'")
        .default("output")
    private val outputDirectory: Path by option("--output-directory", help = "path to output directory")
        .path().required()

    override fun run() {
        DefaultCmdRunner().bamtosignal(
            SignalParameters(
                bam = bam,
                chromosomeSizes = chromosomeSizes,
                stranded = stranded,
                outputPrefix = outputPrefix,
                outputDirectory = outputDirectory
            )
        )
    }

}

package step
import mu.KotlinLogging
import java.nio.file.*
import java.io.File
import util.CmdRunner
private val log = KotlinLogging.logger {}

val OUTPUT_FILE_MAP: Map<String, List<Pair<String, String>>> = mapOf(
    "Stranded" to listOf(
        Pair("Signal.UniqueMultiple.str1.out.bg", "_minusAll.bw"),
        Pair("Signal.Unique.str1.out.bg", "_minusUniq.bw"),
        Pair("Signal.UniqueMultiple.str2.out.bg", "_plusAll.bw"),
        Pair("Signal.Unique.str2.out.bg", "_plusUniq.bw")
    ),
    "Unstranded" to listOf(
        Pair("Signal.UniqueMultiple.str1.out.bg", "_All.bw"),
        Pair("Signal.Unique.str1.out.bg", "_Uniq.bw")
    )
)

data class SignalParameters (
    val bam: Path,
    val chromosomeSizes: Path,
    val stranded: Boolean,
    val outputDirectory: Path,
    val outputPrefix: String = "output"
)

fun CmdRunner.sortedBedGraph(bedGraph: File): Path {
    val sortedBedGraph = createTempFile().toPath()
    this.run("sort -k1,1 -k2,2n ${bedGraph} > ${sortedBedGraph}")
    return sortedBedGraph
}

fun emptyBedGraph(chromosomeSizes: Path, directory: File): File {
    val f = createTempFile(directory = directory)
    chromosomeSizes.toFile().forEachLine {
        val p = it.split('\t')
        if (p[1].toInt() > 2) f.writeText("${p[0]}\t1\t2\t0.0\n")
    }
    return f
}

fun CmdRunner.bedGraphToBigWig(bedGraph: Path, chromosomeSizes: Path, bigWig: Path) {
    val iBedGraph = if (bedGraph.toFile().length() != 0L) bedGraph.toFile() else (
        emptyBedGraph(chromosomeSizes, bigWig.parent.toFile())
    )
    val sorted = sortedBedGraph(iBedGraph)
    this.run("""
        bedGraphToBigWig ${sorted} ${chromosomeSizes} ${bigWig}
    """)
    iBedGraph.delete()
    sorted.toFile().delete()
}

fun CmdRunner.bamtosignal(parameters: SignalParameters) {
    
    Files.createDirectories(parameters.outputDirectory)

    this.run("""
        STAR --runMode inputAlignmentsFromBAM \
            --inputBAMfile ${parameters.bam} \
            --outWigType bedGraph \
            --outWigStrand ${ if (parameters.stranded) "Stranded" else "Unstranded" } \
            --outWigReferencesPrefix chr \
            --outFileNamePrefix ${parameters.outputDirectory.resolve(parameters.outputPrefix)}
    """)

    OUTPUT_FILE_MAP[if (parameters.stranded) "Stranded" else "Unstranded"]?.forEach {
        bedGraphToBigWig(
            parameters.outputDirectory.resolve("${parameters.outputPrefix}${it.first}"),
            parameters.chromosomeSizes,
            parameters.outputDirectory.resolve("${parameters.outputPrefix}${it.second}")
        )
    }

}

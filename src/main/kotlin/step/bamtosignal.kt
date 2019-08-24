package step
import mu.KotlinLogging
import java.nio.file.*
import util.CmdRunner
private val log = KotlinLogging.logger {}

fun CmdRunner.bamtosignal(bamFile:Path,chrom_sizes:Path,strandedness:String, outDir:Path,outputPrefix:String) {
    log.info { "Make output Directory" }
    Files.createDirectories(outDir)
    val star_cmd = "STAR --runMode inputAlignmentsFromBAM \\\n"+
            "--inputBAMfile ${bamFile} \\\n"+
            " --outWigType bedGraph \\\n"+
            " --outWigStrand ${strandedness} \\\n"+
            " --outWigReferencesPrefix chr \\\n"+
            " --outFileNamePrefix ${outDir.resolve(outputPrefix)}"
    this.run(star_cmd)
    if(strandedness == "Stranded") {
        call_bg_to_bw("${outputPrefix}Signal.UniqueMultiple.str1.out.bg", chrom_sizes,
                outputPrefix + "_minusAll.bw",outputPrefix,outDir)
        call_bg_to_bw("${outputPrefix}Signal.Unique.str1.out.bg", chrom_sizes,
                outputPrefix+ "_minusUniq.bw",outputPrefix,outDir)
        call_bg_to_bw("${outputPrefix}Signal.UniqueMultiple.str2.out.bg", chrom_sizes,
                outputPrefix+ "_plusAll.bw",outputPrefix,outDir)
        call_bg_to_bw("${outputPrefix}Signal.Unique.str2.out.bg", chrom_sizes,
                outputPrefix + "_plusUniq.bw",outputPrefix,outDir)
    }
    else {
        call_bg_to_bw("${outputPrefix}Signal.UniqueMultiple.str1.out.bg", chrom_sizes,
                outputPrefix + "_All.bw",outputPrefix,outDir)
        call_bg_to_bw("${outputPrefix}Signal.Unique.str1.out.bg", chrom_sizes,
                outputPrefix+ "_Uniq.bw",outputPrefix,outDir)
    }

}

fun CmdRunner.call_bg_to_bw(input_bg:String, chrom_sizes:Path,out_fn:String, outputPrefix:String,outDir:Path) {

    // sort bedgraph
    val bedGraph = outDir.resolve(input_bg)
    val bedGraph_srt = outDir.resolve(outputPrefix+"_srt.bg")
    val bedgraph_cmd = "sort -k1,1 -k2,2n ${bedGraph} > ${bedGraph_srt}"
    val bigWig = outDir.resolve(out_fn)
    log.info  {"Sorting bedgraph: ${bedgraph_cmd}"}
    this.run(bedgraph_cmd)

    //make bigWig
    val command = "bedGraphToBigWig ${bedGraph_srt} ${chrom_sizes} ${bigWig}"
    this.run(command)
}
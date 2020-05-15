# signal generation task for RNA-seq

This task provides a convenience wrapper around [STAR](https://github.com/alexdobin/STAR) for signal generation. It produces BigWig files from a set of RNA-seq reads aligned to the genome.

## Running

We encourage running this task as a Docker image, which is publicly available through GitHub packages. To pull the image, first [install Docker](https://docs.docker.com/engine/install/), then run
```
docker pull docker.pkg.github.com/krews-community/rnaseq-bam-to-signal-task/rnaseq-bam-to-signal:latest
```
To generate signal, simply run:
```
docker run \
    --volume /path/to/inputs:/input \
    --volume /path/to/outputs:/output \
    docker.pkg.github.com/krews-community/rnaseq-bam-to-signal-task/rnaseq-bam-to-signal:latest \
    java -jar /app/bamtosignal.jar --bam /input/test.bam \
        --chromosomeSizes /input/chrom.sizes \        
        --output-directory /output \
        --stranded
```

This will produce an output directory containing signal files in bigWig format. Two files are produced, one containing all reads and one containing unique reads. If the stranded option is used as above, two pairs of plus and minus strand signal files will be produced.

### Parameters
This task supports several parameters:
|name|description|default|
|--|--|--|
|bam|path to genome alignments in BAM format|(required)|
|chromosomeSizes|path to a TSV containing chromosome names and sizes|(required)|
|output-directory|directory in which to write output|(required)|
|stranded|if set, separate files are produced for the plus and minus strands|unstranded|
|output-prefix|prefix to use when naming output files|output|

## For developers

The task provides integrated unit testing, which generates signal for a small number of reads against a human mitochondrial index and checks that the results match expected values. To run the tests, first install Docker and Java, then clone this repo, then run
```
scripts/test.sh
```
Contributions to the code are welcome via pull request.

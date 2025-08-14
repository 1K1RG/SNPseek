package org.irri.scripts;

import java.util.List;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "cmdExtract", mixinStandardHelpOptions = true, description = "Runs Extract SNP based on provided flags.")
public class CommandLineAnalysis implements Runnable {

    @Option(names = "--dataset", required = true, description = "Dataset name")
    String dataset;

    @Option(names = "--snpset", required = true, description = "SNP set name")
    String snpset;

    @Option(names = "--chr", required = true, description = "Chromosome")
    String chr;

    @Option(names = "--samples", split = ",", description = "Comma-separated sample list")
    List<String> samples;

    @Option(names = "--snps", split = ",", description = "Comma-separated SNP list")
    List<String> snps;

    @Option(names = "--start", required = true, description = "Start position")
    long start;

    @Option(names = "--end", required = true, description = "End position")
    long end;

    @Override
    public void run() {
        System.out.println("Dataset: " + dataset);
        System.out.println("Chr: " + chr);
        System.out.println("Start-End: " + start + " - " + end);
        // Call your service here
//         RunGetSNPS.main(dataset, snpset, chr, samples, snps, start, end);
    }
}

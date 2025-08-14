package org.irri.scripts;

import java.net.URL;

import javax.annotation.PostConstruct;

import picocli.CommandLine;

public class ExtractSNPS {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new RunGetSNPS()).execute(args);
        System.exit(exitCode);
    }
    
  }

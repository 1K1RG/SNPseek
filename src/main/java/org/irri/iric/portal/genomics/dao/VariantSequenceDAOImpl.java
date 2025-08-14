package org.irri.iric.portal.genomics.dao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.irri.iric.ds.chado.domain.MultiReferenceLocus;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.VarietyPlus;
import org.irri.iric.ds.chado.domain.VarietyPlusPlus;
import org.irri.iric.ds.chado.domain.model.VAllstockBasicprop;
import org.irri.iric.ds.chado.domain.object.VariantSequenceQuery;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.CreateZipMultipleFiles;
import org.springframework.stereotype.Repository;

@Repository("VariantSequenceService")
public class VariantSequenceDAOImpl implements VariantSequenceDAO {

	@Override
	public String getFile(VariantSequenceQuery query) throws Exception {
		

		// AppContext.debug(query.toString());
		AppContext.logQuery(query.toString());

		String destdir = query.getJobid();
		String jobname=destdir;
		if (destdir == null) {
			if(query.getMethod().equals("galaxy"))
				jobname="galaxywf-vcf2fasta-" + AppContext.createTempFilename(); 
			else jobname="vcf2fasta-" + AppContext.createTempFilename(); 
			destdir = AppContext.getTempDir() + jobname + "/";
		}
		else {
			if (AppContext.isWindows())
				destdir = AppContext.getTempDir() + destdir + "\\";
			else
				destdir = AppContext.getTempDir() + destdir + "/";
		}

		// destdir=destdir.replace("vcf2fasta-", "vcf2fasta-" + query.getReference()
		// +"-");

		new File(destdir).mkdir();

		Map mapLoc2Int = new LinkedHashMap();

		BufferedWriter bw = new BufferedWriter(new FileWriter(destdir + "loci.gff3"));
		Iterator<MultiReferenceLocus> itLoci = new TreeSet(query.getColLocus()).iterator();
		while (itLoci.hasNext()) {
			MultiReferenceLocus locus = itLoci.next();
			String st = "P";
			if (locus.getStrand() < 0)
				st = "N";
			bw.append(locus.getContig() + "\tIRGSPv1\tgene\t" + locus.getFmin() + "\t" + locus.getFmax() + "\t.\t"
					+ locus.getStrand() + "\t.\tID=" + locus.getContig() + "_" + locus.getFmin() + "_" + locus.getFmax()
					+ st + "\n");

			String origcontigname = locus.getContig();
			if (query.getReference().equals("9311")) {
				origcontigname = origcontigname.replace("sca", "Sca");
				origcontigname = origcontigname.replace("dun", "dUn");
			} else if (query.getReference().equals("Kasalath")) {
				if (origcontigname.equals("um"))
					origcontigname = "UM";
			}

			mapLoc2Int.put(origcontigname + "_" + locus.getFmin() + "_" + locus.getFmax(),
					origcontigname + ":" + locus.getFmin() + "-" + locus.getFmax());
		}
		bw.flush();
		bw.close();

		bw = new BufferedWriter(new FileWriter(destdir + "vars.txt"));
		bw.append("REFERENCE " + query.getReference() + "\n");
		Iterator<ArrayList<VAllstockBasicprop>> itVars = query.getColVars().iterator();
		while (itVars.hasNext()) {
			VAllstockBasicprop var = null;

			Object itVarValue = itVars.next();

			if (itVarValue instanceof List) {
				List<VAllstockBasicprop> lst = (List<VAllstockBasicprop>) itVarValue;
				var = lst.get(0);
			}

			if (itVarValue instanceof VAllstockBasicprop) {
				var = (VAllstockBasicprop) itVarValue;
			}
		try {
				String boxcode = var.getIrisId().replace(" ", "_").trim();
				bw.append(boxcode).append("\t").append(var.getName()).append("\n");
			} catch(Exception ex) {
				//ex.printStackTrace();
				if(var==null)  {
					if (itVarValue instanceof VarietyPlusPlus) {
						VarietyPlusPlus varp=(VarietyPlusPlus)itVarValue;
						bw.append(varp.getBoxCode()).append("\t").append(varp.getName()).append("\n");
					}
					else if (itVarValue instanceof VarietyPlus) {
						VarietyPlus varp=(VarietyPlus)itVarValue;
						bw.append(varp.getBoxCode()).append("\t").append(varp.getName()).append("\n");
					} else {
						AppContext.debug("var==null  itVarValue="+ itVarValue.getClass().getCanonicalName());
					}

				}
				else 
					AppContext.debug(var.getName() + "  " + var.getIrisId());
			}
		}
		bw.flush();
		bw.close();

		/*
		 * bw=new BufferedWriter(new FileWriter(destdir + "dirpath.txt")); bw.append(
		 * destdir +"\n"); bw.flush(); bw.close();
		 */
		if (query.getMethod().equals("galaxy")) {

			//String destdir, String varlistpath, Map<String, String> intervals, boolean concatSeqs, Set varset,
			//String reference
			
			bw = new BufferedWriter(new FileWriter(destdir + "samplelist.txt"));
			itVars = query.getColVars().iterator();
			while (itVars.hasNext()) {
				//VAllstockBasicprop var = null;
				Variety var = null;
				Object itVarValue = itVars.next();
				if (itVarValue instanceof List) {
					List<VAllstockBasicprop> lst = (List<VAllstockBasicprop>) itVarValue;
					var = lst.get(0);
				}
				else if (itVarValue instanceof VAllstockBasicprop) {
					var = (VAllstockBasicprop) itVarValue;
				}
				else if (itVarValue instanceof Variety) {
					var = (Variety) itVarValue;
				}
				try {
					String boxcode = var.getIrisId().replace(" ", "_").trim();
					if(boxcode==null) {
						boxcode=var.getBoxCode().replace(" ", "_").trim();
					}
					bw.append(boxcode).append("\t").append(var.getName()).append("\n");
				} catch(Exception ex) {
					ex.printStackTrace();
					AppContext.debug("itVarValue=" + itVarValue.getClass().getCanonicalName());
				}
			}
			bw.flush();
			bw.close();
			
			String[] status=new GalaxyAltSeqGenerator(destdir,jobname).getAltSequence(destdir + "samplelist.txt", mapLoc2Int, query.getReference(),query.isSync());

		} else if (query.getMethod().equals("gatk")) {

			new GATKAltSeqGenerator(destdir).getAltSequence(destdir + "vars.txt", mapLoc2Int, query.getReference());

		} else if (query.getMethod().equals("vulat")) {

			ProcessBuilder pb = new ProcessBuilder(AppContext.getPathToVCF2FastaGenerator(), destdir);
			String workdir = AppContext.getFlatfilesDir() + "/getvcfseq/";
			pb.directory(new File(workdir));

			File outfile = new File(destdir + "stdout.txt");
			File errfile = new File(destdir + "stderr.txt");
			// pb.redirectErrorStream(true);
			pb.redirectOutput(Redirect.appendTo(outfile));
			pb.redirectError(Redirect.appendTo(errfile));

			Process process = pb.start();
			int exitValue = process.waitFor();
			AppContext.debug("exitValue=" + exitValue);

			int len;
			if ((len = process.getErrorStream().available()) > 0) {
				byte[] buf = new byte[len];
				process.getErrorStream().read(buf);
				AppContext.debug("Command error:\t\"" + new String(buf) + "\"");
			}

			List listFiles = new ArrayList();
			File folder = new File(destdir);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					// System.out.println("File " + listOfFiles[i].getName());
					if (listOfFiles[i].getName().endsWith(".fsa") || listOfFiles[i].getName().endsWith(".txt"))
						listFiles.add(listOfFiles[i].getAbsolutePath());
				} else if (listOfFiles[i].isDirectory()) {
					AppContext.debug("Directory " + listOfFiles[i].getName() + " not added in ZIP");
				}
			}
			String paths[] = destdir.split("/");
			String zipfilename = AppContext.getTempDir() + paths[paths.length - 1] + ".zip";
			CreateZipMultipleFiles zip = new CreateZipMultipleFiles(zipfilename, listFiles);
			zip.create(false);

		}
		// compress directory

		/*
		 * java -jar GenomeAnalysisTK.jar \ -T FastaAlternateReferenceMaker \ -R
		 * reference.fasta \ -o output.fasta \ -L input.intervals \ -V input.vcf \ //
		 * FastaAlternateReferenceMaker maker = new FastaAlternateReferenceMaker();
		 */

		return destdir;

	}
}

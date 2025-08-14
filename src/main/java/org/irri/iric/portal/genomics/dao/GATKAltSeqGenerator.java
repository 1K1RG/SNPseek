package org.irri.iric.portal.genomics.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.CreateZipMultipleFiles;
import org.irri.iric.portal.admin.JobsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class GATKAltSeqGenerator {

	@Autowired
	@Qualifier("JobsFacade")
	private JobsFacade jobsfacade;

	// private boolean isWindows=false;

	// String tempdir="out4/";
	String refseq = null; // "msu7.all.chrs.fa";
	// String outfile="out4/altgenes";
	String outdir = null;
	String workdir = null;
	String varlistpath = null; // "MANIFEST_AWS";
	String gatkpath = null; // workdir + "GenomeAnalysisTK.jar";

	public GATKAltSeqGenerator(String destdir) {
		super();
		// TODO Auto-generated constructor stub
		outdir = destdir;
		workdir = AppContext.getFlatfilesDir() + "getvcfseq/";

		varlistpath = workdir + "MANIFEST_AWS";
		gatkpath = workdir + "GenomeAnalysisTK.jar";

	}

	void getAltSequence(Map<String, String> intervals, String reference) {
		getAltSequence(workdir + "MANIFEST_AWS", intervals, false, null, reference);
	}

	void getAltSequence(Map<String, String> intervals, boolean concatSeqs, String reference) {
		getAltSequence(workdir + "MANIFEST_AWS", intervals, concatSeqs, null, reference);
	}

	
	void getAltSequence(String varlistpath, Map<String, String> intervals, String reference) {
		Set varset = new LinkedHashSet();
		try {
			BufferedReader br = new BufferedReader(new FileReader(varlistpath));
			String line = null;
			while ((line = br.readLine()) != null) {
				String boxcode = line.split("\t")[0];
				varset.add(boxcode);
			}
			br.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		getAltSequence(workdir + "MANIFEST_AWS", intervals, false, varset, reference);
	}

	/*
	 * void writeStatus(String status) { try { BufferedWriter bw=new
	 * BufferedWriter(new FileWriter(outdir + "status")); bw.write(status);
	 * bw.close(); } catch(Exception ex) { ex.printStackTrace(); } }
	 */

	void writeStatus(String status) {
		jobsfacade = (JobsFacade) AppContext.checkBean(jobsfacade, "JobsFacade");
		jobsfacade.setStatus(new File(outdir).getName() + ".status", status);
	}

	private void getAltSequence(String varlistpath, Map<String, String> intervals, boolean concatSeqs, Set varset,
			String reference) {

		// windows
		/*
		 * String gatkpath =
		 * "E:\\My Document\\MyEclipse 2015 CI Workspace\\iric_portal_backend\\lib\\GenomeAnalysisTK.jar"
		 * ; String varlistpath = "E:/My Document/Transfer/MANIFEST_AWS"; String
		 * outfile="E:/My Document/Transfer/domestic4genes3k"; String
		 * refseq="E:\\My Document\\Transfer\\msu7\\msu7.all.chrs.fa"; String
		 * tempdir="E:/My Document/Transfer/temp/";
		 */

		// grchpc settings
		// String refseq="msu7.all.chrs.fsa";

		/*
		 * java -jar GenomeAnalysisTK.jar \ -T FastaAlternateReferenceMaker \ -R
		 * reference.fasta \ -o output.fasta \ -L input.intervals \ -V input.vcf \
		 * [--snpmask mask.vcf]
		 */
		// refseq=workdir+ "msu7.all.chrs.fa";

		if (reference.toLowerCase().equals("nipponbare")) {
			refseq = workdir + "msu7.all.chrs.fa";
		} else {
			workdir = workdir + reference + "/";
			refseq = workdir + reference + ".fasta";

			// 9311
			// ScaffoldUn0
			// Scaffold

			// kas
			// UM
		}

		try {

			BufferedWriter bwlog = new BufferedWriter(new FileWriter(outdir + "cmds.log"));

			Map<Integer, BufferedWriter> mapIntnum2Writer = new LinkedHashMap();
			Map<String, Integer> mapInt2Intnum = new LinkedHashMap();
			Map<Integer, String> mapIntnum2Int = new LinkedHashMap();
			String intvalsarr[] = new String[intervals.size()];
			String intvals = "";
			Iterator<String> itIntname = intervals.keySet().iterator();
			int intcnt = 0;
			while (itIntname.hasNext()) {
				String intname = itIntname.next();
				intvalsarr[intcnt] = intervals.get(intname);
				intvals += " " + intvalsarr[intcnt];

				BufferedWriter brint = mapIntnum2Writer.get(intcnt + 1);
				if (brint == null) {
					String locfilename = intvalsarr[intcnt].replace(":", "_").replace("-", "_");
					if (!intname.equals(locfilename))
						locfilename = intname + "_" + locfilename;
					brint = new BufferedWriter(new FileWriter(outdir + locfilename + ".fas"));
					mapIntnum2Writer.put(intcnt + 1, brint);
					// mapIntnum2Int.put(intcnt+1, intname + "_" + intvalsarr[intcnt].replace(":",
					// "_").replace("-","_"));
					mapIntnum2Int.put(intcnt + 1, locfilename);

					bwlog.append("created file " + outdir + locfilename + ".fas\n");
				}

				Integer intnum = mapInt2Intnum.get(intvalsarr[intcnt]);
				if (intnum == null)
					mapInt2Intnum.put(intvalsarr[intcnt], intcnt + 1);

				intcnt++;
			}

			BufferedReader br = new BufferedReader(new FileReader(varlistpath));
			String line = null;

			writeStatus("0%");

			int cnt = 1;
			while ((line = br.readLine()) != null) {

				try {

					// if(cnt>10) break;
					// if(! (line.startsWith("Nipponbare") && line.endsWith("snp.vcf.gz")) )
					// continue;
					if (!(line.startsWith(reference) && line.endsWith("snp.vcf.gz")))
						continue;

					String vars[] = line.split("\\/");
					String variety = vars[vars.length - 1].split("\\.")[0];
					// bw.append(">" + vars[vars.length-1] +"\n");

					if (varset != null && !varset.contains(variety))
						continue;

					if (cnt % 100 == 0) {
						bwlog.write("reading variety " + cnt + ":  " + line + "\n");
					}
					if (cnt % 10 == 0) {
						int totalvar = 3024;
						if (varset != null)
							totalvar = varset.size();
						writeStatus(cnt * 100 / totalvar + "%");
					}

					int longesti = -1;

					if (AppContext.isLocalhost()) {
						AppContext.debug("workdir:" + workdir);
						AppContext.debug(
								"executing: tabix -h http://s3.amazonaws.com/3kricegenome/" + line + " " + intvals);
						AppContext.debug("executing: java -jar " + gatkpath + " -T FastaAlternateReferenceMaker -R "
								+ refseq + " -L " + intvals + " -V:VCF " + outdir + "temp.vcf -o" + outdir
								+ "temp.fas --use_IUPAC_sample " + variety);

					} else {

						try {
							new File(outdir + "temp.vcf").delete();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						;
						try {
							new File(outdir + "temp.vcf.idx").delete();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						;
						try {
							new File(outdir + "temp.fas").delete();
						} catch (Exception ex) {
							ex.printStackTrace();
						}
						;

						bwlog.append("executing: tabix -h http://s3.amazonaws.com/3kricegenome/" + line + " " + intvals
								+ "\n");

						String[] argarr = (String[]) ArrayUtils.addAll(
								new String[] { "tabix", "-h", "http://s3.amazonaws.com/3kricegenome/" + line },
								intvalsarr);

						// ProcessBuilder pbvcf = new ProcessBuilder("tabix", "-h",
						// "http://s3.amazonaws.com/3kricegenome/" + line, intvals);
						ProcessBuilder pbvcf = new ProcessBuilder(argarr);
						pbvcf.directory(new File(workdir));
						pbvcf.redirectOutput(Redirect.to(new File(outdir + "temp.vcf")));
						Process pvcf = pbvcf.start();
						pvcf.waitFor();
						pvcf.getInputStream().close();
						pvcf.getOutputStream().close();
						pvcf.getErrorStream().close();

						bwlog.append("executing: java -jar " + gatkpath + " -T FastaAlternateReferenceMaker -R "
								+ refseq + " -L " + intvals + " -V:VCF " + outdir + "temp.vcf -o" + outdir
								+ "temp.fas --use_IUPAC_sample " + variety + "\n");

						Runtime rt = Runtime.getRuntime();
						Process proc = null;
						if (concatSeqs) {
							proc = rt.exec(new String[] { "java", "-jar", gatkpath, "-T",
									"FastaAlternateReferenceMaker", "-R", refseq, "-L", outdir + "temp.vcf", "-V:VCF",
									outdir + "temp.vcf", "-o", outdir + "temp.fas", "--use_IUPAC_sample", variety });
						} else {
							proc = rt.exec(new String[] { "java", "-jar", gatkpath, "-T",
									"FastaAlternateReferenceMaker", "-R", refseq, "-L", outdir + "temp.vcf", // intvals,
									"-V:VCF", outdir + "temp.vcf", "-o", outdir + "temp.fas", "--use_IUPAC_sample",
									variety });
						}

						/*
						 * BufferedReader stdInput = new BufferedReader(new
						 * InputStreamReader(proc.getInputStream()));
						 */

						BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

						proc.waitFor();

						String s = null;

						BufferedReader brfas = new BufferedReader(new FileReader(outdir + "temp.fas"));
						StringBuffer seqbuf = new StringBuffer();
						int curintnum = -1;
						while ((s = brfas.readLine()) != null) {
							if (s.startsWith(">")) {
								if (seqbuf.length() > 0) {
									BufferedWriter brint = mapIntnum2Writer.get(curintnum);
									brint.append(seqbuf);
									seqbuf = new StringBuffer();
								}
								curintnum = Integer.valueOf(s.replace(">", "").trim());
								seqbuf.append(">" + variety + "|" + mapIntnum2Int.get(curintnum) + "\n");
							} else
								seqbuf.append(s).append("\n");
						}
						if (seqbuf.length() > 0) {
							BufferedWriter brint = mapIntnum2Writer.get(curintnum);
							brint.append(seqbuf).append("\n");
						}

						// // read the output from the command
						// System.out.println("Here is the standard output of the command:\n");
						// String s = null;
						// List listSeqs=new ArrayList();
						// int icds=0;
						// boolean isfirstlongest=false;
						// StringBuffer buffCDS=new StringBuffer();
						// while ((s = stdInput.readLine()) != null) {
						// String outline=s.trim();
						// if(outline.isEmpty() || outline.startsWith(">")) {
						// if(buffCDS.length()>0) listSeqs.add(buffCDS.toString());
						// buffCDS=new StringBuffer();
						// continue;
						// }
						// else buffCDS.append(outline);
						// /*
						// if(outline.startsWith(">1") && outline.endsWith(">1")) {
						// listSeqs=new ArrayList();
						// continue;
						// } else if(outline.startsWith(">")) {
						// icds++;
						// continue;
						// }
						//
						// else listSeqs.add(outline); */
						// }
						// if(buffCDS.length()>0) listSeqs.add(buffCDS.toString());
						//
						// bw.append(">" + variety + "\n" );
						// for(icds=0; icds<listSeqs.size(); icds++) {
						// bw.append((String)listSeqs.get(icds)).append("\n");
						// if(icds==longesti) {
						// bwlong.append(">" + variety + "|" + (icds+1) + "\n" );
						// bwlong.append(listSeqs.get(icds) + "\n" );
						// }
						//
						// }

						// read any errors from the attempted command
						// System.out.println("Here is the standard error of the command (if any):\n");
						while ((s = stdError.readLine()) != null) {
							// System.out.println(s);
							bwlog.append(s).append("\n");
						}
						stdError.close();

						bwlog.flush();

					}

				} catch (Exception ex) {
					ex.printStackTrace();
				}

				cnt++;
			}

			br.close();
			bwlog.close();
			Iterator<BufferedWriter> itBw = mapIntnum2Writer.values().iterator();
			while (itBw.hasNext()) {
				BufferedWriter bwint = itBw.next();
				bwint.flush();
				bwint.close();
			}

			// zip all files
			/*
			 * try { new File(outdir+"temp.vcf").delete(); } catch(Exception ex)
			 * {ex.printStackTrace();}; try { new File(outdir + "temp.vcf.idx").delete(); }
			 * catch(Exception ex) {ex.printStackTrace();}; try { new
			 * File(outdir+"temp.fas").delete(); } catch(Exception ex)
			 * {ex.printStackTrace();};
			 */

			List listFiles = new ArrayList();

			File folder = new File(outdir);
			File[] listOfFiles = folder.listFiles();

			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile()) {
					// System.out.println("File " + listOfFiles[i].getName());
					if (listOfFiles[i].getName().startsWith("temp."))
						continue;
					if (listOfFiles[i].getName().endsWith(".fas") || listOfFiles[i].getName().endsWith(".txt"))
						listFiles.add(listOfFiles[i].getAbsolutePath());
				} else if (listOfFiles[i].isDirectory()) {
					AppContext.debug("Directory " + listOfFiles[i].getName() + " not added in ZIP");
				}
			}

			String paths[];
			if (AppContext.isWindows())
				paths = outdir.replaceAll(Pattern.quote("\\"), "\\\\").split("\\\\");
			else
				paths = outdir.split("/");

			String zipfilename = AppContext.getTempDir() + paths[paths.length - 1] + ".zip";
			CreateZipMultipleFiles zip = new CreateZipMultipleFiles(zipfilename, listFiles);
			zip.create(false);

			writeStatus("DONE");

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public static void main(String[] args) {
		
		List intervals = new ArrayList();
		/*
		 * intervals.add("chr01:36445018-36449951");
		 * intervals.add("chr04:34231185-34233373");
		 * intervals.add("chr07:2839475-2839979");
		 * intervals.add("chr07:6062888-6069317");
		 */

		Map mapLoc2Int = new LinkedHashMap();

		// run in pollux
		// mapLoc2Int.put( "LOC_Os04g57530", "chr04:34231185-34233373");
		// mapLoc2Int.put( "LOC_Os01g62920", "chr01:36445018-36449951");
		//
		//
		//
		// mapLoc2Int.put("OS01G0182600","chr01:4329362-4338486");
		// mapLoc2Int.put("LOC_OS01G10110","chr01:5270103-5275678");
		// mapLoc2Int.put("OS01G0197700","chr01:5270449-5275585");
		// mapLoc2Int.put("OS01G0367100","chr01:14993204-14998103");
		// mapLoc2Int.put("LOC_OS01G40630","chr01:22954908-22959225");

		// mapLoc2Int.put("LOC_OS01G52050","chr01:29927543-29931487");
		// mapLoc2Int.put("LOC_OS01G54270","chr01:31220321-31228566");
		// mapLoc2Int.put("LOC_OS01G61480","chr01:35558148-35559225");
		// mapLoc2Int.put("OS01G0831000","chr01:35558148-35559225");
		// mapLoc2Int.put("LOC_OS01G62920","chr01:36445019-36449951");
		// mapLoc2Int.put("LOC_OS01G66100","chr01:38382382-38385504");
		// mapLoc2Int.put("OS01G0907600","chr01:39501378-39502910");
		// mapLoc2Int.put("LOC_OS02G05980","chr02:2978855-2982402");
		// mapLoc2Int.put("LOC_OS02G14720","chr02:8114961-8121925");
		// mapLoc2Int.put("OS02G0674800","chr02:27487866-27494913");
		// mapLoc2Int.put("LOC_OS02G49230","chr02:30094259-30099674");
		// mapLoc2Int.put("OS03G0227700","chr03:6737549-6744486");
		// mapLoc2Int.put("OS03G0269100","chr03:8950027-8952082");
		// mapLoc2Int.put("OS03G0449200","chr03:19244015-19248324");
		// mapLoc2Int.put("LOC_OS03G44710","chr03:25197057-25206961");
		// mapLoc2Int.put("OS03G0706500","chr03:28428504-28430438");
		// mapLoc2Int.put("LOC_OS03G49880","chr03:28428504-28430462");
		// mapLoc2Int.put("OS03G0752800","chr03:31031753-31041563");
		// mapLoc2Int.put("LOC_OS04G33740","chr04:20422171-20427062");
		// mapLoc2Int.put("LOC_OS04G38660","chr04:22969845-22970964");

		// pl
		// mapLoc2Int.put("LOC_OS04G38680","chr04:22977814-22980352");
		// mapLoc2Int.put("LOC_OS04G39020","chr04:23171426-23176369");
		// mapLoc2Int.put("OS04G0550600","chr04:27567824-27570449");
		// mapLoc2Int.put("LOC_OS04G46470","chr04:27567824-27570926");
		// mapLoc2Int.put("LOC_OS04G51000","chr04:30182589-30185852");
		// mapLoc2Int.put("OS04G0615000","chr04:31212165-31214739");
		// mapLoc2Int.put("LOC_OS04G53300","chr04:31749103-31752274");
		// mapLoc2Int.put("LOC_OS04G56170","chr04:33488512-33492876");
		// mapLoc2Int.put("LOC_OS04G57530","chr04:34231186-34233373");

		// run in grchpc
		// mapLoc2Int.put( "LOC_Os07g05900", "chr07:2839475-2839979");
		// mapLoc2Int.put( "LOC_Os07g11020", "chr07:6062888-6069317");
		// mapLoc2Int.put("OS05G0187500","chr05:5365121-5366701");
		// mapLoc2Int.put("OS05G0482400","chr05:23728568-23738372");
		// mapLoc2Int.put("OS06G0133000","chr06:1765622-1770574");
		// mapLoc2Int.put("OS06G0142600","chr06:2234581-2239058");

		// run in pollux2
		// mapLoc2Int.put("LOC_OS06G06050","chr06:2780715-2785271");
		// mapLoc2Int.put("OS06G0157500","chr06:2926823-2928474");
		// mapLoc2Int.put("OS06G0157700","chr06:2940004-2942452");
		// mapLoc2Int.put("OS06G0205100","chr06:5315163-5316640");
		// mapLoc2Int.put("LOC_OS06G12450","chr06:6748358-6753338");
		// mapLoc2Int.put("LOC_OS06G16370","chr06:9336359-9338643");
		// mapLoc2Int.put("LOC_OS06G40080","chr06:23853714-23858061");
		// mapLoc2Int.put("LOC_OS06G40780","chr06:24311420-24316382");
		// mapLoc2Int.put("LOC_OS07G05900","chr07:2839476-2839979");
		// mapLoc2Int.put("LOC_OS07G11020","chr07:6062889-6069317");
		// mapLoc2Int.put("LOC_OS07G15770","chr07:9152402-9155185");
		// mapLoc2Int.put("LOC_OS07G39700","chr07:23796611-23797642");

		// // pollux3
		// mapLoc2Int.put("LOC_OS07G42410","chr07:25381698-25389547");
		// mapLoc2Int.put("LOC_OS07G47330","chr07:28299591-28301089");
		// mapLoc2Int.put("OS07G0669500","chr07:28299602-28301089");
		// mapLoc2Int.put("LOC_OS08G07740","chr08:4333717-4335434");
		// mapLoc2Int.put("OS08G0174700","chr08:4344171-4350465");
		// mapLoc2Int.put("LOC_OS08G39890","chr08:25274449-25278696");
		// mapLoc2Int.put("OS09G0286600","chr09:6387891-6389789");
		// mapLoc2Int.put("OS09G0287000","chr09:6404482-6406039");
		// mapLoc2Int.put("LOC_OS09G26999","chr09:16411151-16415862");
		// mapLoc2Int.put("LOC_OS09G28440","chr09:17306100-17307199");
		//
		// // plloux4
		/*
		 * mapLoc2Int.put("OS09G0497500","chr09:19239245-19240734");
		 * mapLoc2Int.put("OS09G0529300","chr09:20731589-20734724");
		 * mapLoc2Int.put("OS10G0419200","chr10:14739603-14743222");
		 * mapLoc2Int.put("OS10G0463400","chr10:17076098-17081344");
		 * mapLoc2Int.put("OS11G0102100","chr11:68344-70989");
		 * mapLoc2Int.put("LOC_OS11G05470","chr11:2452525-2454103");
		 * mapLoc2Int.put("LOC_OS11G12740","chr11:7193230-7198552");
		 * mapLoc2Int.put("OS12G0101600","chr12:64393-67038");
		 * mapLoc2Int.put("OS12G0555600","chr12:22602934-22607307");
		 */

		/*
		 * LOC_Os01g62920.1:cds_1 - chr01:36448696..36449502 chr01:36447492..36447871
		 * chr01:36446308..36446368 ( chr01:36445610..36446200
		 */
		//
		// mapLoc2Int.put("LOC_Os01g62920", "chr01:36445610-36446200
		// chr01:36446308-36446368 chr01:36447492-36447871 chr01:36448696-36449502");

		// -
		// chr04:34232387..34233221
		// chr04:34231186..34231523
		// mapLoc2Int.put("LOC_Os04g57530", "chr04:34231186-34231523
		// chr04:34232387-34233221");

		// -
		// chr07:2839476..2839979

		// mapLoc2Int.put("LOC_Os07g05900", "chr07:2839476-2839979");
		//
		// +
		// chr07:6062889..6062983
		// chr07:6063303..6063608
		// chr07:6066306..6066402
		// chr07:6066791..6066847
		// chr07:6067050..6067599
		// chr07:6067770..6068086
		mapLoc2Int.put("LOC_Os07g11020",
				"chr07:6062889-6062983 chr07:6063303-6063608 chr07:6066306-6066402 chr07:6066791-6066847 chr07:6067050-6067599 chr07:6067770-6068086");

		
		new GATKAltSeqGenerator(null).getAltSequence(mapLoc2Int, false,"Nipponbare");

		// System.out.println("..done");
	}
}

package org.irri.iric.portal.genomics.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.irri.iric.ds.chado.domain.LocalAlignment;
import org.irri.iric.ds.chado.domain.impl.LocalAlignmentImpl;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.dao.LocalAlignmentDAO;
import org.irri.iric.portal.genomics.LocalAlignmentQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Repository;

@Repository("LocalAlignmentDAO")
public class LocalAlignmentBLASTlocalDAOImpl implements LocalAlignmentDAO {

	@Autowired
	private TaskExecutor taskExecutor;

	// @Override
	public List alignWithDBOld(LocalAlignmentQuery query) throws Exception {
		

		// String tmpfasta = AppContext.getTempDir() + "queryseq-" +
		// AppContext.createTempFilename() + ".fasta";
		String tmpfasta = "queryseq_" + AppContext.createTempFilename() + ".fasta";

		FileWriter f1 = new FileWriter(tmpfasta);

		String myquery = query.getQueryseq().trim();
		if (myquery.startsWith(">"))
			f1.write(myquery);
		else {
			f1.write(">queryseq\n");
			f1.write(myquery);
		}
		f1.close();

		// pident means Percentage of identical matches
		// nident means Number of identical matches
		// mismatch means Number of mismatches
		// score means Raw score
		// length means Alignment length
		// sstrand means Subject Strand

		String outfmt = "7 qacc sacc evalue qstart qend sstart send sstrand mismatch nident pident score length";

		String command = AppContext.getPathToLocalBlast() + query.getQuerytype() + " -db "
				+ AppContext.getPathToLocalBlastData() + query.getDbname() + " -query " + tmpfasta + " -outfmt \""
				+ outfmt + "\" -evalue " + query.getEvalue(); // " + " �out " + tmpfasta + ".out" ; // tabular

		// String command = "notepad.exe";
		AppContext.debug("Executing " + command);

		// Here is an example that starts a process with a modified working directory
		// and environment, and redirects standard output and error to be appended to a
		// log file:

		ProcessBuilder pb = new ProcessBuilder(AppContext.getPathToLocalBlast() + query.getQuerytype(), "-db",
				AppContext.getPathToLocalBlastData() + query.getDbname(), "-query", tmpfasta, "-outfmt", outfmt,
				"-evalue", String.valueOf(query.getEvalue()));
		// "�out", tmpfasta + ".out");
		// Map<String, String> env = pb.environment();
		// env.put("VAR1", "myValue");
		// env.remove("OTHERVAR");
		// env.put("VAR2", env.get("VAR1") + "suffix");
		// pb.directory(new File("myDir"));
		File outfile = new File(tmpfasta + ".out");
		File errfile = new File(tmpfasta + ".err");
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

		/*
		 * List<String> commands = new ArrayList<String>(); commands.add(command); //
		 * execute the command SystemCommandExecutor commandExecutor = new
		 * SystemCommandExecutor(commands);
		 * 
		 * //int result = commandExecutor.executeCommand(); // get the stdout and stderr
		 * from the command that was run //StringBuilder stdout =
		 * commandExecutor.getStandardOutputFromCommand(); //StringBuilder stderr =
		 * commandExecutor.getStandardErrorFromCommand();
		 * 
		 * Object[] excommand = commandExecutor.executeCommand2(); int result =
		 * (Integer)excommand[0]; StringBuilder stdout = (StringBuilder)excommand[1];
		 * StringBuilder stderr = (StringBuilder)excommand[2];
		 * 
		 * // print the stdout and stderr
		 * AppContext.debug("The numeric result of the command was: " + result);
		 * //System.out.println("STDOUT:"); //System.out.println(stdout);
		 * AppContext.debug("STDERR:" + stderr);
		 * 
		 * FileWriter fout = new FileWriter(tmpfasta + ".out2");
		 * fout.write(stdout.toString()); //System.out.println(line); fout.close();
		 */

		/*
		 * Process qq=Runtime.getRuntime().exec(command); qq.waitFor(); FileWriter fout
		 * = new FileWriter(tmpfasta + ".out2"); String line=""; BufferedReader in = new
		 * BufferedReader(new InputStreamReader(qq.getInputStream()) ); while ((line =
		 * in.readLine()) != null) { fout.write(line); //System.out.println(line);
		 * AppContext.debug(line); } in.close(); fout.close();
		 */

		int maxwait = 20;
		while (maxwait > 0) {
			File f = new File(tmpfasta + ".out");
			if (f.exists() && f.length() > 0)
				break;
			Thread.sleep(5000);
			maxwait--;
		}

		Map<String, Double> mapQueryMaxE = new HashMap();

		List listAlign = new ArrayList();
		if (maxwait > 0) {
			AppContext.debug("blastoutput written in " + tmpfasta + ".out");
			BufferedReader br = new BufferedReader(new FileReader(tmpfasta + ".out"));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				// System.out.println(sCurrentLine);
				sCurrentLine = sCurrentLine.trim();
				if (sCurrentLine.startsWith("#") || sCurrentLine.isEmpty())
					continue;

				String[] alignment = sCurrentLine.split("\t");

				// "qacc sacc evalue qstart qend sstart send sstrand mismatch nident pident
				// score length"

				// listAlign.add( new LocusLocalAlignment(alignment[0], alignment[1],
				// Double.valueOf(alignment[2]),
				// Long.valueOf(alignment[3]), Long.valueOf(alignment[4]),
				// Long.valueOf(alignment[5]), Long.valueOf(alignment[6]) ) );

				// (String qacc, String sacc, Double evalue,
				// Long qstart, Long qend, Long sstart, Long send, Integer sstrand, Double
				// rawScore,
				// Long alignmentLength, Double percentMatches, Long matches,
				// Long mismatches)

				Integer intstrand = 1;
				if (alignment[7].equals("plus"))
					intstrand = 1;
				else if (alignment[7].equals("minus"))
					intstrand = -1;
				else
					intstrand = 0;

				listAlign.add(new LocalAlignmentImpl(alignment[0], alignment[1], Double.valueOf(alignment[2]),
						Long.valueOf(alignment[3]), Long.valueOf(alignment[4]), Long.valueOf(alignment[5]),
						Long.valueOf(alignment[6]), intstrand, Double.valueOf(alignment[11]),
						Long.valueOf(alignment[12]), Double.valueOf(alignment[10]), Long.valueOf(alignment[9]),
						Long.valueOf(alignment[8])));
			}
		} else
			AppContext.debug("blast did not create output");

		if (listAlign.size() == 0) {
			// check if error
			File f = new File(tmpfasta + ".err");
			if (f.exists() && f.length() > 0) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String sCurrentLine;
				StringBuffer sb = new StringBuffer();
				while ((sCurrentLine = br.readLine()) != null) {
					sb.append(sCurrentLine);
				}
				AppContext.debug("Blast error:" + sb);

				throw new Exception(sb.toString());
			}
		}

		return listAlign;
	}

	@Override
	public List alignWithDB(LocalAlignmentQuery query) throws Exception {
		

		String tempfilecode = AppContext.createTempFilename();

		// String tmpfasta = AppContext.getTempDir() + "queryseq-" +
		// AppContext.createTempFilename() + ".fasta";
		String tmpfasta = AppContext.getTempDir() + "queryseq_" + tempfilecode + ".fasta";

		FileWriter f1 = new FileWriter(tmpfasta);

		Map<String, String> mapBlastIds = new HashMap();
		String myquery = query.getQueryseq().trim();
		if (myquery.startsWith(">") || myquery.startsWith(";")) {

			int querycnt = 1;
			String querylines[] = myquery.split("\n");
			for (int iline = 0; iline < querylines.length; iline++) {
				String queryline = querylines[iline].trim();
				if (queryline.startsWith(";"))
					continue;
				if (queryline.startsWith(">")) {
					mapBlastIds.put("queryseq_" + tempfilecode + "_" + querycnt, queryline.substring(1));
					queryline = ">" + "queryseq_" + tempfilecode + "_" + querycnt;
					querycnt++;
				}
				f1.write(queryline + "\n");
			}
			// f1.write(myquery);
		} else {
			f1.write(">queryseq\n");
			f1.write(myquery);
			mapBlastIds.put("queryseq", "queryseq");
		}
		f1.close();

		// pident means Percentage of identical matches
		// nident means Number of identical matches
		// mismatch means Number of mismatches
		// score means Raw score
		// length means Alignment length
		// sstrand means Subject Strand

		String outfmt = "7 qacc sacc evalue qstart qend sstart send sstrand mismatch nident pident score length";

		if (query.isSequence())
			outfmt += " qseq sseq";

		String command = AppContext.getPathToLocalBlast() + query.getQuerytype() + " -db "
				+ AppContext.getPathToLocalBlastData() + query.getDbname() + " -query " + tmpfasta + " -outfmt \""
				+ outfmt + "\" -evalue " + query.getEvalue() + "  out " + tmpfasta + ".out"; // tabular

		// String command = "notepad.exe";
		AppContext.debug("Executing " + command);

		// Here is an example that starts a process with a modified working directory
		// and environment, and redirects standard output and error to be appended to a
		// log file:

		ProcessBuilder pb = new ProcessBuilder(AppContext.getPathToLocalBlast() + query.getQuerytype(), "-db",
				AppContext.getPathToLocalBlastData() + query.getDbname(), "-query", tmpfasta, "-outfmt", outfmt,
				"-evalue", String.valueOf(query.getEvalue()));
		// "�out", tmpfasta + ".out");
		// Map<String, String> env = pb.environment();
		// env.put("VAR1", "myValue");
		// env.remove("OTHERVAR");
		// env.put("VAR2", env.get("VAR1") + "suffix");
		// pb.directory(new File("myDir"));
		File outfile = new File(tmpfasta + ".out");
		File errfile = new File(tmpfasta + ".err");
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

		int maxwait = 20;
		while (maxwait > 0) {
			File f = new File(tmpfasta + ".out");
			if (f.exists() && f.length() > 0)
				break;
			Thread.sleep(5000);
			maxwait--;
		}

		Map<String, Double> mapQuery2MinE = new HashMap();
		Map<String, List<LocalAlignment>> mapQuery2Alignment = new HashMap();

		List listAlign = new ArrayList();
		if (maxwait > 0) {
			AppContext.debug("blastoutput written in " + tmpfasta + ".out");
			BufferedReader br = new BufferedReader(new FileReader(tmpfasta + ".out"));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				// System.out.println(sCurrentLine);
				sCurrentLine = sCurrentLine.trim();
				if (sCurrentLine.startsWith("#") || sCurrentLine.isEmpty())
					continue;

				String[] alignment = sCurrentLine.split("\t");

				Integer intstrand = 1;
				if (alignment[7].equals("plus"))
					intstrand = 1;
				else if (alignment[7].equals("minus"))
					intstrand = -1;
				else
					intstrand = 0;

				Double eval = Double.valueOf(alignment[2]);
				if (query.isTophitonly()) {

					Double minE = mapQuery2MinE.get(alignment[0].trim());
					if (minE == null) {
						mapQuery2MinE.put(alignment[0].trim(), eval);
						List listAligns = new ArrayList();
						mapQuery2Alignment.put(alignment[0].trim(), listAligns);
						LocalAlignment align = new LocalAlignmentImpl(mapBlastIds.get(alignment[0].trim()),
								alignment[1], eval, Long.valueOf(alignment[3]), Long.valueOf(alignment[4]),
								Long.valueOf(alignment[5]), Long.valueOf(alignment[6]), intstrand,
								Double.valueOf(alignment[11]), Long.valueOf(alignment[12]),
								Double.valueOf(alignment[10]), Long.valueOf(alignment[9]), Long.valueOf(alignment[8]));
						if (query.isSequence()) {
							align.setQSequence(alignment[13]);
							align.setSSequence(alignment[14]);
						}
						listAligns.add(align);
					} else {
						if (eval < minE) {
							mapQuery2MinE.remove(alignment[0].trim());
							mapQuery2MinE.put(alignment[0].trim(), eval);
							mapQuery2Alignment.remove(alignment[0].trim());
							List listAligns = new ArrayList();
							mapQuery2Alignment.put(alignment[0].trim(), listAligns);
							LocalAlignment align = new LocalAlignmentImpl(mapBlastIds.get(alignment[0].trim()),
									alignment[1], eval, Long.valueOf(alignment[3]), Long.valueOf(alignment[4]),
									Long.valueOf(alignment[5]), Long.valueOf(alignment[6]), intstrand,
									Double.valueOf(alignment[11]), Long.valueOf(alignment[12]),
									Double.valueOf(alignment[10]), Long.valueOf(alignment[9]),
									Long.valueOf(alignment[8]));
							if (query.isSequence()) {
								align.setQSequence(alignment[13]);
								align.setSSequence(alignment[14]);
							}
							listAligns.add(align);
						} else if (eval.equals(minE)) {
							List listAligns = mapQuery2Alignment.get(alignment[0].trim());
							LocalAlignment align = new LocalAlignmentImpl(mapBlastIds.get(alignment[0].trim()),
									alignment[1], eval, Long.valueOf(alignment[3]), Long.valueOf(alignment[4]),
									Long.valueOf(alignment[5]), Long.valueOf(alignment[6]), intstrand,
									Double.valueOf(alignment[11]), Long.valueOf(alignment[12]),
									Double.valueOf(alignment[10]), Long.valueOf(alignment[9]),
									Long.valueOf(alignment[8]));
							if (query.isSequence()) {
								align.setQSequence(alignment[13]);
								align.setSSequence(alignment[14]);
							}
							listAligns.add(align);
						} else
							continue;
					}
				} else {
					LocalAlignment align = new LocalAlignmentImpl(mapBlastIds.get(alignment[0].trim()), alignment[1],
							eval, Long.valueOf(alignment[3]), Long.valueOf(alignment[4]), Long.valueOf(alignment[5]),
							Long.valueOf(alignment[6]), intstrand, Double.valueOf(alignment[11]),
							Long.valueOf(alignment[12]), Double.valueOf(alignment[10]), Long.valueOf(alignment[9]),
							Long.valueOf(alignment[8]));
					if (query.isSequence()) {
						align.setQSequence(alignment[13]);
						align.setSSequence(alignment[14]);
					}
					listAlign.add(align);
				}

				// "qacc sacc evalue qstart qend sstart send sstrand mismatch nident pident
				// score length"

				// listAlign.add( new LocusLocalAlignment(alignment[0], alignment[1],
				// Double.valueOf(alignment[2]),
				// Long.valueOf(alignment[3]), Long.valueOf(alignment[4]),
				// Long.valueOf(alignment[5]), Long.valueOf(alignment[6]) ) );

				// (String qacc, String sacc, Double evalue,
				// Long qstart, Long qend, Long sstart, Long send, Integer sstrand, Double
				// rawScore,
				// Long alignmentLength, Double percentMatches, Long matches,
				// Long mismatches)
			}
			br.close();

			if (query.isTophitonly()) {
				Iterator<List<LocalAlignment>> itcolAligns = mapQuery2Alignment.values().iterator();
				while (itcolAligns.hasNext()) {
					List<LocalAlignment> lAlign = itcolAligns.next();
					listAlign.addAll(lAlign);
				}

			}

		} else
			AppContext.debug("blast did not create output");

		if (listAlign.size() == 0) {
			// check if error
			File f = new File(tmpfasta + ".err");
			if (f.exists() && f.length() > 0) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String sCurrentLine;
				StringBuffer sb = new StringBuffer();
				while ((sCurrentLine = br.readLine()) != null) {
					sb.append(sCurrentLine);
				}
				AppContext.debug("Blast error:" + sb);

				throw new Exception(sb.toString());
			}
		}

		return listAlign;
	}

	/*
	 * BLAST output formats
	 *** 
	 * Formatting options -outfmt <String> alignment view options: 0 = pairwise, 1 =
	 * query-anchored showing identities, 2 = query-anchored no identities, 3 = flat
	 * query-anchored, show identities, 4 = flat query-anchored, no identities, 5 =
	 * XML Blast output, 6 = tabular, 7 = tabular with comment lines, 8 = Text
	 * ASN.1, 9 = Binary ASN.1, 10 = Comma-separated values, 11 = BLAST archive
	 * format (ASN.1) 12 = JSON Seqalign output
	 * 
	 * Options 6, 7, and 10 can be additionally configured to produce a custom
	 * format specified by space delimited format specifiers. The supported format
	 * specifiers are: qseqid means Query Seq-id qgi means Query GI qacc means Query
	 * accesion qaccver means Query accesion.version qlen means Query sequence
	 * length sseqid means Subject Seq-id sallseqid means All subject Seq-id(s),
	 * separated by a ';' sgi means Subject GI sallgi means All subject GIs sacc
	 * means Subject accession saccver means Subject accession.version sallacc means
	 * All subject accessions slen means Subject sequence length qstart means Start
	 * of alignment in query qend means End of alignment in query sstart means Start
	 * of alignment in subject send means End of alignment in subject qseq means
	 * Aligned part of query sequence sseq means Aligned part of subject sequence
	 * evalue means Expect value bitscore means Bit score score means Raw score
	 * length means Alignment length pident means Percentage of identical matches
	 * nident means Number of identical matches mismatch means Number of mismatches
	 * positive means Number of positive-scoring matches gapopen means Number of gap
	 * openings gaps means Total number of gaps ppos means Percentage of
	 * positive-scoring matches frames means Query and subject frames separated by a
	 * '/' qframe means Query frame sframe means Subject frame btop means Blast
	 * traceback operations (BTOP) staxids means unique Subject Taxonomy ID(s),
	 * separated by a ';' (in numerical order) sscinames means unique Subject
	 * Scientific Name(s), separated by a ';' scomnames means unique Subject Common
	 * Name(s), separated by a ';' sblastnames means unique Subject Blast Name(s),
	 * separated by a ';' (in alphabetical order) sskingdoms means unique Subject
	 * Super Kingdom(s), separated by a ';' (in alphabetical order) stitle means
	 * Subject Title salltitles means All Subject Title(s), separated by a '<>'
	 * sstrand means Subject Strand qcovs means Query Coverage Per Subject qcovhsp
	 * means Query Coverage Per HSP When not provided, the default value is: 'qseqid
	 * sseqid pident length mismatch gapopen qstart qend sstart send evalue
	 * bitscore', which is equivalent to the keyword 'std' Default = `0' -show_gis
	 * Show NCBI GIs in deflines? -num_descriptions <Integer, >=0> Number of
	 * database sequences to show one-line descriptions for Not applicable for
	 * outfmt > 4 Default = `500' Incompatible with: max_target_seqs -num_alignments
	 * <Integer, >=0> Number of database sequences to show alignments for Default =
	 * `250' Incompatible with: max_target_seqs -line_length <Integer, >=1> Line
	 * length for formatting alignments Not applicable for outfmt > 4 Default = `60'
	 * -html Produce HTML output?
	 * 
	 */
}

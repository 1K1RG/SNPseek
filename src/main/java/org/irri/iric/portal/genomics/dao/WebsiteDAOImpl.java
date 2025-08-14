package org.irri.iric.portal.genomics.dao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.object.WebsiteQuery;
import org.irri.iric.portal.AppContext;
import org.springframework.stereotype.Repository;

@Repository("WebsiteDAO")

public class WebsiteDAOImpl implements WebsiteDAO {

	private String writeGenelistFile(Collection genes, String site, Map params) {
		String infile = "genelist-" + AppContext.createTempFilename() + ".in";
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(AppContext.getTempDir() + infile));
			bw.append("#SITE ").append(site);
			if (params != null && !params.isEmpty()) {
				Iterator itParams = params.keySet().iterator();
				bw.append("#PARAMS\n");
				while (itParams.hasNext()) {
					String param = (String) itParams.next();
					bw.append(param).append("=").append((String) params.get(param)).append("\n");
				}
			}
			bw.append("\n#PANTHOMPATH " + AppContext.getPhantomjsExe()).append("\n#GENES \n");
			Iterator itGenes = genes.iterator();
			while (itGenes.hasNext()) {
				Object gene = itGenes.next();
				if (gene instanceof String)
					bw.append((String) gene);
				else if (gene instanceof Locus)
					bw.append(((Locus) gene).getUniquename());
				else
					bw.append(gene.toString());
				if (itGenes.hasNext())
					bw.append("\n");
			}
			bw.flush();
			bw.close();
			return AppContext.getTempDir() + infile;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	// @Override
	public List<String> getURL1(WebsiteQuery query) {
		
		
		List listeURL = new ArrayList();
		Iterator<String> itSites = query.getSite().iterator();
		while (itSites.hasNext()) {

			String infile = writeGenelistFile(query.getGenes(), itSites.next(), null);
			try {

				AppContext.debug(
						"Executing " + "java  -jar   \"" + AppContext.getWebclientPath() + "\" \"" + infile + "\"");

				// String cmdline=AppContext.getWebclientPath();
				// ProcessBuilder pb = new ProcessBuilder(cmdline, "\"" + infile + "\"");

				// ProcessBuilder pb = new ProcessBuilder("java", "-jar", "\"" +
				// AppContext.getWebclientPath() + "\"", "\"" + infile + "\"");
				ProcessBuilder pb = null;
				if (AppContext.isLocalhost())
					pb = new ProcessBuilder("java", "-jar", "\"" + AppContext.getWebclientPath() + "\"",
							"\"" + infile + "\"");
				else
					pb = new ProcessBuilder("java", "-jar", AppContext.getWebclientPath(), infile);

				// pb.directory(new File(AppContext.getFlatfilesDir()));
				String workdir = AppContext.getFlatfilesDir();
				pb.directory(new File(workdir));

				File outfile = new File(infile + ".stdout.txt");
				File errfile = new File(infile + ".stderr.txt");
				// pb.redirectErrorStream(true);
				pb.redirectOutput(Redirect.appendTo(outfile));
				pb.redirectError(Redirect.appendTo(errfile));

				Process process = pb.start();
				int exitValue = process.waitFor();
				AppContext.debug("exitValue=" + exitValue);

				BufferedReader br = new BufferedReader(new FileReader(infile + ".out"));
				String url = br.readLine();
				if (url != null && url.startsWith("#URL")) {
					listeURL.add(url.split("\\s+", 2)[1].trim());
				}

				AppContext.debug(url);
				int len;
				if ((len = process.getErrorStream().available()) > 0) {
					byte[] buf = new byte[len];
					process.getErrorStream().read(buf);
					AppContext.debug("Command error:\t\"" + new String(buf) + "\"");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return listeURL;

	}

	@Override
	public List<String> getURL(WebsiteQuery query) {
		
		List listeURL = new ArrayList();
		Iterator<String> itSites = query.getSite().iterator();
		while (itSites.hasNext()) {
			System.out.println("Sites: " + itSites);
			String infile = writeGenelistFile(query.getGenes(), itSites.next(), null);
			try {

				AppContext.debug("java  -jar   \"" + AppContext.getWebclientPath() + "\" \"" + infile + "\"");
				
				
				// REPLACED

//				ProcessBuilder pb = new ProcessBuilder("java", "-jar", AppContext.getWebclientPath(), infile);
//				pb.redirectErrorStream(true);
				
				// External folder where JARs are located
				String webclientJarPath = AppContext.getWebclientPath();
				File jarFile = new File(webclientJarPath);
				String externalJarDir = jarFile.getParent();

				// Build classpath: main JAR + all JARs in the same folder
				String classpath = jarFile.getAbsolutePath() + File.pathSeparator + externalJarDir + File.separator + "*";

				// Main class inside snpseek-webclient-log.jar
				String mainClass = "org.irri.iric.portal.webclient.SnpseekWebClient";

				// Optional: Add Java module workaround for reflection (Java 17+)
				List<String> command = new ArrayList<>();
				command.add("java");
				command.add("--add-opens");
				command.add("java.base/java.lang=ALL-UNNAMED");
				command.add("-cp");
				command.add(classpath);
				command.add(mainClass);
				command.add(infile);

				// Launch
				ProcessBuilder pb = new ProcessBuilder(command);
				pb.redirectErrorStream(true);


				
				Process proc = pb.start();

				InputStream stderr = proc.getInputStream();
			    InputStreamReader isr = new InputStreamReader(stderr);
			    BufferedReader brT = new BufferedReader(isr);
			    String line = null;

			    System.out.println();
			    while ((line = brT.readLine()) != null) {
			        System.out.println(line);

			    }
			    
				proc.waitFor();
				AppContext.debug("stderr " + infile + ": " + AppContext.convertStreamToString(proc.getErrorStream()));
				AppContext.debug("stdout " + infile + ": " + AppContext.convertStreamToString(proc.getInputStream()));

				int exitValue = proc.exitValue();

				AppContext.debug("exitValue=" + exitValue);

				BufferedReader br = new BufferedReader(new FileReader(infile + ".out"));
				String url = br.readLine();
				if (url != null && url.startsWith("#URL")) {
					listeURL.add(url.split("\\s+", 2)[1].trim());
				}

				AppContext.debug(url);

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return listeURL;

	}

}

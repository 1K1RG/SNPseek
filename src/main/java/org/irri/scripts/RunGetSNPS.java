package org.irri.scripts;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.irri.iric.ds.chado.dao.OrganismDAO;
import org.irri.iric.ds.chado.dao.VarietyDAO;
import org.irri.iric.ds.chado.domain.MultiReferencePosition;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImpl;
import org.irri.iric.ds.chado.domain.model.Organism;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "extractSNPS", mixinStandardHelpOptions = true, description = "Runs Extract SNP based on provided flags.")
public class RunGetSNPS implements Runnable {

	@Autowired
	@Qualifier("VarietyDAO")
	private static VarietyDAO varietyDAO;

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;

	@Autowired
	static OrganismDAO organismdao;

	@Option(names = "--outputFile", required = true, description = "Output file")
	String outputFile;

	@Option(names = "--dataset", required = true, description = "Dataset name")
	String dataset;

	@Option(names = "--snpset", required = true, description = "SNP set name")
	String snpset;

	@Option(names = "--chr", required = true, description = "Chromosome (e.g. CHR01)")
	String chr;

	@Option(names = "--samples", description = "sample list File")
	String samples;

	@Option(names = "--snps", description = "SNP list File")
	String snps;

	@Option(names = "--start",  description = "Start position")
	long start;

	@Option(names = "--end",  description = "End position")
	long end;

	@Override
	public void run() {
		
		  URL resource = getClass().getClassLoader().getResource("IpAddresses.properties");
	        System.out.println("IpAddresses.properties found at: " + resource);
	        
		// Call your service here
//	         RunGetSNPS.main(dataset, snpset, chr, samples, snps, start, end);

//		ConfigurableApplicationContext context = new ClassPathXmlApplicationContext("LOCAL_eclipse_applicationContext-business.xml");

		System.out.println("Loading..... LOCAL_applicationContext-business.xml" );
		
		ConfigurableApplicationContext  context = new ClassPathXmlApplicationContext("LOCAL_applicationContext-business.xml");

		
		try {

			String names[] = context.getBeanDefinitionNames();

			for (String name : context.getBeanDefinitionNames()) {	
				System.out.println(name);
			}

			organismdao = (OrganismDAO) AppContext.checkBean(organismdao, "OrganismDAO");

			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

			varietyDAO = (VarietyDAO) AppContext.checkBean(varietyDAO, "VarietyDAO");

			GenotypeFacade gf = context.getBean(GenotypeFacade.class);

			Organism org = organismdao.getOrganismByID(9);

			
			Set lst_dataset = new HashSet<String>();

			Set lst_snpset = new HashSet<String>();

			// String contig = "CHR1";
			String contig = chr;

			// Long lStart = (long) 11218;
			long lStart = start;

			// Long lStop = (long) 12435;
			long lStop = end;

			lst_dataset.add(dataset);

			lst_snpset.add(snpset);

			List<Integer> allStockIds = null;

			Collection snppos = null;
			
			System.out.println("Dataset: " + dataset);
			System.out.println("SNPset: " + snpset);
			System.out.println("Chr: " + chr);
			System.out.println("Start-End: " + start + " - " + end);
			
			

			if (samples != null)
				try {
					allStockIds = Files.lines(Paths.get(samples)).map(String::trim).filter(line -> !line.isEmpty())
							.map(Integer::parseInt).collect(Collectors.toList());

					// Print the list to confirm
					System.out.println("Using Samples: " + allStockIds);

				} catch (IOException e) {
					System.err.println("Error reading file: " + e.getMessage());
				} catch (NumberFormatException e) {
					System.err.println("File contains invalid number: " + e.getMessage());
				}

			if (snps != null) {
				try {
					
					snppos = getSNPpos(Files.readAllLines(Paths.get(snps)));
					System.out.println("Using SNPpos: " + snppos.size());

					

				} catch (IOException e) {
					System.err.println("Error reading file: " + e.getMessage());
				}
			}

		

			DownloadListBox bx = new DownloadListBox(allStockIds, snppos, contig, lst_dataset, lst_snpset, lStart,
					lStop, org, outputFile);

			try {

				//bx.downloadRegion(dataset.trim() + "-" + contig, contig);
				bx.downloadListBulk(",");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} finally {
			context.close(); // prevent resource leak!
		}

	}

	private Set<MultiReferencePosition> getSNPpos(List<String> lines) {
		
		Map<String, Map> mapChr2Pos2Pvalue = new HashMap();
		Map<String, Map> mapChr2Pos2Allele = new HashMap();

		Map<String, Set> mapChr2Set = new TreeMap();
		StringBuilder sb = new StringBuilder();

//		try (BufferedWriter writer = new BufferedWriter(new FileWriter(snpfile, true))) {
//		for (int isnp = 0; isnp < lines.length; isnp++) {
		int isnp = 0;
		for (String line : lines) {
			try {

				String chrposline = line.trim();

				if (chrposline.isEmpty())
					continue;

//					writer.write(chrposline);
//					writer.newLine();

				System.out.println("Lines successfully written to the file.");

				String chrpos[] = chrposline.split("\\s+");
				String chr = "";
				try {
					int intchr = Integer.valueOf(chrpos[0]);
					if (intchr > 9)
						chr = "chr" + intchr;
					else
						chr = "chr0" + intchr;
				} catch (Exception ex) {
					chr = chrpos[0].toLowerCase();
				}

				BigDecimal pos = null;
				try {
					pos = BigDecimal.valueOf(Long.valueOf(chrpos[1]));
				} catch (Exception ex) {
					AppContext.debug("Invalid position chrome position");
					continue;
				}

				Map<BigDecimal, String> mapPos2Allele = mapChr2Pos2Allele.get(chr);
				if (mapPos2Allele == null) {
					mapPos2Allele = new HashMap();
					mapChr2Pos2Allele.put(chr, mapPos2Allele);
				}
				Map<BigDecimal, Double> mapPos2Pvalue = mapChr2Pos2Pvalue.get(chr);
				if (mapPos2Pvalue == null) {
					mapPos2Pvalue = new HashMap();
					mapChr2Pos2Pvalue.put(chr, mapPos2Pvalue);
				}

//				if (hasAllele) {
//					mapPos2Allele.put(pos, chrpos[2]);
//					if (hasPvalue) {
//						try {
//							mapPos2Pvalue.put(pos, Double.valueOf(chrpos[3]));
//						} catch (Exception ex) {
//							AppContext.debug("Invalid p-value " + chrpos[3]);
//						}
//					}
//				} else if (hasPvalue) {
//					try {
//						mapPos2Pvalue.put(pos, Double.valueOf(chrpos[2]));
//					} catch (Exception ex) {
//						AppContext.debug("Invalid number " + chrpos[2]);
//					}
//				}

				Set setPos = mapChr2Set.get(chr);
				if (setPos == null) {
					setPos = new HashSet();
					mapChr2Set.put(chr, setPos);
				}
				setPos.add(pos);

				
//				if (snpCnt> 0)
//					sb.append("\n");
//				sb.append(chrposline);
//				
//				snpCnt++;

			} catch (Exception ex) {
				AppContext.debug("onbuttonSaveSNP exception: ");
				ex.printStackTrace();
//				return false;
			}

		}
//		} catch (IOException e) {
//			System.out.println("Error writing to the file: " + e.getMessage());
//		}

		AppContext.debug("set Pos position");
		Set<MultiReferencePosition> setSNPDBPos = new HashSet();

		Set setSNP = null;
		Set setChrSNP = new HashSet();
		Iterator<String> itChr = mapChr2Set.keySet().iterator();
		while (itChr.hasNext()) {
			String chr = itChr.next();
			setSNP = mapChr2Set.get(chr);

//			if (hasAllele || hasPvalue) {
//				if (this.checkboxVerifySNP.isChecked()) {
//					Iterator<SnpsAllvarsPos> itSnpsDB = genotype.checkSNPInChromosome(
//							organism.getOrganismId().intValue(), chr, setSNP, getVariantSets()).iterator();
//					while (itSnpsDB.hasNext()) {
//						BigDecimal ipos = itSnpsDB.next().getPosition();
//						setSNPDBPos.add(new MultiReferencePositionImplAllelePvalue(organism.getName(), chr, ipos,
//								(String) mapChr2Pos2Allele.get(chr).get(ipos),
//								(Double) mapChr2Pos2Pvalue.get(chr).get(ipos)));
//					}
//
//				}
//
//				Iterator<BigDecimal> itPos = setSNP.iterator();
//				while (itPos.hasNext()) {
//					BigDecimal ipos = itPos.next();
//					setChrSNP.add(new MultiReferencePositionImplAllelePvalue(organism.getName(), chr, ipos,
//							(String) mapChr2Pos2Allele.get(chr).get(ipos),
//							(Double) mapChr2Pos2Pvalue.get(chr).get(ipos)));
//				}
//
//			} else {
//				if (input.isVerifySnp()) {
//					Iterator<SnpsAllvarsPos> itSnpsDB = genotype.checkSNPInChromosome(
//							organism.getOrganismId().intValue(), chr, setSNP, getVariantSets()).iterator();
//					while (itSnpsDB.hasNext()) {
//						setSNPDBPos.add(new MultiReferencePositionImpl(organism.getName(), chr,
//								itSnpsDB.next().getPosition()));
//					}
//
//				}
				Iterator<BigDecimal> itPos = setSNP.iterator();
				while (itPos.hasNext()) {
					setChrSNP.add(new MultiReferencePositionImpl("Japonica Nipponbare", chr, itPos.next()));
				}
//			}

		}

//		Messagebox.show(
//				"Found " + snpCnt + " out of " + lines.length
//						+ ". Are you sure you want to proceed in making the list?",
//				"Confirmation", new Messagebox.Button[] { Messagebox.Button.YES, Messagebox.Button.NO },
//				Messagebox.QUESTION, event -> {
//					if (Messagebox.ON_YES.equals(event.getName())) {
//						makeList(sb, setChrSNP, setSNPDBPos, hasAllele, hasPvalue); // Call your list-making
//																					// function
//					}
//				});
		
		return setChrSNP;

	}

	public static void main(String[] args) {

	}

}

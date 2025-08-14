package org.irri.scripts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import org.irri.iric.ds.chado.domain.GenotypeRunPlatform;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.StockSample;
import org.irri.iric.ds.chado.domain.Variety;
import org.irri.iric.ds.chado.domain.model.Organism;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.admin.AsyncJob;
import org.irri.iric.portal.admin.AsyncJobImpl;
import org.irri.iric.portal.admin.JobsFacade;
import org.irri.iric.portal.genotype.GenotypeFacade;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.VariantStringData;
import org.irri.iric.portal.genotype.service.VariantAlignmentTableArraysImpl;
import org.irri.iric.portal.variety.VarietyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class DownloadListBox {

	private String contig;
	private Long lStart;
	private Long lStop;
	private Set dataset;

	@Autowired
	@Qualifier("GenotypeFacade")
	private GenotypeFacade genotype;

	@Autowired
	@Qualifier("VarietyFacade")
	private VarietyFacade varietyfacade;

	@Autowired
	@Qualifier("JobsFacade")
	private JobsFacade jobsfacade_orig;
	private Set snpset;
	private List<Integer> allStockIds;
	private Collection snppos;
	private Organism organism;

	private VariantStringData queryRawResult;

	private VariantAlignmentTableArraysImpl varianttable;
	private VariantStringData queryResult;
	private String outputFile;

	public DownloadListBox(String contig, Set dataset, Long lStart, Long lStop) {
		this.contig = contig;
		this.lStart = lStart;
		this.lStop = lStop;
		this.dataset = dataset;
	}

	public DownloadListBox(List<Integer> allStockIds, Collection snppos, String contig, Set dataset, Set snpset,
			Long start, Long stop, Organism org, String outputFile) {
		this.allStockIds = allStockIds;
		this.snppos = snppos;
		this.contig = contig;
		this.dataset = dataset;
		this.snpset = snpset;
		this.lStart = start;
		this.lStop = stop;
		this.organism = org;
		this.outputFile = outputFile;

	}

	public DownloadListBox(String contig, Set dataset, Set snpset) {
		this.contig = contig;
		this.dataset = dataset;
		this.snpset = snpset;

	}

	public void downloadBigListBox(String str, String contig) throws Exception {

		try {
			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

			int size = genotype.getFeatureLength(contig, AppContext.getDefaultOrganism());
//			int size = 1000;
			int incrementSize = 500000;
			int lStart = 0;
			int lStop = 1;

			while (lStart < size) {
				lStop = lStart + incrementSize;

				if (lStop > size)
					lStop = size;

				downloadList("snp3kvars-" + str + ":" + (lStart + 1) + "-" + lStop + ".csv", ",", "csv",
						new Long(lStart + 1), new Long(lStop));

//				callreport = callableDownloadBigListbox(
//						AppContext.getTempDir() + "snp3kvars-" + str+"----"+ lStart +".csv", ",", "csv", new Long(lStart + 1), new Long(lStop));

				System.out.println("snp3kvars-" + str + ":" + lStart + "-" + lStop + ".csv");

//				AsyncJobReport report = callreport.call();

				lStart = lStart + incrementSize;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		 executor.shutdown();
		// AsyncJobReport report=null;

	}

	public static String getBaseName(String filename) {
		int dot = filename.lastIndexOf('.');
		return (dot >= 0) ? filename.substring(0, dot) : filename;
	}

	public static String getExtension(String filename) {
		int dot = filename.lastIndexOf('.');
		return (dot >= 0) ? filename.substring(dot) : "";
	}

	public void downloadRegion(String filename, String chr) throws Exception {

		try {
			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

			downloadList(outputFile, ",", "csv", new Long(lStart + 1), new Long(lStop));

			System.out.println("snp3kvars-" + filename + ":" + lStart + "-" + lStop + ".csv");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void downloadList(final String filename, final String delimiter, final String format, Long lStart,
			Long lStop) {
		String msg = "";
		String jobid = "";
		String url = "";
		Future future = null;

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

		try {

			List listSNPs = new java.util.ArrayList();

			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

//			lStart = new Long(1);
//			lStop = new Long(500000);
////			lStop = new Long(genotype.getFeatureLength(contig, AppContext.getDefaultOrganism()));

			Set runs = new HashSet(getGenotyperun());

			GenotypeQueryParams params = new GenotypeQueryParams(allStockIds, contig, lStart, lStop, true, false,
					snpset, dataset, runs, false, snppos, null, null, true, false, organism);

			params.setIncludedSnps(false, false, false);

			queryRawResult = genotype.queryGenotype(params);

			varianttable = new VariantAlignmentTableArraysImpl();
			varianttable = (VariantAlignmentTableArraysImpl) genotype.fillGenotypeTable(varianttable, queryRawResult,
					params);

			queryResult = varianttable.getVariantStringData();

			listSNPs = queryRawResult.getListVariantsString();

			downloadBigListbox(varianttable, filename, ",", false, false);

		} catch (Exception ex) {
			ex.printStackTrace();
			// Messagebox.show("call():" + ex.getMessage());
			msg = ex.getMessage();

		}

	}

	public void downloadListBulk(final String delimiter) {
		String msg = "";
		String jobid = "";
		String url = "";
		Future future = null;

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

		try {

			List listSNPs = new java.util.ArrayList();

			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

//			lStart = new Long(1);
//			lStop = new Long(500000);
////			lStop = new Long(genotype.getFeatureLength(contig, AppContext.getDefaultOrganism()));

			Set runs = new HashSet(getGenotyperun());

			long max = 100000;
			long start = lStart;
			long end;

			String fname = getBaseName(outputFile);
			String ext = getExtension(outputFile);

			System.out.println("Start Bulkdownload");

			boolean iterList = true;
			while (iterList) {

				if (lStop > start + max)
					end = start + max;
				else {
					end = lStop;
					iterList = false;
				}
				GenotypeQueryParams params = new GenotypeQueryParams(allStockIds, contig, start, end, true, false,
						snpset, dataset, runs, false, snppos, null, null, true, false, organism);

				params.setIncludedSnps(false, false, false);

				try {
					queryRawResult = genotype.queryGenotype(params);

					varianttable = new VariantAlignmentTableArraysImpl();
					varianttable = (VariantAlignmentTableArraysImpl) genotype.fillGenotypeTable(varianttable,
							queryRawResult, params);

					queryResult = varianttable.getVariantStringData();

					listSNPs = queryRawResult.getListVariantsString();

					System.out.println("making new File");
					downloadBigListbox(varianttable, fname.trim() + ":" + start + "-" + end + "." + ext, ",", false,
							false);

					
				} catch (NullPointerException e) {
					// TODO: handle exception
				}
				
				start = end + 1;
			}

			System.out.println("end Bulkdownload");

		} catch (Exception ex) {
			ex.printStackTrace();
			// Messagebox.show("call():" + ex.getMessage());
			msg = ex.getMessage();

		}

	}

	private void downloadBigListbox(VariantAlignmentTableArraysImpl table, String filename, String delimiter,
			boolean isAppend, boolean isAppendFirst) {

		try {

			// Object2StringMultirefsMatrixModel matrixmodel =
			// (Object2StringMultirefsMatrixModel)biglistboxArray.getModel();
			// VariantAlignmentTableArraysImpl table =
			// (VariantAlignmentTableArraysImpl)matrixmodel.getData();

			StringBuffer buff = new StringBuffer();
			String refs[] = table.getReference();

			varietyfacade = (VarietyFacade) AppContext.checkBean(varietyfacade, "VarietyFacade");
			Map<BigDecimal, Object> mapVarid2Phenotype = null;
			String sPhenotype = null;
			String phenlabel = "";
			String phenfiller = "";
			String columnfillers = delimiter + delimiter + delimiter + delimiter + delimiter;
//			if (listboxPhenotype.getSelectedIndex() > 0) {
//				sPhenotype = listboxPhenotype.getSelectedItem().getLabel();
//				mapVarid2Phenotype = varietyfacade.getPhenotypeValues(sPhenotype, getDataset());
//				phenlabel = sPhenotype + delimiter;
//				columnfillers += delimiter;
//				phenfiller = delimiter;
//			}

			if (!isAppend || isAppendFirst) {

				String strmismatch = "MISMATCH";
//				if (this.listboxAlleleFilter.getSelectedIndex() > 0) {
//					strmismatch = "MATCH";
//				}

				String stririsgsor = "ASSAY ID";
				// if(this.listboxDataset.getSelectedItem().getValue().equals("hdra")) {
				// stririsgsor="GSOR ID";
				// }

				buff.append(organism.getName().toUpperCase() + " POSITIONS").append(delimiter).append(stririsgsor)
						.append(delimiter).append("ACCESSION").append(delimiter).append("SUBPOPULATION")
						.append(delimiter).append(strmismatch).append(delimiter).append(phenlabel);

				/*
				 * if(this.checkboxShowNPBPosition.isChecked())
				 * buff.append(this.listboxReference.getSelectedItem().getLabel().toUpperCase()
				 * + " POSITIONS").append(delimiter).append("IRIS ID").append(delimiter).append(
				 * "SUBPOPULATION").append(delimiter).append("MISMATCH").append(delimiter); else
				 * buff.append("VARIETY").append(delimiter).append("IRIS ID").append(delimiter).
				 * append("SUBPOPULATION").append(delimiter).append("MISMATCH").append(delimiter
				 * );
				 */

				String[] contigs = table.getContigs();
				// BigDecimal[] positions = table.getPosition();
				Position[] positions = table.getPosition();
				StringBuffer buffPos = new StringBuffer();

				// check if multiple contig

				boolean isMulticontig = false;
				String contig0 = positions[0].getContig();
				for (int i = 2; i < positions.length; i++) {
					if (!contig0.equals(positions[i].getContig())) {
						isMulticontig = true;
						break;
					}
				}

				for (int i = 0; i < positions.length; i++) {
					if (isMulticontig)
						buff.append(positions[i].getContig()).append("-");
					buff.append(positions[i].getPosition());
					if (i < positions.length - 1)
						buff.append(delimiter);
				}

				buff.append("\n" + organism.getName().toUpperCase() + " ALLELES").append(columnfillers); // .append(delimiter).append(delimiter).append(delimiter).append(delimiter).append(delimiter);

				/*
				 * if(this.checkboxShowNPBPosition.isChecked()) buff.append("\n" +
				 * listboxReference.getSelectedItem().getLabel().toUpperCase() +
				 * " ALLELES").append(delimiter).append(delimiter).append(delimiter).append(
				 * delimiter); else
				 * buff.append("\nREFERENCE").append(delimiter).append(delimiter).append(
				 * delimiter).append(delimiter);
				 */

				for (int i = 0; i < refs.length; i++) {

					String refnuc = refs[i];
					if (refnuc == null || refnuc.isEmpty()) {
						// tabledata.getIndelstringdata().getMapIndelIdx2Refnuc().get(colIndex-frozenCols);
						BigDecimal pos = table.getVariantStringData().getListPos().get(i).getPosition();
						if (table.getVariantStringData().getIndelstringdata() != null
								&& table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc() != null)
							refnuc = table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc().get(pos);
					}

					if (refnuc == null)
						buff.append("");
					else {
						refnuc = refnuc.substring(0, 1);
						buff.append(refnuc);
					}

					if (i < refs.length - 1)
						buff.append(delimiter);
				}
				buff.append("\n");

				Double refsmatch[] = table.getAllrefallelesmatch();

				// checkboxShowNPBPosition.isChecked(
				if (false) {
					buff.append("NIPPONBARE POSITION").append(columnfillers); // .append(delimiter).append(delimiter).append(delimiter).append(delimiter).append(delimiter);
					positions = table.getPositionNPB();
					for (int i = 0; i < positions.length; i++) {
						buff.append(positions[i]);
						if (i < positions.length - 1)
							buff.append(delimiter);
					}

					buff.append("\nNIPPONBARE ALLELES").append(delimiter).append(delimiter).append(delimiter)
							.append(delimiter); // .append(delimiter);
					// buff.append("REF " +
					// refnames[iref]).append(delimiter).append(delimiter).append(delimiter);
					if (refsmatch != null) {
						buff.append(refsmatch[0]);
					}
					buff.append(phenfiller);
					buff.append(delimiter);

					refs = table.getReferenceNPB();
					for (int i = 0; i < refs.length; i++) {

						String refnuc = refs[i];
						if (refnuc.isEmpty()) {
							// tabledata.getIndelstringdata().getMapIndelIdx2Refnuc().get(colIndex-frozenCols);
							BigDecimal pos = table.getVariantStringData().getListPos().get(i).getPosition();
							if (table.getVariantStringData().getIndelstringdata() != null && table
									.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc() != null)
								refnuc = table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc()
										.get(pos).substring(0, 1);
						}

						if (refnuc == null)
							buff.append("");
						else {
							refnuc = refnuc.substring(0, 1);
							buff.append(refnuc);
						}

						if (i < refs.length - 1)
							buff.append(delimiter);
					}
					buff.append("\n");

				}

				// this.checkboxShowAllRefAlleles.isChecked()
				if (false) {
					String allrefsalleles[][] = table.getAllrefalleles();
					String refnames[] = table.getAllrefallelesnames();
					for (int iref = 0; iref < refnames.length; iref++) {

						buff.append("REF " + refnames[iref]).append(delimiter).append(delimiter).append(delimiter)
								.append(delimiter);

						if (refsmatch != null) {
							buff.append(refsmatch[iref]);
						}
						buff.append(phenfiller);
						buff.append(delimiter);

						String irefs[] = allrefsalleles[iref];
						for (int i = 0; i < refs.length; i++) {

							String refnuc = irefs[i];
							/*
							 * if(refnuc.isEmpty()) {
							 * //tabledata.getIndelstringdata().getMapIndelIdx2Refnuc().get(colIndex-
							 * frozenCols); BigDecimal pos =
							 * table.getVariantStringData().getListPos().get(i).getPos();
							 * if(table.getVariantStringData().getIndelstringdata()!=null &&
							 * table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc()!=
							 * null) refnuc =
							 * table.getVariantStringData().getIndelstringdata().getMapIndelpos2Refnuc().get
							 * (pos); }
							 */
							if (refnuc == null)
								buff.append("-");
							else
								buff.append(refnuc);
							if (i < refs.length - 1)
								buff.append(delimiter);
						}
						buff.append("\n");
					}

				}

				// INIT DELETED
//					String annots[] = table.getSNPGenomicAnnotation(fillGenotypeQueryParams());
//					if (annots != null) {
//						buff.append(
//								"MSU7 EFFECTS (cds-Non-synonymous/cds-Synonymous/Cds/3'UTR/5'UTR/Exon/splice Acceptor/splice Donor/Gene-intron/Promoter)")
//								.append(columnfillers).append(delimiter).append(delimiter).append(delimiter)
//								.append(delimiter).append(delimiter);
//						for (int i = 0; i < annots.length; i++) {
//							buff.append(annots[i]);
//							if (i < annots.length - 1)
//								buff.append(delimiter);
//						}
//						buff.append("\n");
//					}

				// INIT DELETED
//				String queryallele[] = table.getQueryallele();
//				if (queryallele != null) {
//
//					if (this.listboxVarietyAlleleFilter.getSelectedIndex() > 0) {
//						buff.append("Query ");
//						buff.append((String) listboxVarietyAlleleFilter.getSelectedItem().getValue());
//					} else {
//						buff.append("Query alleles");
//					}
//					buff.append(delimiter).append(delimiter).append(delimiter).append(delimiter).append(delimiter);
//					for (int ia = 0; ia < queryallele.length; ia++) {
//						buff.append(queryallele[ia]);
//						if (ia + 1 < queryallele.length)
//							buff.append(delimiter);
//					}
//					buff.append("\n");
//				}

			}

			Object varalleles[][] = table.getVaralleles();
			AppContext.debug("mxn=" + varalleles.length + "x" + varalleles[0].length);
			AppContext.debug("positions = " + refs.length);
			AppContext.debug("varids = " + table.getVarname().length);

			Map<String, Map<BigDecimal, StockSample>> mapDs = varietyfacade.getMapId2Sample(dataset);

			if (!isAppend) {

				for (int i = 0; i < table.getVarid().length; i++) {
					String varname = table.getVarname()[i];

					// if(delimiter.equals(",") && varname.contains(","))
					// varname = "\"" + varname + "\"";
					Map<BigDecimal, StockSample> mapVarId2Var = mapDs.get(table.getDataset()[i]);

					Variety var = mapVarId2Var.get(BigDecimal.valueOf(table.getVarid()[i]));
					String phenvalue = "";
					if (mapVarid2Phenotype != null) {
						phenvalue = delimiter;
						Object phenval = mapVarid2Phenotype.get(var.getVarietyId());
						if (phenval != null) {
							if (phenval instanceof String)
								phenvalue = (String) phenval + delimiter;
							else {
								phenvalue = String.format("%.2f", (Number) phenval);
								phenvalue = phenvalue.replace(".00", "");
								phenvalue += delimiter;
							}
						}
					}

					buff.append("\"").append(varname).append("\"").append(delimiter)
							.append((var.getIrisId() != null ? var.getIrisId() : "")).append(delimiter)
							.append((var.getAccession() != null ? var.getAccession() : "")).append(delimiter)
							.append((var.getSubpopulation() != null ? var.getSubpopulation() : "")).append(delimiter)
							.append(table.getVarmismatch()[i]).append(delimiter).append(phenvalue);
					for (int j = 0; j < refs.length; j++) {
						Object allele = varalleles[i][j];
						if (allele == null)
							buff.append("");
						else
							buff.append(varalleles[i][j]);
						if (j < refs.length - 1)
							buff.append(delimiter);
					}
					buff.append("\n");
				}

				String filetype = "text/plain";
				if (delimiter.equals(","))
					filetype = "text/csv";

				File file = new File(filename);
				try {
					file.createNewFile();
					FileWriter myWriter = new FileWriter(file);
					myWriter.write(buff.toString());
					myWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				System.out.println("Writing to: " + file.getAbsolutePath());
				// Filedownload.save(file, filetype);
//				} catch (FileNotFoundException e) {
//					e.printStackTrace();
//				}

				// Filedownload.save(buff.toString(), filetype, filename);
				// AppContext.debug("File download complete! Saved to: "+filename);
//				org.zkoss.zk.ui.Session zksession = Sessions.getCurrent();
//				AppContext.debug("snpallvars download complete!" + filename + " Downloaded to:"
//						+ zksession.getRemoteHost() + "  " + zksession.getRemoteAddr());

			} else {
				BufferedWriter bw = null;
				if (isAppendFirst) {
					bw = new BufferedWriter(new FileWriter(filename));
					bw.append(buff);
					bw.flush();
				} else
					bw = new BufferedWriter(new FileWriter(filename, true));

				buff = new StringBuffer();
				for (int i = 0; i < table.getVarid().length; i++) {
					String varname = table.getVarname()[i];

					// if(delimiter.equals(",") && varname.contains(","))
					// varname = "\"" + varname + "\"";
					Map<BigDecimal, StockSample> mapVarId2Var = mapDs.get(table.getDataset()[i]);
					StockSample var = mapVarId2Var.get(BigDecimal.valueOf(table.getVarid()[i]));
					String phenvalue = "";
					if (mapVarid2Phenotype != null) {
						phenvalue = delimiter;
						Object phenval = mapVarid2Phenotype.get(var.getVarietyId());
						if (phenval != null) {
							if (phenval instanceof String)
								phenvalue = (String) phenval + delimiter;
							else {
								phenvalue = String.format("%.2f", (Number) phenval);
								phenvalue = phenvalue.replace(".00", "");
								phenvalue += delimiter;
							}
						}
					}

					buff.append("\"").append(varname).append("\"").append(delimiter)
							.append((var.getAssay() != null ? var.getAssay() : "")).append(delimiter)
							.append((var.getAccession() != null ? var.getAccession() : "")).append(delimiter)
							.append(var.getSubpopulation()).append(delimiter).append(table.getVarmismatch()[i])
							.append(delimiter).append(phenvalue);
					for (int j = 0; j < refs.length; j++) {
						Object allele = varalleles[i][j];
						if (allele == null)
							buff.append("");
						else
							buff.append(varalleles[i][j]);
						if (j < refs.length - 1)
							buff.append(delimiter);
					}
					buff.append("\n");
					bw.append(buff);
					bw.flush();
					buff = new StringBuffer();
				}
				bw.close();

			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	private void downloadBulk(final String filename, final String delimiter, final String format, Long lStart,
			Long lStop) {
		String msg = "";
		String jobid = "";
		String url = "";
		Future future = null;

		genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

		try {

			genotype = (GenotypeFacade) AppContext.checkBean(genotype, "GenotypeFacade");

//			lStart = new Long(1);
//			lStop = new Long(500000);
////			lStop = new Long(genotype.getFeatureLength(contig, AppContext.getDefaultOrganism()));

			Set runs = new HashSet(getGenotyperun());

			GenotypeQueryParams params = new GenotypeQueryParams(null, contig, lStart, lStop, true, false, snpset,
					dataset, runs, false, null, null, null, true, false, null);

			if (format.equals("plink"))
				params.setFilename(filename + ".plink");
			else if (format.equals("flapjack"))
				params.setFilename(filename + ".flapjack");
			else {
				params.setFilename(filename);
				params.setDelimiter(delimiter);
			}

			// Object req = Executions.getCurrent().getNativeRequest();
			// String reqstr="";
			// if(req !=null && req instanceof HttpServletRequest) {
			// HttpServletRequest servreq= (HttpServletRequest)req;
			// String forwardedfor= servreq.getHeader("x-forwarded-for");
			// if(forwardedfor!=null) reqstr=forwardedfor;
			// else reqstr= servreq.getRemoteAddr() + "-" + servreq.getRemoteHost();
			//
			// /*
			// String forwardedfor= servreq.getHeader("x-forwarded-for");
			// if(forwardedfor!=null) reqstr+="-" + forwardedfor;
			// */
			// }
			// String submitter=( (AppContext.isIRRILAN() ||
			// reqstr.contains(AppContext.getIRRIIp()))?reqstr+"-"+AppContext.createTempFilename():reqstr);

			params.setSubmitter("LOCAL_SCRIPT_SUBMITTER");

			/*
			 * params.setSubmitter( Sessions.getCurrent().getLocalAddr() +"-"+
			 * Sessions.getCurrent().getLocalName() + "-" +
			 * Sessions.getCurrent().getRemoteAddr() + "-" +
			 * Sessions.getCurrent().getRemoteHost() + "-" +
			 * Sessions.getCurrent().getServerName() + reqstr );
			 */

			// report = genotype.querydownloadGenotypeAsync(params);

			jobsfacade_orig = (JobsFacade) AppContext.checkBean(jobsfacade_orig, "JobsFacade");
			JobsFacade jobsfacade = jobsfacade_orig;

			jobsfacade.setLocalUser(true);

			if (params.getSubmitter() == null) {
				msg = "Submitter ID required for long jobs.";
			} else if (jobsfacade.checkSubmitter(params.getSubmitter())) {
				msg = "You have a running long job. Please try again when that job is done.";
			} else {
				AsyncJob job = new AsyncJobImpl(new File(params.getFilename()).getName(), params.toString(),
						params.getSubmitter());
				if (jobsfacade.addJob(job)) {
					Future futureReportjob = genotype.querydownloadGenotypeAsync(params);
					// AsyncJobReport rep=(AsyncJobReport)futureReportjob.get();
					// AppContext.debug( (rep==null? "rep=null":rep.getMessage() ));
					// rep.
					job.setFuture(futureReportjob);
					future = futureReportjob;
					msg = jobsfacade.JOBSTATUS_SUBMITTED;
					jobid = job.getJobId();
					url = job.getUrl();

					while (!futureReportjob.isDone())
						System.out.println("Still working on it");
				} else {
					msg = jobsfacade.JOBSTATUS_REFUSED;
				}

			}
			AppContext.debug("callableDownloadBigListbox.. submitted");
		} catch (Exception ex) {
			ex.printStackTrace();
			// Messagebox.show("call():" + ex.getMessage());
			msg = ex.getMessage();

		}

	}

	private List getGenotyperun() {
		List l = new ArrayList();
		Set intrunids = new HashSet<>();
		Iterator it = genotype.getGenotyperuns(dataset, snpset, "SNP").iterator();
		while (it.hasNext()) {
			GenotypeRunPlatform r = (GenotypeRunPlatform) it.next();
			if (intrunids.contains(r.getGenotypeRunId()) || intrunids.size() == 0)
				l.add(r);
		}
		return l;
	}

}

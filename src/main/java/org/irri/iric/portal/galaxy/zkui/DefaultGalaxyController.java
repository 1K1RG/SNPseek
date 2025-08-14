package org.irri.iric.portal.galaxy.zkui;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.config.IpAddressesConfig;
import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.service.VariantAlignmentTableArraysImpl;
import user.ui.module.GenotypeQueryController;
import org.irri.iric.portal.variety.VarietyFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.zk.ui.Executions;

public class DefaultGalaxyController implements GalaxyCustomController {

	private static String GALAXY_AWS_DEV="snpseek_dev";
	private static String GALAXY_AWS="snpseek";
	private static String GALAXY_POLLUX="pollux";
	private static String GALAXY_RICEDEV="rice_dev";
	private static String GALAXY_RICE="rice";
	//private static String galaxy_instance=GALAXY_AWS;
	
	@Autowired
	private static org.irri.iric.portal.config.IpAddressesConfig ipProp;

	
	@Override
	public List getGalaxyInstances() {
		List l=new ArrayList();
		l.add(GALAXY_AWS );
		l.add(GALAXY_AWS_DEV);
		l.add(GALAXY_RICEDEV);
		l.add(GALAXY_RICE);
		return l;
	}
	
	@Override
	public String getGalaxyHistoryViewLink(String instance,String historyid) {
		
		if(instance.equals(GALAXY_RICEDEV))
			return getGalaxyAddress(instance) + "/history/view/" + historyid;
		else if(instance.equals(GALAXY_POLLUX))
			return getGalaxyAddress(instance) + "/histories/view?id=" + historyid;
		else if(instance.equals(GALAXY_RICE))
			return getGalaxyAddress(instance) + "/history/view/" + historyid;
		else
			return  getGalaxyAddress(instance) + "/histories/view?id=" + historyid;
	}

	@Override
	public String getGalaxyAddress(String instance) {

		ipProp = (IpAddressesConfig) AppContext.checkBean(ipProp, "ipAddressesConfig");
		
		return "http://13.250.24.103:8080";
		
	}

	@Override
	public  String getGalaxyKey(String instance) {
		if(instance.equals(GALAXY_RICEDEV))
			return "a80ef55feed828cdbe6500b2ba4f8bf7";
		else if(instance.equals(GALAXY_POLLUX))
			return "0529d21031f8e190dc2ba26173627b92"; 
		else if(instance.equals(GALAXY_RICE))
			return  "053435438eeca0c4393ad8ebf66be404"; 
		else if (instance.equals(GALAXY_AWS_DEV))
			// return "dd7ecdf0096f104c0e3ac8fd7f8f8136";  // dev, with ts , admin
			return "13d14be50c14abdfd035731d1d2f10db"; // ts
		else
			return "6a3612b1923b952e7d749b51eb5e0175";  // user snpseek
	}
	
	/*
	@Override
	public void generateDataFiles() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set checkEmbedList(String embed) {
		// TODO Auto-generated method stub
		return  new HashSet();
	}

	@Override
	public Set getCommonClass() {
		// TODO Auto-generated method stub
		return   new HashSet();
	}

	@Override
	public Set getCommonExt() {
		// TODO Auto-generated method stub
		return   new HashSet();
	}
	*/
	
	
	@Autowired
	@Qualifier("VarietyFacade")
	private VarietyFacade varietyfacade;

	
	private HttpSession getSession() {
		return (HttpSession) Executions.getCurrent().getSession().getNativeSession();
	}
	

	@Override
	public void generateDataFiles() {
		// TODO Auto-generated method stub
		generatePlinkfiles();
		
	}

	@Override
	public Set checkEmbedList(String embed) {
		// TODO Auto-generated method stub
		
		if(embed!=null && embed.equals("genotype")) {

			Set setList=new HashSet();
			
			setList.add("snplist");
			setList.add("snpmatrix");
			setList.add("samplelist");
			setList.add("locuslist");
			
			Map mapParamvals=(Map)getSession().getValue( "param_vals");
			if(mapParamvals!=null && mapParamvals.get("sample2pheno")!=null) {
				setList.add("phenolist");
			}; 
			
			return setList;
		}
		
		return null;
	}

	private void generatePlinkfiles() {
	
		/*
		GenotypeQueryParams p= fillGenotypeQueryParams();
		haplofilename = "snp3kvars-" + queryFilename();
		Object2StringMultirefsMatrixModel matrixmodel = (Object2StringMultirefsMatrixModel) biglistboxArray.getModel();
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl) matrixmodel.getData();
			Map mapParamvals=new HashMap();
		mapParamvals.put("query",p);
		mapParamvals.put("table",table);
		mapParamvals.put("reference", this.listboxReference.getSelectedItem().getLabel() );
		getSession().putValue( "param_vals",mapParamvals);
		*/
		
		Map mapParamvals=(Map)getSession().getValue( "param_vals");
		
		GenotypeQueryParams p=(GenotypeQueryParams)mapParamvals.get("query"); 
		String filename = (String)mapParamvals.get("filename");
		VariantAlignmentTableArraysImpl table = (VariantAlignmentTableArraysImpl)mapParamvals.get("table");

		if(p==null || filename==null ||  table==null) {
			AppContext.debug("mapParamvals=" + mapParamvals);
			return;
		}
		
		GenotypeQueryController.generateBigListboxPlink((VarietyFacade)AppContext.checkBean(varietyfacade,"VarietyFacade"), p , p.getsChr(), p.getDataset(),table, 
				AppContext.getTempDir() + filename, (Map)mapParamvals.get( "sample2pheno"), (String)mapParamvals.get("phenoname"));

		if ( p.getsChr()!=null && ! p.getsChr().isEmpty() ) {
			try {
				AppContext.info("Writing on: " + AppContext.getTempDir() + filename + ".bed");
				BufferedWriter bw=new BufferedWriter(new FileWriter(AppContext.getTempDir() + filename + ".bed"));
				bw.write(  p.getsChr() + "\t" + (p.getlStart()-1) + "\t" + p.getlEnd());
				bw.close();
				AppContext.debug("bed file created");
				//table.getVarid()
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}
	

	@Override
	public Set getCommonClass() {
		
		Set<String> sClass=new HashSet<String>();
		sClass.add("Bam(BamNative)");
		sClass.add("BamNative(CompressedArchive)");
		sClass.add("BaseBcf(CompressedArchive)");
		sClass.add("BigBed(BigWig)");
		sClass.add("Data(object)");
		sClass.add("Genbank(data.Text)");
		sClass.add("Gff(Tabular, _RemoteCallMixin)");
		sClass.add("Image(data.Data)");
		sClass.add("Interval(Tabular)");
		sClass.add("Newick(Text)");
		sClass.add("Nexus(Text)");
		sClass.add("Obo(Text)");
		sClass.add("Phylip(Text)");
		sClass.add("Phyloxml(GenericXml)");
		sClass.add("ProBam(Bam)");
		sClass.add("QualityScore(data.Text)");
		sClass.add("Sam(Tabular)");
		sClass.add("Sbml(GenericXml)");
		sClass.add("Sequence(data.Text)");
		sClass.add("Sequences(sequence.Fasta)");
		sClass.add("Triples(data.Data)");
		return sClass;
	}	
	
	@Override
	public Set getCommonExt() {
		
		Set<String> sExt=new HashSet();
	sExt.add("ab1");
	sExt.add("bam");
	sExt.add("bcf");
	sExt.add("bcf_uncompressed");
	sExt.add("bed");
	sExt.add("bed12");
	sExt.add("bed6");
	sExt.add("bedgraph");
	sExt.add("bedstrict");
	sExt.add("bigbed");
	sExt.add("bigwig");
	sExt.add("blastdbd");
	sExt.add("blastdbn");
	sExt.add("blastdbp");
	sExt.add("blastxml");
	sExt.add("blib");
	sExt.add("csv");
	sExt.add("data");
	sExt.add("dbn");
	sExt.add("dcd");
	sExt.add("eps");
	sExt.add("excel.xls");
	sExt.add("fasta");
	sExt.add("fastg");
	sExt.add("fastq");
	sExt.add("fastqcssanger");
	sExt.add("fastqillumina");
	sExt.add("fastqsanger");
	sExt.add("fastqsolexa");
	sExt.add("featurexml");
	sExt.add("genbank");
	sExt.add("gff");
	sExt.add("gff3");
	sExt.add("gif");
	sExt.add("html");
	sExt.add("interval");
	sExt.add("ipynb");
	sExt.add("jpg");
	sExt.add("json");
	sExt.add("maf");
	sExt.add("mol");
	sExt.add("mol2");
	sExt.add("netcdf");
	sExt.add("newick");
	sExt.add("obo");
	sExt.add("owl");
	sExt.add("pbed");
	sExt.add("pbm");
	sExt.add("pcd");
	sExt.add("pcx");
	sExt.add("pdb");
	sExt.add("pdf");
	sExt.add("phar");
	sExt.add("phe");
	sExt.add("pheno");
	sExt.add("phylip");
	sExt.add("phyloxml");
	sExt.add("png");
	sExt.add("postgresql");
	sExt.add("psd");
	sExt.add("raw");
	sExt.add("rdata");
	sExt.add("rdf");
	sExt.add("sam");
	sExt.add("sbml");
	sExt.add("sequences");
	sExt.add("sqlite");
	sExt.add("svslide");
	sExt.add("tiff");
	sExt.add("tsv");
	sExt.add("txt");
	sExt.add("vcf");
	sExt.add("vcf_bgzip");
	sExt.add("wig");
	sExt.add("xlsx");
	sExt.add("xml");
	sExt.add("zip");
	
	return sExt;
	}

	
	
}

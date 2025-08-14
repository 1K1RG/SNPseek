package org.irri.iric.portal.genotype.zkui;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.Label;
import org.zkoss.zul.Vlayout;

public class PopupBand {

	static final List<String> ALL3K = new ArrayList<String>() {
		{
			add("3KAll: 32 million full 3K RG SNPs Dataset");
			add("\n\n3kRG full set (32mio) biallelic & multiallelic SNP");
			add("\nTotal SNPs: 32,064,217");
			add("\nSamples : 3024");
		}
	};

	static final List<String> BASE3K = new ArrayList<String>() {
		{
			add("3kbase: 18million base SNP dataset");
			add("\n\nA Base SNP set of ~18 million SNPs was created from the ~29 million biallelic SNPs subset from the 32M full SNP set");
			add("\nby removing SNPs with excess of heterozygous calls.");
		}
	};

	static final List<String> CORE3K = new ArrayList<String>() {
		{
			add("3K core: 404k CoreSNP dataset");
			add("\n\nThe Core SNP set was obtained from the filtered SNP set by applying two-step LD pruning procedure as follows:");
			add("\n 1) LD pruning with window size 10kb, step 1 SNP, R2 threshold 0.8");
			add("\n 2) LD pruning with window size 50 SNPs, step 1 SNP, R2 threshold 0.8");
		}
	};

	static final List<String> FILTERED3K = new ArrayList<String>() {
		{
			add("3k filtered: 4.8million filtered SNP dataset");
			add("\n\nThe filtered SNP set was obtained from the Base SNP set by applying the following filtering criteria:");
			add("\nalternative allele frequency at least 0.01");
			add("\nproportion of missing calls per SNP at most 0.2");
		}
	};

	static final List<String> HDRA = new ArrayList<String>() {
		{
			add("HDRA germplasm consists of 1,568 diverse rice lines genotyped using a high-density rice array (HDRA) comprised of 700,000 SNPs. ");
			add("\n\n(Source: Open access resources for genome-wide association mapping in rice. DOI: 10.1038/ncomms10532)");
		}
	};

	static final List<String> RICERP = new ArrayList<String>() {
		{
			add("Rice RP: The RICE_RP is comprised of 4591 combined samples from those genotyped on the HDRA (1568) and/or sequenced in the 3kRG (3024). ");
			add("\n\nComplete SNP calls were obtained through imputation across the unique genotypes. (Source: RICE-RP imputed dataset of 5.2M SNPs for 4591 samples from 4481 genotypes. ");
			add("\n(Source: RICE-RP imputed dataset of 5.2M SNPs for 4591 samples from 4481 genotypes.DOI: 10.1038/s41467-018-05538-1)");
		}
	};

	static final List<String> BAAP = new ArrayList<String>() {
		{
			add("The Bengal and Assam Aus Panel (BAAP) comprises 299 cultivars with 2 million SNPs after imputation relative to the 3KRG 4.8M filtered dataset. ");
			add("\n\nSource: Genome Wide Association Mapping of Grain and Straw Biomass Traits in the Rice Bengal and Assam Aus Panel (BAAP) Grown Under Alternate Wetting and Drying and Permanently Flooded Irrigation. ");
			add("\nDOI: 10.3389/fpls.2018.01223)");
		}
	};

	public static Vlayout getPopupDesc(String variantset) {

		Vlayout layout = new Vlayout();
		List<String> content = new ArrayList();

		switch (variantset) {
		case "3kall":
			content.addAll(ALL3K);
			break;
		case "3kbase":
			content.addAll(BASE3K);
			break;
		case "3kcore":
			content.addAll(CORE3K);
			break;
		case "3kfiltered":
			content.addAll(FILTERED3K);
			break;
//		case "baap":
//			content.addAll(BAAP);
//			break;
//		case "rice_rp":
//			content.addAll(RICERP);
//			break;
//		case "hdra":
//			content.addAll(HDRA);
//			break;
		default:
			content.add(variantset);
		}

		for (String line : content) {
			Label labl = new Label(line);
			labl.setParent(layout);
		}

		return layout;
	}

	public static Vlayout getVariantsetPopup(String elementAt) {

		Vlayout layout = new Vlayout();
		List<String> content = new ArrayList();

		switch (elementAt) {

		case "baap":
			content.addAll(BAAP);
			break;
		case "rice_rp":
			content.addAll(RICERP);
			break;
		case "hdra":
			content.addAll(HDRA);
			break;
		default:
			content.add(elementAt);
		}

		for (String line : content) {
			Label labl = new Label(line);
			labl.setParent(layout);
		}

		return layout;

	}
	
	public static String getDesc(String variantset) {

		Vlayout layout = new Vlayout();
		List<String> content = new ArrayList();

		switch (variantset) {
		case "3kall":
			content.addAll(ALL3K);
			break;
		case "3kbase":
			content.addAll(BASE3K);
			break;
		case "3kcore":
			content.addAll(CORE3K);
			break;
		case "3kfiltered":
			content.addAll(FILTERED3K);
			break;
//		case "baap":
//			content.addAll(BAAP);
//			break;
//		case "rice_rp":
//			content.addAll(RICERP);
//			break;
//		case "hdra":
//			content.addAll(HDRA);
//			break;
		default:
			content.add(variantset);
		}

		StringBuffer buff = new StringBuffer();
		for (String line : content) {
			buff.append(line);
		}

		return buff.toString();
	}


	public static Vlayout setPopup(String popMessage) {

		Vlayout layout = new Vlayout();
		layout.setWidth("300px");

		Label labl = new Label(popMessage);
		labl.setParent(layout);

		return layout;

	}

}

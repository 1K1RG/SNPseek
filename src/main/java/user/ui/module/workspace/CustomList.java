package user.ui.module.workspace;

import java.util.Set;

import org.irri.iric.ds.chado.domain.Variety;

public class CustomList {

	String listname;

	String dataset;

	Set<String> lst_dataset;
	
	Integer phenotype;

	String varietyList;
	
	String snpList;

	String LocusList;

	String chromosome;
	
	boolean snpAllele;
	
	boolean snpPvalue;
	
	boolean verifySnp;
	
	Set<Variety> varietySets;

	private boolean displayVarietyBox;

	public String getListname() {
		return listname;
	}

	public void setListname(String listname) {
		this.listname = listname;
	}

	public String getDataset() {
		return dataset;
	}

	public void setDataset(String dataset) {
		this.dataset = dataset;
	}

	public Integer getPhenotype() {
		return phenotype;
	}

	public void setPhenotype(Integer phenotype) {
		this.phenotype = phenotype;
	}

	public String getVarietyList() {
		return varietyList;
	}

	public void setVarietyList(String varietyList) {
		this.varietyList = varietyList;
	}

	public String getLocusList() {
		return LocusList;
	}

	public void setLocusList(String locusList) {
		LocusList = locusList;
	}

	public String getChromosome() {
		return chromosome;
	}

	public void setChromosome(String chromosome) {
		this.chromosome = chromosome;
	}

	public boolean isSnpAllele() {
		return snpAllele;
	}

	public void setSnpAllele(boolean snpAllele) {
		this.snpAllele = snpAllele;
	}

	public boolean isSnpPvalue() {
		return snpPvalue;
	}

	public void setSnpPvalue(boolean snpPvalue) {
		this.snpPvalue = snpPvalue;
	}

	public String getSnpList() {
		return snpList;
	}

	public void setSnpList(String snpList) {
		this.snpList = snpList;
	}

	public boolean isVerifySnp() {
		return verifySnp;
	}

	public void setVerifySnp(boolean verifySnp) {
		this.verifySnp = verifySnp;
	}

	public Set<String> getLst_dataset() {
		return lst_dataset;
	}

	public void setLst_dataset(Set<String> lst_dataset) {
		this.lst_dataset = lst_dataset;
	}

	public Set<Variety> getVarietySets() {
		return varietySets;
	}

	public void setVarietySets(Set<Variety> varietySets) {
		this.varietySets = varietySets;
	}

	public void setListboxVarietySetVisible(boolean displayListboxVarietySet) {
		this.displayVarietyBox = displayListboxVarietySet;
		
	}

	public boolean getListboxVarietySetVisible() {
		return displayVarietyBox;
		
	}

	
	
	
	
	

}

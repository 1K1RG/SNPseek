package org.irri.iric.portal;

import org.irri.iric.portal.genotype.GenotypeQueryParams;
import org.irri.iric.portal.genotype.VariantStringData;
import org.irri.iric.portal.genotype.VariantTable;
import org.irri.iric.portal.variety.VarietyFacade;

public class GenotypeSearchContent {

	private GenotypeQueryParams queryParams;
	
	private VarietyFacade varietyFacade;
	
	private VariantTable varianttable;

	public GenotypeQueryParams getQueryParams() {
		return queryParams;
	}

	public void setQueryParams(GenotypeQueryParams queryParams) {
		this.queryParams = queryParams;
	}

	public VarietyFacade getVarietyFacade() {
		return varietyFacade;
	}

	public void setVarietyFacade(VarietyFacade varietyFacade) {
		this.varietyFacade = varietyFacade;
	}

	public VariantTable getVarianttable() {
		return varianttable;
	}

	public void setVarianttable(VariantTable varianttable) {
		this.varianttable = varianttable;
	}

	 
	
	
}

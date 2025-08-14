package org.irri.iric.portal.hdf5.dao;


import org.irri.iric.portal.hdf5.H5Dataset;
import org.irri.iric.portal.hdf5.H5ReadStringmatrix;
import org.springframework.stereotype.Repository;

@Repository("H5IndelUniAllele1V2DAO")
public class IndelUni3024VarietiesAllele1v2DAO extends H5Dataset {

	public IndelUni3024VarietiesAllele1v2DAO() {
		super("INDELuni_1", new H5ReadStringmatrix(), null);
		
	}

}

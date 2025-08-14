package org.irri.iric.portal.hdf5;


import java.util.List;

public interface H5ReadMatrix {

	public OutputMatrix read(H5Dataset hfdata, InputParamsIdxs input) throws Exception;

	public List<OutputMatrix> read(H5Dataset hfdata, List inputs);

}

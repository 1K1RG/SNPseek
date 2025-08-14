package org.irri.iric.portal.hdf5;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.logging.Logger;

//import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.object.Dataset;

public class H5ReadStringTransMatrix implements H5ReadMatrix {

	private static int DIM_POSITION = 0;
	private static int DIM_VARIETY = 1;

	private static Logger log = Logger.getLogger(H5ReadStringTransMatrix.class.getName());

	@Override
	public List<OutputMatrix> read(H5Dataset hfdata, List inputs) {

		List outputs = new ArrayList();
		Iterator itParams = inputs.iterator();
		while (itParams.hasNext()) {
			try {
				Object param = itParams.next();
				// if(param instanceof InputParams)
				// outputs.add( read(hfdata, (InputParams)itParams.next()));
				// else if (param instanceof InputParamsIdxs )
				outputs.add(read(hfdata, (InputParamsIdxs) itParams.next()));

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return outputs;
	}

	@Override
	public OutputMatrix read(H5Dataset hfdata, InputParamsIdxs input) throws Exception {

		Dataset dataset = hfdata.getDataset();

		dataset.init();

		log.info("reading string trans hdf5 " + dataset.getFile());

		// start, stride and sizes will determined the selected subset
		long[] start = dataset.getStartDims();
		long[] stride = dataset.getStride();
		long[] sizes = dataset.getSelectedDims();

		// select the subset: set stride to (1, 1)
		stride[DIM_POSITION] = 1;
		stride[DIM_VARIETY] = 1;

		long n_dim_position = 1;
		long n_dim_variety = 1;

		// dataset.setConvertByteToString(true);

		Map<BigDecimal, List<String>> mapVar2Indellist = new LinkedHashMap();

		Set setVarsIds = new TreeSet();

		n_dim_variety = dataset.getMaxDims()[DIM_VARIETY];
		// int rows = (int)n_dim_variety;
		int cols = (int) n_dim_variety;

		long maxpositions = dataset.getMaxDims()[DIM_POSITION];
		log.info("varieties=" + n_dim_variety + ", positions=" + maxpositions);

		start[DIM_VARIETY] = 0;
		sizes[DIM_VARIETY] = n_dim_variety;

		if (input.listVaridx == null) {
			start[DIM_VARIETY] = 0;
			sizes[DIM_VARIETY] = n_dim_variety;
			log.info("pri:getting " + cols + " cols x ");

			for (int ivar = 1; ivar <= n_dim_variety; ivar++)
				setVarsIds.add(BigDecimal.valueOf(ivar));

		} else if (input.listVaridx != null) {

			throw new RuntimeException("Transposed HDF5 are not inplemented for varity lists");

		}
		;

		if (input.endPosidx > maxpositions - 1)
			input.endPosidx = (int) maxpositions - 1;

		Map<Integer, List> mapPosidxString = new TreeMap();
		int varstartidx = 0;

		if (input.startPosidx > -1 && input.endPosidx > -1) {

			start[DIM_POSITION] = input.startPosidx; // .listStartEndPosidx[iposrange][0];
			n_dim_position = input.endPosidx - input.startPosidx + 1; // input.listStartEndPosidx[iposrange][1] -
																		// input.listStartEndPosidx[iposrange][0] + 1 ;
			sizes[DIM_POSITION] = n_dim_position;

			Long mydata[][] = new Long[(int) n_dim_position][(int) n_dim_variety];
			dataset.read();

			Iterator<BigDecimal> itVarid = setVarsIds.iterator();
			while (itVarid.hasNext()) {
				BigDecimal varid = itVarid.next();

				List listIndels = new ArrayList();
				Iterator<Integer> itPos = mapPosidxString.keySet().iterator();
				while (itPos.hasNext()) {
					Integer pos = itPos.next();
					List strvars = mapPosidxString.get(pos);
					// if(setVarsIds.size()!=strvars.length) throw new
					// RuntimeException("setVarsIds.size()!=strvars.length " + setVarsIds.size()
					// +"!=" +strvars.length);
					listIndels.add(strvars.get(varid.intValue() - 1 - varstartidx));
				}
				mapVar2Indellist.put(varid, listIndels);
			}

		} else
			throw new RuntimeException("H5ReadStringTransMatrix run for range, allvars only");

		return new OutputMatrix(mapVar2Indellist);
	}

}

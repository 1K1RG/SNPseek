package org.irri.iric.portal.hdf5;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;

import ncsa.hdf.hdf5lib.H5;
import ncsa.hdf.hdf5lib.HDF5Constants;
//import ncsa.hdf.hdf5lib.HDF5Constants;
import ncsa.hdf.object.Dataset;
//import ncsa.hdf.object.Group;
//import ncsa.hdf.object.h5.H5File;

public class H5ReadStringmatrix implements H5ReadMatrix {

	private static int DIM_POSITION = 1;
	private static int DIM_VARIETY = 0;

	private static Logger log = Logger.getLogger(H5ReadStringmatrix.class.getName());

	@Override
	public OutputMatrix read(H5Dataset hfdata, InputParamsIdxs input) throws Exception {

		Dataset dataset = hfdata.getDataset();
		dataset.init();

		log.info("reading string hdf5 " + dataset.getFile());

		// String str_data[][] = (String[][])dataset.read();

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

		Map<BigDecimal, String[]> mapVar2Strarray = new LinkedHashMap();

		Set setVarsIds = null;

		n_dim_variety = dataset.getMaxDims()[DIM_VARIETY];

		// long n_dim_string=dataset.getMaxDims()[DIM_STRING];
		// start[DIM_STRING] = 0;
		// sizes[DIM_STRING] = n_dim_string;

		int rows = (int) n_dim_variety;

		if (input.listVaridx == null) {
			start[DIM_VARIETY] = 0;
			sizes[DIM_VARIETY] = n_dim_variety;
			log.info("pri:getting " + rows + " rows x ");

		} else if (input.listVaridx != null) {

			setVarsIds = new HashSet();
			for (int ivar = 0; ivar < input.listVaridx.length; ivar++)
				setVarsIds.add(input.listVaridx[ivar]);
			log.info("pri:getting " + setVarsIds.size() + " rows x ");
		} else
			throw new RuntimeException("No variety indexes");

		// if(input.startPosidx !='\0' && input.endPosidx!='\0') {
		if (input.startPosidx > -1 && input.endPosidx > -1) {
			// select the subset: starting at (4, 2)
			// start[DIM_POSITION] = input.startPosidx-1;
			start[DIM_POSITION] = input.startPosidx;

			log.info("reading hdf5 colindex: " + input.startPosidx + " to " + input.endPosidx);

			n_dim_position = input.endPosidx - input.startPosidx + 1;
			sizes[DIM_POSITION] = n_dim_position;

			int cols = (int) n_dim_position;

			// // read the data of the subset
			// //byte[] dataRead = (byte[]) dataset.read();
			// String[][] dataRead = (String[][]) dataset.read();
			//
			// log.info(cols + " cols matrix");
			//
			// // print out the data values
			//
			// for (int i = 0; i < rows ; i++) {
			// if(setVarsIds!=null && !setVarsIds.contains(i+1)) continue;
			// //String s = new String( java.util.Arrays.copyOfRange(dataRead, i * cols, i *
			// cols+ cols ) );
			// //log.info(s);
			// listVarString.put( BigDecimal.valueOf(i+1) , dataRead[i] );
			// }

			Map<BigDecimal, List<String[]>> mapVarid2Strbuff = new LinkedHashMap();

			sizes[DIM_VARIETY] = 1;

			if (input.listVaridx != null && input.listVaridx.length > 0) {

				for (int i = 0; i < rows; i++) {
					if (!setVarsIds.contains(i + 1))
						continue;

					start[DIM_VARIETY] = i;
					byte[] dataRead = dataset.readBytes();
					String[] stringArray = Dataset.byteToString(dataRead, dataset.getDatatype().getDatatypeSize());

					List listVarindels = mapVarid2Strbuff.get(BigDecimal.valueOf(i + 1));
					if (listVarindels == null) {
						listVarindels = new ArrayList();
						mapVarid2Strbuff.put(BigDecimal.valueOf(i + 1), listVarindels);
					}
					listVarindels.add(stringArray);

					/*
					 * String s = new String( java.util.Arrays.copyOfRange(dataRead, i * cols, i *
					 * cols+ cols ) ); //log.info(s); StringBuffer buff =
					 * mapVarid2Strbuff.get(BigDecimal.valueOf(i+1)); //listVarString.put(
					 * BigDecimal.valueOf(i+1) , s ); if(buff==null) { buff = new StringBuffer();
					 * mapVarid2Strbuff.put( BigDecimal.valueOf(i+1) , buff); } buff.append(s);
					 */
				}
			} else if (input.startendVaridx != null) {
				sizes[DIM_VARIETY] = input.startendVaridx[1] - input.startendVaridx[0] + 1;
				start[DIM_VARIETY] = input.startendVaridx[0] - 1;
				byte[] dataRead = dataset.readBytes();
				String[] stringArray = Dataset.byteToString(dataRead, dataset.getDatatype().getDatatypeSize());

				int istart = 0;
				for (int i = 0; i < rows; i++) {
					List listVarindels = mapVarid2Strbuff.get(BigDecimal.valueOf(i + input.startendVaridx[0]));
					if (listVarindels == null) {
						listVarindels = new ArrayList();
						mapVarid2Strbuff.put(BigDecimal.valueOf(i + input.startendVaridx[0]), listVarindels);
					}
					// listVarindels.add( Arrays.copyOfRange(stringArray, istart,istart+cols-1) );
					listVarindels.add(Arrays.copyOfRange(stringArray, istart, istart + cols));
					istart += cols;
				}
			} else {
				sizes[DIM_VARIETY] = n_dim_variety;
				start[DIM_VARIETY] = 0;
				byte[] dataRead = dataset.readBytes();
				String[] stringArray = Dataset.byteToString(dataRead, dataset.getDatatype().getDatatypeSize());

				int istart = 0;
				for (int i = 0; i < rows; i++) {
					List listVarindels = mapVarid2Strbuff.get(BigDecimal.valueOf(i + 1));
					if (listVarindels == null) {
						listVarindels = new ArrayList();
						mapVarid2Strbuff.put(BigDecimal.valueOf(i + 1), listVarindels);
					}
					listVarindels.add(Arrays.copyOfRange(stringArray, istart, istart + cols));
					istart += cols;

					/*
					 * listVarindels.add( Arrays.copyOfRange(stringArray, istart,istart+cols-1) );
					 * istart+=cols;
					 */
				}

			}
			BigDecimal varid = null;
			Iterator<BigDecimal> itVar = mapVarid2Strbuff.keySet().iterator();
			while (itVar.hasNext()) {
				varid = itVar.next();
				mapVar2Strarray.put(varid, convertStringarrayList2Array(mapVarid2Strbuff.get(varid)));
			}
			log.info(mapVar2Strarray.get(varid).length + " cols matrix");

		} else if (input.listStartEndPosidx != null) {

			// Map<BigDecimal,StringBuffer> mapVarid2Strbuff=new LinkedHashMap();
			Map<BigDecimal, List<String[]>> mapVarid2Strbuff = new LinkedHashMap();

			for (int iposrange = 0; iposrange < input.listStartEndPosidx.length; iposrange++) {

				// start[DIM_POSITION] = input.listStartEndPosidx[iposrange][0] -1;
				start[DIM_POSITION] = input.listStartEndPosidx[iposrange][0];
				n_dim_position = input.listStartEndPosidx[iposrange][1] - input.listStartEndPosidx[iposrange][0] + 1;
				sizes[DIM_POSITION] = n_dim_position;

				log.info("reading hdf5 colindex: " + input.listStartEndPosidx[iposrange][0] + " to "
						+ input.listStartEndPosidx[iposrange][1]);

				int cols = (int) n_dim_position;
				// read the data of the subset
				// byte[] dataRead = (byte[]) dataset.read();
				log.info("locus " + iposrange + ", " + cols + " cols matrix");

				// String [][] stringArray = get2dStringArray(dataset);

				// if(setVarsIds!=null && !setVarsIds.isEmpty()){
				if (input.listVaridx != null && input.listVaridx.length > 0) {
					sizes[DIM_VARIETY] = 1;
					for (int i = 0; i < rows; i++) {
						// if(setVarsIds!=null && !setVarsIds.contains(i+1)) continue;
						if (!setVarsIds.contains(i + 1))
							continue;

						start[DIM_VARIETY] = i;
						byte[] dataRead = dataset.readBytes();
						String[] stringArray = Dataset.byteToString(dataRead, dataset.getDatatype().getDatatypeSize());

						List listVarindels = mapVarid2Strbuff.get(BigDecimal.valueOf(i + 1));
						if (listVarindels == null) {
							listVarindels = new ArrayList();
							mapVarid2Strbuff.put(BigDecimal.valueOf(i + 1), listVarindels);
						}
						listVarindels.add(stringArray);

						/*
						 * String s = new String( java.util.Arrays.copyOfRange(dataRead, i * cols, i *
						 * cols+ cols ) ); //log.info(s); StringBuffer buff =
						 * mapVarid2Strbuff.get(BigDecimal.valueOf(i+1)); //listVarString.put(
						 * BigDecimal.valueOf(i+1) , s ); if(buff==null) { buff = new StringBuffer();
						 * mapVarid2Strbuff.put( BigDecimal.valueOf(i+1) , buff); } buff.append(s);
						 */
					}
				} else if (input.startendVaridx != null) {
					sizes[DIM_VARIETY] = input.startendVaridx[1] - input.startendVaridx[0] + 1;
					start[DIM_VARIETY] = input.startendVaridx[0] - 1;
					byte[] dataRead = dataset.readBytes();
					String[] stringArray = Dataset.byteToString(dataRead, dataset.getDatatype().getDatatypeSize());

					int istart = 0;
					for (int i = 0; i < rows; i++) {
						List listVarindels = mapVarid2Strbuff.get(BigDecimal.valueOf(i + input.startendVaridx[0]));
						if (listVarindels == null) {
							listVarindels = new ArrayList();
							mapVarid2Strbuff.put(BigDecimal.valueOf(i + input.startendVaridx[0]), listVarindels);
						}
						// listVarindels.add( Arrays.copyOfRange(stringArray, istart,istart+cols-1) );
						listVarindels.add(Arrays.copyOfRange(stringArray, istart, istart + cols));
						istart += cols;
					}
				} else {
					sizes[DIM_VARIETY] = n_dim_variety;
					start[DIM_VARIETY] = 0;
					byte[] dataRead = dataset.readBytes();
					String[] stringArray = Dataset.byteToString(dataRead, dataset.getDatatype().getDatatypeSize());

					int istart = 0;
					for (int i = 0; i < rows; i++) {
						List listVarindels = mapVarid2Strbuff.get(BigDecimal.valueOf(i + 1));
						if (listVarindels == null) {
							listVarindels = new ArrayList();
							mapVarid2Strbuff.put(BigDecimal.valueOf(i + 1), listVarindels);
						}
						// listVarindels.add( Arrays.copyOfRange(stringArray, istart,istart+cols-1) );
						listVarindels.add(Arrays.copyOfRange(stringArray, istart, istart + cols));
						istart += cols;
					}
				}

			}
			BigDecimal varid = null;
			Iterator<BigDecimal> itVar = mapVarid2Strbuff.keySet().iterator();
			while (itVar.hasNext()) {
				varid = itVar.next();
				mapVar2Strarray.put(varid, convertStringarrayList2Array(mapVarid2Strbuff.get(varid)));
			}
			log.info(mapVar2Strarray.get(varid).length + " total cols matrix");

		} else if (input.listPosidx != null) {

			// Map<BigDecimal,StringBuffer> mapVarStringBuff = new HashMap();
			Map<BigDecimal, List<String[]>> mapVarid2Strbuff = new HashMap();

			log.info(input.listPosidx.length + " cols matrix");

			int total_n_dim_position = 0;
			for (int ipos = 0; ipos < input.listPosidx.length; ipos++) {
				// start[DIM_POSITION] = input.listPosidx[ipos]-1;
				start[DIM_POSITION] = input.listPosidx[ipos];
				n_dim_position = 1;
				sizes[DIM_POSITION] = n_dim_position;

				log.info("reading hdf5 colindex: " + (input.listPosidx[ipos]));

				sizes[DIM_VARIETY] = 1;
				// print out the data values
				if (ipos == 0) {
					for (int i = 0; i < rows; i++) {
						if (setVarsIds != null && !setVarsIds.contains(i + 1))
							continue;
						start[DIM_VARIETY] = i;
						byte[] dataRead = dataset.readBytes();
						String[] stringArray = Dataset.byteToString(dataRead, dataset.getDatatype().getDatatypeSize());
						List listVarindels = new ArrayList();
						mapVarid2Strbuff.put(BigDecimal.valueOf(i + 1), listVarindels);
						listVarindels.add(stringArray);
					}

				} else {
					for (int i = 0; i < rows; i++) {
						if (setVarsIds != null && !setVarsIds.contains(i + 1))
							continue;
						start[DIM_VARIETY] = i;
						byte[] dataRead = dataset.readBytes();
						String[] stringArray = Dataset.byteToString(dataRead, dataset.getDatatype().getDatatypeSize());
						mapVarid2Strbuff.get(BigDecimal.valueOf(i + 1)).add(stringArray);
					}
				}
			}
			BigDecimal varid = null;
			Iterator<BigDecimal> itVar = mapVarid2Strbuff.keySet().iterator();
			while (itVar.hasNext()) {
				varid = itVar.next();
				mapVar2Strarray.put(varid, convertStringarrayList2Array(mapVarid2Strbuff.get(varid)));
			}
			log.info(mapVar2Strarray.get(varid).length + " total cols matrix");

		} else
			throw new RuntimeException("No SNP position indexes");

		dataset.clear();

		log.info(mapVar2Strarray.size() + " vars with indels");

		return new OutputMatrix(mapVar2Strarray);
	}

	private String[] convertStringarrayList2Array(List<String[]> listStrArray) {
		if (listStrArray.size() == 0)
			return new String[0];
		if (listStrArray.size() == 1)
			return listStrArray.get(0);

		Iterator<String[]> itStrArrayIterator = listStrArray.iterator();
		String concatArray[] = new String[0];
		while (itStrArrayIterator.hasNext()) {
			String[] nexrarr = itStrArrayIterator.next();
			concatArray = (String[]) ArrayUtils.addAll(concatArray, nexrarr);
		}
		return concatArray;
	}

	private static void readDataset(String FILENAME, String DATASETNAME) {
		int file_id = -1;
		int type_id = -1;
		int dataset_id = -1;
		String[] str_data = { "", "", "", "" };

		try {
			file_id = H5.H5Fopen(FILENAME, HDF5Constants.H5F_ACC_RDONLY, HDF5Constants.H5P_DEFAULT);
			;
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			dataset_id = H5.H5Dopen(file_id, DATASETNAME, HDF5Constants.H5P_DEFAULT);
			type_id = H5.H5Dget_type(dataset_id);

			// H5.

			// H5.H5Ss

			H5.H5DreadVL(dataset_id, type_id, HDF5Constants.H5S_ALL, HDF5Constants.H5S_ALL, HDF5Constants.H5P_DEFAULT,
					str_data);
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (int indx = 0; indx < str_data.length; indx++)
			log.info(DATASETNAME + " [" + indx + "]: " + str_data[indx]);

		try {
			H5.H5Tclose(type_id);
			H5.H5Dclose(dataset_id);
			H5.H5Fclose(file_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

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

	// from API

	/**
	 * Gets a 2d string array from the given hdf5 file and dataset.
	 *
	 * @param hdfFile the hdf5 file object
	 * @param path    the full path to the dataset
	 *
	 * @return the dataset's data
	 *
	 * @throws java.lang.Exception
	 */
	// public static String[][] get2dStringArray(H5Dataset hdfFile, String path)
	// throws Exception
	// public static String[][] get2dStringArray(Dataset dataset) throws Exception
	// {
	// String[][] stringArray = null;
	//
	// long[] dims = dataset.getDims();
	// long[] start = dataset.getStartDims();
	// long[] sizes = dataset.getSelectedDims();
	// long[] stride = dataset.getStride();
	//
	// stringArray = new String[(int) dims[0]][(int) dims[1]];
	//
	// for (int i = 0; i< dims[0]; i++)
	// {
	// start[0] = i;
	//
	// byte[] dataRead = dataset.readBytes();
	// stringArray[i] = Dataset.byteToString(dataRead,
	// dataset.getDatatype().getDatatypeSize());
	// }
	//
	//
	// return stringArray;
	// }
	//
	//

	// /**
	// * Gets a 2d string array from the given hdf5 file and dataset.
	// *
	// * @param hdfFile the hdf5 file object
	// * @param path the full path to the dataset
	// *
	// * @return the dataset's data
	// *
	// * @throws java.lang.Exception
	// */
	// public static String[][] get2dStringArray(FileFormat hdfFile, String path)
	// throws Exception
	// {
	// String[][] stringArray = null;
	// Dataset dataset = (Dataset) hdfFile.get(path);
	//
	// if (dataset != null)
	// {
	// dataset.init();
	//
	// long[] dims = dataset.getDims();
	// long[] start = dataset.getStartDims();
	// stringArray = new String[(int) dims[0]][(int) dims[1]];
	//
	// for (int i = 0; i< dims[0]; i++)
	// {
	// start[0] = i;
	//
	// byte[] dataRead = dataset.readBytes();
	// stringArray[i] = Dataset.byteToString(dataRead,
	// dataset.getDatatype().getDatatypeSize());
	// }
	//
	// dataset.clear();
	// }
	//
	// return stringArray;
	// }

}

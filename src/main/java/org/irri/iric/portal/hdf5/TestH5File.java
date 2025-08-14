package org.irri.iric.portal.hdf5;


import java.util.logging.Logger;

// include the common HDF object package
import ncsa.hdf.object.Dataset;
import ncsa.hdf.object.Group;
// include the HDF5 object package
//import ncsa.hdf.hdf5lib.*;    // include the Java HDF5 interface
import ncsa.hdf.object.h5.H5File;

/**
 * Retreve and print HDF5 objects from file hdf5_test.h5
 * 
 * @version 1.3.0 10/26/2001
 * @author Peter X. Cao
 *
 */
public class TestH5File {

	private static Logger log = Logger.getLogger(TestH5File.class.getName());

	public static void main(String[] argv) {
		// create an H5File object
		// H5File h5file = new H5File("hdf5_test.h5", HDF5Constants.H5F_ACC_RDONLY);
		H5File h5file = new H5File("E:/My Document/Transfer/3kcore_alleles/SNPUni"); // , HDF5Constants.H5F_ACC_RDONLY);

		try {
			// open file and retrieve the file structure
			h5file.open();
		} catch (Exception ex) {
			log.info(ex.toString());
		}

		Object rootobj = ((javax.swing.tree.DefaultMutableTreeNode) h5file.getRootNode()).getUserObject();
		log.info("instanceof root" + rootobj.getClass().getCanonicalName() + " " + rootobj.getClass().getName());
		// Group root = (Group) ((javax.swing.tree.DefaultMutableTreeNode)
		// h5file.getRootNode()).getUserObject();

		try {
			// retrieve the dataset "2D 32-bit integer 20x10"
			Group root = (Group) rootobj;

			Dataset dataset = (Dataset) root.getMemberList().get(0);
			log.info("instanceof dataset " + dataset.getClass().getCanonicalName());
			// Object readobj = dataset.read();

			// initialize the dataset: load dataset information such as datatype
			// and dataspace into memory.
			dataset.init();

			// start, stride and sizes will determined the selected subset
			long[] start = dataset.getStartDims();
			long[] stride = dataset.getStride();
			long[] sizes = dataset.getSelectedDims();

			// select the subset: starting at (4, 2)
			start[0] = 4;
			start[1] = 2;

			int rows = 5;
			int cols = 3;
			// select the subset: subset size (5, 3)
			sizes[0] = rows;
			sizes[1] = cols;

			// select the subset: set stride to (3, 2)
			stride[0] = 1;
			stride[1] = 1;

			// read the data of the subset
			byte[] dataRead = (byte[]) dataset.read();

			// print out the data values
			log.info("\n\nOriginal Data Values");
			for (int i = 0; i < rows; i++) {
				System.out.print("\n" + dataRead[i * cols]);
				for (int j = 1; j < cols; j++) {
					System.out.print(", " + dataRead[i * cols + j]);
				}
			}

			// char[] copyTo = java.util.Arrays.copyOfRange(copyFrom, 2, 9);

			/*
			 * log.info("\n\nSubset Data Values"); for (int i = 0; i < cols; i++) {
			 * System.out.print("\n" + new String( dataRead[i * rows] ) ; for (int j = 1; j
			 * < rows; j++) { System.out.print("," + Byte.toString(dataRead[i * rows + j]));
			 * } }
			 */

			/*
			 * for (int i = 0; i < cols; i++) { byte[] byte3=new byte[cols]; for(int j=0; i<
			 * rows ; i++) byte3[j]=dataRead[i * rows + j]; log.info( new String(
			 * byte3) ) ; }
			 */
			// System.out.print( new String( dataRead ));
			/*
			 * for (int i = 0; i < 5; i++) { System.out.print("\n" + new String( dataRead[i
			 * * 3]) ) ; for (int j = 1; j < 3; j++) { System.out.print("," +
			 * Byte.toString(dataRead[i * 3 + j])); } }
			 */

			// H5ScalarDS dataRead = (H5ScalarDS) dataset.read();
			// dataRead.

			// int rank = dataset.getRank();
			// dataset.getDatatype();
			// dataset.getData();
			log.info("rank " + dataset.getRank());
			log.info(
					"datatype " + dataset.getDatatype() + " " + dataset.getDatatype().getClass().getCanonicalName());
			log.info("getdata " + dataset.getData().getClass().getCanonicalName());

			// log.info("getdims " +
			// dataRead.getDims().getClass().getCanonicalName());

			/*
			 * // print out the data values log.info("\n\nOriginal Data Values");
			 * for (int i = 0; i < 20; i++) { System.out.print("\n" + dataRead[i * 10]); for
			 * (int j = 1; j < 10; j++) { System.out.print(", " + dataRead[i * 10 + j]); }
			 */

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		/*
		 * javax.swing.tree.MutableTreeNode root = (MutableTreeNode)
		 * h5file.getRootNode(); if (root != null) { printNode(root, "    ");
		 * 
		 * //data 8-bit integer, 3000 x 20310628 }
		 */

		try {
			h5file.close();
			log.info("hdf5 file closed.");
		} catch (Exception ex) {
		}
	}

	// print out the data object recusively
	private static void printNode(javax.swing.tree.TreeNode node, String indent) {

		log.info(indent + node);

		int n = node.getChildCount();
		for (int i = 0; i < n; i++) {
			printNode(node.getChildAt(i), indent + "    ");
		}
	}
}
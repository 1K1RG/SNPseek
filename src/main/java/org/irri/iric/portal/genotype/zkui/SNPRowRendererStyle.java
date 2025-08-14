package org.irri.iric.portal.genotype.zkui;

public interface SNPRowRendererStyle {

	public static String STYLE_HETERO = "font-weight:bold;color:yellow;align:center";

	public static String STYLE_A = "color:green;align:center";
	public static String STYLE_T = "color:red;align:center";
	public static String STYLE_G = "color:orange;align:center";
	public static String STYLE_C = "color:blue;align:center";
	// public static String STYLE_SYNONYMOUS =
	// "font-weight:bold;color:lightgray;align:center";
	public static String STYLE_SYNONYMOUS = "color:lightgray;text-align:center";
	public static String STYLE_NONSYNONYMOUS = "color:red;text-align:center;background-color:black;font-weight:bold";

	public static String STYLE_SPLICE_DONOR = "text-align:center;color:aqua;font-weight:bold";
	public static String STYLE_SPLICE_ACCEPTOR = "text-align:center;color:chartreuse;font-weight:bold";
	public static String STYLE_MISMATCH = "text-align:center;color:red";
	public static String STYLE_MATCH = "text-align:center;color:blue";

	public static short COLOR_MISMATCH = 0;
	public static short COLOR_NUCLEOTIDE = 1;

	public static String STYLE_INTERESTING = "font-weight:bold;color:red;font-size:13;text-align:center";
	public static String STYLE_BORING = "font-size:13;text-align:center";
	public static String STYLE_DIFFFROMREF = "font-weight:bold;color:red;align:center";
	public static String STYLE_HEADERNARROW = "font-weight:normal;color:black;background-color:lightgray;font-family:Arial Narrow;";
	public static String STYLE_HEADER = "font-weight:bold;color:black;background-color:lightgray;font-size:13;align:center";
	public static String STYLE_SAMEASREF = "";
}

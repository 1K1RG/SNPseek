package org.irri.iric.portal.hdf5;

import java.math.BigDecimal;
import java.util.List;

public class InputParams {
	public InputParams(List listPosidx) {
		this(null, null, listPosidx, null);
	}

	public InputParams(List listPosidx, List<BigDecimal> listVaridx) {
		this(null, null, listPosidx, listVaridx);
	}

	public InputParams(int startPosidx, int endPosidx) {
		this(BigDecimal.valueOf(startPosidx), BigDecimal.valueOf(endPosidx), null, null);
	}

	public InputParams(BigDecimal startPosidx, BigDecimal endPosidx) {
		this(startPosidx, endPosidx, null, null);
	}

	public InputParams(BigDecimal startPosidx, BigDecimal endPosidx, List<BigDecimal> listVaridx) {
		this(startPosidx, endPosidx, null, listVaridx);
	}

	public InputParams(BigDecimal startPosidx, BigDecimal endPosidx, List listPosidx, List<BigDecimal> listVaridx) {
		super();
		this.startPosidx = startPosidx;
		this.endPosidx = endPosidx;
		this.listPosidx = listPosidx;
		this.listVaridx = listVaridx;
	}

	BigDecimal startPosidx;
	BigDecimal endPosidx;
	List<BigDecimal> listPosidx;
	List<BigDecimal> listVaridx;

	/*
	 * public InputParams(List<BigDecimal> listPosidx) { this(null, null,
	 * listPosidx, null); }
	 * 
	 * public InputParams(List<BigDecimal> listPosidx, List<BigDecimal> listVaridx)
	 * { this(null, null, listPosidx, listVaridx); }
	 * 
	 * public InputParams(int startPosidx,int endPosidx) { this(
	 * BigDecimal.valueOf(startPosidx), BigDecimal.valueOf(endPosidx), null, null);
	 * }
	 * 
	 * 
	 * public InputParams(BigDecimal startPosidx, BigDecimal endPosidx) {
	 * this(startPosidx, endPosidx, null, null); }
	 * 
	 * public InputParams(BigDecimal startPosidx, BigDecimal endPosidx,
	 * List<BigDecimal> listVaridx) { this(startPosidx, endPosidx, null,
	 * listVaridx); }
	 * 
	 * public InputParams(BigDecimal startPosidx, BigDecimal endPosidx,
	 * List<BigDecimal> listPosidx, List<BigDecimal> listVaridx) { super();
	 * this.startPosidx = startPosidx; this.endPosidx = endPosidx; this.listPosidx =
	 * listPosidx; this.listVaridx = listVaridx; }
	 * 
	 * BigDecimal startPosidx; BigDecimal endPosidx; List<BigDecimal> listPosidx;
	 * List<BigDecimal> listVaridx;
	 */
}
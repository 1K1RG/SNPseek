package org.irri.iric.portal.hdf5;

public class InputParamsIdxs {

	public InputParamsIdxs(int listPosidx[]) {
		this(-1, -1, listPosidx, null, null);
	}

	public InputParamsIdxs(int listPosidx[], int listVaridx[]) {
		this(-1, -1, listPosidx, listVaridx, null);
	}

	public InputParamsIdxs(int listStartEndPosidx[][]) {
		this(-1, -1, null, null, listStartEndPosidx);
	}

	public InputParamsIdxs(int listStartEndPosidx[][], int listVaridx[]) {
		this(-1, -1, null, listVaridx, listStartEndPosidx);
	}

	public InputParamsIdxs(int startPosidx, int endPosidx) {
		this(startPosidx, endPosidx, null, null, null);
	}

	public InputParamsIdxs(int startPosidx, int endPosidx, int listVaridx[]) {
		this(startPosidx, endPosidx, null, listVaridx, null);
	}

	public InputParamsIdxs(int listPosidx[], int startVaridx, int endVaridx) {
		this(-1, -1, listPosidx, null, null, new int[] { startVaridx, endVaridx });
	}

	public InputParamsIdxs(int listStartEndPosidx[][], int startVaridx, int endVaridx) {
		this(-1, -1, null, null, listStartEndPosidx, new int[] { startVaridx, endVaridx });
	}

	public InputParamsIdxs(int startPosidx, int endPosidx, int listPosidx[], int listVaridx[],
			int listStartEndPosidx[][]) {
		this(startPosidx, endPosidx, listPosidx, listVaridx, listStartEndPosidx, null);
	}

	public InputParamsIdxs(int startPosidx, int endPosidx, int listPosidx[], int listVaridx[],
			int listStartEndPosidx[][], int startEndVaridx[]) {
		super();
		this.startPosidx = startPosidx;
		this.endPosidx = endPosidx;
		this.listPosidx = listPosidx;
		this.listVaridx = listVaridx;
		this.listStartEndPosidx = listStartEndPosidx;
		this.startendVaridx = startEndVaridx;

	}

	int startPosidx = -1;
	int endPosidx = -1;
	int listPosidx[];
	int listVaridx[];
	int listStartEndPosidx[][];
	int startendVaridx[];
}
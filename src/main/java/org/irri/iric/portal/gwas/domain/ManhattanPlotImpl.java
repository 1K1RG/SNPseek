package org.irri.iric.portal.gwas.domain;

import java.math.BigDecimal;

public class ManhattanPlotImpl implements ManhattanPlot {

	// private Long chr;
	private Byte chr;
	private BigDecimal minp;
	private BigDecimal position;

	public ManhattanPlotImpl(Byte chr, BigDecimal position, BigDecimal minp) {
		super();
		this.chr = chr;
		this.minp = minp;
		this.position = position;
	}

	@Override
	public String getContig() {
		
		if (chr > 9)
			return "chr" + chr;
		else
			return "chr0" + chr;
	}

	@Override
	public BigDecimal getPosition() {
		
		return position;
	}

	@Override
	public String getRefnuc() {
		return null;
	}

	@Override
	public Long getChr() {
		
		return Long.valueOf(chr);
	}

	@Override
	public int compareTo(Object o) {
		
		ManhattanPlot obj = (ManhattanPlot) o;
		int ret = Long.valueOf(chr).compareTo(obj.getChr());
		if (ret == 0)
			ret = this.position.compareTo(obj.getPosition());
		return ret;
	}

	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = (int) (prime * result + ((chr == null) ? 0 : chr.hashCode()));
		result = (int) (prime * result + ((position == null) ? 0 : position.hashCode()));
		return result;

	}

	@Override
	public boolean equals(Object obj) {
		
		return this.compareTo(obj) == 0;
	}

	@Override
	public String toString() {
		
		return chr + "-" + position + " " + minp;
	}

	@Override
	public BigDecimal getMarkerId() {
		return null;
	}

	@Override
	public Double getMinusLogPvalue() {
		
		return minp.doubleValue();
	}

	@Override
	public void setMinusLogPvalue(Double pval) {
		
		minp = BigDecimal.valueOf(pval);
	}

}

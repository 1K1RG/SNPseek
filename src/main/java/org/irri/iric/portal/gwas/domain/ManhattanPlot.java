package org.irri.iric.portal.gwas.domain;

import java.math.BigDecimal;

import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.PositionLogPvalue;

public interface ManhattanPlot extends Position, PositionLogPvalue {

	BigDecimal getMarkerId();
	// BigDecimal getMinusLogP();

}

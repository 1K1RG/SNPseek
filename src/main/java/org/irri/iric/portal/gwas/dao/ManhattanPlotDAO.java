package org.irri.iric.portal.gwas.dao;

import java.util.List;

import org.irri.iric.ds.chado.domain.GWASRun;
import org.irri.iric.portal.gwas.domain.ManhattanPlot;

public interface ManhattanPlotDAO {

	public List<ManhattanPlot> getMinusPValues(GWASRun run);

	public List<ManhattanPlot> getMinusPValues(GWASRun run, Double minlogP, String region);

}

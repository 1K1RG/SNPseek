package org.irri.iric.portal.hdf5.dao;


import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.irri.iric.ds.LocalDatasource;
import org.irri.iric.ds.chado.dao.IndelsAllvarsPosDAO;
import org.irri.iric.ds.chado.domain.impl.IndelsAllvarsPosAlleleImpl;
import org.springframework.stereotype.Repository;

@Repository("IndelsAllvarsTransposedNormalizedPosDAO")
public class IndelsAllvarsTransposedNormalizedPosDAO implements IndelsAllvarsPosDAO {

	@Override
	public long countSNPsInChromosome(String chr, Collection posset, Set type) {
		
		return -1;
	}

	@Override
	public long countSNPs(String chr, Integer startPos, Integer endPos, Set type) {
		
		return -1;
	}

	@Override
	public List getSNPs(Integer organismId, String chromosome, Integer startPos, Integer endPos, Set dataset) {
		

		if (!dataset.contains(TYPE_3KALLINDEL_V2))
			throw new RuntimeException(
					"IndelsAllvarsTransposedNormalizedPosDAO.getSNPs: !type.contains(TYPE_3KALLINDEL_V2)");
		List lisPos = new ArrayList();

		try {
			BufferedReader br = new BufferedReader(
					new FileReader(LocalDatasource.getFlatfilesDir() + "indelhdf5_678b.mapping.txt"));
			String line = "";
			boolean started = false;

			while ((line = br.readLine()) != null) {
				String cols[] = line.split("\t");
				// 1 chr01 1 1037 null ? 0 1 0
				Integer pos = Integer.valueOf(cols[3]);
				if (chromosome.equals(cols[1]) && pos.compareTo(startPos) >= 0 && pos.compareTo(endPos) <= 0) {

					lisPos.add(new IndelsAllvarsPosAlleleImpl(BigDecimal.valueOf(pos.longValue()),
							BigDecimal.valueOf(Long.valueOf(cols[6])), BigDecimal.valueOf(Long.valueOf(cols[0])),
							Integer.valueOf(7), null, cols[1], cols[5], Integer.valueOf(8)));

					started = true;
				} else {
					if (started)
						break;
				}
			}
			br.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return lisPos;
	}

	@Override
	public List getSNPs(Integer organismId, String chromosome, Integer startPos, Integer endPos, Set type, int firstRow, int maxRows) {
		return null;
	}

	@Override
	public List getSNPsInChromosome(Integer organismId, String chr, Collection posset, Set type) {
		return null;
	}

	@Override
	public Map getMapIndelId2Indels(String chromosome, Integer startPos, Integer endPos, Set variantset) {
		return null;
	}

	@Override
	public Map getMapIndelId2Indels(String chromosome, Collection poslist, Set variantset) {
		return null;
	}

}

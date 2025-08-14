package org.irri.iric.portal.gwas.dao;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.irri.iric.ds.chado.domain.GWASRun;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.gwas.domain.ManhattanPlot;
import org.irri.iric.portal.gwas.domain.ManhattanPlotImpl;
import org.springframework.stereotype.Repository;

@Repository("ManhattanPlotDAOFlatfileImpl")
public class ManhattanPlotDAOFlatfileImpl implements ManhattanPlotDAO {

	@Override
	public List<ManhattanPlot> getMinusPValues(GWASRun run) {
		
		return getMinusPValues(run, null, "all");
	}

	@Override
	public List<ManhattanPlot> getMinusPValues(GWASRun run, Double minlogP, String region) {
		

		List list = new ArrayList();
		try {
			FileInputStream fis = new FileInputStream(
					AppContext.getFlatfilesDir() + "gwas/emmax_" + run.getGwasRunId() + ".dat");
			DataInputStream dis = new DataInputStream(fis);

			// String fileName = "outputManhattan";
			//
			// String dir = "E:/";
			// File file = new File(dir+fileName + ".txt");
			//
			// System.out.println(file.getAbsolutePath());
			//
			//
			// int it = 1;
			// boolean createFile = true;
			// while (createFile) {
			// if (!file.exists()) {
			// file.createNewFile();
			// createFile = false;
			// }else{
			// String Fname = fileName + "" + it;
			// file = new File(dir+Fname + ".txt");
			// it++;
			// }
			//
			// }
			//
			// BufferedWriter output = new BufferedWriter(new FileWriter(file));
			// output.write("CHR\tPOS\tPVAL");
			boolean bAll = region.equals("all");
			Byte byteregion = null;
			if (!bAll)
				byteregion = Byte.valueOf(AppContext.guessChrFromString(region));

			Set sortPos = new TreeSet();
			// read till end of the stream
			if (minlogP != null) {
				while (dis.available() > 0) {
					/*
					 * dos.writeInt(type); // marker type/src dos.writeByte(chr); // chr
					 * dos.writeInt(pos); // pos dos.writeFloat(logp); // p-val
					 */
					if (bAll) {

						Integer type = dis.readInt();
						Byte chr = dis.readByte();
						Integer pos = dis.readInt();
						Float pval = dis.readFloat();
						if (pval < minlogP)
							break;
						sortPos.add(new ManhattanPlotImpl(chr, BigDecimal.valueOf(pos), BigDecimal.valueOf(pval)));
						// output.newLine();
						// output.write(chr + "\t" + BigDecimal.valueOf(pos) + "\t" +
						// BigDecimal.valueOf(pval));

					} else {
						Integer type = dis.readInt();
						Byte chr = dis.readByte();
						Integer pos = dis.readInt();
						Float pval = dis.readFloat();
						if (pval < minlogP)
							break;
						if (byteregion.equals(chr)) {
							sortPos.add(new ManhattanPlotImpl(chr, BigDecimal.valueOf(pos), BigDecimal.valueOf(pval)));
							// output.newLine();
							// output.write(chr + "\t" + BigDecimal.valueOf(pos) + "\t" +
							// BigDecimal.valueOf(pval));
						}

					}

				}
			} else {
				while (dis.available() > 0) {
					if (bAll) {
						Integer type = dis.readInt();
						Byte chr = dis.readByte();
						Integer pos = dis.readInt();
						Float pval = dis.readFloat();
						sortPos.add(new ManhattanPlotImpl(chr, BigDecimal.valueOf(pos), BigDecimal.valueOf(pval)));
						// output.newLine();
						// output.write(chr + "\t" + BigDecimal.valueOf(pos) + "\t" +
						// BigDecimal.valueOf(pval));
					} else {
						Integer type = dis.readInt();
						Byte chr = dis.readByte();
						Integer pos = dis.readInt();
						Float pval = dis.readFloat();
						if (byteregion.equals(chr)) {
							sortPos.add(new ManhattanPlotImpl(chr, BigDecimal.valueOf(pos), BigDecimal.valueOf(pval)));
							// output.newLine();
							// output.write(chr + "\t" + BigDecimal.valueOf(pos) + "\t" +
							// BigDecimal.valueOf(pval));
						}
					}
				}
			}

			list.addAll(sortPos);

			// output.close();
			dis.close();
			fis.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return list;
	}

}

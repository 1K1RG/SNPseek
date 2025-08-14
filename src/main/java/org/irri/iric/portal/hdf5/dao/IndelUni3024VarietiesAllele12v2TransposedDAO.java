package org.irri.iric.portal.hdf5.dao;


import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.irri.iric.ds.chado.dao.IndelsAllvarsDAO;
import org.irri.iric.ds.chado.domain.IndelsAllvars;
import org.irri.iric.ds.chado.domain.IndelsAllvarsPos;
import org.irri.iric.ds.chado.domain.impl.IndelsAllvarsStrImpl;
import org.irri.iric.ds.utils.DbUtils;
import org.irri.iric.ds.utils.HDF5Utils;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.hdf5.H5Dataset;
import org.irri.iric.portal.hdf5.H5ReadStringTransMatrix;
import org.springframework.stereotype.Repository;

@Repository("IndelAllvarsDAONormalizedHDF5")
public class IndelUni3024VarietiesAllele12v2TransposedDAO extends H5Dataset implements IndelsAllvarsDAO {
	
	private Logger log = Logger.getLogger(IndelUni3024VarietiesAllele12v2TransposedDAO.class.getName());

	public IndelUni3024VarietiesAllele12v2TransposedDAO() {
		super("INDEL_geno_NB_trans_678b.h5", new H5ReadStringTransMatrix(), null);
	}

	@Override
	public Set<IndelsAllvars> getAllIndelCalls() {
		return null;
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByChrPosBetween(Integer organismId, String chr, BigDecimal start, BigDecimal end,
			List listpos) {
	
		return null;
		// return readSNPString(chr,start.intValue(), end.intValue());
		// createIndelsAllvars(Map<BigDecimal,Long[]> allele1, Map<BigDecimal,String[]>
		// allele2, List listpos
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByVarChrPosBetween(Integer organismId, Collection varids, String chr, BigDecimal start,
			BigDecimal end, List listpos) {
		return null;
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByVarChrPosIn(Collection varList, String chr, int[][] posIdx,
			List listpos) {
		return null;
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByChrPosIn(String chr, int[][] posIdx, List listpos) {
		return null;
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByVarChrPosIn(Collection varList, String chr, Collection posList,
			List listpos) {
		return null;
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByChrPosIn(String chr, Collection posList, List listpos) {
		return null;
	}

	private Set<IndelsAllvars> createIndelsAllvars(Map<BigDecimal, String[]> allele1, Map<BigDecimal, String[]> allele2,
			List listpos) {
		Set setSnps = new TreeSet();
		try {
			if (allele1.size() != allele2.size())
				throw new RuntimeException("allele1.size()!=allele2.size(): " + allele1.size() + "  " + allele2.size());

		} catch (Exception ex) {
			log.info("allele1=" + allele1 + "  allele2=" + allele2);
			throw new RuntimeException(ex);
		}
		Iterator<BigDecimal> itVarid = allele1.keySet().iterator();

		while (itVarid.hasNext()) {
			BigDecimal varid = itVarid.next();
			String varallele1[] = allele1.get(varid);

			String varallele2[] = allele2.get(varid);

			if (varallele1.length != varallele2.length)
				throw new RuntimeException(
						"varallele1.length!=varallele2.length: " + varallele1.length + "  " + varallele2.length);

			// long prevdel1=0;
			Integer prevdel1 = null;
			long prevdel1pos = 0;
			Integer prevdel2 = null;
			long prevdel2pos = 0;
			for (int idx = 0; idx < varallele1.length; idx++) {
				// BigDecimal varId, BigDecimal pos, String refnuc, String varnuc, String
				// contig, Long chr
				IndelsAllvarsPos pos = (IndelsAllvarsPos) listpos.get(idx);

				String varnuc = "";
				String allele1idx = new String(varallele1[idx]);
				String allele2idx = new String(varallele2[idx]);

				if (!allele1idx.equals(allele2idx)) {
					// log.info( "var=" + varid + " i=" + idx + " pos=" +pos.getPos() + "
					// allele1=" + allele1idx + "; allele2=" + allele2idx );

					if (HDF5Utils.isIgnoreHeteroIndels()) {
						allele1idx = "?";
						allele2idx = "?";
					}
				}

				// int del1=0;
				// int del1pos=0;
				if (allele1idx.equals(".") || allele1idx.equals("0") || allele1idx.isEmpty()) {
					// same as reference
				} else {

					boolean isdel = false;
					try {
						Integer.valueOf(allele1idx);
						isdel = true;
					} catch (Exception ex) {
					}

					//

					if (prevdel1 != null)
						prevdel1 = prevdel1 - Long.valueOf(pos.getPosition().longValue() - prevdel1pos).intValue();

					if (isdel) {
						// if(prevdel1!=null && prevdel1>-1) throw new RuntimeException("var=" + varid +
						// " pos=" + pos.getPos() + ": past deletion at " + prevdel1pos + " extend here
						// but allele1=" +allele1idx);
						if (prevdel1 != null && prevdel1 > 0)
							throw new RuntimeException("var=" + varid + " pos=" + pos.getPosition()
									+ ": past deletion at " + prevdel1pos + " extend here but allele1=" + allele1idx);
						// if(prevdel1>0) throw new RuntimeException("var=" + varid + " pos=" + pos + ":
						// past deletion at " + prevdel1pos + " extend here but allele1=" +allele1idx);
						try {
							// del=Integer.valueOf(allele1idx.replace("del","").trim());
							prevdel1 = Integer.valueOf(allele1idx);
							prevdel1pos = pos.getPosition().longValue();
						} catch (Exception ex) {
						}
						;
					} else if (prevdel1 != null && prevdel1 > -1) {
						// extend deletion from prev pos

						if (!allele1idx.equals("?")) {
							// throw new RuntimeException("var=" + varid + "pos=" + pos + ": past deletion
							// at " + prevdel1pos + " extend here but allele1=" + allele1idx);
							log.info("var=" + varid + "pos=" + pos.getPosition() + " prevdel1=" + prevdel1
									+ "  : past deletion at " + prevdel1pos + " extend here but allele1=" + allele1idx);
						}
						if (prevdel1 > 0)
							allele1idx = "-" + prevdel1;
						else if (prevdel1 == 0) {
							// log.info("allele1 extdel0 at " + pos.getPos());
							allele1idx = "-0";
							prevdel1 = null;
							prevdel1pos = 0;
						} else {
							prevdel1 = null;
							prevdel1pos = 0;
						}
					}
					varnuc = allele1idx;
				}

				varnuc += "/";
				// int del2=0;
				// int del2pos=0;
				if (allele2idx.equals(".") || allele2idx.equals("0") || allele2idx.isEmpty()) {
				} else {
					boolean isdel = false;
					try {
						Integer.valueOf(allele2idx);
						isdel = true;
					} catch (Exception ex) {
					}

					if (prevdel2 != null) {

						// log.info("prevdel2pos=" + prevdel2pos);
						prevdel2 = prevdel2 - Long.valueOf(pos.getPosition().longValue() - prevdel2pos).intValue();
						// log.info("prevdel2pos=" + prevdel2pos);
					}

					if (isdel) {
						// if(prevdel2!=null && prevdel2>-1) throw new RuntimeException("var=" + varid +
						// " pos=" + pos.getPos() + ": past deletion at " + prevdel2pos + " extend here
						// but allele2=" + allele2idx);
						if (prevdel2 != null && prevdel2 > 0)
							throw new RuntimeException("var=" + varid + " pos=" + pos.getPosition()
									+ ": past deletion at " + prevdel2pos + " extend here but allele2=" + allele2idx);
						// log.info("Illegal?? pos=" + pos + ": past deletion at " + prevdel2pos
						// + " extend here but allele2=" + allele2idx);
						try {
							prevdel2 = Integer.valueOf(allele2idx);
							prevdel2pos = pos.getPosition().longValue();

						} catch (Exception ex) {
						}
						;
					}
					// else if(prevdel2!=null && prevdel2>-1) {
					else if (prevdel2 != null && prevdel2 > -1) {
						// extend deletion from prev pos

						if (!allele2idx.equals("?")) {
							// throw new RuntimeException("var=" + varid + "pos=" + pos + ": past deletion
							// at " + prevdel2pos + " extend here but allele2=" +allele2idx);
							log.info("var=" + varid + "pos=" + pos.getPosition() + "prevdel2=" + prevdel2
									+ " : past deletion at " + prevdel2pos + " extend here but allele2=" + allele2idx);
						}

						if (prevdel2 > 0)
							allele2idx = "-" + prevdel2;
						else if (prevdel2 == 0) {
							// log.info("allele2 extdel0 at " + pos.getPos());
							allele2idx = "-0";
							prevdel2 = null;
							prevdel2pos = 0;
						} else {
							prevdel2 = null;
							prevdel2pos = 0;
						}
					}

					varnuc += allele2idx;
				}

				if (prevdel1 != null && prevdel1 > 100)
					throw new RuntimeException("var=" + varid + "prevdel1>100 " + prevdel1 + "; prevdel1pos="
							+ prevdel1pos + "; curpos=" + pos);
				if (prevdel2 != null && prevdel2 > 100)
					throw new RuntimeException("var=" + varid + "prevdel2>100 " + prevdel2 + "; prevdel1pos="
							+ prevdel1pos + "; curpos=" + pos);

				// if(varnuc.equals("/")) continue;
				setSnps.add(new IndelsAllvarsStrImpl(varid, pos.getPosition(), pos.getRefnuc(), varnuc, pos.getContig(),
						Long.valueOf(DbUtils.guessChrFromString(pos.getContig())), allele1idx, allele2idx));

				// setSnps.add( new IndelsAllvarsStrImpl(varid, pos.getPos(), pos.getRefnuc(),
				// varnuc, pos.getContig() ,
				// Long.valueOf(AppContext.guessChrFromString(pos.getContig()))) );
				// setSnps.add( new IndelsAllvarsStrImpl(varid, pos.getPos(), pos.getRefnuc(),
				// varnuc, pos.getContig() ,
				// Long.valueOf(AppContext.guessChrFromString(pos.getContig()))) );
			}
		}
		return setSnps;
	}

}

package org.irri.iric.portal.hdf5.dao;


import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.irri.iric.ds.SnpSeekDatasource;
import org.irri.iric.ds.chado.dao.IndelsAllvarsDAO;
import org.irri.iric.ds.chado.domain.IndelsAllvars;
import org.irri.iric.ds.chado.domain.IndelsAllvarsPos;
import org.irri.iric.ds.chado.domain.impl.IndelsAllvarsStrImpl;
import org.irri.iric.ds.utils.DbUtils;
import org.irri.iric.ds.utils.HDF5Utils;
import org.irri.iric.portal.hdf5.H5Dataset;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Repository;

/**
 * Read indel matrix from indel1, indel2 matrices, and join them into an
 * IndelsAllvarsStr object
 * 
 * @author LMansueto
 *
 */
@Repository("IndelAllvarsDAOHDF5")
@Scope("prototype")
public class IndelAllvarsHDF5DAOImpl implements IndelsAllvarsDAO {

	private H5Dataset indelallele1;
	private H5Dataset indelallele2;
	private boolean bAutowire = true;
	private ClassPathXmlApplicationContext context;

	private static Logger log = Logger.getLogger(IndelAllvarsHDF5DAOImpl.class.getName());

	public IndelAllvarsHDF5DAOImpl(H5Dataset indelallele1, H5Dataset indelallele2) {
		super();
		this.indelallele1 = indelallele1;
		this.indelallele2 = indelallele2;
		bAutowire = false;
		this.context = SnpSeekDatasource.getContext();
	}

	private void checkBeans() {

		if (bAutowire) {
			indelallele1 = (H5Dataset) context.getBean("H5IndelUniAllele1V2DAO");
			indelallele2 = (H5Dataset) context.getBean("H5IndelUniAllele2V2DAO");
		}
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByChrPosBetween(Integer organismId, String chr, BigDecimal startIdx, BigDecimal endIdx,
			List listpos) {

		checkBeans();
		Map mapAllele1 = indelallele1.readSNPString(organismId,chr, startIdx.intValue(), endIdx.intValue());
		Map mapAllele2 = indelallele2.readSNPString(organismId,chr, startIdx.intValue(), endIdx.intValue());
		return createIndelsAllvars(mapAllele1, mapAllele2, listpos);
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByVarChrPosBetween(Integer organismId, Collection varids, String chr, BigDecimal startIdx,
			BigDecimal endIdx, List listpos) {

		checkBeans();
		Map mapAllele1 = indelallele1.readSNPString(organismId, (Set) varids, chr, startIdx.intValue(), endIdx.intValue());
		Map mapAllele2 = indelallele2.readSNPString(organismId, (Set) varids, chr, startIdx.intValue(), endIdx.intValue());
		return createIndelsAllvars(mapAllele1, mapAllele2, listpos);
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByVarChrPosIn(Collection varList, String chr, int posList[][],
			List listpos) {

		Map mapAllele1 = indelallele1.readSNPString((Set) varList, chr, posList);
		Map mapAllele2 = indelallele2.readSNPString((Set) varList, chr, posList);
		return createIndelsAllvars(mapAllele1, mapAllele2, listpos);
	}

	@Override
	public Set<IndelsAllvars> findIndelAllvarsByChrPosIn(String chr, int posList[][], List listpos) {

		Map mapAllele1 = indelallele1.readSNPString(chr, posList);
		Map mapAllele2 = indelallele2.readSNPString(chr, posList);
		return createIndelsAllvars(mapAllele1, mapAllele2, listpos);
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

	@Override
	public Set<IndelsAllvars> getAllIndelCalls() {
		checkBeans();
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

			if (varallele1.length != varallele2.length || listpos.size() != varallele1.length
					|| listpos.size() != varallele2.length) {
				log.info(this.getClass().getCanonicalName()
						+ ".createIndelsAllvars : varallele1.length!=varallele2.length " + varallele1.length + "  "
						+ varallele2.length + ", listpos.size=" + listpos.size() + "; varid=" + varid);
			}

			// long prevdel1=0;
			Integer prevdel1 = null;
			long prevdel1pos = 0;
			Integer prevdel2 = null;
			long prevdel2pos = 0;
			for (int idx = 0; idx < varallele1.length; idx++) {
				IndelsAllvarsPos pos = (IndelsAllvarsPos) listpos.get(idx);

				String varnuc = "";
				String allele1idx = new String(varallele1[idx]);
				String allele2idx = new String(varallele2[idx]);

				if (!allele1idx.equals(allele2idx)) {

					if (HDF5Utils.isIgnoreHeteroIndels()) {
						allele1idx = "?";
						allele2idx = "?";
					} else {

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
						if (prevdel1 != null && prevdel1 > 0) {
							// throw new RuntimeException("var=" + varid + " pos=" + pos.getPosition() + ":
							// past deletion at " + prevdel1pos + " extend here but allele1=" +allele1idx);
							log.info("var=" + varid + " pos=" + pos.getPosition() + ": past deletion at " + prevdel1pos
									+ " extend here but allele1=" + allele1idx);
						}
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
							log.info("var=" + varid + " pos=" + pos.getPosition() + " prevdel1=" + prevdel1
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

						// if(prevdel2!=null && prevdel2>0) throw new RuntimeException("var=" + varid +
						// " pos=" + pos.getPosition() + ": past deletion at " + prevdel2pos + " extend
						// here but allele2=" + allele2idx);
						if (prevdel2 != null && prevdel2 > 0)
							log.info("var=" + varid + " pos=" + pos.getPosition() + ": past deletion at " + prevdel2pos
									+ " extend here but allele2=" + allele2idx);

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
					log.info("var=" + varid + " prevdel1>100 " + prevdel1 + "; prevdel1pos=" + prevdel1pos + "; curpos="
							+ pos.getContig() + "-" + pos.getPosition());
				if (prevdel2 != null && prevdel2 > 100)
					log.info("var=" + varid + " prevdel2>100 " + prevdel2 + "; prevdel1pos=" + prevdel1pos + "; curpos="
							+ pos.getContig() + "-" + pos.getPosition());

				setSnps.add(new IndelsAllvarsStrImpl(varid, pos.getPosition(), pos.getRefnuc(), varnuc, pos.getContig(),
						Long.valueOf(DbUtils.guessChrFromString(pos.getContig())), allele1idx, allele2idx));

			}
		}
		return setSnps;
	}

}

// ****************** OLD CODE RETAINED **********************

/*
 * @Override public Set findIndelAllvarsByVarChrPosIn(Collection varList, String
 * chr, Collection posList) { if(chr.toLowerCase().equals("loci")) {
 * 
 * StringBuffer buffVar = new StringBuffer(); Iterator<String> itList =
 * AppContext.setSlicerIds((Set)varList).iterator(); buffVar.append(" ( ");
 * while(itList.hasNext()) { buffVar.append( " var in (" + itList.next() +
 * ") "); if(itList.hasNext()) buffVar.append(" or "); } buffVar.append(")");
 * 
 * String sql="select * from iric.V_INDEL_ALLVARS where " + buffVar.toString() +
 * " and ("; Iterator<Locus> itLoc = posList.iterator(); while(itLoc.hasNext())
 * { Locus loc = itLoc.next(); sql += "( partition_id=" + (
 * Integer.valueOf(AppContext.guessChrFromString(loc.getContig()))+2) +
 * " and pos between " + loc.getFmin() + " and " + loc.getFmax() + ") ";
 * if(itLoc.hasNext()) sql+= " or "; } sql +=")"; return new
 * LinkedHashSet<VIndelAllvars>(executeSQL(sql)); } else
 * if(chr.toLowerCase().equals("any")) {
 * log.info("snp list not supported for indel."); StringBuffer buffVar = new
 * StringBuffer(); Iterator<String> itList =
 * AppContext.setSlicerIds((Set)varList).iterator(); buffVar.append(" ( ");
 * while(itList.hasNext()) { buffVar.append( " var in (" + itList.next() +
 * ") "); if(itList.hasNext()) buffVar.append(" or "); } buffVar.append(")");
 * String sql="select * from iric.V_INDEL_ALLVARS where " + buffVar.toString() +
 * " and ("; Iterator<MultiReferencePosition> itLoc = posList.iterator();
 * while(itLoc.hasNext()) { MultiReferencePosition loc = itLoc.next(); sql +=
 * "( partition_id=" + (Integer.valueOf(
 * AppContext.guessChrFromString(loc.getContig()))+2) + " and pos=" +
 * loc.getPosition() + ") "; if(itLoc.hasNext()) sql+= " or "; } sql +=")";
 * return new LinkedHashSet<VIndelAllvars>(executeSQL(sql)); } else {
 * chr=AppContext.guessChrFromString(chr); Query query =
 * createNamedQuery("findVIndelAllvarsByVarChrPosIn", -1, -1,
 * BigDecimal.valueOf(Long.valueOf(chr)+2), varList, posList ); return new
 * LinkedHashSet<VIndelAllvars>(query.getResultList()); } }
 * 
 * @Override public Set findIndelAllvarsByChrPosIn(String chr, Collection
 * posList) {
 * 
 * if(chr.toLowerCase().equals("loci")) {
 * 
 * String sql="select * from iric.V_INDEL_ALLVARS where ("; Iterator<Locus>
 * itLoc = posList.iterator(); while(itLoc.hasNext()) { Locus loc =
 * itLoc.next(); sql += "( partition_id=" + (
 * Integer.valueOf(AppContext.guessChrFromString(loc.getContig()))+2) +
 * " and pos between " + loc.getFmin() + " and " + loc.getFmax() + ") ";
 * if(itLoc.hasNext()) sql+= " or "; } sql +=")"; return new
 * LinkedHashSet<VIndelAllvars>(executeSQL(sql)); } else
 * if(chr.toLowerCase().equals("any")) {
 * log.info("snp list not supported for indel."); String
 * sql="select * from iric.V_INDEL_ALLVARS where (";
 * Iterator<MultiReferencePosition> itLoc = posList.iterator();
 * while(itLoc.hasNext()) { MultiReferencePosition loc = itLoc.next(); sql +=
 * "( partition_id=" + (
 * Integer.valueOf(AppContext.guessChrFromString(loc.getContig()))+2) +
 * " and pos=" + loc.getPosition() + ") "; if(itLoc.hasNext()) sql+= " or "; }
 * sql +=")"; return new LinkedHashSet<VIndelAllvars>(executeSQL(sql)); } else {
 * chr=AppContext.guessChrFromString(chr); Query query =
 * createNamedQuery("findVIndelAllvarsByChrPosIn", -1, -1,
 * BigDecimal.valueOf(Long.valueOf(chr)+2), posList ); return new
 * LinkedHashSet<VIndelAllvars>(query.getResultList()); }
 * 
 * 
 * }
 * 
 */

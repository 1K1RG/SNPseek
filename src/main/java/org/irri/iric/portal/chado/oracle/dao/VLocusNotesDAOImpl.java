package org.irri.iric.portal.chado.oracle.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.Session;
import org.irri.iric.ds.chado.domain.Locus;
import org.irri.iric.ds.chado.domain.Position;
import org.irri.iric.ds.chado.domain.impl.MultiReferencePositionImpl;
import org.irri.iric.ds.chado.domain.model.VLocusMergedNotes;
import org.irri.iric.ds.chado.domain.model.VLocusNotes;
import org.irri.iric.ds.utils.TextSearchOptions;
import org.irri.iric.portal.AppContext;
import org.irri.iric.portal.chado.oracle.domain.VLocusNotesDAO;
import org.irri.iric.portal.dao.ListItemsDAO;
import org.irri.iric.portal.genomics.GenomicsFacade;
import org.skyway.spring.util.dao.AbstractJpaDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO to manage VLocusNotes entities.
 * 
 */
// @Repository("VLocusNotesDAO")
@Repository("LocusNotesDAO")
@Transactional
public class VLocusNotesDAOImpl extends AbstractJpaDao<VLocusNotes> implements VLocusNotesDAO {

	@Autowired
	@Qualifier("ListItems")
	private ListItemsDAO listitemsdao;

	/**
	 * Set of entity classes managed by this DAO. Typically a DAO manages a single
	 * entity.
	 *
	 */
	private final static Set<Class<?>> dataTypes = new HashSet<Class<?>>(
			Arrays.asList(new Class<?>[] { VLocusNotes.class }));

	/**
	 * EntityManager injected by Spring for persistence unit Production
	 *
	 */
	@PersistenceContext(unitName = "IRIC_Production")
	private EntityManager entityManager;

	/**
	 * Instantiates a new VLocusNotesDAOImpl
	 *
	 */
	public VLocusNotesDAOImpl() {
		super();
	}

	/**
	 * Get the entity manager that manages persistence unit
	 *
	 */
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * Returns the set of entity classes managed by this DAO.
	 *
	 */
	public Set<Class<?>> getTypes() {
		return dataTypes;
	}

	/**
	 * JPQL Query - findVLocusNotesByNameContaining
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByNameContaining(String name) throws DataAccessException {

		return findVLocusNotesByNameContaining(name, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByNameContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByNameContaining(String name, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByNameContaining", startResult, maxRows, name);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByStrand
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByStrand(java.math.BigDecimal strand) throws DataAccessException {

		return findVLocusNotesByStrand(strand, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByStrand
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByStrand(java.math.BigDecimal strand, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByStrand", startResult, maxRows, strand);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findAllVLocusNotess
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findAllVLocusNotess() throws DataAccessException {

		return findAllVLocusNotess(-1, -1);
	}

	/**
	 * JPQL Query - findAllVLocusNotess
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findAllVLocusNotess(int startResult, int maxRows) throws DataAccessException {
		Query query = createNamedQuery("findAllVLocusNotess", startResult, maxRows);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByCommonName
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByCommonName(String commonName) throws DataAccessException {

		return findVLocusNotesByCommonName(commonName, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByCommonName
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByCommonName(String commonName, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByCommonName", startResult, maxRows, commonName);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByFmax
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByFmax(java.math.BigDecimal fmax) throws DataAccessException {

		return findVLocusNotesByFmax(fmax, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByFmax
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByFmax(java.math.BigDecimal fmax, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByFmax", startResult, maxRows, fmax);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByName
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByName(String name) throws DataAccessException {

		return findVLocusNotesByName(name, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByName
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByName(String name, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByName", startResult, maxRows, name);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByCommonNameContaining
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByCommonNameContaining(String commonName) throws DataAccessException {

		return findVLocusNotesByCommonNameContaining(commonName, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByCommonNameContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByCommonNameContaining(String commonName, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByCommonNameContaining", startResult, maxRows, commonName);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByFmin
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByFmin(java.math.BigDecimal fmin) throws DataAccessException {

		return findVLocusNotesByFmin(fmin, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByFmin
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByFmin(java.math.BigDecimal fmin, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByFmin", startResult, maxRows, fmin);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByContigName
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByContigName(String contigName) throws DataAccessException {

		return findVLocusNotesByContigName(contigName, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByContigName
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByContigName(String contigName, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByContigName", startResult, maxRows, contigName);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByContigId
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByContigId(java.math.BigDecimal contigId) throws DataAccessException {

		return findVLocusNotesByContigId(contigId, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByContigId
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByContigId(java.math.BigDecimal contigId, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByContigId", startResult, maxRows, contigId);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByContigNameContaining
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByContigNameContaining(String contigName) throws DataAccessException {

		return findVLocusNotesByContigNameContaining(contigName, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByContigNameContaining
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByContigNameContaining(String contigName, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByContigNameContaining", startResult, maxRows, contigName);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByNotesContaining(String notes, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByNotesContaining", startResult, maxRows, notes);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByOrganismId
	 *
	 */
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByOrganismId(java.math.BigDecimal organismId) throws DataAccessException {

		return findVLocusNotesByOrganismId(organismId, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByOrganismId
	 *
	 */

	@SuppressWarnings("unchecked")
	@Transactional
	public Set<VLocusNotes> findVLocusNotesByOrganismId(java.math.BigDecimal organismId, int startResult, int maxRows)
			throws DataAccessException {
		Query query = createNamedQuery("findVLocusNotesByOrganismId", startResult, maxRows, organismId);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/**
	 * JPQL Query - findVLocusNotesByFeatureId
	 *
	 */
	@Transactional
	public VLocusNotes findVLocusNotesByFeatureId(BigDecimal featureId) throws DataAccessException {

		return findVLocusNotesByFeatureId(featureId, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByFeatureId
	 *
	 */

	@Transactional
	public VLocusNotes findVLocusNotesByFeatureId(BigDecimal featureId, int startResult, int maxRows)
			throws DataAccessException {
		try {
			Query query = createNamedQuery("findVLocusNotesByFeatureId", startResult, maxRows, featureId);
			return (VLocusNotes) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * JPQL Query - findVLocusNotesByPrimaryKey
	 *
	 */
	@Transactional
	public VLocusNotes findVLocusNotesByPrimaryKey(BigDecimal featureId) throws DataAccessException {

		return findVLocusNotesByPrimaryKey(featureId, -1, -1);
	}

	/**
	 * JPQL Query - findVLocusNotesByPrimaryKey
	 *
	 */

	@Transactional
	public VLocusNotes findVLocusNotesByPrimaryKey(BigDecimal featureId, int startResult, int maxRows)
			throws DataAccessException {
		try {
			Query query = createNamedQuery("findVLocusNotesByPrimaryKey", startResult, maxRows, featureId);
			return (VLocusNotes) query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}
	}

	/**
	 * Used to determine whether or not to merge the entity or persist the entity
	 * when calling Store
	 * 
	 * @see store
	 * 
	 *
	 */
	public boolean canBeMerged(VLocusNotes entity) {
		return true;
	}

	private BigDecimal bi2bd(Object bi) {

		// return BigDecimal.valueOf( ((Integer)bi).longValue());
		return BigDecimal.valueOf(Double.valueOf(bi.toString()));
	}

	private List<Locus> executeSQL(String sql) {

		List listResult = null;
		try {
			// listResult=
			// getSession().createSQLQuery(sql).addEntity(VLocusNotes.class).list();
			listResult = getSession().createSQLQuery(sql).addEntity(VLocusNotes.class).list();
			// listResult = executeNativeSQL(sql);
		} catch (Exception ex) {

			ex.printStackTrace();
			try {
				listResult = executeNativeSQL(sql);
			} catch (Exception ex2) {

				ex2.printStackTrace();
				throw new RuntimeException(ex2);
			}
		}

		return listResult;
	}

	private List<Locus> executeNativeSQL(String sql) {
		// System.out.println("executing :" + sql);
		// log.info("executing :" + sql);

		List listResult = null;
		try {
			// listResult=
			// getSession().createSQLQuery(sql).addEntity(VLocusNotes.class).list();
			// listResult=
			// getSession().createSQLQuery(sql).addEntity(VLocusNotes.class).list();
			listResult = new ArrayList();
			List<Object[]> li = entityManager.createNativeQuery(sql).getResultList();
			Iterator<Object[]> itil = li.iterator();
			while (itil.hasNext()) {
				Object[] o = itil.next();
				VLocusNotes l = new VLocusNotes();

				// l.setFeatureId( (BigDecimal)o[0]);
				l.setFeatureId(bi2bd(o[0]));
				l.setName((String) o[1]);
				l.setFmin(bi2bd(o[2]));
				l.setFmax(bi2bd(o[3]));
				// l.setStrand( ((BigInteger)o[4]).intValue() );
				l.setStrand(Integer.valueOf(o[4].toString()));
				l.setContigId(bi2bd(o[5]));
				l.setContigName((String) o[6]);
				l.setNotes((String) o[7]);
				l.setOrganismId(bi2bd(o[8]));
				l.setCommonName((String) o[9]);
				l.setFeatureType((String) o[10]);
				listResult.add(l);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return listResult;
	}

	private List<Locus> executeSQLMerged(String sql) {
		// System.out.println("executing :" + sql);
		// log.info("executing :" + sql);
		List listResult = null;
		try {
			listResult = getSession().createSQLQuery(sql).addEntity(VLocusMergedNotes.class).list();
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException(ex);
		}
		return listResult;
	}

	private Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	@Override
	public List<Locus> getLocusByName(String name) {

		/*
		 * String sql =
		 * "select * from (select distinct f.feature_id, f.name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(f3.value,1000,1) notes, f.organism_id, o.common_name"
		 * + " from " + AppContext.getDefaultSchema() + ".featureloc fl, " +
		 * AppContext.getDefaultSchema() + ".feature fsrc, " +
		 * AppContext.getDefaultSchema() + ".organism o , " +
		 * AppContext.getDefaultSchema() + ".cvterm cvtype,  " +
		 * AppContext.getDefaultSchema() + ".feature f" +
		 * " left join ( select f2.feature_id, fp.value from " +
		 * AppContext.getDefaultSchema() + ".featureprop fp, " +
		 * AppContext.getDefaultSchema() + ".cvterm cv, " +
		 * AppContext.getDefaultSchema() + ".feature f2" +
		 * " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
		 * + " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id " +
		 * " and lower(f.name)='" + name.toLowerCase() + "'" +
		 * " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id " +
		 * " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id)" +
		 * " order by contig_name, fmin, fmax";
		 */

		/*
		 * String sql =
		 * "select t.feature_id, t.name, t.fmin, t.fmax, t.strand, t.contig_id, t.contig_name, t.notes, t.organism_id, t.common_name from (select distinct f.feature_id, f.name , fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(f3.value,1000,1) notes, f.organism_id, o.common_name"
		 * + " from " + AppContext.getDefaultSchema() + ".featureloc fl, " +
		 * AppContext.getDefaultSchema() + ".feature fsrc, " +
		 * AppContext.getDefaultSchema() + ".organism o , " +
		 * AppContext.getDefaultSchema() + ".cvterm cvtype,  " +
		 * AppContext.getDefaultSchema() + ".feature f" +
		 * " left join ( select f2.feature_id, fp.value from " +
		 * AppContext.getDefaultSchema() + ".featureprop fp, " +
		 * AppContext.getDefaultSchema() + ".cvterm cv, " +
		 * AppContext.getDefaultSchema() + ".feature f2" +
		 * " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
		 * + " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id " +
		 * " and upper(f.name)= '" + name.toUpperCase() + "' " +
		 * " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id " +
		 * " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id) t " +
		 * " order by contig_name, fmin, fmax";
		 * 
		 * return executeSQL(sql);
		 */

		/*
		 * List list=new ArrayList(); list.addAll(findVLocusNotesByName(name)); return
		 * list;
		 */

		/*
		 * String sql =
		 * "select f.feature_id, f.uniquename name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(fp.value,1000,1) notes, f.organism_id, o.common_name from "
		 * + AppContext.getDefaultSchema() + ".feature f, " +
		 * AppContext.getDefaultSchema() + ".featureloc fl, " +
		 * AppContext.getDefaultSchema() + ".featureprop fp, " +
		 * AppContext.getDefaultSchema() + ".cvterm cv , " +
		 * AppContext.getDefaultSchema() + ".feature fsrc, " +
		 * AppContext.getDefaultSchema() + ".organism o , " +
		 * AppContext.getDefaultSchema() + ".cvterm cvtype" +
		 * " where fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id and f.feature_id=fl.feature_id "
		 * + " and lower(f.name)='" + name.toLowerCase() + "'" +
		 * " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id " +
		 * " and cv.name='Note' and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
		 * + " order by contig_name, fmin, fmax";
		 */

		// List listResult = executeSQL(sql);
		// return (Locus)listResult.get(0);

		Query query = createNamedQuery("findVLocusNotesByName", -1, -1, name);
		return query.getResultList();

	}

	@Override
	public Collection getLocusByName(Collection<String> name) {

		/*
		 * StringBuffer buffname=new StringBuffer(); Iterator<String>
		 * itName=name.iterator(); while(itName.hasNext()) {
		 * buffname.append("'").append(itName.next().toUpperCase()).append("'");
		 * if(itName.hasNext()) buffname.append(","); }
		 * 
		 * String sql =
		 * "select t.feature_id, t.name, t.fmin, t.fmax, t.strand, t.contig_id, t.contig_name, t.notes, t.organism_id, t.common_name from (select distinct f.feature_id, f.name , fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(f3.value,1000,1) notes, f.organism_id, o.common_name"
		 * + " from " + AppContext.getDefaultSchema() + ".featureloc fl, " +
		 * AppContext.getDefaultSchema() + ".feature fsrc, " +
		 * AppContext.getDefaultSchema() + ".organism o , " +
		 * AppContext.getDefaultSchema() + ".cvterm cvtype,  " +
		 * AppContext.getDefaultSchema() + ".feature f" +
		 * " left join ( select f2.feature_id, fp.value from " +
		 * AppContext.getDefaultSchema() + ".featureprop fp, " +
		 * AppContext.getDefaultSchema() + ".cvterm cv, " +
		 * AppContext.getDefaultSchema() + ".feature f2" +
		 * " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
		 * + " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id " +
		 * " and upper(f.name) in (" + buffname + ") " +
		 * " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id " +
		 * " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id) t " +
		 * " order by contig_name, fmin, fmax"; return executeSQL(sql);
		 */

		Query query = createNamedQuery("findVLocusNotesByNameIn", -1, -1, name);
		return new LinkedHashSet<VLocusNotes>(query.getResultList());
	}

	/*
	 * @Override public List<Locus>
	 * getLocusByDescriptionAndSynonym(TextSearchOptions description, String
	 * organism, String genemodel) {
	 * 
	 * List listresult = new ArrayList(); String locusmapping=""; String
	 * locuspref=""; if(genemodel.equals( GenomicsFacade.GENEMODEL_ALL) ||
	 * !organism.equals(Organism.REFERENCE_NIPPONBARE) ) { return
	 * getLocusByDescription( description, organism); } else if(genemodel.equals(
	 * GenomicsFacade.GENEMODEL_IRIC)) { locuspref =
	 * " where fdesc.name like 'OsNippo%'"; locusmapping ="  left join " +
	 * AppContext.getDefaultSchema() + ".locus_mapping lm on lm.name=fdesc.name "; }
	 * else if(genemodel.equals( GenomicsFacade.GENEMODEL_MSU7)) { locuspref =
	 * " where fdesc.name like 'LOC_%'"; locusmapping ="  left join " +
	 * AppContext.getDefaultSchema() +
	 * ".locus_mapping lm on catsearch(lm.msu7,fdesc.name,null)>0 " ; } else
	 * if(genemodel.equals( GenomicsFacade.GENEMODEL_RAP)) { locuspref =
	 * " where (fdesc.name like 'Os0%' or fdesc.name like 'Os1%') "; locusmapping
	 * ="  left join " + AppContext.getDefaultSchema() +
	 * ".locus_mapping lm on catsearch(lm.rap_representative,fdesc.name,null)>0 " ;
	 * } String sql =
	 * "select fdesc.*,  lm.name iric, lm.msu7, lm.rap_representative rap_rep, lm.rap_predicted rap_pred, lm.fgenesh from ("
	 * 
	 * +
	 * " select distinct feature_id, name, fmin, fmax, strand, contig_id, contig_name, lower(notes) notes, organism_id, common_name from ("
	 * 
	 * +
	 * "select f.feature_id, f.name name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(fp.value,1000,1) notes, f.organism_id, o.common_name from "
	 * + AppContext.getDefaultSchema() + ".feature f, " +
	 * AppContext.getDefaultSchema() + ".featureloc fl, " +
	 * AppContext.getDefaultSchema() + ".featureprop fp, " +
	 * AppContext.getDefaultSchema() + ".cvterm cv , " +
	 * AppContext.getDefaultSchema() + ".feature fsrc, " +
	 * AppContext.getDefaultSchema() + ".organism o , " +
	 * AppContext.getDefaultSchema() + ".cvterm cvtype" +
	 * " where fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id and f.feature_id=fl.feature_id and "
	 * + " contains(fp.value,'" + description + "',1)>0 " // +
	 * " lower(f.name) like '%" + description.toLowerCase() + "%' " +
	 * " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id " +
	 * " and cv.name='Note' and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
	 * + " and  o.common_name='" + organism + "'"
	 * 
	 * 
	 * +
	 * " union select f.feature_id, f.name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || s.name || ') ' || dbms_lob.substr(fp.value,1000,1)  notes, f.organism_id, o.common_name"
	 * + " from " + AppContext.getDefaultSchema() + ".feature f, " +
	 * AppContext.getDefaultSchema() + ".featureloc fl,   " +
	 * AppContext.getDefaultSchema() + ".feature fsrc, " +
	 * AppContext.getDefaultSchema() + ".organism o, " +
	 * AppContext.getDefaultSchema() + ".synonym_ s, " +
	 * AppContext.getDefaultSchema() + ".feature_synonym fsyn,   " +
	 * AppContext.getDefaultSchema() + ".featureprop fp, " +
	 * AppContext.getDefaultSchema() + ".cvterm cv,  " +
	 * AppContext.getDefaultSchema() + ".cvterm cvtype" +
	 * " where f.feature_id=fsyn.feature_id and fsyn.synonym_id=s.synonym_id" +
	 * " and f.feature_id=fl.feature_id and" +
	 * " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id" // +
	 * " and contains(s.name,'" + description + "',1)>0 " +
	 * " and lower(s.name) like '%" + description.toLowerCase() + "%' " +
	 * " and  o.common_name='" + organism + "'"
	 * 
	 * + " and fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id" +
	 * " and cv.name='Note' and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
	 * 
	 * + ")) fdesc "
	 * 
	 * + locusmapping + locuspref + " order by contig_name, fmin, fmax";
	 * 
	 * //order by contig_name, fmin, fmax";
	 * 
	 * Set treeset = new TreeSet(executeSQLMerged(sql)); listresult.addAll(treeset);
	 * return listresult; }
	 * 
	 * 
	 * @Override public List<Locus> getLocusByDescriptionAndSynonym(String
	 * description, String organism) {
	 * 
	 * List listresult = new ArrayList();
	 * 
	 * String sql =
	 * "select distinct feature_id, name, fmin, fmax, strand, contig_id, contig_name, lower(notes) notes, organism_id, common_name from ("
	 * +
	 * "select distinct f.feature_id, f.name name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(fp.value,1000,1) notes, f.organism_id, o.common_name from "
	 * + AppContext.getDefaultSchema() + ".feature f, " +
	 * AppContext.getDefaultSchema() + ".featureloc fl, " +
	 * AppContext.getDefaultSchema() + ".featureprop fp, " +
	 * AppContext.getDefaultSchema() + ".cvterm cv , " +
	 * AppContext.getDefaultSchema() + ".feature fsrc, " +
	 * AppContext.getDefaultSchema() + ".organism o , " +
	 * AppContext.getDefaultSchema() + ".cvterm cvtype" +
	 * " where fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id and f.feature_id=fl.feature_id and "
	 * + " contains(fp.value,'" + description + "',1)>0 " // +
	 * " lower(f.name) like '%" + description.toLowerCase() + "%' " +
	 * " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id " +
	 * " and cv.name='Note' and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
	 * + " and  o.common_name='" + organism + "'"
	 * 
	 * 
	 * +
	 * " union select f.feature_id, f.name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || s.name || ') ' || dbms_lob.substr(fp.value,1000,1)  notes, f.organism_id, o.common_name"
	 * + " from " + AppContext.getDefaultSchema() + ".feature f, " +
	 * AppContext.getDefaultSchema() + ".featureloc fl,   " +
	 * AppContext.getDefaultSchema() + ".feature fsrc, " +
	 * AppContext.getDefaultSchema() + ".organism o, " +
	 * AppContext.getDefaultSchema() + ".synonym_ s, " +
	 * AppContext.getDefaultSchema() + ".feature_synonym fsyn,   " +
	 * AppContext.getDefaultSchema() + ".featureprop fp, " +
	 * AppContext.getDefaultSchema() + ".cvterm cv,  " +
	 * AppContext.getDefaultSchema() + ".cvterm cvtype" +
	 * " where f.feature_id=fsyn.feature_id and fsyn.synonym_id=s.synonym_id" +
	 * " and f.feature_id=fl.feature_id and" +
	 * " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id" // +
	 * " and contains(s.name,'" + description + "',1)>0 " +
	 * " and lower(s.name) like '%" + description.toLowerCase() + "%' " +
	 * " and  o.common_name='" + organism + "'"
	 * 
	 * + " and fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id" +
	 * " and cv.name='Note' and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
	 * 
	 * + ") order by contig_name, fmin, fmax";
	 * 
	 * Set treeset = new TreeSet(executeSQL(sql)); listresult.addAll(treeset);
	 * return listresult; }
	 */

	@Override
	public List<Locus> getLocusByDescription(TextSearchOptions description, String organism, String genemodel) {

		if (genemodel.equals(GenomicsFacade.GENEMODEL_ALL) || !organism.equals(AppContext.REFERENCE_NIPPONBARE())) {
			return getLocusByDescription(description, organism);
		}

		return getLocusByDescriptionPostgres(description, organism, genemodel);

		// List listresult = new ArrayList();
		// String locusmapping="";
		// String locuspref="";
		// if(genemodel.equals( GenomicsFacade.GENEMODEL_IRIC)) {
		// locuspref = " where fdesc.name like 'OsNippo%'";
		// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
		// lm on lm.name=fdesc.name ";
		// }
		// else if(genemodel.equals( GenomicsFacade.GENEMODEL_MSU7)) {
		// locuspref = " where fdesc.name like 'LOC_%'";
		// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
		// lm on catsearch(lm.msu7,fdesc.name,null)>0 " ;
		// }
		// else if(genemodel.equals( GenomicsFacade.GENEMODEL_RAP)) {
		// locuspref = " where (fdesc.name like 'Os0%' or fdesc.name like 'Os1%') ";
		// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
		// lm on catsearch(lm.rap_representative,fdesc.name,null)>0 " ;
		// }
		// String sql =
		// "select fdesc.*, lm.name iric, lm.msu7, lm.rap_representative rap_rep,
		// lm.rap_predicted rap_pred, lm.fgenesh from ("
		//
		// + " select distinct feature_id, name, fmin, fmax, strand, contig_id,
		// contig_name, notes, organism_id, common_name from ("
		//
		// + "select f.feature_id, f.name \"NAME\", fl.fmin, fl.fmax, fl.strand,
		// fsrc.feature_id contig_id, fsrc.uniquename contig_name,
		// dbms_lob.substr(fp.value,1000,1) notes, f.organism_id, o.common_name "
		//
		// /*
		// + " from " + AppContext.getDefaultSchema() + ".feature f, " +
		// AppContext.getDefaultSchema() + ".featureloc fl, " +
		// AppContext.getDefaultSchema() + ".featureprop fp, " +
		// AppContext.getDefaultSchema() + ".cvterm cv , " +
		// AppContext.getDefaultSchema() + ".feature fsrc, " +
		// AppContext.getDefaultSchema() + ".organism o , " +
		// AppContext.getDefaultSchema() + ".cvterm cvtype"
		// + " where fp.feature_id=f.feature_id and fp.type_id=cv.cvterm_id and
		// f.feature_id=fl.feature_id and ";
		// */
		//
		// + " from " + AppContext.getDefaultSchema() + ".featureloc fl, " +
		// AppContext.getDefaultSchema() + ".featureprop fp, " +
		// AppContext.getDefaultSchema() + ".cvterm cvtype , " +
		// AppContext.getDefaultSchema() + ".feature fsrc, " +
		// AppContext.getDefaultSchema() + ".organism o , "
		// + " " + AppContext.getDefaultSchema() + ".feature f "
		// + " where f.feature_id=fp.feature_id and fp.type_id=78 and
		// f.feature_id=fl.feature_id and ";
		//
		// /*
		// if(description.isWholeWord() || ) {
		// sql += " regexp_like(fp.value, '[^a-zA-Z\\d]" + description.getText() +
		// "[^a-zA-Z\\d]', 'i') ";
		// //sql += " regexp_like(fp.value, '[^\\w]" + description.getText() + "[^\\w]',
		// 'i') ";
		// //sql += " regexp_like(fp.value, '\\b" + description.getText() + "\\b', 'i')
		// ";
		// }
		// else
		// */ if(description.isRegex() || description.isWholeWord()) {
		// sql += " regexp_like(fp.value, '" + description.getText() + "','i') ";
		// }
		// else if(description.isExact()) {
		// //sql += " lower(fp.value)='" + description.getText().toLowerCase() + "' ";
		// sql += " dbms_lob.compare(lower(fp.value), '" +
		// description.getText().toLowerCase() + "')=0 ";
		// }
		// else
		// sql += " contains(fp.value,'" + description.getText() + "',1)>0 ";
		// // + " lower(f.name) like '%" + description.toLowerCase() + "%' "
		//
		// sql += " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id
		// "
		// + " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
		// + " and o.common_name='" + organism + "'"
		//
		// + ")) fdesc "
		//
		// + locusmapping
		// + locuspref
		// + " order by contig_name, fmin, fmax";
		//
		// //order by contig_name, fmin, fmax";
		//
		// Set treeset = new TreeSet(executeSQLMerged(sql));
		// listresult.addAll(treeset);
		// return listresult;
	}

	private List<Locus> getLocusByDescriptionOracle(TextSearchOptions description, String organism, String genemodel) {
		List listresult = new ArrayList();
		String locusmapping = "";
		String locuspref = "";
		if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC)) {
			locuspref = " where fdesc.name like 'OsNippo%'";
			locusmapping = "  left join public.locus_mapping lm on lm.name=fdesc.name ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)) {
			locuspref = " where fdesc.name like 'LOC_%'";
			locusmapping = "  left join public.locus_mapping lm on catsearch(lm.msu7,fdesc.name,null)>0 ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)) {
			locuspref = " where (fdesc.name like 'Os0%' or fdesc.name like 'Os1%') ";
			// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
			// lm on catsearch(lm.rap_representative,fdesc.name,null)>0 " ;
			locusmapping = "  left join public.locus_mapping lm on ( catsearch(lm.rap_representative,fdesc.name,null)>0 or catsearch(lm.rap_predicted,fdesc.name,null)>0) ";
		}
		String sql = "select fdesc.*,  lm.name iric, lm.msu7, lm.rap_representative rap_rep, lm.rap_predicted rap_pred, lm.fgenesh from ("

				+ " select distinct feature_id, name, fmin, fmax, strand, contig_id, contig_name, notes, organism_id, common_name from ("

				+ "select f.feature_id, f.name \"NAME\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(fp.value,1000,1) notes, f.organism_id, o.common_name "

				/*
				 * + " from " + AppContext.getDefaultSchema() + ".feature f, " +
				 * AppContext.getDefaultSchema() + ".featureloc fl, " +
				 * AppContext.getDefaultSchema() + ".featureprop fp, " +
				 * AppContext.getDefaultSchema() + ".cvterm cv , " +
				 * AppContext.getDefaultSchema() + ".feature fsrc, " +
				 * AppContext.getDefaultSchema() + ".organism o , " +
				 * AppContext.getDefaultSchema() + ".cvterm cvtype" +
				 * " where fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id and f.feature_id=fl.feature_id and "
				 * ;
				 */

				+ "	from  public.featureloc fl, public.featureprop fp, public.cvterm cvtype , public.feature fsrc, public.organism o , "
				+ " public.feature f "
				+ " where f.feature_id=fp.feature_id and fp.type_id=78 and  f.feature_id=fl.feature_id and ";

		/*
		 * if(description.isWholeWord() || ) { sql +=
		 * " regexp_like(fp.value, '[^a-zA-Z\\d]" + description.getText() +
		 * "[^a-zA-Z\\d]', 'i') "; //sql += " regexp_like(fp.value, '[^\\w]" +
		 * description.getText() + "[^\\w]', 'i') "; //sql +=
		 * " regexp_like(fp.value, '\\b" + description.getText() + "\\b', 'i') "; } else
		 */ if (description.isRegex() || description.isWholeWord()) {
			sql += " regexp_like(fp.value, '" + description.getText() + "','i') ";
		} else if (description.isExact()) {
			// sql += " lower(fp.value)='" + description.getText().toLowerCase() + "' ";
			sql += " dbms_lob.compare(lower(fp.value), '" + description.getText().toLowerCase() + "')=0 ";
		} else
			sql += " contains(fp.value,'" + description.getText() + "',1)>0 ";
		// + " lower(f.name) like '%" + description.toLowerCase() + "%' "

		sql += " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id " + " and  o.common_name='" + organism + "'"

				+ ")) fdesc "

				+ locusmapping + locuspref + " order by contig_name, fmin, fmax";

		// order by contig_name, fmin, fmax";

		Set treeset = new TreeSet(executeSQLMerged(sql));
		listresult.addAll(treeset);
		return listresult;
	}

	private List<Locus> getLocusByDescriptionPostgres(TextSearchOptions description, String organism,
			String genemodel) {

		// select fdesc.*, lm.name iric, lm.msu7, lm.rap_representative rap_rep,
		// lm.rap_predicted rap_pred, lm.fgenesh from ( select distinct feature_id,
		// name, fmin, fmax, strand, contig_id, contig_name, notes, organism_id,
		// common_name from (select f.feature_id, f.name , fl.fmin, fl.fmax, fl.strand,
		// fsrc.feature_id contig_id, fsrc.uniquename contig_name,
		// substr(fp.value,1,1000) notes, f.organism_id, o.common_name from
		// public.featureloc fl, public.featureprop fp, public.cvterm cvtype ,
		// public.feature fsrc, public.organism o , public.feature f where
		// f.feature_id=fp.feature_id and fp.type_id=78 and f.feature_id=fl.feature_id
		// and fp.value ~ '(^|\W)alcohol($|\W)' and fsrc.feature_id=fl.srcfeature_id and
		// f.organism_id=o.organism_id and cvtype.name='gene' and
		// cvtype.cvterm_id=f.type_id and o.common_name='Japonica nipponbare') foo )
		// fdesc left join public.locus_mapping lm on lm.msu7 like '%' || fdesc.name ||
		// '%' where fdesc.name like 'LOC_%' order by contig_name, fmin, fmax;

		List listresult = new ArrayList();
		String locusmapping = "";
		String locuspref = "";
		String sqlret = "select fdesc.*,  lm.name iric, lm.msu7, lm.rap_representative rap_rep, lm.rap_predicted rap_pred, lm.fgenesh from (";
		if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC)) {
			locuspref = " where fdesc.name like 'OsNippo%'";
			locusmapping = "  left join public.locus_mapping lm on lm.name=fdesc.name ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)) {
			locuspref = " where fdesc.name like 'LOC_%'";
			// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
			// lm on catsearch(lm.msu7,fdesc.name,null)>0 " ;
			locusmapping = "  left join public.locus_mapping lm on lm.msu7 like '%' || fdesc.name || '%' ";
			// lm.msu7 like '%' || fdesc.name || '%'
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)) {
			locuspref = " where (fdesc.name like 'Os0%' or fdesc.name like 'Os1%') ";
			// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
			// lm on catsearch(lm.rap_representative,fdesc.name,null)>0 " ;
			locusmapping = "  left join public.locus_mapping lm on (lm.rap_representative like '%' || fdesc.name || '%' or  lm.rap_predicted like '%' || fdesc.name || '%')";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC_ONLY)) {
			locuspref = " where fdesc.name like 'OsNippo%'";
			// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
			// lm on lm.name=fdesc.name ";
			sqlret = "select fdesc.*,  fdesc.name iric, '' as msu7, '' as rap_rep, ''as  rap_pred, '' as fgenesh from (";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY)) {
			locuspref = " where fdesc.name like 'LOC_%'";
			// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
			// lm on catsearch(lm.msu7,fdesc.name,null)>0 " ;
			// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
			// lm on lm.msu7 like '%' || fdesc.name || '%' " ;
			// lm.msu7 like '%' || fdesc.name || '%'
			sqlret = "select fdesc.*,  '' as iric, fdesc.name as msu7, '' as rap_rep, ''as  rap_pred, '' as fgenesh from (";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP_ONLY)) {
			locuspref = " where (fdesc.name like 'Os0%' or fdesc.name like 'Os1%') ";
			// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
			// lm on catsearch(lm.rap_representative,fdesc.name,null)>0 " ;
			// locusmapping =" left join " + AppContext.getDefaultSchema() + ".locus_mapping
			// lm on (lm.rap_representative like '%' || fdesc.name || '%' or
			// lm.rap_predicted like '%' || fdesc.name || '%')" ;
			sqlret = "select fdesc.*,  '' as iric, '' as msu7, fdesc.name as rap_rep, '' as  rap_pred, '' as fgenesh from (";
		}

		String sql = sqlret

				+ " select distinct feature_id, name, fmin, fmax, strand, contig_id, contig_name, notes, organism_id, common_name, feature_type from ("

				+ "select f.feature_id, f.name , fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, substr(fp.value,1,1000) notes, f.organism_id, o.common_name, cvtype.name feature_type "

				/*
				 * + " from " + AppContext.getDefaultSchema() + ".feature f, " +
				 * AppContext.getDefaultSchema() + ".featureloc fl, " +
				 * AppContext.getDefaultSchema() + ".featureprop fp, " +
				 * AppContext.getDefaultSchema() + ".cvterm cv , " +
				 * AppContext.getDefaultSchema() + ".feature fsrc, " +
				 * AppContext.getDefaultSchema() + ".organism o , " +
				 * AppContext.getDefaultSchema() + ".cvterm cvtype" +
				 * " where fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id and f.feature_id=fl.feature_id and "
				 * ;
				 */

				+ "	from  public.featureloc fl, public.featureprop fp, public.cvterm cvtype , public.feature fsrc, public.organism o , "
				+ " public.feature f "
				+ " where f.feature_id=fp.feature_id and fp.type_id=78 and  f.feature_id=fl.feature_id and ";

		/*
		 * if(description.isWholeWord() || ) { sql +=
		 * " regexp_like(fp.value, '[^a-zA-Z\\d]" + description.getText() +
		 * "[^a-zA-Z\\d]', 'i') "; //sql += " regexp_like(fp.value, '[^\\w]" +
		 * description.getText() + "[^\\w]', 'i') "; //sql +=
		 * " regexp_like(fp.value, '\\b" + description.getText() + "\\b', 'i') "; } else
		 */ if (description.isRegex() || description.isWholeWord()) {
			// sql += " regexp_like(fp.value, '" + description.getText() + "','i') ";

			// sql += " lower(fp.value) ~ '" + description.getText().toLowerCase() + "' ";
			sql += " lower(fp.value) ~  '" + description.getText().toLowerCase() + "' ";

			// lm.msu7 like '%' || fdesc.name || '%'
			// fp.value ~ '(^|\W)cdp-alcohol($|\W)'
		} else if (description.isExact()) {
			// sql += " lower(fp.value)='" + description.getText().toLowerCase() + "' ";
			// sql += " dbms_lob.compare(lower(fp.value), '" +
			// description.getText().toLowerCase() + "')=0 ";
			sql += " lower(fp.value)='" + description.getText().toLowerCase() + "' ";
		} else
			// sql += " contains(fp.value,'" + description.getText() + "',1)>0 ";
			sql += " lower(f.name) like '%" + description.getText().toLowerCase() + "%' ";

		sql += " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id " + " and  o.common_name='" + organism + "'"

				+ " ) foo ) fdesc "

				+ locusmapping + locuspref + " order by contig_name, fmin, fmax";

		// order by contig_name, fmin, fmax";

		Set treeset = new TreeSet(executeSQLMerged(sql));
		listresult.addAll(treeset);
		return listresult;
	}

	@Override
	public List<Locus> getLocusByDescription(TextSearchOptions description, String organism) {

		return getLocusByDescriptionPostgres(description, organism);

	}

	private List<Locus> getLocusByDescriptionOracle(TextSearchOptions description, String organism) {

		List listresult = new ArrayList();

		String sql = "select * from (select distinct feature_id, name, fmin, fmax, strand, contig_id, contig_name, notes, organism_id, common_name from ("
				+ "select distinct f.feature_id, f.name \"NAME\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(fp.value,1000,1) notes, f.organism_id, o.common_name "
				+ "	from  public.featureloc fl, public.featureprop fp, public.cvterm cvtype , public.feature fsrc, public.organism o , "
				+ " public.feature f  "
				+ " where  f.feature_id=fl.feature_id and f.feature_id=fp.feature_id and fp.type_id=78 and ";

		/*
		 * if(description.isWholeWord()) { sql +=
		 * "  regexp_like(fp.value, '[^a-zA-Z\\d]" + description.getText() +
		 * "[^a-zA-Z\\d]', 'i') "; //sql += " regexp_like(fp.value, '\\b" +
		 * description.getText() + "\\b', 'i') ";
		 * 
		 * }
		 */
		if (description.isRegex() || description.isWholeWord()) {
			sql += " regexp_like(fp.value, '" + description.getText() + "','i') ";
		} else if (description.isExact()) {
			// sql += " lower(fp.value)='" + description.getText().toLowerCase() + "' ";
			sql += " dbms_lob.compare(lower(fp.value), '" + description.getText().toLowerCase() + "')=0 ";
		} else
			sql += "  contains(fp.value,'" + description.getText() + "',1)>0 ";
		// + " lower(f.name) like '%" + description.toLowerCase() + "%' "

		sql += " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id " + " and  o.common_name='" + organism + "'"

				+ ") order by contig_name, fmin, fmax)";

		Set treeset = new TreeSet(executeSQL(sql));
		listresult.addAll(treeset);
		return listresult;
	}

	private List<Locus> getLocusByDescriptionPostgres(TextSearchOptions description, String organism) {

		List listresult = new ArrayList();

		String sql = "select foo.* from (select distinct feature_id, name, fmin, fmax, strand, contig_id, contig_name, notes, organism_id, common_name, feature_type, '0' AS DTYPE from ("
				+ "select distinct f.feature_id, f.name , fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, substr(fp.value,1,1000) notes, f.organism_id, o.common_name, cvtype.name as feature_type "
				+ "	from  public.featureloc fl, public.featureprop fp, public.cvterm cvtype , public.feature fsrc, public.organism o , "
				+ " public.feature f  "
				+ " where  f.feature_id=fl.feature_id and f.feature_id=fp.feature_id and fp.type_id=78 and ";

		/*
		 * if(description.isWholeWord()) { sql +=
		 * "  regexp_like(fp.value, '[^a-zA-Z\\d]" + description.getText() +
		 * "[^a-zA-Z\\d]', 'i') "; //sql += " regexp_like(fp.value, '\\b" +
		 * description.getText() + "\\b', 'i') ";
		 * 
		 * }
		 */
		if (description.isRegex() || description.isWholeWord()) {
			// sql += " regexp_like(fp.value, '" + description.getText() + "','i') ";
			sql += " lower(fp.value) ~  '" + description.getText().toLowerCase() + "' ";

			// sql += " lower(fp.value) ~* '\\y" + description.getText().toLowerCase() +
			// "\\y' ";
		} else if (description.isExact()) {
			// sql += " lower(fp.value)='" + description.getText().toLowerCase() + "' ";
			// sql += " dbms_lob.compare(lower(fp.value), '" +
			// description.getText().toLowerCase() + "')=0 ";
			sql += " lower(fp.value) = '" + description.getText().toLowerCase() + "' ";
		} else
			// sql += " contains(fp.value,'" + description.getText() + "',1)>0 ";
			sql += " lower(f.name) like '%" + description.getText().toLowerCase() + "%' ";

		sql += " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id " + " and  o.common_name='" + organism + "'"

				+ ") as foo order by contig_name, fmin, fmax) as foo";

		AppContext.debug("EXECUTING.... " + sql);
		Set treeset = new TreeSet(executeSQL(sql));
		listresult.addAll(treeset);
		return listresult;
	}

	@Override
	public List<Locus> getLocusBySynonyms(TextSearchOptions synonym, String organism) {
		return getLocusBySynonymsPostgres(synonym, organism);
	}

	private List<Locus> getLocusBySynonymsOracle(TextSearchOptions synonym, String organism) {

		List listresult = new ArrayList();

		String sql = "select * from ( select distinct feature_id, NAME, fmin, fmax, strand, contig_id, contig_name,  notes, organism_id, common_name from ("

				+ " select f.feature_id, f.name \"NAME\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || f.name || ') ' || dbms_lob.substr(f3.value,1000,1)  notes, f.organism_id, o.common_name"
				+ " from  public.featureloc fl,   public.feature fsrc, public.organism o, public.cvterm cvtype, "

				+ " public.feature f  LEFT JOIN ( SELECT f2.feature_id, fp.value FROM public.featureprop fp, public.feature f2 "
				+ " WHERE fp.feature_id = f2.feature_id AND fp.type_id = 78) f3 ON f.feature_id = f3.feature_id "

				+ " where f.feature_id=fl.feature_id and"
				+ " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and fsrc.organism_id=f.organism_id";

		/*
		 * if(synonym.isWholeWord()) { sql +=
		 * " and  regexp_like(f.name, '(^|[^a-zA-Z\\d])" + synonym.getText() +
		 * "($|[^a-zA-Z\\d])', 'i') "; //sql += " and regexp_like(fp.value, '\\b" +
		 * synonym.getText() + "\\b', 'i') ";
		 * 
		 * } else
		 */
		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " and regexp_like(f.name, '" + synonym.getText() + "','i') ";
		} else if (synonym.isExact()) {
			sql += " and lower(f.name)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " and lower(f.name) like '%" + synonym.getText().toLowerCase() + "%' ";

		if (!organism.equals("ALL"))
			sql += " and  o.common_name='" + organism + "'";

		sql += " and f.type_id=cvtype.cvterm_id and cvtype.name='gene' "

				+ " union select f.feature_id, f.name \"NAME\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || s.name || ') ' || dbms_lob.substr(f3.value, 1000, 1 )  notes, f.organism_id, o.common_name"
				+ " from public.featureloc fl,   public.feature fsrc, public.organism o, public.synonym_ s, public.feature_synonym fsyn, public.cvterm cvtype, "
				+ " public.feature f  LEFT JOIN ( SELECT f2.feature_id, fp.value FROM public.featureprop fp, public.feature f2 "
				+ " WHERE fp.feature_id = f2.feature_id AND fp.type_id = 78) f3 ON f.feature_id = f3.feature_id "

				+ " where f.feature_id=fsyn.feature_id and fsyn.synonym_id=s.synonym_id"
				+ " and f.feature_id=fl.feature_id and"
				+ " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and fsrc.organism_id=f.organism_id ";

		/*
		 * if(synonym.isWholeWord()) { sql +=
		 * " and regexp_like(s.name, '(^|[^a-zA-Z\\d])" + synonym.getText() +
		 * "($|[^a-zA-Z\\d])', 'i') "; //sql += " and regexp_like(s.name, '\\b" +
		 * synonym.getText() + "\\b', 'i') ";
		 * 
		 * }
		 */

		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " and regexp_like(s.name, '" + synonym.getText() + "','i') ";
		} else if (synonym.isExact()) {
			sql += " and lower(s.name)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " and lower(s.name) like '%" + synonym.getText().toLowerCase() + "%' ";

		if (!organism.equals("ALL"))
			sql += " and  o.common_name='" + organism + "'";
		sql += " and f.type_id=cvtype.cvterm_id and cvtype.name='gene' " + " ) order by contig_name, fmin, fmax )";

		return executeSQL(sql);
	}

	private List<Locus> getLocusBySynonymsPostgres(TextSearchOptions synonym, String organism) {

		List listresult = new ArrayList();

		/*
		 * select fdesc.* from ( (select f.feature_id, f.name , fl.fmin, fl.fmax,
		 * fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' ||
		 * f.name || ') ' || substring(f3.value,1,1000) notes, f.organism_id,
		 * o.common_name from public.featureloc fl, public.feature fsrc, public.organism
		 * o, public.cvterm cvtype, public.feature f LEFT JOIN ( SELECT f2.feature_id,
		 * fp.value FROM public.featureprop fp, public.feature f2 WHERE fp.feature_id =
		 * f2.feature_id AND fp.type_id = 78 offset 0) f3 ON f.feature_id =
		 * f3.feature_id where f.feature_id=fl.feature_id and
		 * fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and
		 * fsrc.organism_id=f.organism_id and (f.name ~ '\yalcohol\y' or f3.value ~
		 * '\yalcohol\y' ) and o.common_name='Kasalath' and f.type_id=cvtype.cvterm_id
		 * and cvtype.name='gene' offset 0) union (select f.feature_id, f.name ,
		 * fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename
		 * contig_name ,'(' || s.name || ') ' || substring(f3.value,1,1000) notes,
		 * f.organism_id, o.common_name from public.featureloc fl, public.feature fsrc,
		 * public.organism o, public.synonym s, public.feature_synonym fsyn,
		 * public.cvterm cvtype, public.feature f LEFT JOIN ( SELECT f2.feature_id,
		 * fp.value FROM public.featureprop fp, public.feature f2 WHERE fp.feature_id =
		 * f2.feature_id AND fp.type_id = 78 offset 0) f3 ON f.feature_id =
		 * f3.feature_id where f.feature_id=fsyn.feature_id and
		 * fsyn.synonym_id=s.synonym_id and f.feature_id=fl.feature_id and
		 * fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and
		 * fsrc.organism_id=f.organism_id and s.name ~ '\yalcohol\y' and
		 * o.common_name='Kasalath' and f.type_id=cvtype.cvterm_id and
		 * cvtype.name='gene' offset 0) ) fdesc order by contig_name, fmin, fmax;
		 */

		// String sql = "select distinct feature_id, name, fmin, fmax, strand,
		// contig_id, contig_name, notes, organism_id, common_name, 'gene' feature_type
		// from ("
		String sql = "select distinct fdesc.*, 'gene' feature_type from ("
				+ " (select f.feature_id, f.name , fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || f.name || ') ' || substring(f3.value,1,1000)   notes, f.organism_id, o.common_name"
				+ " from  public.featureloc fl,   " + AppContext.getDefaultSchema() + ".feature fsrc, "
				+ AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema() + ".cvterm cvtype, "
				+ " " + AppContext.getDefaultSchema() + ".feature f  LEFT JOIN ( SELECT f2.feature_id, fp.value FROM "
				+ AppContext.getDefaultSchema() + ".featureprop fp, " + AppContext.getDefaultSchema() + ".feature f2 "
				+ " WHERE fp.feature_id = f2.feature_id AND fp.type_id = 78 offset 0) f3 ON f.feature_id = f3.feature_id "
				+ " where f.feature_id=fl.feature_id and"
				+ " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and fsrc.organism_id=f.organism_id";

		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " and (f.name ~* '" + synonym.getText() + "' ";
		} else if (synonym.isExact()) {
			sql += " and (lower(f.name)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " and (lower(f.name) like '%" + synonym.getText().toLowerCase() + "%' ";

		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " or f3.value ~* '" + synonym.getText() + "' ";
		} else if (synonym.isExact()) {
			sql += " or lower(f3.value)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " or lower(f3.value) like '%" + synonym.getText().toLowerCase() + "%' ";
		sql += " )";

		if (!organism.equals("ALL"))
			sql += " and  o.common_name='" + organism + "'";
		sql += " and f.type_id=cvtype.cvterm_id and cvtype.name='gene' offset 0)"

				+ " union (select f.feature_id, f.name , fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name ,'(' || s.name || ') ' || substring(f3.value,1,1000)  notes, f.organism_id, o.common_name"
				+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,   " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".synonym s, " + AppContext.getDefaultSchema() + ".feature_synonym fsyn, "
				+ AppContext.getDefaultSchema() + ".cvterm cvtype, " + " " + AppContext.getDefaultSchema()
				+ ".feature f  LEFT JOIN ( SELECT f2.feature_id, fp.value FROM " + AppContext.getDefaultSchema()
				+ ".featureprop fp, " + AppContext.getDefaultSchema() + ".feature f2 "
				+ " WHERE fp.feature_id = f2.feature_id AND fp.type_id = 78 offset 0) f3 ON f.feature_id = f3.feature_id "
				+ " where f.feature_id=fsyn.feature_id and fsyn.synonym_id=s.synonym_id"
				+ " and f.feature_id=fl.feature_id and"
				+ " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and fsrc.organism_id=f.organism_id ";
		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " and s.name ~* '" + synonym.getText() + "' ";
		} else if (synonym.isExact()) {
			sql += " and lower(s.name)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " and lower(s.name) like '%" + synonym.getText().toLowerCase() + "%' ";

		if (!organism.equals("ALL"))
			sql += " and  o.common_name='" + organism + "'";
		sql += " and f.type_id=cvtype.cvterm_id and cvtype.name='gene' "

				+ " offset 0) ) fdesc order by contig_name, fmin, fmax ";

		return executeNativeSQL(sql);
	}

	@Override
	public List<Locus> getLocusBySynonyms(TextSearchOptions synonym, String organism, String genemodel) {

		if (AppContext.isOracle())
			return getLocusBySynonymsOracle(synonym, organism, genemodel);
		else if (AppContext.isPostgres()) {
			List l = getLocusBySynonymsPostgres(synonym, organism, genemodel, true);
			if (l.size() == 0) {
				return getLocusBySynonymsPostgres(synonym, organism, genemodel, false);
			} else
				return l;
		}

		return null;

	}

	private List<Locus> getLocusBySynonymsOracle(TextSearchOptions synonym, String organism, String genemodel) {

		List listresult = new ArrayList();

		String locusmapping = "";
		String locuspref = "";
		if (genemodel.equals(GenomicsFacade.GENEMODEL_ALL) || !organism.equals(AppContext.REFERENCE_NIPPONBARE())) {
			return getLocusBySynonyms(synonym, organism);
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC)) {
			locuspref = " where fdesc.\"NAME\" like 'OsNippo%'";
			locusmapping = "  left join " + AppContext.getDefaultSchema()
					+ ".locus_mapping lm on lm.name=fdesc.\"NAME\" ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)) {
			locuspref = " where fdesc.\"NAME\" like 'LOC_%'";
			locusmapping = "  left join " + AppContext.getDefaultSchema()
					+ ".locus_mapping lm on catsearch(lm.msu7,fdesc.\"NAME\",null)>0 ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)) {
			locuspref = " where (fdesc.\"NAME\" like 'Os0%' or fdesc.\"NAME\" like 'Os1%') ";
			locusmapping = "  left join " + AppContext.getDefaultSchema()
					+ ".locus_mapping lm on catsearch(lm.rap_representative,fdesc.\"NAME\",null)>0 ";
		}

		String sql = "select fdesc.*,  lm.name iric, lm.msu7, lm.rap_representative rap_rep, lm.rap_predicted rap_pred, lm.fgenesh from ("

				+ "select distinct feature_id, \"NAME\", fmin, fmax, strand, contig_id, contig_name,  notes, organism_id, common_name from ("

				+ " select f.feature_id, f.name \"NAME\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || f.name || ') ' || dbms_lob.substr(f3.value,1000, 1)  notes, f.organism_id, o.common_name"
				+ " from  " + AppContext.getDefaultSchema() + ".featureloc fl,   " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".cvterm cvtype, "

				+ " " + AppContext.getDefaultSchema() + ".feature f  LEFT JOIN ( SELECT f2.feature_id, fp.value FROM "
				+ AppContext.getDefaultSchema() + ".featureprop fp, " + AppContext.getDefaultSchema() + ".feature f2 "
				+ " WHERE fp.feature_id = f2.feature_id AND fp.type_id = 78) f3 ON f.feature_id = f3.feature_id "

				+ " where f.feature_id=fl.feature_id and"
				+ " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and fsrc.organism_id=f.organism_id ";

		/*
		 * if(synonym.isWholeWord()) { sql +=
		 * " and regexp_like(f.name, '(^|[^a-zA-Z\\d])" + synonym.getText() +
		 * "($|[^a-zA-Z\\d])', 'i') "; //sql += " and regexp_like(f.name, '\\b" +
		 * synonym.getText() + "\\b', 'i') ";
		 * 
		 * } else
		 */
		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " and regexp_like(f.name, '" + synonym.getText() + "','i') ";
		} else if (synonym.isExact()) {
			sql += " and lower(f.name)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " and lower(f.name) like '%" + synonym.getText().toLowerCase() + "%' ";

		sql += " and  o.common_name='" + organism + "'" + " and f.type_id=cvtype.cvterm_id and cvtype.name='gene' "

				+ " union select f.feature_id, f.name \"NAME\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || s.name || ') ' || dbms_lob.substr(f3.value,1000,1)  notes, f.organism_id, o.common_name"
				+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,   " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".synonym_ s, " + AppContext.getDefaultSchema() + ".feature_synonym fsyn, "
				+ AppContext.getDefaultSchema() + ".cvterm cvtype, "

				+ " " + AppContext.getDefaultSchema() + ".feature f  LEFT JOIN ( SELECT f2.feature_id, fp.value FROM "
				+ AppContext.getDefaultSchema() + ".featureprop fp, " + AppContext.getDefaultSchema() + ".feature f2 "
				+ " WHERE fp.feature_id = f2.feature_id AND fp.type_id = 78) f3 ON f.feature_id = f3.feature_id "

				+ " where f.feature_id=fsyn.feature_id and fsyn.synonym_id=s.synonym_id"
				+ " and f.feature_id=fl.feature_id and"
				+ " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and fsrc.organism_id=f.organism_id ";

		/*
		 * if(synonym.isWholeWord()) { sql +=
		 * " and regexp_like(s.name, '(^|[^a-zA-Z\\d])" + synonym.getText() +
		 * "($|[^a-zA-Z\\d])', 'i') "; //sql += " and regexp_like(s.name, '\\b" +
		 * synonym.getText() + "\\b', 'i') "; } else
		 */
		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " and regexp_like(s.name, '" + synonym.getText() + "','i') ";
		} else if (synonym.isExact()) {
			sql += " and lower(s.name)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " and lower(s.name) like '%" + synonym.getText().toLowerCase() + "%' ";

		sql += " and  o.common_name='" + organism + "'" + " and f.type_id=cvtype.cvterm_id and cvtype.name='gene' "

		// + " ) order by contig_name, fmin, fmax";
				+ ")) fdesc "

				+ locusmapping + locuspref + " order by contig_name, fmin, fmax";

		Set treeset = new TreeSet(executeSQLMerged(sql));
		listresult.addAll(treeset);
		return listresult;
	}

	private List<Locus> getLocusBySynonymsPostgres(TextSearchOptions synonym, String organism, String genemodel,
			boolean exactText) {

		List listresult = new ArrayList();

		String locusmapping = "";
		String locuspref = "";
		String sqlret = "select fdesc.*,  lm.name iric, lm.msu7, lm.rap_representative rap_rep, lm.rap_predicted rap_pred, lm.fgenesh from (";
		if (genemodel.equals(GenomicsFacade.GENEMODEL_ALL) || !organism.equals(AppContext.REFERENCE_NIPPONBARE())) {
			return getLocusBySynonyms(synonym, organism);
			// locuspref = "where 1=1";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC)) {
			// locuspref = " where fdesc.\"NAME\" like 'OsNippo%'";
			locuspref = " and f.name like 'OsNippo%'";
			locusmapping = "  left join " + AppContext.getDefaultSchema()
					+ ".locus_mapping lm on lm.name=fdesc.\"NAME\" ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)) {
			locuspref = " and f.name  like 'LOC_%'";
			if (exactText)
				locusmapping = "  left join " + AppContext.getDefaultSchema()
						+ ".locus_mapping lm on lm.msu7=fdesc.\"NAME\" ";
			else
				locusmapping = "  left join " + AppContext.getDefaultSchema()
						+ ".locus_mapping lm on lm.msu7 like '%' || fdesc.\"NAME\" || '%' ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)) {
			locuspref = "  and (f.name  like 'Os0%' or f.name  like 'Os1%') ";
			if (exactText)
				locusmapping = "  left join " + AppContext.getDefaultSchema()
						+ ".locus_mapping lm on lm.rap_representative=fdesc.\"NAME\" ";
			else
				locusmapping = "  left join " + AppContext.getDefaultSchema()
						+ ".locus_mapping lm on lm.rap_representative like '%' || fdesc.\"NAME\" || '%' ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC_ONLY)) {
			locuspref = " and f.name  like  'OsNippo%'";
			sqlret = "select fdesc.*,   fdesc.\"NAME\"  iric, '' as msu7, '' as rap_rep, '' as rap_pred, '' as fgenesh from (";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY)) {
			locuspref = " and f.name  like   'LOC_%'";
			sqlret = "select fdesc.*, '' as   iric,  fdesc.\"NAME\" as msu7, '' as rap_rep, '' as rap_pred, '' as fgenesh from (";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP_ONLY)) {
			locuspref = " and (f.name  like  'Os0%' or f.name  like  'Os1%') ";
			sqlret = "select fdesc.*,  '' as  iric, '' as msu7,  fdesc.\"NAME\" as rap_rep, '' as rap_pred, '' as fgenesh from (";
		}

		/*
		 * else if(genemodel.equals( GenomicsFacade.GENEMODEL_IRIC_ONLY)) { locuspref =
		 * " where fdesc.name like 'OsNippo%'"; //locusmapping ="  left join " +
		 * AppContext.getDefaultSchema() + ".locus_mapping lm on lm.name=fdesc.name ";
		 * sqlret="select fdesc.*,  fdesc.name iric, '' as msu7, '' as rap_rep, ''as  rap_pred, '' as fgenesh from ("
		 * ; } else if(genemodel.equals( GenomicsFacade.GENEMODEL_MSU7_ONLY)) {
		 * locuspref = " where fdesc.name like 'LOC_%'"; //locusmapping ="  left join "
		 * + AppContext.getDefaultSchema() +
		 * ".locus_mapping lm on catsearch(lm.msu7,fdesc.name,null)>0 " ; //locusmapping
		 * ="  left join " + AppContext.getDefaultSchema() +
		 * ".locus_mapping lm on lm.msu7 like '%' || fdesc.name || '%' " ; //lm.msu7
		 * like '%' || fdesc.name || '%'
		 * sqlret="select fdesc.*,  '' as iric, fdesc.name as msu7, '' as rap_rep, ''as  rap_pred, '' as fgenesh from ("
		 * ; } else if(genemodel.equals( GenomicsFacade.GENEMODEL_RAP_ONLY)) { locuspref
		 * = " where (fdesc.name like 'Os0%' or fdesc.name like 'Os1%') ";
		 * //locusmapping ="  left join " + AppContext.getDefaultSchema() +
		 * ".locus_mapping lm on catsearch(lm.rap_representative,fdesc.name,null)>0 " ;
		 * //locusmapping ="  left join " + AppContext.getDefaultSchema() +
		 * ".locus_mapping lm on (lm.rap_representative like '%' || fdesc.name || '%' or  lm.rap_predicted like '%' || fdesc.name || '%')"
		 * ;
		 * sqlret="select fdesc.*,  '' as iric, '' as msu7, fdesc.name as rap_rep, '' as  rap_pred, '' as fgenesh from ("
		 * ; }
		 */

		String sql = sqlret

				// + "select distinct feature_id, \"NAME\", fmin, fmax, strand, contig_id,
				// contig_name, notes, organism_id, common_name, feature_type from ("

				+ " (select f.feature_id, f.name \"NAME\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || f.name || ') ' || substring(f3.value,1,1000)  notes, f.organism_id, o.common_name, cvtype.name feature_type "
				+ " from  " + AppContext.getDefaultSchema() + ".featureloc fl,   " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".cvterm cvtype, "

				+ " " + AppContext.getDefaultSchema() + ".feature f  LEFT JOIN ( SELECT f2.feature_id, fp.value FROM "
				+ AppContext.getDefaultSchema() + ".featureprop fp, " + AppContext.getDefaultSchema() + ".feature f2 "
				+ " WHERE fp.feature_id = f2.feature_id AND fp.type_id = 78 offset 0) f3 ON f.feature_id = f3.feature_id "

				+ " where f.feature_id=fl.feature_id and"
				+ " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and fsrc.organism_id=f.organism_id ";

		/*
		 * if(synonym.isWholeWord()) { sql +=
		 * " and regexp_like(f.name, '(^|[^a-zA-Z\\d])" + synonym.getText() +
		 * "($|[^a-zA-Z\\d])', 'i') "; //sql += " and regexp_like(f.name, '\\b" +
		 * synonym.getText() + "\\b', 'i') ";
		 * 
		 * } else
		 */
		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " and (f.name ~* '" + synonym.getText() + "' ";
		} else if (synonym.isExact()) {
			sql += " and (lower(f.name)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " and (lower(f.name) like '%" + synonym.getText().toLowerCase() + "%' ";

		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " or f3.value ~ '" + synonym.getText() + "' ";
		} else if (synonym.isExact()) {
			sql += " or lower(f3.value)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " or lower(f3.value) like '%" + synonym.getText().toLowerCase() + "%' ";
		sql += " )";

		sql += " and  o.common_name='" + organism + "'" + " and f.type_id=cvtype.cvterm_id and cvtype.name='gene' "
				+ locuspref + " offset 0)"

				+ " union (select f.feature_id, f.name \"NAME\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || s.name || ') ' || substring(f3.value,1,1000)  notes, f.organism_id, o.common_name, cvtype.name feature_type "
				+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,   " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".synonym s, " + AppContext.getDefaultSchema() + ".feature_synonym fsyn, "
				+ AppContext.getDefaultSchema() + ".cvterm cvtype, "

				+ " " + AppContext.getDefaultSchema() + ".feature f  LEFT JOIN ( SELECT f2.feature_id, fp.value FROM "
				+ AppContext.getDefaultSchema() + ".featureprop fp, " + AppContext.getDefaultSchema() + ".feature f2 "
				+ " WHERE fp.feature_id = f2.feature_id AND fp.type_id = 78 offset 0) f3 ON f.feature_id = f3.feature_id "

				+ " where f.feature_id=fsyn.feature_id and fsyn.synonym_id=s.synonym_id"
				+ " and f.feature_id=fl.feature_id and"
				+ " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and fsrc.organism_id=f.organism_id ";

		/*
		 * if(synonym.isWholeWord()) { sql +=
		 * " and regexp_like(s.name, '(^|[^a-zA-Z\\d])" + synonym.getText() +
		 * "($|[^a-zA-Z\\d])', 'i') "; //sql += " and regexp_like(s.name, '\\b" +
		 * synonym.getText() + "\\b', 'i') "; } else
		 */
		if (synonym.isRegex() || synonym.isWholeWord()) {
			sql += " and s.name ~* '" + synonym.getText() + "' ";
		} else if (synonym.isExact()) {
			sql += " and lower(s.name)='" + synonym.getText().toLowerCase() + "' ";
		} else
			sql += " and lower(s.name) like '%" + synonym.getText().toLowerCase() + "%' ";

		sql += " and  o.common_name='" + organism + "'" + " and f.type_id=cvtype.cvterm_id and cvtype.name='gene'"
				+ locuspref

				// + " ) order by contig_name, fmin, fmax";
				+ " offset 0) ) fdesc "

				+ locusmapping
				// + locuspref
				+ " order by contig_name, fmin, fmax";

		Set treeset = new TreeSet(executeSQLMerged(sql));
		listresult.addAll(treeset);
		return listresult;
		/*
		 * explain analyze select fdesc.*, '' as iric, fdesc."NAME" as msu7, '' as
		 * rap_rep, '' as rap_pred, '' as fgenesh from ((select f.feature_id, f.name
		 * "NAME", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id,
		 * fsrc.uniquename contig_name , '(' || f.name || ') ' ||
		 * substring(f3.value,1,1000) notes, f.organism_id, o.common_name, cvtype.name
		 * feature_type from public.featureloc fl, public.feature fsrc, public.organism
		 * o, public.cvterm cvtype, public.feature f LEFT JOIN ( SELECT f2.feature_id,
		 * fp.value FROM public.featureprop fp, public.feature f2 WHERE fp.feature_id =
		 * f2.feature_id AND fp.type_id = 78 offset 0) f3 ON f.feature_id =
		 * f3.feature_id where f.feature_id=fl.feature_id and
		 * fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and
		 * fsrc.organism_id=f.organism_id and (f.name ~ '\yssd1i\y' or f3.value ~
		 * '\yssd1i\y' ) and o.common_name='Japonica nipponbare' and
		 * f.type_id=cvtype.cvterm_id and cvtype.name='gene' and f.name like 'LOC_%'
		 * offset 0) union (select f.feature_id, f.name "NAME", fl.fmin, fl.fmax,
		 * fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' ||
		 * s.name || ') ' || substring(f3.value,1,1000) notes, f.organism_id,
		 * o.common_name, cvtype.name feature_type from public.featureloc fl,
		 * public.feature fsrc, public.organism o, public.synonym s,
		 * public.feature_synonym fsyn, public.cvterm cvtype, public.feature f LEFT JOIN
		 * ( SELECT f2.feature_id, fp.value FROM public.featureprop fp, public.feature
		 * f2 WHERE fp.feature_id = f2.feature_id AND fp.type_id = 78 offset 0) f3 ON
		 * f.feature_id = f3.feature_id where f.feature_id=fsyn.feature_id and
		 * fsyn.synonym_id=s.synonym_id and f.feature_id=fl.feature_id and
		 * fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and
		 * fsrc.organism_id=f.organism_id and s.name ~ '\yssd1i\y' and
		 * o.common_name='Japonica nipponbare' and f.type_id=cvtype.cvterm_id and
		 * cvtype.name='gene' and f.name like 'LOC_%' offset 0) )fdesc order by
		 * contig_name, fmin, fmax
		 */
	}

	// @Override
	private List<Locus> getLocusByDescriptionSynonyms(String description, String organism) {

		List listresult = new ArrayList();
		/*
		 * String sql =
		 * "select distinct f.feature_id, f.uniquename \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, fp.value notes, f.organism_id, o.common_name from "
		 * + AppContext.getDefaultSchema() + ".feature f, " +
		 * AppContext.getDefaultSchema() + ".featureloc fl, " +
		 * AppContext.getDefaultSchema() + ".featureprop fp, " +
		 * AppContext.getDefaultSchema() + ".cvterm cv , " +
		 * AppContext.getDefaultSchema() + ".feature fsrc, " +
		 * AppContext.getDefaultSchema() + ".organism o , " +
		 * AppContext.getDefaultSchema() + ".cvterm cvtype" +
		 * " where fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id and f.feature_id=fl.feature_id and "
		 * //+ " contains(fp.value,'" + description + "',1)>0 " +
		 * " lower(fp.value) like '%" + description.toLowerCase() + "%' " +
		 * " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id " +
		 * " and cv.name='Note' and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
		 * + " and  o.common_name='" + organism + "'" +
		 * " order by contig_name, fmin, fmax";
		 */

		// String sql = "select distinct feature_id, name, fmin, fmax, strand,
		// contig_id, contig_name, lower(notes) notes, organism_id, common_name from ("
		String sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, substring(fp.value from 1 for 1000) notes, f.organism_id, o.common_name, cvtype.name feature_type "
				+ " from " + AppContext.getDefaultSchema() + ".feature f, " + AppContext.getDefaultSchema()
				+ ".featureloc fl, " + AppContext.getDefaultSchema() + ".featureprop fp, "
				+ AppContext.getDefaultSchema() + ".cvterm cv , " + AppContext.getDefaultSchema() + ".feature fsrc, "
				+ AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema() + ".cvterm cvtype "
				+ " where fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id and f.feature_id=fl.feature_id and "
				// + " contains(fp.value,'" + description + "',1)>0 "
				+ " lower(fp.value) like '%" + description.toLowerCase() + "%' "
				+ " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id  and fsrc.organism_id=f.organism_id"
				+ " and cv.name='Note' and cvtype.name='gene' and cvtype.cvterm_id=f.type_id " + " and  o.common_name='"
				+ organism + "'"

				+ " union select f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name , '(' || lower(s.name) || ') ' || substring(fp.value from 1 for 1000)  notes, f.organism_id, o.common_name, cvtype.name feature_type "
				+ " from " + AppContext.getDefaultSchema() + ".feature f, " + AppContext.getDefaultSchema()
				+ ".featureloc fl,   " + AppContext.getDefaultSchema() + ".feature fsrc, "
				+ AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema() + ".synonym s, "
				+ AppContext.getDefaultSchema() + ".feature_synonym fsyn,   " + AppContext.getDefaultSchema()
				+ ".featureprop fp, " + AppContext.getDefaultSchema() + ".cvterm cv , " + AppContext.getDefaultSchema()
				+ ".cvterm cvtype" + " where f.feature_id=fsyn.feature_id and fsyn.synonym_id=s.synonym_id"
				+ " and f.feature_id=fl.feature_id and"
				+ " fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and fsrc.organism_id=f.organism_id"
				+ " and lower(s.name) like '%" + description.toLowerCase() + "%' " + " and  o.common_name='" + organism
				+ "'"

				+ " and fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id"
				+ " and cv.name='Note' and f.type_id=cvtype.cvterm_id and cvtype.name='gene' "

				+ " order by contig_name, fmin, fmax";

		return executeSQL(sql);

	}

	@Override
	public List<Locus> getLocusByRegion(String contig, Long start, Long end, String organism, String genemodel) {
		if (AppContext.isOracle())
			return getLocusByRegionOracle(contig, start, end, organism, genemodel);
		else if (AppContext.isPostgres()) {
			List l = getLocusByRegionPostgres(contig, start, end, organism, genemodel, true);
			return l;
			/*
			 * if(l.size()==0) { return getLocusByRegionPostgres( contig, start, end,
			 * organism, genemodel, false); } else return l;
			 */
		}
		return null;
	}

	/*
	 * @Override public List<Locus> getLocusByRegion(String contig, Long start, Long
	 * end, String organism, String genemodel) { if(AppContext.isOracle()) return
	 * getLocusByRegionOracle( contig, start, end, organism); else
	 * if(AppContext.isPostgres()) return getLocusByRegionPostgres( contig, start,
	 * end, organism, genemodel); return null; }
	 */

	@Override
	public List<Locus> getLocusByRegion(String contig, Long start, Long end, String organism, String genemodel,
			String featuretype) {

		if (featuretype.equals(GenomicsFacade.FEATURETYPE_GENE))
			return getLocusByRegion(contig, start, end, organism, genemodel);
		if (AppContext.isPostgres())
			return getLocusByRegionPostgres(contig, start, end, organism, genemodel, featuretype, null);
		return null;
	}

	@Override
	public List<Locus> getLocusByRegion(String contig, Long start, Long end, String organism, String genemodel,
			Set featureset) {

		// if(featuretype.equals(GenomicsFacade.FEATURETYPE_GENE)) return
		// getLocusByRegion( contig, start, end, organism, genemodel);
		if (AppContext.isPostgres())
			return getLocusByRegionPostgres(contig, start, end, organism, genemodel, null, featureset);
		return null;
	}

	public List<Locus> getLocusByRegionOracle(String contig, Long start, Long end, String organism) {

		// use left join to include even without description

		String notes = "'' notes";
		if (organism.equals(AppContext.REFERENCE_NIPPONBARE()))
			notes = "dbms_lob.substr(f3.value,1000,1) notes";

		String sql = "select distinct f.feature_id, f.name name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, "
				+ notes + ", f.organism_id, o.common_name "
				+ ", f.name iric, '' as msu7, '' as rap_rep, '' as rap_pred, '' as fgenesh" + " from "
				+ AppContext.getDefaultSchema() + ".featureloc fl,  " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".cvterm cvtype, " + AppContext.getDefaultSchema() + ".feature f"
				+ " left join ( select f2.feature_id, fp.value from " + AppContext.getDefaultSchema()
				+ ".featureprop fp, " + AppContext.getDefaultSchema() + ".cvterm cv, " + AppContext.getDefaultSchema()
				+ ".feature f2"
				+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
				+ " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id " + " and ((fl.fmin>=" + start
				+ " and fl.fmax<=" + end + ") " + " or (fl.fmin<=" + start + " and fl.fmax>=" + end + ") "
				+ " or (fl.fmin<=" + start + " and fl.fmax>=" + start + ") " + " or (fl.fmin<=" + end + " and fl.fmax>="
				+ end + ") " + " ) and lower(fsrc.uniquename)='" + contig.toLowerCase() + "' "
				+ " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id " + " and o.common_name='" + organism + "' "
				+ " order by contig_name, fmin, fmax";

		return executeSQLMerged(sql);
	}

	private List<Locus> getLocusByRegionPostgresNomapping(String contig, Long start, Long end, String organism,
			String genemodel, String featuretype, Set setfeaturetype) {

		// use left join to include even without description

		String notes = "'' notes";
		if (organism.equals(AppContext.REFERENCE_NIPPONBARE()))
			notes = "substr(f3.value,1,1000) notes";

		String sqltype = "";
		if (featuretype != null)
			sqltype = " and cvtype.name='" + featuretype + "' ";
		else if (setfeaturetype != null) {
			Iterator<String> itType = setfeaturetype.iterator();
			sqltype = " and cvtype.name in (";
			while (itType.hasNext()) {
				sqltype += "'" + (String) itType.next() + "'";
				if (itType.hasNext())
					sqltype += ",";
			}
			sqltype += ") ";
		}

		String pref = "";
		String sql = "";
		if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC_ONLY)) {
			sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, "
					+ notes + ", f.organism_id, o.common_name, cvtype.name as feature_type "
					+ ", f.name iric, '' as msu7, '' as rap_rep, '' as rap_pred, '' as fgenesh";
			pref = " and f.name like 'OsNippo%' ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY)) {
			sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, "
					+ notes + ", f.organism_id, o.common_name,  cvtype.name as feature_type "
					+ ", '' iric, f.name as msu7, '' as rap_rep, '' as rap_pred, '' as fgenesh";
			if (organism.equals(AppContext.JAPONICA_NIPPONBARE))
				pref = " and f.name like 'LOC_%' ";
			else
				pref = " and f.name like 'OsMH63_%' ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP_ONLY)) {
			sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, "
					+ notes + ", f.organism_id, o.common_name,   cvtype.name  as feature_type "
					+ ", '' iric, '' as msu7, f.name as rap_rep, '' as rap_pred, '' as fgenesh";
			pref = " and (f.name like 'Os0%' or f.name like 'Os1%') ";
		} else {
			sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, "
					+ notes + ", f.organism_id, o.common_name,   cvtype.name  as feature_type ";
			// + ", '' iric, '' as msu7, f.name as rap_rep, '' as rap_pred, '' as fgenesh";
			// pref=" and (f.name like 'Os0%' or f.name like 'Os1%') ";
		}

		sql += ", '0' AS DTYPE  from " + AppContext.getDefaultSchema() + ".featureloc fl,  " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".cvterm cvtype, " + AppContext.getDefaultSchema() + ".feature f"
				+ " left join ( select f2.feature_id, fp.value from " + AppContext.getDefaultSchema()
				+ ".featureprop fp, " + AppContext.getDefaultSchema() + ".cvterm cv, " + AppContext.getDefaultSchema()
				+ ".feature f2"
				+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
				+ " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id " + " and ((fl.fmin>=" + start
				+ " and fl.fmax<=" + end + ") " + " or (fl.fmin<=" + start + " and fl.fmax>=" + end + ") "
				+ " or (fl.fmin<=" + start + " and fl.fmax>=" + start + ") " + " or (fl.fmin<=" + end + " and fl.fmax>="
				+ end + ") " + " ) and lower(fsrc.name)='" + contig.toLowerCase() + "' "
				+ " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				// + " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
				// + " and cvtype.name='" + featuretype + "'"
				+ sqltype + " and cvtype.cvterm_id=f.type_id " + " and o.common_name='" + organism + "' " + pref
				+ " order by contig_name, fmin, fmax";

		if (genemodel.equals(GenomicsFacade.GENEMODEL_ALL))
			return executeSQL(sql);
		else
			return executeSQLMerged(sql);
	}

	private List<Locus> getLocusByRegionPostgresNomapping(List<Locus> listLocus, String organism, String genemodel,
			String featuretype, Set setfeaturetype) {

		// use left join to include even without description

		String notes = "'' notes";
		if (organism.equals(AppContext.REFERENCE_NIPPONBARE()))
			notes = "substr(f3.value,1,1000) notes";

		String sqltype = "";
		if (featuretype != null)
			sqltype = " and cvtype.name='" + featuretype + "' ";
		else if (setfeaturetype != null) {
			Iterator<String> itType = setfeaturetype.iterator();
			sqltype = " and cvtype.name in (";
			while (itType.hasNext()) {
				sqltype += "'" + (String) itType.next() + "'";
				if (itType.hasNext())
					sqltype += ",";
			}
			sqltype += ") ";
		}

		String pref = "";
		String sql = "";
		if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC_ONLY)) {
			sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, "
					+ notes + ", f.organism_id, o.common_name, cvtype.name as feature_type "
					+ ", f.name iric, '' as msu7, '' as rap_rep, '' as rap_pred, '' as fgenesh";
			pref = " and f.name like 'OsNippo%' ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY)) {
			sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, "
					+ notes + ", f.organism_id, o.common_name,  cvtype.name as feature_type "
					+ ", '' iric, f.name as msu7, '' as rap_rep, '' as rap_pred, '' as fgenesh";
			pref = " and f.name like 'LOC_%' ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP_ONLY)) {
			sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, "
					+ notes + ", f.organism_id, o.common_name,   cvtype.name  as feature_type "
					+ ", '' iric, '' as msu7, f.name as rap_rep, '' as rap_pred, '' as fgenesh";
			pref = " and (f.name like 'Os0%' or f.name like 'Os1%') ";

		}

		sql += " from " + AppContext.getDefaultSchema() + ".featureloc fl,  " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".cvterm cvtype, " + AppContext.getDefaultSchema() + ".feature f"
				+ " left join ( select f2.feature_id, fp.value from " + AppContext.getDefaultSchema()
				+ ".featureprop fp, " + AppContext.getDefaultSchema() + ".cvterm cv, " + AppContext.getDefaultSchema()
				+ ".feature f2"
				+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
				+ " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id and (";

		Iterator<Locus> itloc = listLocus.iterator();
		while (itloc.hasNext()) {
			Locus loc = itloc.next();
			String contig = loc.getContig();
			int start = loc.getFmin();
			int end = loc.getFmax();
			sql += "(((fl.fmin>=" + start + " and fl.fmax<=" + end + ") " + " or (fl.fmin<=" + start + " and fl.fmax>="
					+ end + ") " + " or (fl.fmin<=" + start + " and fl.fmax>=" + start + ") " + " or (fl.fmin<=" + end
					+ " and fl.fmax>=" + end + ") " + " ) and lower(fsrc.uniquename)='" + contig.toLowerCase() + "')";
			if (itloc.hasNext())
				sql += " or ";

		}

		sql += ") and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				// + " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
				// + " and cvtype.name='" + featuretype + "'"
				+ sqltype + " and cvtype.cvterm_id=f.type_id " + " and o.common_name='" + organism + "' " + pref
				+ " order by contig_name, fmin, fmax";

		return executeSQLMerged(sql);
	}

	public List<Locus> getLocusByRegionPostgres(String contig, Long start, Long end, String organism, String genemodel,
			String featuretype, Set setfeaturetype) {

		if (featuretype == null && setfeaturetype != null && setfeaturetype.size() == 1) {
			featuretype = (String) setfeaturetype.iterator().next();
		}

		if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY) || genemodel.equals(GenomicsFacade.GENEMODEL_RAP_ONLY)
				|| genemodel.equals(GenomicsFacade.GENEMODEL_IRIC_ONLY))
			return getLocusByRegionPostgresNomapping(contig, start, end, organism, genemodel, featuretype,
					setfeaturetype);

		String locuspref = "";
		if (genemodel.equals(GenomicsFacade.GENEMODEL_ALL) || !organism.equals(AppContext.REFERENCE_NIPPONBARE())) {
			return getLocusByRegion(contig, start, end, organism, featuretype);
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC)) {
			locuspref = " and f.name like 'OsNippo%'";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)) {
			locuspref = " and f.name like 'LOC_%'";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)) {
			locuspref = " and (f.name like 'Os0%' or f.name like 'Os1%') ";
		}

		String ftypesql = "";
		if (featuretype != null) {
			ftypesql = " and lower(cvtype.name)='" + featuretype.toLowerCase() + "' ";
		} else {
			ftypesql = " and lower(cvtype.name) in (";
			Iterator<String> itF = setfeaturetype.iterator();
			while (itF.hasNext()) {
				ftypesql += "'" + itF.next().toLowerCase() + "'";
				if (itF.hasNext())
					ftypesql += ",";
			}
			ftypesql += ") ";
		}

		String sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, substring(f3.value,1,1000) notes, f.organism_id, o.common_name, cvtype.name feature_type "
				+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,  " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".cvterm cvtype, " + AppContext.getDefaultSchema() + ".feature f"
				+ " left join ( select f2.feature_id, fp.value from " + AppContext.getDefaultSchema()
				+ ".featureprop fp, " + AppContext.getDefaultSchema() + ".cvterm cv, " + AppContext.getDefaultSchema()
				+ ".feature f2"
				+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
				+ " on f.feature_id=f3.feature_id "

				+ " where f.feature_id=fl.feature_id " + " and ((fl.fmin>=" + start + " and fl.fmax<=" + end + ") "
				+ " or (fl.fmin<=" + start + " and fl.fmax>=" + end + ") " + " or (fl.fmin<=" + start + " and fl.fmax>="
				+ start + ") " + " or (fl.fmin<=" + end + " and fl.fmax>=" + end + ") " + " ) and lower(fsrc.name)='"
				+ contig.toLowerCase() + "' " + " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				// + " and lower(cvtype.name)='" + featuretype.toLowerCase() + "' and
				// cvtype.cvterm_id=f.type_id "
				+ ftypesql + " and cvtype.cvterm_id=f.type_id and o.common_name='" + organism + "' " + locuspref
				+ " order by contig_name, fmin, fmax";

		return executeNativeSQL(sql);
	}

	/*
	 * private List<Locus> getLocusByRegionPostgresNomapping(String contig, Long
	 * start, Long end, String organism, String genemodel, boolean exactString) {
	 * 
	 * }
	 */
	private List<Locus> getLocusByRegionPostgres(String contig, Long start, Long end, String organism, String genemodel,
			boolean exactString) {

		try {

			if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY)
					|| genemodel.equals(GenomicsFacade.GENEMODEL_RAP_ONLY)
					|| genemodel.equals(GenomicsFacade.GENEMODEL_IRIC_ONLY))
				return getLocusByRegionPostgresNomapping(contig, start, end, organism, genemodel,
						GenomicsFacade.FEATURETYPE_GENE, null);

			// use left join to include even without description

			String locusmapping = "";
			String locuspref = "";
			if (genemodel.equals(GenomicsFacade.GENEMODEL_ALL) || !organism.equals(AppContext.REFERENCE_NIPPONBARE())) {
				// return getLocusByRegion( contig, start, end, organism,
				// GenomicsFacade.GENEMODEL_ALL);
				return getLocusByRegionPostgresNomapping(contig, start, end, organism, genemodel,
						GenomicsFacade.FEATURETYPE_GENE, null);
			} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC)) {
				locuspref = " and f.name like 'OsNippo%'";
				locusmapping = "  left join " + AppContext.getDefaultSchema() + ".locus_mapping lm on lm.name=f.name ";
			} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)) {
				locuspref = " and f.name like 'LOC_%'";
				if (exactString)
					locusmapping = "  left join " + AppContext.getDefaultSchema()
							+ ".locus_mapping lm on lm.msu7=f.name ";
				// else locusmapping =" left join " + AppContext.getDefaultSchema() +
				// ".locus_mapping lm on lm.msu7 like '%' || f.name || '%' " ;
				else
					locusmapping = "  left join " + AppContext.getDefaultSchema()
							+ ".locus_mapping lm on position(f.name in lm.msu7)>0  ";
			} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)) {
				locuspref = " and (f.name like 'Os0%' or f.name like 'Os1%') ";
				if (exactString)
					locusmapping = "  left join " + AppContext.getDefaultSchema()
							+ ".locus_mapping lm on (lm.rap_representative=f.name or  lm.rap_predicted=f.name) ";
				// else locusmapping =" left join " + AppContext.getDefaultSchema() +
				// ".locus_mapping lm on lm.rap_representative like '%' || f.name || '%' " ;
				else
					locusmapping = "  left join " + AppContext.getDefaultSchema()
							+ ".locus_mapping lm on (position(f.name in lm.rap_representative)>0 || position(f.name in lm.rap_predicted)>0) ";
			}

			String sql = "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, substring(f3.value,1,1000) notes, f.organism_id, o.common_name, cvtype.name feature_type, "
					+ " lm.name iric, lm.msu7, lm.rap_representative rap_rep, lm.rap_predicted rap_pred, lm.fgenesh "
					+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,  " + AppContext.getDefaultSchema()
					+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, "
					+ AppContext.getDefaultSchema() + ".cvterm cvtype, " + AppContext.getDefaultSchema() + ".feature f"
					+ " left join ( select f2.feature_id, fp.value from " + AppContext.getDefaultSchema()
					+ ".featureprop fp, " + AppContext.getDefaultSchema() + ".cvterm cv, "
					+ AppContext.getDefaultSchema() + ".feature f2"
					+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
					+ " on f.feature_id=f3.feature_id " + locusmapping

					+ " where f.feature_id=fl.feature_id " + " and ((fl.fmin>=" + start + " and fl.fmax<=" + end + ") "
					+ " or (fl.fmin<=" + start + " and fl.fmax>=" + end + ") " + " or (fl.fmin<=" + start
					+ " and fl.fmax>=" + start + ") " + " or (fl.fmin<=" + end + " and fl.fmax>=" + end + ") "
					+ " ) and lower(fsrc.uniquename)='" + contig.toLowerCase() + "' "
					+ " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
					+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id " + " and o.common_name='" + organism
					+ "' "

					+ " and lm.name is not null" + locuspref

					+ " order by contig_name, fmin, fmax";

			return executeSQLMerged(sql);

		} catch (Exception ex) {
			AppContext.debug("getLocusByRegionPostgres");
			ex.printStackTrace();
			return new ArrayList();
		}
	}

	public List<Locus> getLocusByRegionOracle(String contig, Long start, Long end, String organism, String genemodel) {

		// use left join to include even without description

		String locusmapping = "";
		String locuspref = "";
		if (genemodel.equals(GenomicsFacade.GENEMODEL_ALL) || !organism.equals(AppContext.REFERENCE_NIPPONBARE())) {
			return getLocusByRegion(contig, start, end, organism, GenomicsFacade.GENEMODEL_ALL);
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC)) {
			locuspref = " and f.name like 'OsNippo%'";
			locusmapping = "  left join " + AppContext.getDefaultSchema() + ".locus_mapping lm on lm.name=f.name ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)) {
			locuspref = " and f.name like 'LOC_%'";
			locusmapping = "  left join " + AppContext.getDefaultSchema()
					+ ".locus_mapping lm on catsearch(lm.msu7,f.name,null)>0 ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)) {
			locuspref = " and (f.name like 'Os0%' or f.name like 'Os1%') ";
			locusmapping = "  left join " + AppContext.getDefaultSchema()
					+ ".locus_mapping lm on catsearch(lm.rap_representative,f.name,null)>0 ";
		}

		String sql = "select distinct f.feature_id, f.name name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(f3.value,1000,1) notes, f.organism_id, o.common_name, "
				+ " lm.name iric, lm.msu7, lm.rap_representative rap_rep, lm.rap_predicted rap_pred, lm.fgenesh "
				+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,  " + AppContext.getDefaultSchema()
				+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, " + AppContext.getDefaultSchema()
				+ ".cvterm cvtype, " + AppContext.getDefaultSchema() + ".feature f"
				+ " left join ( select f2.feature_id, fp.value from " + AppContext.getDefaultSchema()
				+ ".featureprop fp, " + AppContext.getDefaultSchema() + ".cvterm cv, " + AppContext.getDefaultSchema()
				+ ".feature f2"
				+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
				+ " on f.feature_id=f3.feature_id " + locusmapping

				+ " where f.feature_id=fl.feature_id " + " and ((fl.fmin>=" + start + " and fl.fmax<=" + end + ") "
				+ " or (fl.fmin<=" + start + " and fl.fmax>=" + end + ") " + " or (fl.fmin<=" + start + " and fl.fmax>="
				+ start + ") " + " or (fl.fmin<=" + end + " and fl.fmax>=" + end + ") "
				+ " ) and lower(fsrc.uniquename)='" + contig.toLowerCase() + "' "
				+ " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id " + " and o.common_name='" + organism + "' "

				+ " and lm.name is not null" + locuspref

				+ " order by contig_name, fmin, fmax";

		return executeSQLMerged(sql);
	}

	@Override
	public List getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus) {
		Set allset = new HashSet();
		Set sets[] = AppContext.setSlicer(new HashSet(posset), 500);
		for (int iset = 0; iset < sets.length; iset++) {
			allset.addAll(_getLocusByContigPositions(contig, sets[iset], organism, plusminus,
					GenomicsFacade.GENEMODEL_MSU7_ONLY, GenomicsFacade.FEATURETYPE_GENE));
		}
		List listall = new ArrayList();
		listall.addAll(allset);
		return listall;
	}

	@Override
	public List getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel) {
		Set allset = new HashSet();
		Set sets[] = AppContext.setSlicer(new HashSet(posset), 500);
		for (int iset = 0; iset < sets.length; iset++) {
			allset.addAll(_getLocusByContigPositions(contig, sets[iset], organism, genemodel, plusminus,
					GenomicsFacade.FEATURETYPE_GENE));
		}
		List listall = new ArrayList();
		listall.addAll(allset);
		return listall;
	}

	@Override
	public List getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel, String featuretype) {
		/*
		 * Set allset=new HashSet(); Set sets[] = AppContext.setSlicer(new
		 * HashSet(posset),500); for(int iset=0; iset<sets.length; iset++) {
		 * allset.addAll( _getLocusByContigPositions( contig, sets[iset], organism,
		 * plusminus, genemodel, featuretype)); } List listall=new ArrayList();
		 * listall.addAll(allset); return listall;
		 */
		List listall = new ArrayList();
		listall.addAll(_getLocusByContigPositions(contig, posset, organism, plusminus, genemodel, featuretype));
		return listall;

	}

	@Override
	public List getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel, Set featuretype) {
		/*
		 * Set allset=new HashSet(); Set sets[] = AppContext.setSlicer(new
		 * HashSet(posset),900); for(int iset=0; iset<sets.length; iset++) {
		 * //allset.addAll( _getLocusByContigPositions( contig, sets[iset], organism,
		 * plusminus, genemodel, featuretype)); } allset.addAll(
		 * _getLocusByContigPositions( contig, posset, organism, plusminus, genemodel,
		 * featuretype));
		 */
		List listall = new ArrayList();
		listall.addAll(_getLocusByContigPositions(contig, posset, organism, plusminus, genemodel, featuretype));
		return listall;
	}

	/*
	 * @Override public List getLocusByContigPositions(String contig, Collection
	 * posset, String organism, Integer plusminus, String genemodel, String
	 * featuretype) { if(featuretype.equals(GenomicsFacade.FEATURETYPE_GENE)) return
	 * getLocusByContigPositions( contig, posset, organism, plusminus, genemodel);
	 * if(AppContext.isPostgres()) { //getLocusByContigPositions( contig, posset,
	 * organism, plusminus, genemodel,featuretype); return
	 * _getLocusByContigPositionsPostgres( contig, posset, organism, plusminus,
	 * genemodel, featuretype); } return null; }
	 */

	private List _getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel, String featuretype) {
		if (AppContext.isOracle())
			return _getLocusByContigPositionsOracle(contig, posset, organism, plusminus);
		else if (AppContext.isPostgres())
			return _getLocusByContigPositionsPostgres(contig, posset, organism, plusminus, genemodel, featuretype);
		return null;
	}

	private List _getLocusByContigPositions(String contig, Collection posset, String organism, Integer plusminus,
			String genemodel, Set featuretype) {
		if (AppContext.isOracle())
			return _getLocusByContigPositionsOracle(contig, posset, organism, plusminus);
		else if (AppContext.isPostgres())
			return _getLocusByContigPositionsPostgres(contig, posset, organism, plusminus, genemodel, featuretype);
		return null;
	}

	private List _getLocusByContigPositionsOracle(String contig, Collection posset, String organism,
			Integer plusminus) {

		posset = AppContext.convertPos2Position(posset);

		/*
		 * String sql =
		 * "select f.feature_id, f.uniquename name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(fp.value,1000,1) notes, f.organism_id, o.common_name from "
		 * + AppContext.getDefaultSchema() + ".feature f, " +
		 * AppContext.getDefaultSchema() + ".featureloc fl, " +
		 * AppContext.getDefaultSchema() + ".featureprop fp, " +
		 * AppContext.getDefaultSchema() + ".cvterm cv , " +
		 * AppContext.getDefaultSchema() + ".feature fsrc, " +
		 * AppContext.getDefaultSchema() + ".organism o, " +
		 * AppContext.getDefaultSchema() + ".cvterm cvtype " +
		 * " where fp.feature_id=f.feature_id and  fp.type_id=cv.cvterm_id and f.feature_id=fl.feature_id "
		 * 
		 * + " and ((fl.fmin>=" + start + " and fl.fmax<=" + end + ") " +
		 * " or (fl.fmin<=" + start + " and fl.fmax>=" + end +") " + " or (fl.fmin<=" +
		 * start + " and fl.fmax>=" + start +") " + " or (fl.fmin<=" + end +
		 * " and fl.fmax>=" + end +") " + " ) and lower(fsrc.uniquename)='" +
		 * contig.toLowerCase() + "' " +
		 * " and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id " +
		 * " and cv.name='Note' and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
		 * 
		 * + " and o.common_name='" + organism + "' " +
		 * " order by contig_name, fmin, fmax"; AppContext.debug("sql query: " + sql);
		 * return executeSQL(sql);
		 */

		/*
		 * Set listLocus = new LinkedHashSet(); Set sortsetpos = new TreeSet(posset);
		 * Iterator<BigDecimal> it=sortsetpos.iterator(); while(it.hasNext()) {
		 * BigDecimal pos = it.next(); listLocus.addAll( getLocusByRegion( contig,
		 * pos.longValue(), pos.longValue(), organism) ); } List list = new ArrayList();
		 * list.addAll(listLocus); return list;
		 */

		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItems");

		String mpm = "";
		String ppm = "";
		if (plusminus != null && plusminus.intValue() != 0) {
			mpm = "-" + plusminus;
			ppm = "+" + plusminus;
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select * from ( ");
		sql.append(
				"select distinct f.feature_id, f.name name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(f3.value,1000,1) notes, f.organism_id, o.common_name "
						+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,  " + AppContext.getDefaultSchema()
						+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, "
						+ AppContext.getDefaultSchema() + ".cvterm cvtype, ");

		sql.append("  (select distinct column_value pos from table(sys.odcinumberlist(" + AppContext.toCSV(posset)
				+ "))) postable, ");

		sql.append(
				" " + AppContext.getDefaultSchema() + ".feature f" + " left join ( select f2.feature_id, fp.value from "
						+ AppContext.getDefaultSchema() + ".featureprop fp, " + AppContext.getDefaultSchema()
						+ ".cvterm cv, " + AppContext.getDefaultSchema() + ".feature f2"
						+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
						+ " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id ");

		/*
		 * + " and ((fl.fmin>=" + start + " and fl.fmax<=" + end + ") " +
		 * " or (fl.fmin<=" + start + " and fl.fmax>=" + end +") " + " or (fl.fmin<=" +
		 * start + " and fl.fmax>=" + start +") " + " or (fl.fmin<=" + end +
		 * " and fl.fmax>=" + end +") " + " )" ;
		 */

		String cont = contig.toLowerCase();
		if (organism.equals(AppContext.getDefaultOrganism())) {
			cont = cont.replace("r0", "r");
		}

		sql.append("  and lower(fsrc.name)='" + cont + "' and postable.pos between (fl.fmin" + mpm + ") and (fl.fmax"
				+ ppm + ")");

		/*
		 * Iterator<String> itPoslist = AppContext.setSlicerIds(new
		 * HashSet(posset)).iterator(); while(itPoslist.hasNext()) {
		 * sql.append(" pos in (" + itPoslist.next() + ") "); if(itPoslist.hasNext())
		 * sql.append(" or "); }
		 */

		sql.append(" and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
				// + " and o.common_name='" + organism + "' "
				+ " and o.organism_id=" + listitemsdao.getOrganismByName(organism).getOrganismId()
				+ " )  order by contig_name, fmin, fmax");

		// AppContext.debug("sql query: " + sql);
		return executeSQL(sql.toString());

	}

	private List _getLocusByContigPositionsPostgres(String contig, Collection posset, String organism,
			Integer plusminus, String genemodel, Set featuretype) {
		return _getLocusByContigPositionsPostgres(contig, posset, organism, plusminus, genemodel, null, featuretype);
	}

	private List _getLocusByContigPositionsPostgres(String contig, Collection posset, String organism,
			Integer plusminus, String genemodel, String featuretype) {
		return _getLocusByContigPositionsPostgres(contig, posset, organism, plusminus, genemodel, featuretype, null);
	}

	private List _getLocusByContigPositionsPostgres(String contig, Collection posset, String organism,
			Integer plusminus, String genemodel, String featuretype, Set featureset) {
		return _getLocusByOneContigPositionsPostgres(contig, posset, organism, plusminus, genemodel, featuretype,
				featureset);
		/*
		 * if(contig.toLowerCase().equals("any")) {
		 * 
		 * List listall=new ArrayList(); Map<String,Set<BigDecimal>>
		 * mapchr2pos=MultiReferencePositionImpl.getMapContig2SNPPos(posset);
		 * Iterator<String> itChr=mapchr2pos.keySet().iterator(); while(itChr.hasNext())
		 * { String schr=itChr.next(); Set setpos=mapchr2pos.get(schr);
		 * listall.addAll(_getLocusByOneContigPositionsPostgres( schr, setpos, organism,
		 * plusminus, genemodel, featuretype, featureset)); } return listall;
		 * 
		 * } else return _getLocusByOneContigPositionsPostgres( contig, posset,
		 * organism, plusminus, genemodel, featuretype, featureset);
		 */
	}

	private List _getLocusByOneContigPositionsPostgres(String contig, Collection posset, String organism,
			Integer plusminus, String genemodel, String featuretype, Set featureset) {

		String featuretypecons = "";
		if (featureset != null && featureset.size() == 1) {
			featuretype = (String) featureset.iterator().next();
		}

		// if(featuretype!=null && featuretype.equals(GenomicsFacade.FEATURETYPE_GENE))
		// return _getLocusByContigPositionsPostgres( contig, posset, organism,
		// plusminus);

		if (featuretype != null)
			featuretypecons = " and lower(cvtype.name)='" + featuretype.toLowerCase() + "' ";
		else if (featureset != null && !featureset.isEmpty()) {
			featuretypecons = " and lower(cvtype.name) in (";
			Iterator<String> itTypes = featureset.iterator();
			while (itTypes.hasNext()) {
				featuretypecons += "'" + ((String) itTypes.next()).toLowerCase() + "'";
				if (itTypes.hasNext())
					featuretypecons += ", ";
			}
			featuretypecons += ") ";
		}

		String fname = "";
		if (organism.equals(AppContext.getDefaultOrganism())) {
			if (genemodel != null) {
				if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC))
					fname = " and f.name like 'OsNippo%' ";
				else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)
						|| genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY)) {

					if (organism.equals(AppContext.JAPONICA_NIPPONBARE))
						fname = " and f.name like 'LOC_%' ";
					else
						fname = " and f.name like 'OsMH63_%' ";
				}
				if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)
						|| genemodel.equals(GenomicsFacade.GENEMODEL_RAP_ONLY))
					fname = " and (f.name like 'Os01%' or f.name like 'Os1%') ";
			}
		}

		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItems");

		String mpm = "";
		String ppm = "";
		if (plusminus != null && plusminus.intValue() != 0) {
			mpm = "-" + plusminus;
			ppm = "+" + plusminus;
		}

		StringBuffer sql = new StringBuffer();
		sql.append("select t.* from ( ");

		// posset=AppContext.convertPos2Position(posset);
		Map<String, Set<BigDecimal>> mapchr2pos = null;
		if (contig.toLowerCase().equals("any")) {
			mapchr2pos = MultiReferencePositionImpl.getMapContig2SNPPos(posset);
		} else {
			// mapchr2pos=MultiReferencePositionImpl.getMapContig2SNPPos(posset);
			posset = AppContext.convertPos2Position(posset);
			mapchr2pos = new HashMap(); // MultiReferencePositionImpl.getMapContig2SNPPos(posset);
			Iterator itPos = posset.iterator();
			Set s = new TreeSet();
			while (itPos.hasNext()) {
				Object o = itPos.next();
				if (o instanceof Position) {
					Position p = (Position) o;
					s.add(p.getPosition());
				} else
					s.add((BigDecimal) o);
			}
			mapchr2pos.put(contig, (Set<BigDecimal>) s);
		}

		Iterator<String> itChr = mapchr2pos.keySet().iterator();
		while (itChr.hasNext()) {
			String schr = itChr.next();
			Set _setpos = mapchr2pos.get(schr);

			// listall.addAll(_getLocusByOneContigPositionsPostgres( schr, setpos, organism,
			// plusminus, genemodel, featuretype, featureset));

			sql.append(
					"select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, substring(f3.value,1,1000) notes, f.organism_id, o.common_name,  cvtype.name feature_type "
							+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,  "
							+ AppContext.getDefaultSchema() + ".feature fsrc, " + AppContext.getDefaultSchema()
							+ ".organism o, " + AppContext.getDefaultSchema() + ".cvterm cvtype, ");

			// select t.column_value from (select unnest(ARRAY[" +
			// slicedset[iset].toString().replace("[", "").replace("]", "") + "])
			// column_value) t
			// select t.column_value from (select unnest(ARRAY[" +
			// slicedset[iset].toString().replace("[", "").replace("]", "") + "])
			// column_value) t

			// sql.append( " (select distinct column_value pos from
			// table(sys.odcinumberlist(" + AppContext.toCSV(posset)+ "))) postable, ");
			// sql.append( " (select distinct column_value pos from ( select postable.pos
			// from (select unnest(ARRAY[" + AppContext.toCSV(posset).replace("[",
			// "").replace("]", "") + "]) pos) postable, "); // table(sys.odcinumberlist(" +
			// AppContext.toCSV(posset)+ "))) postable, ");
			//
			// sql.append( " (select unnest(ARRAY[" + AppContext.toCSV(posset).replace("[",
			// "").replace("]", "") + "]) pos) postable, " );
			sql.append(" (select unnest(ARRAY[" + AppContext.toCSV(_setpos).replace("[", "").replace("]", "")
					+ "]) pos)  postable, ");

			sql.append(" " + AppContext.getDefaultSchema() + ".feature f"
					+ " left join ( select f2.feature_id, fp.value from " + AppContext.getDefaultSchema()
					+ ".featureprop fp, " + AppContext.getDefaultSchema() + ".cvterm cv, "
					+ AppContext.getDefaultSchema() + ".feature f2"
					+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
					+ " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id ");

			// String cont=contig.toLowerCase();
			String cont = schr.toLowerCase();
			if (organism.equals(AppContext.getDefaultOrganism())) {
				cont = cont.replace("r0", "r");
			}

			sql.append("  and lower(fsrc.name)='" + cont + "' and postable.pos between (fl.fmin" + mpm
					+ ") and (fl.fmax" + ppm + ")");

			sql.append(
					" and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id and cvtype.cvterm_id=f.type_id "
							+ featuretypecons
							// + " and lower(cvtype.name)='" + featuretype.toLowerCase() + "' "
							// + " and o.common_name='" + organism + "' "
							+ " and o.organism_id=" + listitemsdao.getOrganismByName(organism).getOrganismId() + fname);
			if (itChr.hasNext())
				sql.append(" UNION \n");
		}

		sql.append(" ) as t order by contig_name, fmin, fmax");

		// AppContext.debug("sql query: " + sql);
		// return executeSQL(sql.toString());
		return executeNativeSQL(sql.toString());
	}

	private List _getLocusByContigPositionsPostgres(String contig, Collection posset, String organism,
			Integer plusminus) {
		return this._getLocusByContigPositionsPostgres(contig, posset, organism, plusminus,
				GenomicsFacade.GENEMODEL_MSU7_ONLY, GenomicsFacade.FEATURETYPE_GENE);
		/*
		 * if(contig.toLowerCase().equals("any")) {
		 * 
		 * List listall=new ArrayList(); Map<String,Set<BigDecimal>>
		 * mapchr2pos=MultiReferencePositionImpl.getMapContig2SNPPos(posset);
		 * Iterator<String> itChr=mapchr2pos.keySet().iterator(); while(itChr.hasNext())
		 * { String schr=itChr.next(); Set setpos=mapchr2pos.get(schr);
		 * listall.addAll(_getLocusByOneContigPositionsPostgres( schr, setpos, organism,
		 * plusminus)); } return listall;
		 * 
		 * } else return _getLocusByOneContigPositionsPostgres( contig, posset,
		 * organism, plusminus);
		 */
	}

	// private List _getLocusByOneContigPositionsPostgres(String contig, Collection
	// posset, String organism, Integer plusminus) {
	//
	// posset=AppContext.convertPos2Position(posset);
	//
	// listitemsdao = (ListItemsDAO)AppContext.checkBean(listitemsdao, "ListItems");
	//
	// String mpm="";
	// String ppm="";
	// if(plusminus!=null && plusminus.intValue()!=0 ) {
	// mpm= "-" + plusminus;
	// ppm= "+" + plusminus;
	// }
	//
	// StringBuffer sql = new StringBuffer();
	// sql.append("select t.* from ( ");
	// sql.append( "select distinct f.feature_id, f.name \"name\", fl.fmin, fl.fmax,
	// fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name,
	// substring(f3.value,1,1000) notes, f.organism_id, o.common_name, cvtype.name
	// feature_type "
	// + " from " + AppContext.getDefaultSchema() + ".featureloc fl, " +
	// AppContext.getDefaultSchema() + ".feature fsrc, " +
	// AppContext.getDefaultSchema() + ".organism o, " +
	// AppContext.getDefaultSchema() + ".cvterm cvtype, ");
	//
	// //select t.column_value from (select unnest(ARRAY[" +
	// slicedset[iset].toString().replace("[", "").replace("]", "") + "])
	// column_value) t
	// //select t.column_value from (select unnest(ARRAY[" +
	// slicedset[iset].toString().replace("[", "").replace("]", "") + "])
	// column_value) t
	//
	// //sql.append( " (select distinct column_value pos from
	// table(sys.odcinumberlist(" + AppContext.toCSV(posset)+ "))) postable, ");
	// //sql.append( " (select distinct column_value pos from ( select postable.pos
	// from (select unnest(ARRAY[" + AppContext.toCSV(posset).replace("[",
	// "").replace("]", "") + "]) pos) postable, "); // table(sys.odcinumberlist(" +
	// AppContext.toCSV(posset)+ "))) postable, ");
	// sql.append( " (select unnest(ARRAY[" + AppContext.toCSV(posset).replace("[",
	// "").replace("]", "") + "]) pos) postable, " );
	//
	// sql.append(" " + AppContext.getDefaultSchema() + ".feature f"
	// + " left join ( select f2.feature_id, fp.value from " +
	// AppContext.getDefaultSchema() + ".featureprop fp, " +
	// AppContext.getDefaultSchema() + ".cvterm cv, " +
	// AppContext.getDefaultSchema() + ".feature f2"
	// + " where fp.feature_id=f2.feature_id and fp.type_id=cv.cvterm_id and
	// cv.name='Note') f3"
	// + " on f.feature_id=f3.feature_id"
	// + " where f.feature_id=fl.feature_id ");
	//
	// String cont=contig.toLowerCase();
	// if(organism.equals(AppContext.getDefaultOrganism())) {
	// cont=cont.replace("r0","r");
	// }
	//
	// sql.append(" and lower(fsrc.name)='" + cont + "' and postable.pos between
	// (fl.fmin" + mpm + ") and (fl.fmax" + ppm + ")");
	//
	//
	// sql.append(" and fsrc.feature_id=fl.srcfeature_id and
	// f.organism_id=o.organism_id "
	// + " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
	// //+ " and o.common_name='" + organism + "' "
	// + " and o.organism_id=" +
	// listitemsdao.getOrganismByName(organism).getOrganismId()
	// + " ) as t order by contig_name, fmin, fmax");
	//
	// //AppContext.debug("sql query: " + sql);
	// return executeNativeSQL(sql.toString());
	//
	// }

	private List _getLocusByContigPositions(String contig, Collection posset, String organism, String genemodel,
			Integer plusminus, String featuretype) {
		if (AppContext.isOracle())
			return _getLocusByContigPositionsOracle(contig, posset, organism, genemodel, plusminus);
		else if (AppContext.isPostgres()) {
			List l = _getLocusByContigPositionsPostgres(contig, posset, organism, genemodel, plusminus, true,
					featuretype);
			if (l.size() == 0) {
				return _getLocusByContigPositionsPostgres(contig, posset, organism, genemodel, plusminus, false,
						featuretype);
			} else
				return l;
		}
		return null;
	}

	private List _getLocusByContigPositionsOracle(String contig, Collection posset, String organism, String genemodel,
			Integer plusminus) {

		String locusmapping = "";
		String locuspref = "";
		if (genemodel == null || genemodel.equals(GenomicsFacade.GENEMODEL_ALL)
				|| !organism.equals(AppContext.REFERENCE_NIPPONBARE())) {
			return _getLocusByContigPositions(contig, posset, organism, plusminus, genemodel,
					GenomicsFacade.FEATURETYPE_GENE);
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC)) {
			locuspref = " where  flist.name like 'OsNippo%'";
			locusmapping = "  left join " + AppContext.getDefaultSchema() + ".locus_mapping lm on lm.name=flist.name ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)) {
			locuspref = " where  flist.name like 'LOC_%'";
			locusmapping = "  left join " + AppContext.getDefaultSchema()
					+ ".locus_mapping lm on catsearch(lm.msu7,flist.name,null)>0 ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)) {
			locuspref = " where  (flist.name like 'Os0%' or flist.name like 'Os1%') ";
			locusmapping = "  left join " + AppContext.getDefaultSchema()
					+ ".locus_mapping lm on catsearch(lm.rap_representative,flist.name,null)>0 ";
		}

		posset = AppContext.convertPos2Position(posset);

		/*
		 * List posset=new ArrayList(); Iterator<Position> itpos=possetobj.iterator();
		 * while(itpos.hasNext()) { posset.add( itpos.next().getPosition() ); }
		 */

		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItems");

		StringBuffer sql = new StringBuffer();

		sql.append(
				"select flist.* , lm.name iric, lm.msu7, lm.rap_representative rap_rep, lm.rap_predicted rap_pred, lm.fgenesh from (");

		sql.append(
				"select distinct f.feature_id, f.name name, fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, dbms_lob.substr(f3.value,1000,1) notes, f.organism_id, o.common_name "
						+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,  " + AppContext.getDefaultSchema()
						+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, "
						+ AppContext.getDefaultSchema() + ".cvterm cvtype, ");

		sql.append("  (select distinct column_value pos from table(sys.odcinumberlist(" + AppContext.toCSV(posset)
				+ "))) postable, ");

		sql.append(
				" " + AppContext.getDefaultSchema() + ".feature f" + " left join ( select f2.feature_id, fp.value from "
						+ AppContext.getDefaultSchema() + ".featureprop fp, " + AppContext.getDefaultSchema()
						+ ".cvterm cv, " + AppContext.getDefaultSchema() + ".feature f2"
						+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
						+ " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id ");

		String ppm = "";
		String mpm = "";
		if (plusminus != null && plusminus.intValue() != 0) {
			ppm = "+" + plusminus;
			mpm = "-" + plusminus;
		}

		String cont = contig.toLowerCase();
		if (organism.equals(AppContext.getDefaultOrganism())) {
			cont = cont.replace("chr0", "chr");
		}

		sql.append("  and lower(fsrc.name)='" + cont + "' and postable.pos between (fl.fmin" + mpm + ") and (fl.fmax"
				+ ppm + ")");

		sql.append(" and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
				// + " and o.common_name='" + organism + "' "
				+ " and o.organism_id=" + listitemsdao.getOrganismByName(organism).getOrganismId() + ") flist ");

		sql.append(locusmapping);
		sql.append(locuspref);
		sql.append(" order by contig_name, fmin, fmax");

		// AppContext.debug("sql query: " + sql);
		return executeSQLMerged(sql.toString());

	}

	private List _getLocusByContigPositionsPostgres(String contig, Collection posset, String organism, String genemodel,
			Integer plusminus, boolean exactString, String featuretype) {

		if (!featuretype.equals("gene")) {
			AppContext.debug("_getLocusByContigPositionsPostgres fo gene only");
			return null;
		}

		String locusmapping = "";
		String locuspref = "";
		String sqlselect = "select flist.* , lm.name iric, lm.msu7, lm.rap_representative rap_rep, lm.rap_predicted rap_pred, lm.fgenesh from (";
		if (genemodel == null || genemodel.equals(GenomicsFacade.GENEMODEL_ALL)
				|| !organism.equals(AppContext.REFERENCE_NIPPONBARE())) {
			return _getLocusByContigPositions(contig, posset, organism, plusminus, genemodel, featuretype);
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC)) {
			// locuspref=" and f.dbxref_id=dx.dbxref_id and dx.accession='osnippo:all_genes'
			// and dx.db_id=db.db_id and f.type_id in (select cvterm_id from cvterm where
			// name='gene') " ;
			locuspref = " where  flist.name like 'OsNippo%'";
			locusmapping = "  left join " + AppContext.getDefaultSchema() + ".locus_mapping lm on lm.name=flist.name ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7)) {
			locuspref = " where  flist.name like 'LOC_%'";
			// locuspref=" and f.dbxref_id=dx.dbxref_id and db.name='msu7_annot' and
			// dx.db_id=db.db_id and f.type_id in (select cvterm_id from cvterm where
			// name='gene') " ;
			if (exactString)
				locusmapping = "  left join " + AppContext.getDefaultSchema()
						+ ".locus_mapping lm on lm.msu7=flist.name ";
			else
				locusmapping = "  left join " + AppContext.getDefaultSchema()
						+ ".locus_mapping lm on lm.msu7 like '%' || flist.name || '%' ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP)) {
			locuspref = " where  (flist.name like 'Os0%' or flist.name like 'Os1%') ";
			// locuspref=" and f.dbxref_id=dx.dbxref_id and db.name
			// in('raprep_irgsp1_annot','rappred_irgsp1_annot') and dx.db_id=db.db_id and
			// f.type_id in (select cvterm_id from cvterm where name='gene') " ;
			if (exactString)
				locusmapping = "  left join " + AppContext.getDefaultSchema()
						+ ".locus_mapping lm on lm.rap_representative=flist.name ";
			else
				locusmapping = "  left join " + AppContext.getDefaultSchema()
						+ ".locus_mapping lm on lm.rap_representative like '%' || flist.name% || '%' ";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_IRIC_ONLY)) {
			locuspref = " where  flist.name like 'OsNippo%'";
			// locuspref=" and f.dbxref_id=dx.dbxref_id and dx.accession='osnippo:all_genes'
			// and dx.db_id=db.db_id and f.type_id in (select cvterm_id from cvterm where
			// name='gene') " ;
			sqlselect = "select flist.* , flist.name iric, '' msu7, '' rap_rep, '' rap_pred, '' fgenesh from (";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_MSU7_ONLY)) {
			locuspref = " where  flist.name like 'LOC_%'";
			// locuspref=" and f.dbxref_id=dx.dbxref_id and db.name='msu7_annot' and
			// dx.db_id=db.db_id and f.type_id in (select cvterm_id from cvterm where
			// name='gene') " ;
			sqlselect = "select flist.* , '' iric, flist.name msu7, '' rap_rep, '' rap_pred, '' fgenesh from (";
		} else if (genemodel.equals(GenomicsFacade.GENEMODEL_RAP_ONLY)) {
			locuspref = " where  (flist.name like 'Os0%' or flist.name like 'Os1%') ";
			// locuspref=" and f.dbxref_id=dx.dbxref_id and db.name
			// in('raprep_irgsp1_annot','rappred_irgsp1_annot') and dx.db_id=db.db_id and
			// f.type_id in (select cvterm_id from cvterm where name='gene') " ;

			sqlselect = "select flist.* , '' iric, '' msu7, flist.name rap_rep, '' rap_pred, '' fgenesh from (";
		}

		posset = AppContext.convertPos2Position(posset);

		/*
		 * List posset=new ArrayList(); Iterator<Position> itpos=possetobj.iterator();
		 * while(itpos.hasNext()) { posset.add( itpos.next().getPosition() ); }
		 */

		listitemsdao = (ListItemsDAO) AppContext.checkBean(listitemsdao, "ListItems");

		StringBuffer sql = new StringBuffer();

		// sql.append( "select flist.* , lm.name iric, lm.msu7, lm.rap_representative
		// rap_rep, lm.rap_predicted rap_pred, lm.fgenesh from (");
		sql.append(sqlselect);

		/*
		 * sql.append(
		 * "select distinct f.feature_id, f.name , fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, substring(f3.value,1,1000) notes, f.organism_id, o.common_name, cvtype.name feature_type "
		 * + " from " + AppContext.getDefaultSchema() + ".featureloc fl,  " +
		 * AppContext.getDefaultSchema() + ".feature fsrc, " +
		 * AppContext.getDefaultSchema() + ".organism o, " +
		 * AppContext.getDefaultSchema() + ".cvterm cvtype, ");
		 * 
		 * //sql.append(
		 * "  (select distinct column_value pos from table(sys.odcinumberlist(" +
		 * AppContext.toCSV(posset)+ "))) postable, "); sql.append(
		 * " (select unnest(ARRAY[" + AppContext.toCSV(posset).replace("[",
		 * "").replace("]", "") + "]) pos)  postable, " );
		 * 
		 * sql.append(" " + AppContext.getDefaultSchema() + ".feature f" +
		 * " left join ( select f2.feature_id, fp.value from " +
		 * AppContext.getDefaultSchema() + ".featureprop fp, " +
		 * AppContext.getDefaultSchema() + ".cvterm cv, " +
		 * AppContext.getDefaultSchema() + ".feature f2" +
		 * " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
		 * + " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id ");
		 */

		sql.append(
				"select distinct f.feature_id, f.name , fl.fmin, fl.fmax, fl.strand, fsrc.feature_id contig_id, fsrc.uniquename contig_name, substring(f3.value,1,1000) notes, f.organism_id, o.common_name, cvtype.name feature_type "
						// + " from " + AppContext.getDefaultSchema() + ".db, " +
						// AppContext.getDefaultSchema() + ".dbxref dx, " +
						// AppContext.getDefaultSchema() + ".featureloc fl, " +
						// AppContext.getDefaultSchema() + ".feature fsrc, " +
						// AppContext.getDefaultSchema() + ".organism o, " +
						// AppContext.getDefaultSchema() + ".cvterm cvtype, ");
						+ " from " + AppContext.getDefaultSchema() + ".featureloc fl,  " + AppContext.getDefaultSchema()
						+ ".feature fsrc, " + AppContext.getDefaultSchema() + ".organism o, "
						+ AppContext.getDefaultSchema() + ".cvterm cvtype, ");

		// sql.append( " (select distinct column_value pos from
		// table(sys.odcinumberlist(" + AppContext.toCSV(posset)+ "))) postable, ");
		sql.append(" (select unnest(ARRAY[" + AppContext.toCSV(posset).replace("[", "").replace("]", "")
				+ "]) pos)  postable, ");

		sql.append(
				" " + AppContext.getDefaultSchema() + ".feature f" + " left join ( select f2.feature_id, fp.value from "
						+ AppContext.getDefaultSchema() + ".featureprop fp, " + AppContext.getDefaultSchema()
						+ ".cvterm cv, " + AppContext.getDefaultSchema() + ".feature f2"
						+ " where fp.feature_id=f2.feature_id and  fp.type_id=cv.cvterm_id and cv.name='Note') f3"
						+ " on f.feature_id=f3.feature_id" + " where f.feature_id=fl.feature_id ");

		// sql.append(locuspref);

		String ppm = "";
		String mpm = "";
		if (plusminus != null && plusminus.intValue() != 0) {
			ppm = "+" + plusminus;
			mpm = "-" + plusminus;
		}

		String cont = contig.toLowerCase();
		if (organism.equals(AppContext.getDefaultOrganism())) {
			cont = cont.replace("chr0", "chr");
		}

		sql.append("  and lower(fsrc.name)='" + cont + "' and postable.pos between (fl.fmin" + mpm + ") and (fl.fmax"
				+ ppm + ")");

		sql.append(" and fsrc.feature_id=fl.srcfeature_id and f.organism_id=o.organism_id "
				+ " and cvtype.name='gene' and cvtype.cvterm_id=f.type_id "
				// + " and o.common_name='" + organism + "' "
				+ " and o.organism_id=" + listitemsdao.getOrganismByName(organism).getOrganismId() + ") flist ");

		sql.append(locusmapping);
		sql.append(locuspref);
		sql.append(" order by contig_name, fmin, fmax");

		// AppContext.debug("sql query: " + sql);
		return executeSQLMerged(sql.toString());

	}

}

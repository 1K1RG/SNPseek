package org.irri.iric.portal.chado.oracle.domain;

import java.math.BigDecimal;
import java.util.Set;

import org.irri.iric.ds.chado.dao.LocusDAO;
import org.irri.iric.ds.chado.domain.model.VLocusNotes;
import org.skyway.spring.util.dao.JpaDao;
import org.springframework.dao.DataAccessException;

/**
 * DAO to manage VLocusNotes entities.
 * 
 */
public interface VLocusNotesDAO extends JpaDao<VLocusNotes>, LocusDAO {

	/**
	 * JPQL Query - findVLocusNotesByNameContaining
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByNameContaining(String name) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByNameContaining
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByNameContaining(String name, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByStrand
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByStrand(java.math.BigDecimal strand) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByStrand
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByStrand(BigDecimal strand, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findAllVLocusNotess
	 *
	 */
	public Set<VLocusNotes> findAllVLocusNotess() throws DataAccessException;

	/**
	 * JPQL Query - findAllVLocusNotess
	 *
	 */
	public Set<VLocusNotes> findAllVLocusNotess(int startResult, int maxRows) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByCommonName
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByCommonName(String commonName) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByCommonName
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByCommonName(String commonName, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByFmax
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByFmax(java.math.BigDecimal fmax) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByFmax
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByFmax(BigDecimal fmax, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByName
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByName(String name_1) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByName
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByName(String name_1, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByCommonNameContaining
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByCommonNameContaining(String commonName_1) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByCommonNameContaining
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByCommonNameContaining(String commonName_1, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByFmin
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByFmin(java.math.BigDecimal fmin) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByFmin
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByFmin(BigDecimal fmin, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByContigName
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByContigName(String contigName) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByContigName
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByContigName(String contigName, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByContigId
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByContigId(java.math.BigDecimal contigId) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByContigId
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByContigId(BigDecimal contigId, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByContigNameContaining
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByContigNameContaining(String contigName_1) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByContigNameContaining
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByContigNameContaining(String contigName_1, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByOrganismId
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByOrganismId(java.math.BigDecimal organismId) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByOrganismId
	 *
	 */
	public Set<VLocusNotes> findVLocusNotesByOrganismId(BigDecimal organismId, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByFeatureId
	 *
	 */
	public VLocusNotes findVLocusNotesByFeatureId(BigDecimal featureId) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByFeatureId
	 *
	 */
	public VLocusNotes findVLocusNotesByFeatureId(BigDecimal featureId, int startResult, int maxRows)
			throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByPrimaryKey
	 *
	 */
	public VLocusNotes findVLocusNotesByPrimaryKey(BigDecimal featureId_1) throws DataAccessException;

	/**
	 * JPQL Query - findVLocusNotesByPrimaryKey
	 *
	 */
	public VLocusNotes findVLocusNotesByPrimaryKey(BigDecimal featureId_1, int startResult, int maxRows)
			throws DataAccessException;

}
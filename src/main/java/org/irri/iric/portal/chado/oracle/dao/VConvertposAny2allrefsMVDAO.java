package org.irri.iric.portal.chado.oracle.dao;

import org.irri.iric.ds.chado.dao.SnpsAllvarsMultirefsPosDAO;
import org.irri.iric.ds.chado.domain.model.VConvertposAny2allrefsMV;
import org.irri.iric.portal.dao.MultipleReferenceConverterDAO;
import org.skyway.spring.util.dao.JpaDao;

/**
 * DAO to manage VConvertposAny2allrefs entities.
 * 
 */
public interface VConvertposAny2allrefsMVDAO
		extends JpaDao<VConvertposAny2allrefsMV>, MultipleReferenceConverterDAO, SnpsAllvarsMultirefsPosDAO {
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByIr64Position
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByIr64Position(BigDecimal ir64Position) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByIr64Position
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByIr64Position(BigDecimal ir64Position, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromOrganismId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromOrganismId(java.math.BigDecimal
	// fromOrganismId) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromOrganismId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromOrganismId(BigDecimal fromOrganismId, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromRefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromRefcallContaining(String fromRefcall) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromRefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromRefcallContaining(String fromRefcall, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByNbRefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByNbRefcallContaining(String nbRefcall) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByNbRefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByNbRefcallContaining(String nbRefcall, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByKasalathContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByKasalathContigId(BigDecimal kasalathContigId)
	// throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByKasalathContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByKasalathContigId(BigDecimal kasalathContigId, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByDj123Position
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByDj123Position(BigDecimal dj123Position) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByDj123Position
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByDj123Position(BigDecimal dj123Position, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByPrimaryKey
	// *
	// */
	// public VConvertposAny2allrefs
	// findVConvertposAny2allrefsByPrimaryKey(BigDecimal snpFeatureId) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByPrimaryKey
	// *
	// */
	// public VConvertposAny2allrefs
	// findVConvertposAny2allrefsByPrimaryKey(BigDecimal snpFeatureId, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByDj123ContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByDj123ContigId(BigDecimal dj123ContigId) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByDj123ContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByDj123ContigId(BigDecimal dj123ContigId, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByNbContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByNbContigId(BigDecimal nbContigId) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByNbContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByNbContigId(BigDecimal nbContigId, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByRice9311RefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByRice9311RefcallContaining(String rice9311Refcall)
	// throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByRice9311RefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByRice9311RefcallContaining(String rice9311Refcall,
	// int startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByIr64Refcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByIr64Refcall(String ir64Refcall) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByIr64Refcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByIr64Refcall(String ir64Refcall, int startResult,
	// int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByAlleleIndex
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByAlleleIndex(BigDecimal alleleIndex) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByAlleleIndex
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByAlleleIndex(BigDecimal alleleIndex, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByKasalathRefcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByKasalathRefcall(String kasalathRefcall) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByKasalathRefcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByKasalathRefcall(String kasalathRefcall, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByDj123Refcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByDj123Refcall(String dj123Refcall) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByDj123Refcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByDj123Refcall(String dj123Refcall, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByIr64RefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByIr64RefcallContaining(String ir64Refcall_1)
	// throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByIr64RefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByIr64RefcallContaining(String ir64Refcall_1, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByRice9311Refcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByRice9311Refcall(String rice9311Refcall_1) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByRice9311Refcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByRice9311Refcall(String rice9311Refcall_1, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findAllVConvertposAny2allrefss
	// *
	// */
	// public Set<VConvertposAny2allrefsMV> findAllVConvertposAny2allrefss() throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findAllVConvertposAny2allrefss
	// *
	// */
	// public Set<VConvertposAny2allrefsMV> findAllVConvertposAny2allrefss(int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByNbPosition
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByNbPosition(BigDecimal nbPosition) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByNbPosition
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByNbPosition(BigDecimal nbPosition, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByKasalathRefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByKasalathRefcallContaining(String
	// kasalathRefcall_1) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByKasalathRefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByKasalathRefcallContaining(String
	// kasalathRefcall_1, int startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByRice9311Position
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByRice9311Position(BigDecimal rice9311Position)
	// throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByRice9311Position
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByRice9311Position(BigDecimal rice9311Position, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByRice9311ContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByRice9311ContigId(BigDecimal rice9311ContigId)
	// throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByRice9311ContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByRice9311ContigId(BigDecimal rice9311ContigId, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsBySnpFeatureId
	// *
	// */
	// public VConvertposAny2allrefs
	// findVConvertposAny2allrefsBySnpFeatureId(BigDecimal snpFeatureId_1) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsBySnpFeatureId
	// *
	// */
	// public VConvertposAny2allrefs
	// findVConvertposAny2allrefsBySnpFeatureId(BigDecimal snpFeatureId_1, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByNbRefcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByNbRefcall(String nbRefcall_1) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByNbRefcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByNbRefcall(String nbRefcall_1, int startResult,
	// int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByOrganismId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByOrganismId(BigDecimal organismId) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByOrganismId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByOrganismId(BigDecimal organismId, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromContigId(BigDecimal fromContigId) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromContigId(BigDecimal fromContigId, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByDj123RefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByDj123RefcallContaining(String dj123Refcall_1)
	// throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByDj123RefcallContaining
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByDj123RefcallContaining(String dj123Refcall_1, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromPosition
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromPosition(BigDecimal fromPosition) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromPosition
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromPosition(BigDecimal fromPosition, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByKasalathPosition
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByKasalathPosition(BigDecimal kasalathPosition)
	// throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByKasalathPosition
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByKasalathPosition(BigDecimal kasalathPosition, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByIr64ContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByIr64ContigId(BigDecimal ir64ContigId) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByIr64ContigId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByIr64ContigId(BigDecimal ir64ContigId, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromRefcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromRefcall(String fromRefcall_1) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByFromRefcall
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByFromRefcall(String fromRefcall_1, int
	// startResult, int maxRows) throws DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByTypeId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByTypeId(BigDecimal typeId) throws
	// DataAccessException;
	//
	// /**
	// * JPQL Query - findVConvertposAny2allrefsByTypeId
	// *
	// */
	// public Set<VConvertposAny2allrefsMV>
	// findVConvertposAny2allrefsByTypeId(BigDecimal typeId, int startResult, int
	// maxRows) throws DataAccessException;

}
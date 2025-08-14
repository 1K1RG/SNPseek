package org.irri.iric.portal.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Session;
//import org.irri.iric.portal.chado.domain.MismatchCount;

public class DAOLongQueryProcessor {

	private String requestid;
	private EntityManager entityManager;

	public DAOLongQueryProcessor(EntityManager em, String requestid) {
		super();
		
		this.requestid = requestid;
		this.entityManager = em;
	}

	private void executeSQL(String sql) {
		// System.out.println("executing :" + sql);
		// log.info("executing :" + sql);
		getSession().createSQLQuery(sql).list();
	}

	private Session getSession() {
		return entityManager.unwrap(Session.class);
	}

	public List executeQuery(Query query) {
		if (requestid != null) {
			executeSQL("call DBMS_APPLICATION_INFO.set_action(action_name => '" + requestid + "')");
		}
		List resultlist = query.getResultList();
		if (requestid != null) {
			executeSQL("call DBMS_APPLICATION_INFO.set_action(null)");
		}
		return resultlist;
	}

}

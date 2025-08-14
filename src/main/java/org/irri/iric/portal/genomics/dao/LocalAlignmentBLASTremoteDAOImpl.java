package org.irri.iric.portal.genomics.dao;

import java.util.List;

import org.irri.iric.portal.dao.LocalAlignmentDAO;
import org.irri.iric.portal.genomics.LocalAlignmentQuery;
import org.springframework.stereotype.Repository;

@Repository("LocalAlignmentDAORemoteBlast")
public class LocalAlignmentBLASTremoteDAOImpl implements LocalAlignmentDAO {

	@Override
	public List alignWithDB(LocalAlignmentQuery query) throws Exception {
		

		//
		// //LocalClient wsclient = new LocalClient("http://" + AppContext.getHostname()
		// + "/iric-portal-dev");
		//
		// LocalClient wsclient=new LocalClient(AppContext.getBlastServer());
		// /*
		// if(AppContext.isLocalhost() || AppContext.isPollux())
		// wsclient = new LocalClient(AppContext "http://pollux:8080/iric-portal-dev");
		// else
		// wsclient = new LocalClient("http://pollux:8080/iric-portal-dev");
		// */
		//
		// List listresult = wsclient.getBlast(query);
		// AppContext.debug("ws result:" + listresult.size());
		// return listresult;

		return null;
	}

}

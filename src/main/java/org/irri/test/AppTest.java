package org.irri.test;

import java.math.BigDecimal;
import java.util.List;

import org.irri.iric.ds.chado.dao.ScaffoldDAO;
import org.irri.iric.ds.chado.dao.SequenceDAO;
import org.irri.iric.ds.chado.dao.UserDAO;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;



public class AppTest {

	
	@Test
	public void testGetStockSampleByDataset() {

		ApplicationContext context = new ClassPathXmlApplicationContext("LOCAL_applicationContext-business.xml");
		ScaffoldDAO ssDs = (ScaffoldDAO) context.getBean("ScaffoldDAO");

		List result = ssDs.getScaffolds(new BigDecimal(9));

		System.out.println("result size" + result.size());

	}
	
	
	public void testUsert() {

		ApplicationContext context = new ClassPathXmlApplicationContext("LOCAL_applicationContext-business.xml");
		UserDAO ssDs = (UserDAO) context.getBean("UserDAO");

		org.irri.iric.ds.chado.domain.model.User user = new org.irri.iric.ds.chado.domain.model.User();
		user.setEmail("lhendrixbarbasowza3@gmail.com");
		user.setUsername("lhendrixasbarbozwa3@gmail.com");
		user.setPasswordHash("test");
		
		ssDs.save(user);
		
	

		
	}

	public void testGetSequenceDao() {

		ApplicationContext context = new ClassPathXmlApplicationContext("LOCAL_applicationContextDs2.xml");
		
		SequenceDAO ssDs = (SequenceDAO) context.getBean("FeatureDAO");

		try {
			String result = ssDs.getSubSequence("", 1, 59, 1);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	
	
	

}

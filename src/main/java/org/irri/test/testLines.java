package org.irri.test;

import java.io.InputStream;
import java.net.URL;

import org.irri.iric.portal.AppContext;
import org.junit.Test;

public class testLines {

	@Test
	public void testGetStockSampleByDataset() {

		InputStream is = AppContext.class.getResourceAsStream("/test_prop.xlsx");
		String filename = is.toString();
		
		URL url = getClass().getResource("/test_prop.xlsx");
		
		System.out.println("Filename: "+ url.getPath());

	}
}

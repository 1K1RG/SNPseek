package com.github.jmchilton.blend4j.galaxy;

public class MyGalaxyInstanceFactory {


	  public static GalaxyInstance get(final String url, final String apiKey, int connectTimeoutSec, 
			  int readTimeoutSec ) {
		    return get(new MyWebResourceFactoryImpl(url, apiKey,connectTimeoutSec,readTimeoutSec));
		  }

	  public static GalaxyInstance get(final String url, final String apiKey) {
	    return get(new MyWebResourceFactoryImpl(url, apiKey));
	  }

	  public static GalaxyInstance get(final String url, final String apiKey, final boolean debug) {
	    return get(new MyWebResourceFactoryImpl(url, apiKey, debug));
	  }
	  
	  public static GalaxyInstance getFromCredentials(final String url, final String email, final String password) {
	    return get(new GalaxyAuthWebResourceFactoryImpl(url, email, password));
	  }

	  public static GalaxyInstance getFromCredentials(final String url, final String email, final String password, final boolean debug) {
	    return get(new GalaxyAuthWebResourceFactoryImpl(url, email, password, debug));
	  }

	  public static GalaxyInstance get(final WebResourceFactory webResourceFactory) {
	    return new GalaxyInstanceImpl(webResourceFactory);
	  }
}

package ch.winfor.monopoly.res;

import java.io.InputStream;

/**
 * class providing functions to read data from this package
 * 
 * @author Nicolas Winkler
 * 
 */
public class Ressources {

	/**
	 * creates a stream ressource of a stored file
	 * 
	 * @param path
	 *            the name of the file
	 * @return an {@link InputStream} streamimg the content of this file
	 */
	public static InputStream getRessource(String path) {
		return Ressources.class.getResourceAsStream(path);
	}
}

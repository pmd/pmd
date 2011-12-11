/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.build;


/**
 * @author Romain PELISSE, belaran@gmail.com
 *
 */
public class PmdBuildException extends Exception {

	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 1L;

	public PmdBuildException(String message){
		super(message);
	}

	public PmdBuildException(Throwable e) {
		super(e);
	}
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

/**
 * 
 * @author Romain PELISSE belaran@gmail.com
 */
public class FortranLanguage extends AbstractLanguage {
	public FortranLanguage() {
		super(new FortranTokenizer(), ".for", ".f", ".f66", ".f77", ".f90");
	}
}

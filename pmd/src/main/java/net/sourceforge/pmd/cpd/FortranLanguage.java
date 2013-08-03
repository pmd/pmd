/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 * @author Romain PELISSE belaran@gmail.com
 */
package net.sourceforge.pmd.cpd;

public class FortranLanguage extends AbstractLanguage {
	public FortranLanguage() {
		super(new FortranTokenizer(), ".for", ".f", ".f66", ".f77", ".f90");
	}
}

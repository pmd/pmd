/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

/**
 * Stores LanguageVersion terse name value.
 */
public class Version {
    private String terseName;

    public void addText(String text) {
	this.terseName = text;
    }

    public String getTerseName() {
	return terseName;
    }
}

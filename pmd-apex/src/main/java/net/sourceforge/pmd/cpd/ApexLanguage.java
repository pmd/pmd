/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.util.Properties;

public class ApexLanguage extends AbstractLanguage {
	
	public ApexLanguage() {
        super("Apex", "apex", new ApexTokenizer(), ".class");
    }
}

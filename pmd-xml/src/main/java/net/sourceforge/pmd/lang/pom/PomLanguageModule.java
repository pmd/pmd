/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.pom;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.xml.XmlHandler;
import net.sourceforge.pmd.lang.xml.rule.XmlRuleChainVisitor;

public class PomLanguageModule extends BaseLanguageModule {
    public static final String NAME = "Maven POM";
    public static final String TERSE_NAME = "pom";

    public PomLanguageModule() {
        super(NAME, null, TERSE_NAME, XmlRuleChainVisitor.class, "pom");
        addVersion("", new XmlHandler(), true);
    }
}

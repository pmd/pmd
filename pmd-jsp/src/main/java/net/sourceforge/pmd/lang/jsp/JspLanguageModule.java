package net.sourceforge.pmd.lang.jsp;

import net.sourceforge.pmd.lang.BaseLanguageModule;
import net.sourceforge.pmd.lang.jsp.rule.JspRuleChainVisitor;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JspLanguageModule extends BaseLanguageModule {

    public static final String NAME = "Java Server Pages";
    public static final String TERSE_NAME = "jsp";

    public JspLanguageModule() {
        super(NAME, "JSP", TERSE_NAME, JspRuleChainVisitor.class, "jsp");
        addVersion("", new JspHandler(), true);
    }

}

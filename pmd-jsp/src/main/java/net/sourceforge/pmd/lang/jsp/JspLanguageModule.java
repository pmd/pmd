/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.impl.SimpleLanguageModuleBase;

/**
 * Created by christoferdutz on 20.09.14.
 */
public class JspLanguageModule extends SimpleLanguageModuleBase {

    public static final String NAME = "Java Server Pages";
    public static final String TERSE_NAME = "jsp";
    @InternalApi
    public static final List<String> EXTENSIONS = listOf("jsp", "jspx", "jspf", "tag");

    public JspLanguageModule() {
        super(LanguageMetadata.withId(TERSE_NAME).name(NAME).shortName("JSP")
                              .extensions(EXTENSIONS.get(0), EXTENSIONS.toArray(new String[0]))
                              .addVersion("2")
                              .addDefaultVersion("3"),
              new JspHandler());
    }

}

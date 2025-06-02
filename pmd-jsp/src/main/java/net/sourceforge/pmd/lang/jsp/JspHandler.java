/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.jsp;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.jsp.ast.JspParser;

/**
 * Implementation of LanguageVersionHandler for the JSP parser.
 *
 * @author pieter_van_raemdonck - Application Engineers NV/SA - www.ae.be
 */
public class JspHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public Parser getParser() {
        return new JspParser();
    }

}

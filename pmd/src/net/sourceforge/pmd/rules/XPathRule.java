/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Iterator;

/**
 * @author daniels
 *
 * To change this generated comment go to
 * Window>Preferences>Java>Code Generation>Code Template
 */
public class XPathRule extends AbstractRule {

    private XPath xpath;

    public Object visit(ASTCompilationUnit node, Object data) {
        try {
            init();
            for (Iterator iter = xpath.selectNodes(node).iterator(); iter.hasNext();) {
                SimpleNode actualNode = (SimpleNode) iter.next();
                RuleContext ctx = (RuleContext) data;
                String msg = getMessage();
                if (actualNode instanceof ASTVariableDeclaratorId && getBooleanProperty("pluginname")) {
                    msg = MessageFormat.format(msg, new Object[]{actualNode.getImage()});
                }
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, actualNode.getBeginLine(), msg));
            }
        } catch (JaxenException ex) {
            throwJaxenAsRuntime(ex);
        }
        return data;
    }

    private void init() throws JaxenException {
        if(xpath == null) {
            String path = getStringProperty("xpath");
            String subst = getStringProperty("subst");
            if(subst != null && subst.length() > 0) {
				path = MessageFormat.format(path, new String[] {subst});
            }
            xpath = new BaseXPath(path, new DocumentNavigator());
        }
    }

    private static void throwJaxenAsRuntime(final JaxenException ex) {
        throw new RuntimeException() {
            public void printStackTrace() {
                super.printStackTrace();
                ex.printStackTrace();
            }
            public void printStackTrace(PrintWriter writer) {
                super.printStackTrace(writer);
                ex.printStackTrace(writer);
            }
            public void printStackTrace(PrintStream stream) {
                super.printStackTrace(stream);
                ex.printStackTrace(stream);
            }
            public String getMessage() {
                return super.getMessage() + ex.getMessage();
            }
        };
    }
}

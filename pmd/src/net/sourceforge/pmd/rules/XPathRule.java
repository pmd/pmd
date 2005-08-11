/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPath;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class XPathRule extends AbstractRule {

    private XPath xpath;

    public Object visit(ASTCompilationUnit compilationUnit, Object data) {
        try {
            initializeXPathExpression();
            List results = xpath.selectNodes(compilationUnit);
            for (Iterator i = results.iterator(); i.hasNext();) {
                SimpleNode n = (SimpleNode) i.next();
                String msg = getMessage();
                if (n instanceof ASTVariableDeclaratorId && getBooleanProperty("pluginname")) {
                    msg = MessageFormat.format(msg, new Object[]{n.getImage()});
                }
                addViolation(data, n, msg);
            }
        } catch (JaxenException ex) {
            throwJaxenAsRuntime(ex);
        }
        return data;
    }

    private void initializeXPathExpression() throws JaxenException {
        if (xpath != null) {
            return;
        }
        xpath = new BaseXPath(getStringProperty("xpath"), new DocumentNavigator());
        if (properties.size() > 1) {
            SimpleVariableContext vc = new SimpleVariableContext();
            for (Iterator i = properties.entrySet().iterator(); i.hasNext();) {
                Entry e = (Entry) i.next();
                if (!"xpath".equals(e.getKey())) {
                    vc.setVariableValue((String)e.getKey(), e.getValue());
                }
            }
            xpath.setVariableContext(vc);
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

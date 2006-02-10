/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.sourceforge.pmd.CommonAbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import net.sourceforge.pmd.jaxen.MatchesFunction;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPath;

/**
 * Rule that tries to match an XPath expression against a DOM
 * view of the AST of a "compilation unit".
 * 
 * This rule needs a property "xpath". 
 */
public class XPathRule extends CommonAbstractRule {

    private XPath xpath;
    private boolean regexpFunctionRegistered;

    /**
     * Evaluate the AST with compilationUnit as root-node, against
     * the XPath expression found as property with name "xpath".
     * All matches are reported as violations.
     * 
     * @param compilationUnit the Node that is the root of the AST to be checked
     * @param data
     * @return
     */
    public void evaluate(Node compilationUnit, RuleContext data) {
        try {
            initializeXPathExpression();
            List results = xpath.selectNodes(compilationUnit);
            for (Iterator i = results.iterator(); i.hasNext();) {
                SimpleNode n = (SimpleNode) i.next();
                if (n instanceof ASTVariableDeclaratorId && getBooleanProperty("pluginname")) {
                    addViolation(data, n, n.getImage());
                } else {
                    addViolation(data, (SimpleNode)n, getMessage());
                }
            }
        } catch (JaxenException ex) {
            throwJaxenAsRuntime(ex);
        }
    }

    private void initializeXPathExpression() throws JaxenException {
        if (xpath != null) {
            return;
        }

        if (!regexpFunctionRegistered) {
            MatchesFunction.registerSelfInSimpleContext();
            regexpFunctionRegistered = true;
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

    /**
     * Apply the rule to all compilation units.
     */
	public void apply(List astCompilationUnits, RuleContext ctx) {
		for(Iterator i = astCompilationUnits.iterator(); i.hasNext(); ) {
			evaluate((Node) i.next(), ctx);
		}
	}
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.jaxen.DocumentNavigator;
import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Iterator;

public class XPathRule extends AbstractRule {

    private XPath xpath;

    public Object visit(ASTCompilationUnit node, Object data) {
        try {
            findPackageName(node);
            init();
            for (Iterator iter = xpath.selectNodes(node).iterator(); iter.hasNext();) {
                SimpleNode actualNode = (SimpleNode) iter.next();
                RuleContext ctx = (RuleContext) data;
                String msg = getMessage();
                if (actualNode instanceof ASTVariableDeclaratorId && getBooleanProperty("pluginname")) {
                    msg = MessageFormat.format(msg, new Object[]{actualNode.getImage()});
                }
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, actualNode, msg));
            }
        } catch (JaxenException ex) {
            throwJaxenAsRuntime(ex);
        }
        return data;
    }

    private void findPackageName(ASTCompilationUnit node) {
        for (int i=0; i<node.jjtGetNumChildren(); i++) {
            Node n = node.jjtGetChild(i);
            if (n instanceof ASTPackageDeclaration) {
                String name = ((SimpleNode)n.jjtGetChild(0)).getImage();
                setPackageName(name);
                break;
            }
        }
    }

    private void init() throws JaxenException {
        if (xpath == null) {
            String path = getStringProperty("xpath");
            String subst = getStringProperty("subst");
            if (subst != null && subst.length() > 0) {
                path = MessageFormat.format(path, new String[]{subst});
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

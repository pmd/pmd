/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTImportDeclaration;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTReferenceType;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Makes sure you close your database connections. It does this by
 * looking for code patterned like this:
 * <pre>
 *  Connection c = X;
 *  try {
 *   // do stuff, and maybe catch something
 *  } finally {
 *   c.close();
 *  }
 * </pre>
 */
public class CloseResource extends AbstractRule {

    private Set types = new HashSet();

    public Object visit(ASTCompilationUnit node, Object data) {
        if (!importsJavaSqlPackage(node)) {
            return data;
        }
        if (types.isEmpty()) {
            for (StringTokenizer st = new StringTokenizer(getStringProperty("types"), ","); st.hasMoreTokens();) {
                types.add(st.nextToken());
            }
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        List vars = node.findChildrenOfType(ASTLocalVariableDeclaration.class);
        List ids = new Vector();

        // find all variable references to Connection objects
        for (Iterator it = vars.iterator(); it.hasNext();) {
            ASTLocalVariableDeclaration var = (ASTLocalVariableDeclaration) it.next();
            ASTType type = (ASTType) var.jjtGetChild(0);

            if (type.jjtGetChild(0) instanceof ASTReferenceType) {
                ASTReferenceType ref = (ASTReferenceType) type.jjtGetChild(0);
                if (ref.jjtGetChild(0) instanceof ASTClassOrInterfaceType) {
                    ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType) ref.jjtGetChild(0);
                    if (types.contains(clazz.getImage())) {
                        ASTVariableDeclaratorId id = (ASTVariableDeclaratorId) var.jjtGetChild(1).jjtGetChild(0);
                        ids.add(id);
                    }
                }
            }
        }

        // if there are connections, ensure each is closed.
        for (int i = 0; i < ids.size(); i++) {
            ASTVariableDeclaratorId x = (ASTVariableDeclaratorId) ids.get(i);
            ensureClosed((ASTLocalVariableDeclaration) x.jjtGetParent().jjtGetParent(), x, data);
        }
        return data;
    }

    private void ensureClosed(ASTLocalVariableDeclaration var,
                              ASTVariableDeclaratorId id, Object data) {
        // What are the chances of a Connection being instantiated in a
        // for-loop init block? Anyway, I'm lazy!
        String target = id.getImage() + ".close";
        Node n = var;

        while (!((n = n.jjtGetParent()) instanceof ASTBlock)) ;

        ASTBlock top = (ASTBlock) n;

        List tryblocks = new Vector();
        top.findChildrenOfType(ASTTryStatement.class, tryblocks, true);

        boolean closed = false;

        // look for try blocks below the line the variable was
        // introduced and make sure there is a .close call in a finally
        // block.
        for (Iterator it = tryblocks.iterator(); it.hasNext();) {
            ASTTryStatement t = (ASTTryStatement) it.next();

            if ((t.getBeginLine() > id.getBeginLine()) && (t.hasFinally())) {
                ASTBlock f = (ASTBlock) t.getFinally().jjtGetChild(0);
                List names = new ArrayList();
                f.findChildrenOfType(ASTName.class, names, true);
                for (Iterator it2 = names.iterator(); it2.hasNext();) {
                    if (((ASTName) it2.next()).getImage().equals(target)) {
                        closed = true;
                    }
                }
            }
        }

        // if all is not well, complain
        if (!closed) {
            ASTType type = (ASTType) var.jjtGetChild(0);
            ASTReferenceType ref = (ASTReferenceType) type.jjtGetChild(0);
            ASTClassOrInterfaceType clazz = (ASTClassOrInterfaceType) ref.jjtGetChild(0);
            addViolation(data, id, clazz.getImage());
        }
    }

    private boolean importsJavaSqlPackage(ASTCompilationUnit node) {
        List nodes = node.findChildrenOfType(ASTImportDeclaration.class);
        for (Iterator i = nodes.iterator(); i.hasNext();) {
            ASTImportDeclaration n = (ASTImportDeclaration) i.next();
            if (n.getPackageName().startsWith("java.sql")) {
                return true;
            }
        }
        return false;
    }

}

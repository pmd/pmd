/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.migrating;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * This is a rule, that detects unnecessary casts when using Java 1.5 generics
 * and collections.
 *
 * <p>Example:</p>
 *
 * <pre>
 * List&lt;Double&gt; list = new ArrayList&lt;Double&gt;();
 * ...
 * Double d = (Double) list.get(0); //The cast is unnecessary on this typed array.
 * </pre>
 *
 * @see <a href=
 *      "http://sourceforge.net/p/pmd/discussion/188192/thread/276fd6f0">Java 5
 *      rules: Unnecessary casts/Iterators</a>
 */
// TODO This is not referenced by any RuleSet?
public class UnnecessaryCastRule extends AbstractJavaRule {

    private static Set<String> implClassNames = new HashSet<>();

    static {
        implClassNames.add("List");
        implClassNames.add("Set");
        implClassNames.add("Map");
        implClassNames.add("java.util.List");
        implClassNames.add("java.util.Set");
        implClassNames.add("java.util.Map");
        implClassNames.add("ArrayList");
        implClassNames.add("HashSet");
        implClassNames.add("HashMap");
        implClassNames.add("LinkedHashMap");
        implClassNames.add("LinkedHashSet");
        implClassNames.add("TreeSet");
        implClassNames.add("TreeMap");
        implClassNames.add("Vector");
        implClassNames.add("java.util.ArrayList");
        implClassNames.add("java.util.HashSet");
        implClassNames.add("java.util.HashMap");
        implClassNames.add("java.util.LinkedHashMap");
        implClassNames.add("java.util.LinkedHashSet");
        implClassNames.add("java.util.TreeSet");
        implClassNames.add("java.util.TreeMap");
        implClassNames.add("java.util.Vector");
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        return process(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        return process(node, data);
    }

    private Object process(Node node, Object data) {
        ASTClassOrInterfaceType cit = node.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (cit == null || !implClassNames.contains(cit.getImage())) {
            return data;
        }
        cit = cit.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (cit == null) {
            return data;
        }
        ASTVariableDeclaratorId decl = node.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        List<NameOccurrence> usages = decl.getUsages();
        for (NameOccurrence no : usages) {
            ASTName name = (ASTName) no.getLocation();
            Node n = name.getParent().getParent().getParent();
            if (n instanceof ASTCastExpression) {
                addViolation(data, n);
            }
        }
        return null;
    }
}

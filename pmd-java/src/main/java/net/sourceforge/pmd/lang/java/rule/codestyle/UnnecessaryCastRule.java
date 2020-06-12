/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeArgument;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

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
        implClassNames.add("Iterator");
        implClassNames.add("java.util.Iterator");
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        process(node, data);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        process(node, data);
        return super.visit(node, data);
    }

    private void process(Node node, Object data) {
        ASTClassOrInterfaceType collectionType = node.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        if (collectionType == null || !implClassNames.contains(collectionType.getImage())) {
            return;
        }
        ASTClassOrInterfaceType cit = getCollectionItemType(collectionType);
        if (cit == null) {
            return;
        }
        ASTVariableDeclaratorId decl = node.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
        List<NameOccurrence> usages = decl.getUsages();
        for (NameOccurrence no : usages) {
            ASTCastExpression castExpression = findCastExpression(no.getLocation());
            if (castExpression != null) {
                ASTClassOrInterfaceType castTarget = castExpression.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
                if (castTarget != null
                        && cit.getImage().equals(castTarget.getImage())
                        && !castTarget.hasDescendantOfType(ASTTypeArgument.class)) {
                    addViolation(data, castExpression);
                }
            }
        }
    }

    private ASTClassOrInterfaceType getCollectionItemType(ASTClassOrInterfaceType collectionType) {
        if (TypeHelper.isA(collectionType, Map.class)) {
            List<ASTClassOrInterfaceType> types = collectionType.findDescendantsOfType(ASTClassOrInterfaceType.class);
            if (types.size() >= 2) {
                return types.get(1); // the value type of the map
            }
        } else {
            return collectionType.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
        }
        return null;
    }

    private ASTCastExpression findCastExpression(ScopedNode usage) {
        Node n = usage.getNthParent(2);
        if (n instanceof ASTCastExpression) {
            return (ASTCastExpression) n;
        }
        n = n.getParent();
        if (n instanceof ASTCastExpression) {
            return (ASTCastExpression) n;
        }
        return null;
    }
}

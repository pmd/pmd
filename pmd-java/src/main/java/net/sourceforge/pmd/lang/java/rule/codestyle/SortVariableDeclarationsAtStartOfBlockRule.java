/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.conventionalEnumProperty;

import java.util.function.Function;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class SortVariableDeclarationsAtStartOfBlockRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<SortBy> SORT_BY =
            conventionalEnumProperty("sortBy", SortBy.class)
                    .desc("Enforce lexicographic sorting of variable declarations. When sorting by type declarations of the same type will be ordered by name.")
                    .defaultValue(SortBy.NAME)
                    .build();

    private static final PropertyDescriptor<Boolean> CASE_SENSITIVE_SORTING =
            PropertyFactory.booleanProperty("caseSensitiveSorting")
                    .desc("Use case sensitive sorting")
                    .defaultValue(false)
                    .build();

    private static final PropertyDescriptor<Boolean> IGNORE_FIELD_DECLARATIONS =
            PropertyFactory.booleanProperty("ignoreFieldDeclarations")
                    .desc("Do not check the ordering of field declarations.\n"
                            + "Mutually exclusive with ignoreLocalVariableDeclarations.")
                    .defaultValue(false)
                    .build();

    private static final PropertyDescriptor<Boolean> IGNORE_LOCAL_VARIABLE_DECLARATIONS =
            PropertyFactory.booleanProperty("ignoreLocalVariableDeclarations")
                    .desc("Do not check the ordering of local variable declarations.\n"
                            + "Mutually exclusive with ignoreFieldDeclarations.")
                    .defaultValue(false)
                    .build();


    private enum SortBy {
        NAME, TYPE
    }

    public SortVariableDeclarationsAtStartOfBlockRule() {
        super(ASTLocalVariableDeclaration.class,
                ASTFieldDeclaration.class);
        definePropertyDescriptor(SORT_BY);
        definePropertyDescriptor(CASE_SENSITIVE_SORTING);
        definePropertyDescriptor(IGNORE_FIELD_DECLARATIONS);
        definePropertyDescriptor(IGNORE_LOCAL_VARIABLE_DECLARATIONS);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        return processNode(node,
                data,
                getProperty(IGNORE_LOCAL_VARIABLE_DECLARATIONS),
                getPreviousDeclaration(node),
                (ASTLocalVariableDeclaration n) -> n.getVarIds().get(0).getName(),
                ASTLocalVariableDeclaration::getTypeNode);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        return processNode(node,
                data,
                getProperty(IGNORE_FIELD_DECLARATIONS),
                getPreviousDeclaration(node),
                (ASTFieldDeclaration n) -> n.getVarIds().get(0).getName(),
                ASTFieldDeclaration::getTypeNode);
    }


    private <T extends Node> Object processNode(T node,
                                                Object data,
                                                boolean isIgnored,
                                                T previousDeclaration,
                                                Function<T, String> getName,
                                                Function<T, TypeNode> getType) {
        if (isIgnored) {
            return data;
        }

        if (!isAtStartOfBlock(node)) {
            return data;
        }

        // it is the first declaration in the scope
        if (previousDeclaration == null) {
            return data;
        }

        String prevName = getName.apply(previousDeclaration);
        String nodeName = getName.apply(node);
        TypeNode prevType = getType.apply(previousDeclaration);
        TypeNode nodeType = getType.apply(node);

        if (isDeclarationOrderCorrect(prevType, prevName, nodeType, nodeName)) {
            return data;
        }

        asCtx(data).addViolation(node, nodeName, prevName,
                getProperty(SORT_BY).toString().toLowerCase());

        return data;
    }

    /*
    It takes the properties of two variables, 1 comes before 2 in the code and returns whether they are in the correct order
     */
    private boolean isDeclarationOrderCorrect(TypeNode type1, String name1, TypeNode type2, String name2) {
        if (getProperty(SORT_BY) == SortBy.TYPE) {
            String t1;
            String t2;

            if (type1 == null) {
                t1 = "var";
            } else {
                t1 = type1.getOriginalText().toString();
            }

            if (type2 == null) {
                t2 = "var";
            } else {
                t2 = type2.getOriginalText().toString();
            }

            if (!getProperty(CASE_SENSITIVE_SORTING)) {
                t1 = t1.toLowerCase();
                t2 = t2.toLowerCase();
            }

            // if they are the same then continue to name sorting
            if (!t2.equals(t1)) {
                // if either is var then the result is decided
                if ("var".equals(t1)) {
                    return false;
                }
                if ("var".equals(t2)) {
                    return true;
                }
                // if neither are var then they can be compared directly
                return t1.compareTo(t2) < 0;
            }

        }

        // either sort by is set to name or their types are the same and it has defaulted to name
        if (!getProperty(CASE_SENSITIVE_SORTING)) {
            name1 = name1.toLowerCase();
            name2 = name2.toLowerCase();
        }
        return name1.compareTo(name2) < 0;

    }


    /*
    Not necessarily the first thing in the block but at the start as defined in the description
    of the rule.
    Statements that are allowed before are currently:
    - other variable/field declarations
    - super()/this() calls
    (- ASTSwitchLabel because they are on the same level in the AST hierarchy as case contents)
     */
    private <T extends Node> boolean isAtStartOfBlock(T node) {
        Node sibling = node.getPreviousSibling();
        while (sibling != null) {
            if (!(sibling instanceof ASTLocalVariableDeclaration
                    || sibling instanceof ASTFieldDeclaration
                    || sibling instanceof ASTExplicitConstructorInvocation
                    || sibling instanceof ASTSwitchLabel)) {
                return false;
            }
            sibling = sibling.getPreviousSibling();
        }
        return true;
    }


    private ASTLocalVariableDeclaration getPreviousDeclaration(ASTLocalVariableDeclaration node) {
        JavaNode sibling = node.getPreviousSibling();
        while (sibling != null && !(sibling instanceof ASTLocalVariableDeclaration)) {
            sibling = sibling.getPreviousSibling();
        }
        return (ASTLocalVariableDeclaration) sibling;
    }

    private ASTFieldDeclaration getPreviousDeclaration(ASTFieldDeclaration node) {
        JavaNode sibling = node.getPreviousSibling();
        while (sibling != null && !(sibling instanceof ASTFieldDeclaration)) {
            sibling = sibling.getPreviousSibling();
        }
        return (ASTFieldDeclaration) sibling;
    }

}



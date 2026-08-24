/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.conventionalEnumProperty;

import java.util.Locale;

import net.sourceforge.pmd.lang.ast.internal.StreamImpl;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class LocalVariableDeclarationShouldBeAtStartOfBlockRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> REQUIRE_BEFORE_THIS_SUPER =
            PropertyFactory.booleanProperty("requireBeforeThisSuper")
                    .desc("Require that variable declaration comes before super(...) and this(...) calls. Always behaves as false in Java24 and below.")
                    .defaultValue(true)
                    .build();


    private static final PropertyDescriptor<SortBy> SORT_BY =
            conventionalEnumProperty("sortBy", SortBy.class)
                    .desc("Enforce lexicographic sorting of variable declarations. When sorting by type declarations of the same type will be ordered by name.")
                    .defaultValue(SortBy.NONE)
                    .build();

    private static final PropertyDescriptor<Boolean> CASE_SENSITIVE_SORTING =
            PropertyFactory.booleanProperty("caseSensitiveSorting")
                    .desc("Use case sensitive sorting")
                    .defaultValue(false)
                    .build();


    private enum SortBy {
        NAME, TYPE, NONE
    }

    public LocalVariableDeclarationShouldBeAtStartOfBlockRule() {
        super(ASTLocalVariableDeclaration.class);
        definePropertyDescriptor(REQUIRE_BEFORE_THIS_SUPER);
        definePropertyDescriptor(SORT_BY);
        definePropertyDescriptor(CASE_SENSITIVE_SORTING);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration declaration, Object data) {
        // rule does not apply to variables declared and initialized inside for loop initializers
        // it also does not apply to try-with-resources blocks
        if (isInStatementInitializer(declaration)) {
            return data;
        }

        String version = declaration.getLanguageVersion().getName().replace("Java ", "");
        String numericPart = version.replace("-preview", "");
        double versionNum = Double.parseDouble(numericPart);

        boolean declarationIsAtStartOfBlock = isAtStartOfBlock(declaration, versionNum < 25);

        // initialisation and start of block enforcement does not apply to variables declared with var keyword
        if (!declaration.isTypeInferred()) {
            declaration.children(ASTVariableDeclarator.class).forEach(child -> {
                if (child.hasInitializer()) {
                    String childName = child.getVarId().getName();
                    asCtx(data).addViolationWithMessage(child,
                            "Local variable `" + childName + "` is declared with initialization");
                }
                if (!declarationIsAtStartOfBlock) {
                    String childName = child.getVarId().getName();
                    asCtx(data).addViolationWithMessage(child,
                            "Local variable `" + childName + "` is not declared at start of block");
                }
            });
        }

        if (declarationIsAtStartOfBlock) {
            return flagSorting(declaration, data, getPreviousDeclaration(declaration));
        }
        return data;
    }

    private boolean isInStatementInitializer(ASTLocalVariableDeclaration declaration) {
        // this will stop working if a distinct scope can exist inside a new type of statement (not braces or case of switch)
        return !(declaration.getParent() instanceof ASTBlock
                || declaration.getParent() instanceof ASTSwitchFallthroughBranch);
    }

    private boolean isAtStartOfBlock(ASTLocalVariableDeclaration declaration, boolean ignoreThisSuper) {

        return StreamImpl.precedingSiblings(declaration).all(
            sibling -> {
                if (sibling instanceof ASTExplicitConstructorInvocation) {
                    // super or this
                    return !getProperty(REQUIRE_BEFORE_THIS_SUPER) || ignoreThisSuper;
                }
                return sibling instanceof ASTLocalVariableDeclaration || sibling instanceof ASTSwitchLabel;
            }
        );
    }

    /*
    Takes a declaration and raises a violation if it is out of order with the previous declaration
     */
    private Object flagSorting(ASTLocalVariableDeclaration node,
                                                Object data,
                                                ASTLocalVariableDeclaration previousDeclaration) {

        if (getProperty(SORT_BY) == SortBy.NONE) {
            return data;
        }

        // it is the first declaration in the scope
        if (previousDeclaration == null) {
            return data;
        }

        String prevName = previousDeclaration.getVarIds().get(0).getName();
        String nodeName = node.getVarIds().get(0).getName();
        TypeNode prevType = previousDeclaration.getTypeNode();
        TypeNode nodeType = node.getTypeNode();

        if (isDeclarationOrderCorrect(prevType, prevName, nodeType, nodeName)) {
            return data;
        }

        asCtx(data).addViolation(node, nodeName, prevName,
                getProperty(SORT_BY).toString().toLowerCase(Locale.ENGLISH));

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
                t1 = type1.getOriginalText().toString().replace(" ", "");
            }

            if (type2 == null) {
                t2 = "var";
            } else {
                t2 = type2.getOriginalText().toString().replace(" ", "");
            }

            if (!getProperty(CASE_SENSITIVE_SORTING)) {
                t1 = t1.toLowerCase(Locale.ENGLISH);
                t2 = t2.toLowerCase(Locale.ENGLISH);
            }

            // if they are the same then continue to name sorting
            if (!t2.equals(t1)) {
                // if either is var then the result is decided otherwise compare directly
                if ("var".equals(t1)) {
                    return false;
                }
                return "var".equals(t2) || t1.compareTo(t2) < 0;
            }
        }

        // either sort by is set to name or their types are the same and it has defaulted to name
        if (!getProperty(CASE_SENSITIVE_SORTING)) {
            name1 = name1.toLowerCase(Locale.ENGLISH);
            name2 = name2.toLowerCase(Locale.ENGLISH);
        }
        // non-strict inequality because variables with the same name but different capitalization are equal
        // but should not be flagged when caseSensitiveSorting is false
        return name1.compareTo(name2) <= 0;

    }

    private ASTLocalVariableDeclaration getPreviousDeclaration(ASTLocalVariableDeclaration node) {
        return (ASTLocalVariableDeclaration) StreamImpl
                .precedingSiblings(node)
                .filter(n -> n instanceof ASTLocalVariableDeclaration)
                .last();
    }
}

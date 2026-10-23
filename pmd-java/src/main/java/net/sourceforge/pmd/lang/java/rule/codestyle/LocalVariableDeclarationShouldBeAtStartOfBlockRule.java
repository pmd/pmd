/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.ast.internal.StreamImpl;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * @since 7.29.0
 */
public class LocalVariableDeclarationShouldBeAtStartOfBlockRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> REQUIRE_BEFORE_THIS_SUPER =
            PropertyFactory.booleanProperty("requireBeforeThisSuper")
                    .desc("Require that variable declaration comes before super(...) and this(...) calls. Possible only with Java25+. Always behaves as false in Java24 and below.")
                    .defaultValue(true)
                    .build();


    private static final PropertyDescriptor<SortBy> SORT_BY =
            PropertyFactory.conventionalEnumProperty("sortBy", SortBy.class)
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
        RuleContext ctx = (RuleContext) data;

        // rule does not apply to variables declared and initialized inside for loop initializers
        // it also does not apply to try-with-resources blocks
        if (isInStatementInitializer(declaration)) {
            return null;
        }
        // rule does not apply to variables declared with var keyword
        if (declaration.isTypeInferred()) {
            return null;
        }

        boolean java25orLater = declaration.getLanguageVersion().compareToVersion("25") >= 0;
        boolean declarationIsAtStartOfBlock = isAtStartOfBlock(declaration, java25orLater);

        declaration.children(ASTVariableDeclarator.class).forEach(child -> {
            if (child.hasInitializer()) {
                String childName = child.getVarId().getName();
                ctx.addViolationWithMessage(child,
                        "Local variable `{0}` is declared with initialization", childName);
            }
            if (!declarationIsAtStartOfBlock) {
                String childName = child.getVarId().getName();
                ctx.addViolationWithMessage(child,
                        "Local variable `{0}` is not declared at start of block", childName);
            }
        });

        if (declarationIsAtStartOfBlock) {
            flagSorting(declaration, ctx, getPreviousDeclaration(declaration));
        }
        return null;
    }

    private boolean isInStatementInitializer(ASTLocalVariableDeclaration declaration) {
        // this will stop working if a distinct scope can exist inside a new type of statement (not braces or case of switch)
        return !(declaration.getParent() instanceof ASTBlock
                || declaration.getParent() instanceof ASTSwitchFallthroughBranch);
    }

    private boolean isAtStartOfBlock(ASTLocalVariableDeclaration declaration, boolean flexibleCtorBodiesSupported) {
        boolean requireBeforeThisSuper = getProperty(REQUIRE_BEFORE_THIS_SUPER) && flexibleCtorBodiesSupported;

        if (requireBeforeThisSuper) {
            // when there are only local var declarations before,
            // then we are at the start of the block. This excludes any
            // super()/this() calls.
            return StreamImpl.precedingSiblings(declaration).all(sibling ->
                    sibling instanceof ASTLocalVariableDeclaration
                            || sibling instanceof ASTSwitchLabel);
        }

        // requireBeforeThisSuper==false: declarations must be after super() or this()

        return  // when there is no super()/this() call after, then we are at the start of the block.
                StreamImpl.followingSiblings(declaration)
                    .filterIs(ASTExplicitConstructorInvocation.class)
                    .isEmpty()
                // and when there are only local var declarations or super()/this() calls before,
                // then we are at the start of the block.
                && StreamImpl.precedingSiblings(declaration).all(sibling ->
                        sibling instanceof ASTLocalVariableDeclaration
                                || sibling instanceof ASTSwitchLabel
                                || sibling instanceof ASTExplicitConstructorInvocation);
    }

    /**
     * Takes a declaration and raises a violation if it is out of order with the previous declaration
     */
    private void flagSorting(ASTLocalVariableDeclaration node,
                                                RuleContext ctx,
                                                ASTLocalVariableDeclaration previousDeclaration) {

        if (getProperty(SORT_BY) == SortBy.NONE) {
            return;
        }

        // it is the first declaration in the scope
        if (previousDeclaration == null) {
            return;
        }

        String prevName = previousDeclaration.getVarIds().get(0).getName();
        String nodeName = node.getVarIds().get(0).getName();
        TypeNode prevType = previousDeclaration.getTypeNode();
        TypeNode nodeType = node.getTypeNode();

        if (isDeclarationOrderCorrect(prevType, prevName, nodeType, nodeName)) {
            return;
        }

        ctx.addViolation(node, nodeName, prevName, SORT_BY.serializer().toString(getProperty(SORT_BY)));
    }

    private String getSimpleTypeName(TypeNode typeNode) {
        JTypeDeclSymbol typeSymbol1 = typeNode.getTypeMirror().getSymbol();
        if (typeSymbol1 instanceof JClassSymbol) {
            if (((JClassSymbol) typeSymbol1).getArrayComponent() != null) {
                typeSymbol1 = ((JClassSymbol) typeSymbol1).getArrayComponent();
            }
            return typeSymbol1.getSimpleName();
        }
        return typeNode.getOriginalText().toString();
    }

    /**
     * Takes the properties of two variables, 1 comes before 2 in the code and returns whether they are in the correct order
     */
    private boolean isDeclarationOrderCorrect(TypeNode type1, String name1, TypeNode type2, String name2) {
        if (getProperty(SORT_BY) == SortBy.TYPE) {
            String t1 = getSimpleTypeName(type1);
            String t2 = getSimpleTypeName(type2);

            int result;
            if (getProperty(CASE_SENSITIVE_SORTING)) {
                result = t1.compareTo(t2);
            } else {
                result = t1.compareToIgnoreCase(t2);
            }

            // if they are not the same, then we are done; else continue with name sorting
            if (result != 0) {
                return result < 0;
            }
        }

        // either sort by is set to name or their types are the same and it has defaulted to name

        if (getProperty(CASE_SENSITIVE_SORTING)) {
            return name1.compareTo(name2) <= 0;
        }
        return name1.compareToIgnoreCase(name2) <= 0;
    }

    private ASTLocalVariableDeclaration getPreviousDeclaration(ASTLocalVariableDeclaration node) {
        return StreamImpl
                .precedingSiblings(node)
                .filterIs(ASTLocalVariableDeclaration.class)
                .filterNot(ASTLocalVariableDeclaration::isTypeInferred)
                .last();
    }
}

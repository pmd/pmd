/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

/**
 * Tests new java14 standard features.
 */
public class Java14Test {
    private final JavaParsingHelper java14 =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("14")
                                             .withResourceContext(Java14Test.class, "jdkversiontests/java14/");

    private final JavaParsingHelper java14p = java14.withDefaultVersion("14-preview");
    private final JavaParsingHelper java13 = java14.withDefaultVersion("13");
    private final JavaParsingHelper java13p = java14.withDefaultVersion("13-preview");

    /**
     * Tests switch expressions with yield.
     * The switch expressions have no changed between java 13-preview and 14, so behave exactly the same.
     */
    @Test
    public void switchExpressions() {
        parseAndCheckSwitchExpression(java13p);
        parseAndCheckSwitchExpression(java14);
        parseAndCheckSwitchExpression(java14p);
    }

    /**
     * In java13, switch expressions are only available with preview.
     */
    @Test(expected = ParseException.class)
    public void switchExpressions13ShouldFail() {
        parseAndCheckSwitchExpression(java13);
    }

    private void parseAndCheckSwitchExpression(JavaParsingHelper parser) {
        ASTCompilationUnit compilationUnit = parser.parseResource("SwitchExpressions.java");
        List<ASTSwitchStatement> switchStatements = compilationUnit.findDescendantsOfType(ASTSwitchStatement.class);
        assertEquals(2, switchStatements.size());

        assertTrue(switchStatements.get(0).getChild(0) instanceof ASTExpression);
        assertTrue(switchStatements.get(0).getChild(1) instanceof ASTSwitchLabeledExpression);
        assertTrue(switchStatements.get(0).getChild(1).getChild(0) instanceof ASTSwitchLabel);
        assertEquals(3, switchStatements.get(0).getChild(1).getChild(0).getNumChildren());
        assertTrue(switchStatements.get(0).getChild(2).getChild(0) instanceof ASTSwitchLabel);
        assertFalse(((ASTSwitchLabel) switchStatements.get(0).getChild(2).getChild(0)).isDefault());
        assertEquals(1, switchStatements.get(0).getChild(2).getChild(0).getNumChildren());

        assertTrue(switchStatements.get(1).getChild(3) instanceof ASTSwitchLabeledExpression);
        assertTrue(switchStatements.get(1).getChild(3).getChild(0) instanceof ASTSwitchLabel);
        assertTrue(((ASTSwitchLabel) switchStatements.get(1).getChild(3).getChild(0)).isDefault());

        List<ASTSwitchExpression> switchExpressions = compilationUnit.findDescendantsOfType(ASTSwitchExpression.class);
        assertEquals(4, switchExpressions.size());

        assertEquals(Integer.TYPE, switchExpressions.get(0).getType());
        assertEquals(4, switchExpressions.get(0).findChildrenOfType(ASTSwitchLabeledExpression.class).size());
        assertEquals(Integer.TYPE, switchExpressions.get(0).getFirstChildOfType(ASTSwitchLabeledExpression.class)
                                                    .getFirstChildOfType(ASTExpression.class).getType());

        assertTrue(switchExpressions.get(1).getChild(3) instanceof ASTSwitchLabeledBlock);

        assertEquals(Integer.TYPE, switchExpressions.get(2).getType());
        List<ASTYieldStatement> yields = switchExpressions.get(2).findDescendantsOfType(ASTYieldStatement.class);
        assertEquals(4, yields.size());
        assertEquals("SwitchExpressions.BAZ", yields.get(2).getImage());

        assertEquals(String.class, switchExpressions.get(3).getType());
    }

    @Test
    public void checkYieldConditionalBehaviour() {
        checkYieldStatements(java13p);
    }

    @Test
    public void checkYieldConditionalBehaviourJ14() {
        checkYieldStatements(java14);
    }

    private void checkYieldStatements(JavaParsingHelper parser) {
        ASTCompilationUnit compilationUnit = parser.parseResource("YieldStatements.java");
        List<JavaNode> stmts = compilationUnit.<JavaNode>findDescendantsOfType(ASTBlockStatement.class);
        // fetch the interesting node, on the java-grammar branch this is not needed
        for (int i = 0; i < stmts.size(); i++) {
            JavaNode child = stmts.get(i).getChild(0);

            if (child instanceof ASTStatement) {
                stmts.set(i, child.getChild(0));
            } else {
                stmts.set(i, child);
            }
        }

        assertEquals(18, stmts.size());

        int i = 0;
        assertThat(stmts.get(i++), instanceOf(ASTLocalVariableDeclaration.class));
        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));
        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));
        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));

        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));

        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));
        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));
        assertThat(stmts.get(i++), instanceOf(ASTYieldStatement.class));
        assertThat(stmts.get(i++), instanceOf(ASTYieldStatement.class));
        assertThat(stmts.get(i++), instanceOf(ASTYieldStatement.class));
        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));
        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));

        assertThat(stmts.get(i++), instanceOf(ASTIfStatement.class));

        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));
        assertThat(stmts.get(i++), instanceOf(ASTYieldStatement.class));

        assertThat(stmts.get(i++), instanceOf(ASTYieldStatement.class));
        assertThat(stmts.get(i++), instanceOf(ASTStatementExpression.class));
        assertThat(stmts.get(i++), instanceOf(ASTYieldStatement.class));

        assertEquals(i, stmts.size());
    }

    @Test
    public void multipleCaseLabels() {
        multipleCaseLabels(java13p);
        multipleCaseLabels(java14);
        multipleCaseLabels(java14p);
    }

    private void multipleCaseLabels(JavaParsingHelper parser) {
        ASTCompilationUnit compilationUnit = parser.parseResource("MultipleCaseLabels.java");
        ASTSwitchStatement switchStatement = compilationUnit.getFirstDescendantOfType(ASTSwitchStatement.class);
        assertTrue(switchStatement.getChild(0) instanceof ASTExpression);
        assertTrue(switchStatement.getChild(1) instanceof ASTSwitchLabel);
        ASTSwitchLabel switchLabel = switchStatement.getFirstChildOfType(ASTSwitchLabel.class);
        assertEquals(3, switchLabel.findChildrenOfType(ASTExpression.class).size());
    }

    @Test
    public void switchRules() {
        switchRules(java13p);
        switchRules(java14);
        switchRules(java14p);
    }

    private void switchRules(JavaParsingHelper parser) {
        ASTCompilationUnit compilationUnit = parser.parseResource("SwitchRules.java");
        ASTSwitchStatement switchStatement = compilationUnit.getFirstDescendantOfType(ASTSwitchStatement.class);
        assertTrue(switchStatement.getChild(0) instanceof ASTExpression);
        assertTrue(switchStatement.getChild(1) instanceof ASTSwitchLabeledExpression);
        ASTSwitchLabeledExpression switchLabeledExpression = (ASTSwitchLabeledExpression) switchStatement.getChild(1);
        assertEquals(2, switchLabeledExpression.getNumChildren());
        assertTrue(switchLabeledExpression.getChild(0) instanceof ASTSwitchLabel);
        assertTrue(switchLabeledExpression.getChild(1) instanceof ASTExpression);

        ASTSwitchLabeledBlock switchLabeledBlock = (ASTSwitchLabeledBlock) switchStatement.getChild(4);
        assertEquals(2, switchLabeledBlock.getNumChildren());
        assertTrue(switchLabeledBlock.getChild(0) instanceof ASTSwitchLabel);
        assertTrue(switchLabeledBlock.getChild(1) instanceof ASTBlock);

        ASTSwitchLabeledThrowStatement switchLabeledThrowStatement = (ASTSwitchLabeledThrowStatement) switchStatement.getChild(5);
        assertEquals(2, switchLabeledThrowStatement.getNumChildren());
        assertTrue(switchLabeledThrowStatement.getChild(0) instanceof ASTSwitchLabel);
        assertTrue(switchLabeledThrowStatement.getChild(1) instanceof ASTThrowStatement);
    }

    @Test
    public void simpleSwitchExpressions() {
        simpleSwitchExpressions(java13p);
        simpleSwitchExpressions(java14);
        simpleSwitchExpressions(java14p);
    }

    private void simpleSwitchExpressions(JavaParsingHelper parser) {
        ASTCompilationUnit compilationUnit = parser.parseResource("SimpleSwitchExpressions.java");
        ASTSwitchExpression switchExpression = compilationUnit.getFirstDescendantOfType(ASTSwitchExpression.class);
        assertEquals(6, switchExpression.getNumChildren());
        assertTrue(switchExpression.getChild(0) instanceof ASTExpression);
        assertEquals(5, switchExpression.findChildrenOfType(ASTSwitchLabeledRule.class).size());

        ASTLocalVariableDeclaration localVar = compilationUnit.findDescendantsOfType(ASTLocalVariableDeclaration.class).get(1);
        ASTVariableDeclarator localVarDecl = localVar.getFirstChildOfType(ASTVariableDeclarator.class);
        assertEquals(Integer.TYPE, localVarDecl.getType());
        assertEquals(Integer.TYPE, switchExpression.getType());
    }
}

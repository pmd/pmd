/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;

/**
 * @author Clément Fournier
 */
class DataflowPassTest extends BaseParserTest {


    @Test
    void testSimple() {

        ASTCompilationUnit ast = java.parseResource(
            "/net/sourceforge/pmd/lang/java/ast/jdkversiontests/java21/RecordPatterns.java",
            "21"
        );

        DataflowResult dataflow = DataflowPass.getDataflowResult(ast);
        assertThat(dataflow.getUnusedAssignments(), hasSize(0));

    }

    @Test
    void testBlankLocals() {
        ASTCompilationUnit ast = java.parse(
            "class A { static {"
                + "  int a;"
                + "  a = 0;"
                + " } }");
        DataflowResult df = DataflowPass.getDataflowResult(ast);
        List<ASTVariableId> list = ast.descendants(ASTVariableId.class).toList();
        ASTVariableId a = list.get(0);
        ReachingDefinitionSet reachingAEqZero = df.getReachingDefinitions(a.getLocalUsages().get(0));
        assertThat(reachingAEqZero.isNotFullyKnown(), is(false));
        assertThat(reachingAEqZero.getReaching(), hasSize(0));
    }

    @Test
    void testBlankFinalField() {
        ASTCompilationUnit ast = java.parse(
            "class A { final int field; int nonFinal; A() { field = 2; } {"
                + "  use(field);"
                + "  use(nonFinal);"
                + " } }");
        DataflowResult df = DataflowPass.getDataflowResult(ast);
        List<ASTVariableId> list = ast.descendants(ASTVariableId.class).toList();
        ASTVariableId field = list.get(0);
        ReachingDefinitionSet finalUse = df.getReachingDefinitions(field.getLocalUsages().get(0));
        assertThat(finalUse.isNotFullyKnown(), is(false));
        assertThat(finalUse.getReaching(), hasSize(1));
        AssignmentEntry assignment = finalUse.getReaching().iterator().next();
        assertFalse(assignment.isBlankDeclaration());
        assertFalse(assignment.isFieldDefaultValue());
        assertTrue(JavaAstUtils.isLiteralInt(assignment.rhs, 2));

        ASTVariableId nonFinal = list.get(1);
        ReachingDefinitionSet nonFinalUse = df.getReachingDefinitions(nonFinal.getLocalUsages().get(0));
        assertThat(nonFinalUse.isNotFullyKnown(), is(true));
        assertThat(nonFinalUse.getReaching(), hasSize(1));
        assignment = nonFinalUse.getReaching().iterator().next();
        assertTrue(assignment.isBlankDeclaration());
        assertTrue(assignment.isFieldDefaultValue());
    }
}

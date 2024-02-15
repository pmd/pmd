/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.internal;

import static org.hamcrest.MatcherAssert.assertThat;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.DataflowResult;

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
        assertThat(dataflow.getUnusedAssignments(), Matchers.hasSize(0));

    }

}

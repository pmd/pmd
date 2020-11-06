/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.lang.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.lang.dfa.pathfinder.Executable;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;

public class DAAPathFinderTest {

    @Test
    public void testTwoUpdateDefs() {
        ASTMethodDeclarator meth = JavaParsingHelper.WITH_PROCESSING.getNodes(ASTMethodDeclarator.class, TWO_UPDATE_DEFS).get(0);
        DAAPathFinder a = new DAAPathFinder(meth.getDataFlowNode().getFlow().get(0), new Executable() {
            @Override
            public void execute(CurrentPath path) {

            }
        });
        // a.run();
    }

    private static final String TWO_UPDATE_DEFS = "class Foo {" + PMD.EOL + " void bar(int x) {" + PMD.EOL
            + "  for (int i=0; i<10; i++, j--) {}" + PMD.EOL + " }" + PMD.EOL + "}";
}

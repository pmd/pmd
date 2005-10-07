package test.net.sourceforge.pmd;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Report;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import test.net.sourceforge.pmd.testframework.RuleTst;

public class SuppressWarningsTest extends RuleTst {

    private static class FooRule extends AbstractRule {
        public Object visit(ASTClassOrInterfaceDeclaration c, Object ctx) {
            if (c.getImage().equalsIgnoreCase("Foo")) addViolation(ctx,c);
            return super.visit(c, ctx);
        }
        public Object visit(ASTVariableDeclaratorId c, Object ctx) {
            if (c.getImage().equalsIgnoreCase("Foo")) addViolation(ctx,c);
            return super.visit(c, ctx);
        }
    }

    public void testClassLevelSuppressWarningsAnnotation() throws Throwable {
        Report rpt = new Report();
        runTestFromString15(TEST1, new FooRule(), rpt);
        assertEquals(0, rpt.size());
        runTestFromString15(TEST2, new FooRule(), rpt);
        assertEquals(0, rpt.size());
        runTestFromString15(TEST3, new FooRule(), rpt);
        assertEquals(0, rpt.size());
    }

    private static final String TEST1 =
    "@SuppressWarnings()" + PMD.EOL +
    "public class Foo {}";

    private static final String TEST2 =
    "@SuppressWarnings()" + PMD.EOL +
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  int foo;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Baz {" + PMD.EOL +
    " @SuppressWarnings()" + PMD.EOL +
    " public class Bar {" + PMD.EOL +
    "  void bar() {" + PMD.EOL +
    "   int foo;" + PMD.EOL +
    "  }" + PMD.EOL +
    " }"  + PMD.EOL +
    "}";

}

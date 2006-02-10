package test.net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSetNotFoundException;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class ImmutableFieldTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() throws RuleSetNotFoundException {
        rule = findRule("design", "ImmutableField");
    }

    public void testAll() {
        runTests(new TestDescriptor[]{
            new TestDescriptor(TEST1, "could be immutable, only assigned in constructor", 1, rule),
            new TestDescriptor(TEST2, "could be immutable, only assigned in decl", 1, rule),
            new TestDescriptor(TEST3, "ok, assigned twice", 0, rule),
            new TestDescriptor(TEST4, "ok, static field ", 0, rule), // TODO - probably should check these, not just discard them
            new TestDescriptor(TEST5, "ok, one constructor assigns, one doesn't", 0, rule),
            new TestDescriptor(TEST6, "ok, assignment via postfix expression", 0, rule),
            new TestDescriptor(TEST7, "postfix expressions imply mutability", 0, rule),
            new TestDescriptor(TEST8, "compound assignment", 0, rule),
            new TestDescriptor(TEST9, "preincrement", 0, rule),
            new TestDescriptor(TEST10, "predecrement", 0, rule),
            new TestDescriptor(TEST11, "compound assignment 2", 0, rule),
            new TestDescriptor(TEST12, "rhs 2", 0, rule),
            new TestDescriptor(TEST13, "assignment in constructor is in try block", 0, rule),
            new TestDescriptor(TEST14, "assignment in method is in try block", 0, rule),
            new TestDescriptor(TEST15, "assignment in constructor in loop is ok", 0, rule),
            new TestDescriptor(TEST16, "assignment in anonymous inner class method is OK", 0, rule),
        });
    }

    private static final String TEST1 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            "  x = 2;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST2 =
            "public class Foo {" + PMD.EOL +
            " private int x = 42;" + PMD.EOL +
            "}";

    private static final String TEST3 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            "  x = 41;" + PMD.EOL +
            " }" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  x = 42;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST4 =
            "public class Foo {" + PMD.EOL +
            " private static int x = 0;" + PMD.EOL +
            " private final int y;" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            "  y = x;" + PMD.EOL +
            "  x++;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST5 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " public Foo(int y) {" + PMD.EOL +
            "  x = y;" + PMD.EOL +
            " }" + PMD.EOL +
            " public Foo() {}" + PMD.EOL +
            "}";

    private static final String TEST6 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " public Foo() {}" + PMD.EOL +
            " private void bar() {x++;}" + PMD.EOL +
            "}";

    private static final String TEST7 =
            "public class Foo {" + PMD.EOL +
            " private int x = 0;" + PMD.EOL +
            " private void bar() {" + PMD.EOL +
            "  x++;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST8 =
            "public class Foo {" + PMD.EOL +
            " private int w;" + PMD.EOL +
            " private int z;" + PMD.EOL +
            " private void bar() {" + PMD.EOL +
            "  w = 2;" + PMD.EOL +
            "  z = 4;" + PMD.EOL +
            " }" + PMD.EOL +
            " private void gaz() {" + PMD.EOL +
            "  w += z++;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST9 =
            "public class Foo {" + PMD.EOL +
            " private int x = 0;" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  ++x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST10 =
            "public class Foo {" + PMD.EOL +
            " private int x = 0;" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  --x;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST11 =
            "public class Foo {" + PMD.EOL +
            " private int x = 0;" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  x += 1;" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST12 =
            "public class Foo {" + PMD.EOL +
            " private int x = 0;" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  Object y = new Bar(x++);" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST13 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            "  try {" + PMD.EOL +
            "   x = 2;" + PMD.EOL +
            "  } catch (Exception e) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST14 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " public void bar() {" + PMD.EOL +
            "  try {" + PMD.EOL +
            "   x = 2;" + PMD.EOL +
            "  } catch (Exception e) {}" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST15 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            "  for (int i=0; i<10; i++) { x += 5; }" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

    private static final String TEST16 =
            "public class Foo {" + PMD.EOL +
            " private int x;" + PMD.EOL +
            " public Foo() {" + PMD.EOL +
            "   mouseListener = new MouseAdapter() {" + PMD.EOL +
            "      public void mouseClicked(MouseEvent e) {" + PMD.EOL +
            "        x = e.getSource();" + PMD.EOL +
            "        super.mouseClicked(e);" + PMD.EOL +
            "      }" + PMD.EOL +
            "    };" + PMD.EOL +
            " }" + PMD.EOL +
            "}";

}

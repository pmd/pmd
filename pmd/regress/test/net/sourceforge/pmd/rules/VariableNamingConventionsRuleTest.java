/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.VariableNamingConventionsRule;
import test.net.sourceforge.pmd.testframework.SimpleAggregatorTst;
import test.net.sourceforge.pmd.testframework.TestDescriptor;

public class VariableNamingConventionsRuleTest extends SimpleAggregatorTst {

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "final statics should be all caps", 1, new VariableNamingConventionsRule()),
           new TestDescriptor(TEST2, "non-finals shouldn't have underscores", 1, new VariableNamingConventionsRule()),
           new TestDescriptor(TEST3, "variables names should start with lowercase character", 1, new VariableNamingConventionsRule()),
           new TestDescriptor(TEST4, "all is well", 0, new VariableNamingConventionsRule()),
           new TestDescriptor(TEST5, "local finals are ok", 0, new VariableNamingConventionsRule()),
           new TestDescriptor(TEST6, "serialVersionUID is OK", 0, new VariableNamingConventionsRule()),
           new TestDescriptor(TEST7, "interface fields are tested", 1, new VariableNamingConventionsRule()),
           new TestDescriptor(TEST8, "final non-statics need not be all caps", 0, new VariableNamingConventionsRule()),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private static final int foo = 2;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private int foo_bar = 2;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private int Ubar = 2;" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " private int bar = 2;" + PMD.EOL +
    " private static final int FOO_BAR = 2;" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " private void bar() {" + PMD.EOL +
    "  final int STATE_READING = 0;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    "  static final long serialVersionUID = 423343L;" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public interface Foo {" + PMD.EOL +
    "  int foo = 42;" + PMD.EOL +
    "}";

    private static final String TEST8 =
    "public class Foo {" + PMD.EOL +
    "  final int foo = 42;" + PMD.EOL +
    "}";
}

package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.PMD;

import java.io.StringReader;

public class AssertTest extends TestCase  {

    public void testAssertAsKeyword() {
        new JavaParser(new StringReader(TEST1)).CompilationUnit();
        new JavaParser(new StringReader(TEST1)).CompilationUnit();
        new JavaParser(new StringReader(TEST1)).CompilationUnit();
        new JavaParser(new StringReader(TEST1)).CompilationUnit();
    }

    public void testAssertAsName() {
        new JavaParser(new StringReader(TEST5)).CompilationUnit();
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert x>2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert (x>2);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert x>2 : \"hi!\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert (x>2) : \"hi!\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    "  int assert = 2;" + PMD.EOL +
    "}";
}

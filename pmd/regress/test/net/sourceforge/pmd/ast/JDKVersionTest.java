package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.TargetJDKVersion;
import net.sourceforge.pmd.TargetJDK1_5;
import net.sourceforge.pmd.TargetJDK1_4;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

import java.io.StringReader;

public class JDKVersionTest extends TestCase  {

    // enum keyword/identifier
    public void testEnumAsKeywordShouldFailWith14() throws Throwable {
        try {
            TargetJDKVersion jdk = new TargetJDK1_4();
            JavaParser p = jdk.createParser(new StringReader(JDK15_ENUM));
            p.CompilationUnit();
            throw new Error("JDK 1.4 parser should have failed to parse enum used as keyword");
        } catch (ParseException e) {}    // cool
    }

    public void testEnumAsIdentifierShouldPassWith14() throws Throwable {
        TargetJDKVersion jdk = new TargetJDK1_4();
        JavaParser p = jdk.createParser(new StringReader(JDK14_ENUM));
        p.CompilationUnit();
    }

    public void testEnumAsKeywordShouldPassWith15() throws Throwable {
        TargetJDKVersion jdk = new TargetJDK1_5();
        JavaParser p = jdk.createParser(new StringReader(JDK15_ENUM));
        p.CompilationUnit();
    }

    public void testEnumAsIdentifierShouldFailWith15() throws Throwable {
        try {
            TargetJDKVersion jdk = new TargetJDK1_5();
            JavaParser p = jdk.createParser(new StringReader(JDK14_ENUM));
            p.CompilationUnit();
            throw new Error("JDK 1.5 parser should have failed to parse enum used as identifier");
        } catch (ParseException e) {}    // cool
    }
    // enum keyword/identifier

    private static final String JDK15_ENUM =
    "public class Test {" + PMD.EOL +
    " enum Season { winter, spring, summer, fall };" + PMD.EOL +
    "}";

    private static final String JDK14_ENUM =
    "public class Test {" + PMD.EOL +
    " int enum;" + PMD.EOL +
    "}";
}

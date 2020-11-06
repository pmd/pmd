/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Map;

import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class LocalScopeTest extends BaseNonParserTest {

    @Test
    public void testNameWithThisOrSuperIsNotFlaggedAsUnused() {
        LocalScope scope = new LocalScope();
        ASTName name = new ASTName(1);
        name.setImage("foo");
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesThisModifier();
        name.jjtAddChild(prefix, 1);
        JavaNameOccurrence occ = new JavaNameOccurrence(name, "foo");
        scope.addNameOccurrence(occ);
        assertFalse(scope.getDeclarations().keySet().iterator().hasNext());
    }

    @Test
    public void testNameWithSuperIsNotFlaggedAsUnused() {
        LocalScope scope = new LocalScope();
        ASTName name = new ASTName(1);
        name.setImage("foo");
        ASTPrimaryPrefix prefix = new ASTPrimaryPrefix(2);
        prefix.setUsesSuperModifier();
        name.jjtAddChild(prefix, 1);
        JavaNameOccurrence occ = new JavaNameOccurrence(name, "foo");
        scope.addNameOccurrence(occ);
        assertFalse(scope.getDeclarations().keySet().iterator().hasNext());
    }

    @Test
    public void testLocalVariableDeclarationFound() {
        ASTCompilationUnit acu = parseCode(TEST1);
        List<ASTVariableDeclaratorId> nodes = acu.findDescendantsOfType(ASTVariableDeclaratorId.class);
        ASTVariableDeclaratorId node = nodes.get(0);
        Map<NameDeclaration, List<NameOccurrence>> vars = node.getScope().getDeclarations();
        assertEquals(1, vars.size());
        NameDeclaration decl = vars.keySet().iterator().next();
        assertEquals("b", decl.getImage());
    }

    @Test
    public void testQualifiedNameOccurrence() {
        ASTCompilationUnit acu = parseCode(TEST2);
        List<ASTVariableDeclaratorId> nodes = acu.findDescendantsOfType(ASTVariableDeclaratorId.class);
        ASTVariableDeclaratorId node = nodes.get(0);
        Map<NameDeclaration, List<NameOccurrence>> vars = node.getScope().getDeclarations();
        NameDeclaration decl = vars.keySet().iterator().next();
        JavaNameOccurrence occ = (JavaNameOccurrence) vars.get(decl).get(0);
        assertEquals("b", occ.getImage());
    }

    @Test
    public void testPostfixUsageIsRecorded() {
        ASTCompilationUnit acu = parseCode(TEST3);
        List<ASTVariableDeclaratorId> nodes = acu.findDescendantsOfType(ASTVariableDeclaratorId.class);
        ASTVariableDeclaratorId node = nodes.get(0);
        Map<NameDeclaration, List<NameOccurrence>> vars = node.getScope().getDeclarations();
        NameDeclaration decl = vars.keySet().iterator().next();
        List<NameOccurrence> usages = vars.get(decl);
        JavaNameOccurrence occ = (JavaNameOccurrence) usages.get(0);
        assertEquals(4, occ.getLocation().getBeginLine());
    }

    @Test
    public void testLocalVariableTypesAreRecorded() {
        ASTCompilationUnit acu = parseCode(TEST1);
        List<ASTVariableDeclaratorId> nodes = acu.findDescendantsOfType(ASTVariableDeclaratorId.class);
        Map<NameDeclaration, List<NameOccurrence>> vars = nodes.get(0).getScope().getDeclarations();
        VariableNameDeclaration decl = (VariableNameDeclaration) vars.keySet().iterator().next();
        assertEquals("Bar", decl.getTypeImage());
    }

    @Test
    public void testMethodArgumentTypesAreRecorded() {
        ASTCompilationUnit acu = parseCode(TEST5);
        List<ASTFormalParameter> nodes = acu.findDescendantsOfType(ASTFormalParameter.class);
        Map<NameDeclaration, List<NameOccurrence>> vars = nodes.get(0).getScope().getDeclarations();
        VariableNameDeclaration decl = (VariableNameDeclaration) vars.keySet().iterator().next();
        assertEquals("String", decl.getTypeImage());
    }

    @Test
    public void testgetEnclosingMethodScope() {
        ASTCompilationUnit acu = parseCode(TEST4);
        ASTLocalVariableDeclaration node = acu.findDescendantsOfType(ASTLocalVariableDeclaration.class).get(0);
        LocalScope scope = (LocalScope) node.getScope();
        MethodScope ms = scope.getEnclosingScope(MethodScope.class);
        assertEquals(2, ms.getDeclarations().size());
    }

    public static final String TEST1 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL
            + "  Bar b = new Bar();" + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST2 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL
            + "  Bar b = new Bar();" + PMD.EOL + "  b.buz = 2;" + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST3 = "public class Foo {" + PMD.EOL + " void foo() {" + PMD.EOL + "  int x = 2;"
            + PMD.EOL + "  x++;" + PMD.EOL + " }" + PMD.EOL + "}";

    public static final String TEST4 = "public class Foo {" + PMD.EOL + " void foo(String x, String z) { { int x; } }"
            + PMD.EOL + "}";

    public static final String TEST5 = "public class Foo {" + PMD.EOL + " void foo(String x);" + PMD.EOL + "}";
}

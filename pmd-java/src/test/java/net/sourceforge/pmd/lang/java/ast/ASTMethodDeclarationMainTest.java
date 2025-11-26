/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import net.sourceforge.pmd.lang.java.BaseParserTest;

class ASTMethodDeclarationMainTest extends BaseParserTest {

    @ParameterizedTest
    @CsvSource({
        "static void main(), static void main(String[] args)",
        "void main(), static void main(String[] args)",
        "void main(), void main(String[] args)",
        "void main(String[] args), static void main()",
        "private static void main(String[] args), void main()",
        "void main(), void main(String... args)",
        "void main(String... args), static void main()"
    })
    void mainMethodPriorityTest(String lowPriority, String highPriority) {
        ASTCompilationUnit nodes = java.parse(
            "public class App {\n"
                + lowPriority + "{ }\n"
                + highPriority + "{ }\n"
                + "}"
        );
        List<ASTMethodDeclaration> methods = nodes.descendants(ASTMethodDeclaration.class).toList();
        assertFalse(methods.get(0).isMainMethod());
        assertTrue(methods.get(1).isMainMethod());
    }

    @ParameterizedTest
    @ValueSource(strings = { "String[]", "String..."})
    void mainMethodPriorityWithOverride(String parentArgType) {
        ASTCompilationUnit nodes = java.parse(
            "public class BaseApp {\n"
                + "  public void main(" + parentArgType + " args) { }\n"
                + "  public class App extends BaseApp {\n"
                + "    public void main() { }\n"
                + "  }\n"
                + "}"
        );
        List<ASTMethodDeclaration> methods = nodes.descendants(ASTMethodDeclaration.class)
            .crossFindBoundaries().toList();
        assertTrue(methods.get(0).isMainMethod());
        assertFalse(methods.get(1).isMainMethod());
    }

    @Test
    void mainMethodPriorityWithStaticInSuperclass() {
        ASTCompilationUnit nodes = java.parse(
            "public class BaseApp {\n"
                + "  public static void main(String[] args) { }\n"
                + "  public class App extends BaseApp {\n"
                + "    public void main() { }\n"
                + "  }\n"
                + "}"
        );
        List<ASTMethodDeclaration> methods = nodes.descendants(ASTMethodDeclaration.class)
            .crossFindBoundaries().toList();
        assertTrue(methods.get(0).isMainMethod());
        assertTrue(methods.get(1).isMainMethod());
    }
}

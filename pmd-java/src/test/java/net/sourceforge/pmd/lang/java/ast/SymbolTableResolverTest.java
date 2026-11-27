/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

class SymbolTableResolverTest extends BaseParserTest {
    @Test
    void allClassTypesShouldHaveSymbol() {
        ASTCompilationUnit compilationUnit = parseCode(
                "import java.util.*;\n"
                + "public class Book {\n"
                + "    private Map<String, Set<String>> map;\n"
                + "    \n"
                + "    public static void main(String[] args) {\n"
                + "        Book b = new Book();\n"
                + "        for (Iterator<Map.Entry<String, Set<String>>> it = b.map.entrySet().iterator(); it.hasNext();) {\n"
                + "            Map.Entry<String, Set<String>> entry = it.next();\n"
                + "        }\n"
                + "    }\n"
                + "}");
        List<ASTClassType> types = compilationUnit.descendants(ASTClassType.class).toList();
        for (ASTClassType type : types) {
            assertNotNull(type.getReferencedSym());
        }
    }

    @Test
    void recordComponentTypeShouldBeResolvedBeforeAnonymousClassInference() {
        ASTCompilationUnit compilationUnit = java.withDefaultVersion("16").parse(
                "public class J {\n"
                        + "    record Actor(String userView) {}\n"
                        + "    static Object convert(Object a, Object b) { return null; }\n"
                        + "    Object make(Actor actor) { return convert(actor.userView(), new Object() {}); }\n"
                        + "}");
        ASTRecordDeclaration record = compilationUnit.descendants(ASTRecordDeclaration.class)
                .crossFindBoundaries().firstOrThrow();
        ASTRecordComponent component = record.getRecordComponents()
                .children(ASTRecordComponent.class).firstOrThrow();
        assertNotNull(((ASTClassType) component.getTypeNode()).getReferencedSym());
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "new StringReader(text) {};",
        "new StringReader(text);",
        "{ String content = text; yield new StringReader(content) {}; }"
    })
    void switchPatternVariableTypeShouldBeResolvedBeforeConstructorInference(String arm) {
        ASTCompilationUnit compilationUnit = java.withDefaultVersion("21").parse(
                "import java.io.Reader;\n"
                        + "import java.io.StringReader;\n"
                        + "class Repro {\n"
                        + "    Reader readerFor(Object value) {\n"
                        + "        return switch (value) {\n"
                        + "            case String text -> " + arm + "\n"
                        + "            default -> Reader.nullReader();\n"
                        + "        };\n"
                        + "    }\n"
                        + "}\n");
        ASTConstructorCall constructor = compilationUnit.descendants(ASTConstructorCall.class)
                .crossFindBoundaries().firstOrThrow();
        assertFalse(constructor.getOverloadSelectionInfo().isFailed());
        assertTrue(TypeTestUtil.isExactlyA(String.class, constructor.getArguments().get(0)));
    }
}

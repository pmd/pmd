/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.BaseParserTest;

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
}

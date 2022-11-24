/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Locale;

import org.junit.jupiter.api.Test;

class TrailingCommaTest extends EcmascriptParserTestBase {

    @Test
    void testTrailingCommaDefaultLocale() {
        testTrailingComma();
    }

    @Test
    void testTrailingCommaFrFr() {
        runWithLocale(Locale.FRANCE, () -> testTrailingComma());
    }

    @Test
    void testTrailingCommaRootLocale() {
        runWithLocale(Locale.ROOT, () -> testTrailingComma());
    }

    private void testTrailingComma() {
        ASTAstRoot node = js.parse("x = {a : 1, };\n");
        ASTObjectLiteral fn = node.getFirstDescendantOfType(ASTObjectLiteral.class);
        assertTrue(fn.isTrailingComma());
    }

    private void runWithLocale(Locale locale, Runnable runnable) {
        Locale prev = Locale.getDefault();
        try {
            Locale.setDefault(locale);
            runnable.run();
        } finally {
            Locale.setDefault(prev);
        }
    }
}

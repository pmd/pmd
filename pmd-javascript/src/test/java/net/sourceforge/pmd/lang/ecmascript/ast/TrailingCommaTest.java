/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.ast;

import java.util.Locale;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

public class TrailingCommaTest extends EcmascriptParserTestBase {

    @Rule
    public DefaultLocale defaultLocale = new DefaultLocale();


    @Test
    public void testTrailingCommaDefaultLocale() {
        testTrailingComma();
    }

    @Test
    public void testTrailingCommaFrFr() {
        defaultLocale.set(Locale.FRANCE);
        testTrailingComma();
    }

    @Test
    public void testTrailingCommaRootLocale() {
        defaultLocale.set(Locale.ROOT);
        testTrailingComma();
    }

    public void testTrailingComma() {
        ASTAstRoot node = js.parse("x = {a : 1, };\n");
        ASTObjectLiteral fn = node.getFirstDescendantOfType(ASTObjectLiteral.class);
        Assert.assertTrue(fn.isTrailingComma());
    }
}

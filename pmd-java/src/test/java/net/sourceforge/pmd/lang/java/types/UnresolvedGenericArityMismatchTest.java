/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types;

import static net.sourceforge.pmd.lang.java.symbols.table.internal.JavaSemanticErrors.UNRESOLVED_GENERIC_ARITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.SemanticException;
import net.sourceforge.pmd.lang.java.BaseParserTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper.TestCheckLogger;

/**
 * Regression for inconsistent arity of the same unresolved type name
 * (e.g. Builder used once with 2 type args and later with 3). Analysis
 * must report a semantic error and skip the file instead of crashing
 * in Substitution.mapping.
 */
class UnresolvedGenericArityMismatchTest extends BaseParserTest {

    @Test
    void mismatchedUnresolvedArityIsSemanticError() {
        String source = ""
            + "public class ComboBox2Test {\n"
            + "  public class ComboBoxIntStringItem extends ComboBox1<Integer, String> {\n"
            + "    public ComboBoxIntStringItem() {\n"
            + "      super(new Builder<Integer, String>() {});\n"
            + "    }\n"
            + "    public static class Item extends Item1<Integer, String> {\n"
            + "      public Item(Integer key, String displayValue) { super(key, displayValue); }\n"
            + "    }\n"
            + "  }\n"
            + "\n"
            + "  public class ComboBoxIntStringLongItem extends ComboBox2<Integer, String, Long> {\n"
            + "    public ComboBoxIntStringLongItem() {\n"
            + "      super(new Builder<Integer, String, Long>() {});\n"
            + "    }\n"
            + "    public static class Item extends Item2<Integer, String, Long> {\n"
            + "      public Item(Integer key, String displayValue, Long displayValue2) {\n"
            + "        super(key, displayValue);\n"
            + "      }\n"
            + "    }\n"
            + "  }\n"
            + "}\n";

        TestCheckLogger logger = new TestCheckLogger(false);
        SemanticException ex = assertThrows(SemanticException.class, () -> java.withLogger(logger).parse(source));

        assertEquals(1, logger.errors.get(UNRESOLVED_GENERIC_ARITY).size());
        assertEquals("Builder", logger.errors.get(UNRESOLVED_GENERIC_ARITY).get(0).getSecond()[0]);
        assertThat(ex.getMessage(), containsString("Mismatched generic count on unresolved type \"Builder\""));
        assertThat(ex.getMessage(), containsString("missing aux-classpath"));
    }
}

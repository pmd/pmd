/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/**
 * Miscellaneous Kotlin parser regression tests.
 */
class KotlinParserTests extends BaseKotlinTreeDumpTest {

    @Test
    void testSimpleKotlin() {
        doTest("Simple");
    }

    // Regression tests for https://github.com/pmd/pmd/issues/6648
    // Multi-dollar string interpolation (Kotlin 2.2, KEEP-375) in function and annotation args.

    @Test
    void multiDollarLineStringInFunctionArg() {
        assertDoesNotThrow(() -> KotlinParsingHelper.DEFAULT.parse(
            "private fun clazz(name: String) = name\n"
            + "val x = clazz($$\"java.util.Collections\\$SingletonList\")"
        ));
    }

    @Test
    void multiDollarLineStringInAnnotationArg() {
        assertDoesNotThrow(() -> KotlinParsingHelper.DEFAULT.parse(
            "annotation class Scheduled(val fixedDelayString: String)\n"
            + "@Scheduled(fixedDelayString = $$\"\\${app.interval:PT59M}\")\n"
            + "fun execute() { }"
        ));
    }

    // KEEP-375 spec examples: https://github.com/Kotlin/KEEP/blob/main/proposals/KEEP-0375-dollar-escape.md

    @Test
    void specExampleLiteralDollarsInFormatString() {
        // $$"..." — single $ not interpolation (needs 2), so %1$s is verbatim
        assertDoesNotThrow(() -> KotlinParsingHelper.DEFAULT.parse(
            "fun tr(s: String) = s\n"
            + "val x = tr($$\"Could not copy the file into the %1$s directory: %2$s\")"
        ));
    }

    @Test
    void specExampleDollarBlockLongerThanPrefix() {
        // $$ starts interpolation; $$$ = one literal $ + $$ interpolation start
        assertDoesNotThrow(() -> KotlinParsingHelper.DEFAULT.parse(
            "data class Item(val name: String, val price: Int)\n"
            + "val item = Item(\"Foo\", 42)\n"
            + "val s = $$\"$${item.name} costs $$${item.price}\""
        ));
    }

    @Test
    void specExampleEscapeNotCountingForInterpolation() {
        // $$"$\$$hello" -> value $$$hello: first $ literal, \$ verbatim, last $ literal
        assertDoesNotThrow(() -> KotlinParsingHelper.DEFAULT.parse(
            "val s = $$\"$\\$$hello\""
        ));
    }

    @Test
    void specExampleMultilineDollarPrefix() {
        // $$"""...""" — single $ is literal (no backslash needed), $$ starts interpolation
        assertDoesNotThrow(() -> KotlinParsingHelper.DEFAULT.parse(
            "val title = \"example\"\n"
            + "val schema = $$\"\"\"\n"
            + "  \"$schema\": \"draft-2020\",\n"
            + "  \"title\": \"$${title}\"\n"
            + "\"\"\""
        ));
    }

}

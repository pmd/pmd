/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtClassDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtFunctionDeclaration;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtImportHeader;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.rule.xpath.Attribute;

/**
 * Miscellaneous Kotlin parser regression tests.
 */
class KotlinParserTests extends BaseKotlinTreeDumpTest {

    @Test
    void testSimpleKotlin() {
        doTest("Simple");
    }

    @Test
    void testDesignPatterns() {
        doTest("DesignPatterns");
    }

    @Test
    void syntaxErrorThrowsParseException() {
        ParseException parseException = assertThrows(ParseException.class, () ->
                KotlinParsingHelper.DEFAULT.parse("fun foo( { }", null, FileId.fromPathLikeString("myfile.kt"))
        );
        assertThat(parseException.getMessage(), containsString("myfile.kt"));
        assertEquals("myfile.kt", parseException.getFileId().getOriginalPath());
    }

    @Test
    void lexerErrorThrowsLexException() {
        // ^ is Java XOR but not a valid Kotlin token - triggers a lexer error
        assertThrows(LexException.class, () ->
            KotlinParsingHelper.DEFAULT.parse("fun xor(a: Int, b: Int) = (a ^ b)")
        );
    }

    @Test
    void multipleErrorsShouldBeCollectedAsSuppressedExceptions() {
        String badCode = "package nl.stokpop\n"
                + "\n"
                + "fun xor1(a: Int, b: Int) = (a ^ b)\n"
                + "fun xor2(a: Int, b: Int) = (a ^ b)\n"
                + "fun broken( { }";
        LexException lexException = assertThrows(LexException.class, () -> KotlinParsingHelper.DEFAULT.parse(badCode));
        Throwable[] suppressed = lexException.getSuppressed();
        assertEquals(4, suppressed.length);
        assertInstanceOf(ParseException.class, suppressed[0]);
        assertInstanceOf(LexException.class, suppressed[1]);
        assertInstanceOf(ParseException.class, suppressed[2]);
        assertInstanceOf(ParseException.class, suppressed[3]);
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

    @Test
    void identifierAttributeOnClassDeclaration() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("class Foo");
        KtClassDeclaration clazz =
                file.descendants(KtClassDeclaration.class).first();
        assertEquals("Foo", clazz.attributes(KtClassDeclarationAttributes.class).getIdentifier());
    }

    @Test
    void modifiersAttributeOnFunctionDeclaration() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "abstract class Base { open suspend fun doWork() {} }");
        KtFunctionDeclaration func =
                file.descendants(KtFunctionDeclaration.class).first();
        assertEquals("open suspend", func.attributes(KtFunctionDeclarationAttributes.class).getModifiers());
    }

    @Test
    void modifiersAttributeNullWhenNoModifiers() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse("fun plain() {}");
        KtFunctionDeclaration func =
                file.descendants(KtFunctionDeclaration.class).first();
        assertNull(func.attributes(KtFunctionDeclarationAttributes.class).getModifiers());
    }

    @Test
    void nameAttributeOnImportHeader() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "import com.example.Foo\nfun f() {}");
        KtImportHeader imp =
                file.descendants(KtImportHeader.class).first();
        assertEquals("com.example.Foo", imp.attributes(KtImportHeaderAttributes.class).getName());
    }

    @Test
    void xpathAttributesHaveNoNullValues() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "import com.example.Foo\npublic fun greet(name: String) {}");
        KtFunctionDeclaration func = file.descendants(KtFunctionDeclaration.class).first();
        KtImportHeader imp = file.descendants(KtImportHeader.class).first();
        KotlinNode terminal = file.descendants(KotlinNode.class)
                .filter(n -> n instanceof KotlinTerminalNode).first();
        // file and terminal have no AttributeView; func and imp do
        Stream.of(file, func, imp, terminal).forEach(node -> {
            Iterator<Attribute> it = node.getXPathAttributesIterator();
            while (it.hasNext()) {
                Attribute attr = it.next();
                assertNotNull(attr.getValue(),
                        "Attribute @" + attr.getName() + " has null value on " + node.getXPathNodeName());
            }
        });
    }

    @Test
    void xpathAttributesHaveNoDuplicates() {
        KtKotlinFile file = KotlinParsingHelper.DEFAULT.parse(
                "import com.example.Foo\nfun greet(name: String) {}");
        KtFunctionDeclaration func = file.descendants(KtFunctionDeclaration.class).first();
        KtImportHeader imp = file.descendants(KtImportHeader.class).first();
        KotlinNode terminal = file.descendants(KotlinNode.class)
                .filter(n -> n instanceof KotlinTerminalNode).first();
        // file and terminal have no AttributeView; func and imp do
        Stream.of(file, func, imp, terminal).forEach(node -> {
            List<String> names = new ArrayList<>();
            Iterator<Attribute> it = node.getXPathAttributesIterator();
            while (it.hasNext()) {
                names.add(it.next().getName());
            }
            assertEquals(names.stream().distinct().count(), names.size(),
                    "Duplicate XPath attributes on " + node.getXPathNodeName() + ": " + names);
        });
    }

}

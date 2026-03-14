/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtMultiLineStringExpression;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtMultiLineStringLiteral;

/**
 * Minimal test that parses a Kotlin snippets with new syntax and see if there
 * are no parsing issues.
 */
class KotlinParserMultiLineStringTest {

    @Test
    void testMultiLineStringRefs() {
        String code = KotlinParsingHelper.readResourcePath("net/sourceforge/pmd/lang/kotlin/ast/testdata/MultiLineStringRefs.kt");

        // Parse using KotlinParsingHelper
        KtKotlinFile root = KotlinParsingHelper.parseAndAssertNoStderr(code);

        // In this grammar, `$identifier` (and multi-dollar variants like `$$$productName`) are represented
        // as `MultiLineStrRef` tokens inside `KtMultiLineStringContent`. `KtMultiLineStringExpression` is
        // reserved for `$+{...}` forms.
        List<KtMultiLineStringLiteral> literals = root.descendants(KtMultiLineStringLiteral.class).toList();
        assertEquals(1, literals.size(), "Expected exactly one multi-line string literal");

        long refCount = findTerminalNodes(root, KotlinParser.MultiLineStrRef).count();
        assertEquals(1, refCount, "Expected one multi-line string ref ($$$productName)");

        // There is no `${...}` / `$$${...}` in this snippet.
        assertEquals(0, root.descendants(KtMultiLineStringExpression.class).count(), "Expected no multi-line string expression entries");
    }

    @Test
    void testMultiLineStringExpressions() {
        String code = KotlinParsingHelper.readResourcePath("net/sourceforge/pmd/lang/kotlin/ast/testdata/MultiLineStringExpressions.kt");

        // Parse using KotlinParsingHelper
        KtKotlinFile root = KotlinParsingHelper.parseAndAssertNoStderr(code);

        // In this grammar, `$identifier` (and multi-dollar variants like `$$$productName`) are represented
        // as `MultiLineStrRef` tokens inside `KtMultiLineStringContent`. `KtMultiLineStringExpression` is
        // reserved for `$+{...}` forms.
        List<KtMultiLineStringLiteral> literals = root.descendants(KtMultiLineStringLiteral.class).toList();
        assertEquals(1, literals.size(), "Expected exactly one multi-line string literal");

        // check number of dollars in the prefix before the tripple quotes
        List<KotlinTerminalNode> nodes = literals.get(0).children(KotlinTerminalNode.class).toList();
        assertEquals(3, nodes.size()); // expected: '$$$' and '"""' and '"""'
        // this is to demonstrate the potential matching of the real multi-dollar expression in next step
        assertEquals("$$$", nodes.get(0).getText(), "Expected the first token to be the triple dollar prefix");

        // With Kotlin-style min-dollar gating for `${...}` in multi-dollar raw strings:
        // - `$${...}` starts an entry (run length 2 < prefix 3 is *not* enough) -> treated as text
        // - `$$${...}` starts an entry (run length 3 == prefix 3) -> parsed as one expression entry
        List<KtMultiLineStringExpression> expressions = root.descendants(KtMultiLineStringExpression.class).toList();
        assertEquals(1, expressions.size(), "Expected one multi-line string expression entry");
    }

    @Test
    void testSingleDollarRefWithoutPrefix() {
        String code = KotlinParsingHelper.readResourcePath("net/sourceforge/pmd/lang/kotlin/ast/testdata/SingleDollarRefNoPrefix.kt");

        KtKotlinFile root = KotlinParsingHelper.parseAndAssertNoStderr(code);

        // In plain strings, `$myRef` is a LineStrRef. In raw multi-line strings without multi-dollar
        // prefix, `$myRef` is also a MultiLineStrRef.
        long lineRefCount = findTerminalNodes(root, KotlinParser.LineStrRef).count();
        assertEquals(1, lineRefCount, "Expected one line string ref ($myRef)");

        long multiLineRefCount = findTerminalNodes(root, KotlinParser.MultiLineStrRef).count();
        assertEquals(1, multiLineRefCount, "Expected one multiline raw string ref ($myRef)");
    }

    @Test
    void testMultiDollarRefSplitting() {
        String code = KotlinParsingHelper.readResourcePath("net/sourceforge/pmd/lang/kotlin/ast/testdata/MultiLineStringRefSplitting.kt");

        KtKotlinFile root = KotlinParsingHelper.parseAndAssertNoStderr(code);

        // In a `$$$"""` raw string, `$$$$myRef` should be tokenized as:
        // - one literal '$' (text)
        // - one reference '$$$myRef'
        // This mirrors what we do for expressions like `$$$${...}`.
        long refCount = findTerminalNodes(root, KotlinParser.MultiLineStrRef).count();
        assertEquals(1, refCount, "Expected one multiline ref ($$$myRef)");

        long singleDollarTextCount = findTerminalNodes(root, KotlinParser.MultiLineStrText, "$").count();
        assertEquals(1, singleDollarTextCount, "Expected one literal '$' text token from splitting");
    }

    @Test
    void testMultiDollarExpressionSplitting() {
        String code = KotlinParsingHelper.readResourcePath("net/sourceforge/pmd/lang/kotlin/ast/testdata/MultiLineStringExprSplitting.kt");

        KtKotlinFile root = KotlinParsingHelper.parseAndAssertNoStderr(code);

        // In a `$$$"""` raw string, `$$$$${...}` should be tokenized as:
        // - one literal '$' (text)
        // - one expression entry starting with `$$$${...}`
        List<KtMultiLineStringExpression> expressions = root.descendants(KtMultiLineStringExpression.class).toList();
        assertEquals(1, expressions.size(), "Expected one multiline expression entry from splitting");

        long singleDollarTextCount = findTerminalNodes(root, KotlinParser.MultiLineStrText, "$").count();
        assertTrue(singleDollarTextCount >= 1, "Expected at least one literal '$' text token from splitting");
    }

    @Test
    void testSingleDollarExpressionWithoutPrefix() {
        String code = KotlinParsingHelper.readResourcePath("net/sourceforge/pmd/lang/kotlin/ast/testdata/SingleDollarExprNoPrefix.kt");

        KtKotlinFile root = KotlinParsingHelper.parseAndAssertNoStderr(code);

        long lineExprStartCount = findTerminalNodes(root, KotlinParser.LineStrExprStart).count();

        assertEquals(1, lineExprStartCount, "Expected one line string expression start (${...})");

        List<KtMultiLineStringExpression> multilineExprs = root.descendants(KtMultiLineStringExpression.class).toList();
        assertEquals(1, multilineExprs.size(), "Expected one multiline raw string expression (${...})");
    }

    private static NodeStream<KotlinTerminalNode> findTerminalNodes(KotlinNode node, int tokenType) {
        return node.descendants(KotlinTerminalNode.class)
                .filter(n -> n.getFirstAntlrToken().getType() == tokenType);
    }

    private static NodeStream<KotlinTerminalNode> findTerminalNodes(KotlinNode node, int tokenType, String exactText) {
        NodeStream<KotlinTerminalNode> stream = findTerminalNodes(node, tokenType);
        if (exactText == null) {
            return stream;
        }
        return stream.filter(n -> exactText.equals(n.getText()));
    }
}

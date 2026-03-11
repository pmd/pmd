/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtKotlinFile;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinParser.KtMultiLineStringContent;
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

        long refCount = root.descendants(KtMultiLineStringContent.class)
                            .filter(c -> c.getFirstChild() instanceof KotlinTerminalNode
                                    && ((KotlinTerminalNode) c.getFirstChild()).getFirstAntlrToken().getType() == KotlinParser.MultiLineStrRef)
                            .count();
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
}

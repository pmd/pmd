/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.ast;

import java.util.Locale;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.document.Chars;
import net.sourceforge.pmd.lang.plsql.symboltable.SymbolFacade;

public class PLSQLParser extends JjtreeParserAdapter<ASTInput> {

    // Stores images of constant string literals.
    // This is to reuse the image strings for PLSQL keywords.
    // JavaCC unfortunately does not store a constant image for those
    // keywords because the grammar is case-insensitive.
    // This optimization has the effect that the image of keyword tokens
    // is always upper-case, regardless of the actual case used in the code.
    // The original casing can be found by looking at the TextDocument for the file.

    // NOTE: the size of this array should be greater than the number of tokens in the file.
    private static final String[] STRING_LITERAL_IMAGES_EXTRA = new String[PLSQLTokenKinds.TOKEN_NAMES.size()];

    static {
        for (int i = 0; i < PLSQLTokenKinds.TOKEN_NAMES.size(); i++) {
            String image = PLSQLTokenKinds.TOKEN_NAMES.get(i);
            if (image.startsWith("\"") && image.endsWith("\"")) {
                // a string literal image, remove the quotes
                image = image.substring(1, image.length() - 1);
                STRING_LITERAL_IMAGES_EXTRA[i] = image;
            }
        }
    }

    static final TokenDocumentBehavior TOKEN_BEHAVIOR = new TokenDocumentBehavior(PLSQLTokenKinds.TOKEN_NAMES) {
        @Override
        public JavaccToken createToken(JavaccTokenDocument self, int kind, CharStream cs, @Nullable String image) {
            if (image == null) {
                // fetch another constant image if possible.
                image = STRING_LITERAL_IMAGES_EXTRA[kind];
            }

            if (image == null) {
                Chars imageCs = cs.getTokenImageCs();
                if (kind == PLSQLTokenKinds.IDENTIFIER && imageCs.charAt(0) == '"') {
                    // remove quotes to make identical to bare ID
                    image = imageCs.substring(1, imageCs.length() - 1);
                } else {
                    image = imageCs.toString();
                }

                // PLSQL is case-insensitive, but the contents of
                // string literals and the like are case-sensitive.
                // Note: tokens are normalized to uppercase make CPD case-insensitive.
                // We use uppercase and not lowercase because that way, PLSQL keywords
                // will be returned unchanged (they are already uppercase, see PLSQLParser),
                // therefore creating fewer strings in memory.
                if (kind != PLSQLTokenKinds.CHARACTER_LITERAL
                    && kind != PLSQLTokenKinds.STRING_LITERAL
                    && kind != PLSQLTokenKinds.QUOTED_LITERAL) {
                    image = image.toUpperCase(Locale.ROOT);
                }
            }

            return super.createToken(self, kind, cs, image);
        }
    };

    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return TOKEN_BEHAVIOR;
    }

    @Override
    protected ASTInput parseImpl(CharStream cs, ParserTask task) throws ParseException {
        ASTInput root = new PLSQLParserImpl(cs).Input().addTaskInfo(task);
        TimeTracker.bench("PLSQL symbols", () -> SymbolFacade.process(root));
        return root;
    }

}

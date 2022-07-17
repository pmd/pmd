/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.javacc;

import java.util.function.Function;

import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.document.TextDocument;

public final class CharStreamFactory {

    private CharStreamFactory() {
        // util class
    }

    /**
     * A char stream that doesn't perform any escape translation.
     */
    public static CharStream simpleCharStream(TextDocument input) {
        return simpleCharStream(input, JavaccTokenDocument::new);
    }

    /**
     * A char stream that doesn't perform any escape translation.
     */
    public static CharStream simpleCharStream(TextDocument input,
                                              Function<? super TextDocument, ? extends JavaccTokenDocument> documentMaker) {
        JavaccTokenDocument document = documentMaker.apply(input);
        return new SimpleCharStream(document);
    }

    /**
     * A char stream that translates java unicode sequences.
     */
    public static CharStream javaCharStream(TextDocument input) {
        return javaCharStream(input, JavaccTokenDocument::new);
    }

    /**
     * A char stream that translates java unicode sequences.
     */
    public static CharStream javaCharStream(TextDocument input, Function<? super TextDocument, ? extends JavaccTokenDocument> documentMaker) {
        JavaccTokenDocument document = documentMaker.apply(input);
        return new JavaCharStream(document);
    }

}

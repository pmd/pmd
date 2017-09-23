/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea.syntaxhighlighting;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public class SyntaxHighlighterConstants {

    private static final Map<String, SyntaxHighlighter> HIGHLIGHTER_MAP;


    static {
        Map<String, SyntaxHighlighter> map = new HashMap<>();
        map.put("Java", new JavaSyntaxHighlighter());
        HIGHLIGHTER_MAP = Collections.unmodifiableMap(map);
    }


    private SyntaxHighlighterConstants() {

    }


    public static SyntaxHighlighter getHighlighterForLanguage(String langName) {
        return HIGHLIGHTER_MAP.get(langName);
    }

}

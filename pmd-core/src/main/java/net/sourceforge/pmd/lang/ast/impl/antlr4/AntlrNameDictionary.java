/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ast.impl.antlr4;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.Vocabulary;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class AntlrNameDictionary implements Vocabulary {

    private final String[] xpathNames;
    private final Vocabulary vocabulary;

    public AntlrNameDictionary(Vocabulary vocab) {
        this.vocabulary = vocab;

        String[] xpathNames = new String[vocab.getMaxTokenType()];
        for (int i = 0; i < xpathNames.length; i++) {
            String name = vocab.getSymbolicName(i);

            if (name == null) {
                name = vocab.getLiteralName(i);

                if (name != null) {
                    // cleanup literal name
                    name = name.substring(1, name.length() - 1);
                    if (!StringUtils.isAlphanumeric(name)) {
                        name = maybePunctName(name);
                    }
                }
            }
            if (name == null) {
                name = String.valueOf(i);
            }

            xpathNames[i] = "T-" + name;
        }

        this.xpathNames = xpathNames;
    }

    private static @Nullable String maybePunctName(String s) {
        switch (s) {
        case "!": return "bang";
        case "?": return "question";
        case ":": return "colon";
        case ";": return "semi";
        case ",": return "comma";
        case "(": return "lparen";
        case ")": return "rparen";
        }
        return null;
    }

    public @NonNull String getXPathName(int tokenType) {
        if (tokenType >= 0 && tokenType < xpathNames.length) {
            return xpathNames[tokenType];
        }

        if (tokenType == Token.EOF) {
            return "EOF";
        }

        throw new IllegalArgumentException("I don't know token type " + tokenType);
    }

    @Override
    public int getMaxTokenType() {
        return vocabulary.getMaxTokenType();
    }

    @Override
    public String getLiteralName(int tokenType) {
        return vocabulary.getLiteralName(tokenType);
    }

    @Override
    public String getSymbolicName(int tokenType) {
        return vocabulary.getSymbolicName(tokenType);
    }

    @Override
    public String getDisplayName(int tokenType) {
        return vocabulary.getDisplayName(tokenType);
    }
}

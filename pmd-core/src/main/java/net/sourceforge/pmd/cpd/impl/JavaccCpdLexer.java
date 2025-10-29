/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.impl;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Base class for a {@link CpdLexer} for a language implemented by a JavaCC tokenizer.
 */
public abstract class JavaccCpdLexer extends CpdLexerBase<JavaccToken> {


    @Override
    public Set<String> commonImages() {
        Set<String> images = new HashSet<>();
        for (String name : javaccTokenNames()) {
            if (name.startsWith("\"") && name.endsWith("\"")) {
                name = StringUtil.removeSurrounding(name, '"');
                images.add(name);
            }
            // other tokens have names like <token > but no quotes.
        }
        return images;
    }

    protected List<String> javaccTokenNames() {
        return Collections.emptyList();
    }
}

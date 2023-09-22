/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.antlr.v4.runtime.Vocabulary;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrNameDictionary;


final class KotlinNameDictionary extends AntlrNameDictionary {

    KotlinNameDictionary(Vocabulary vocab, String[] ruleNames) {
        super(vocab, ruleNames);
    }

    @Override
    protected @Nullable String nonAlphaNumName(String name) {
        // todo
        return super.nonAlphaNumName(name);
    }
}

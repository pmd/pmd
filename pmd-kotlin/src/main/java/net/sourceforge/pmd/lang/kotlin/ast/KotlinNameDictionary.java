/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrNameDictionary;
import org.antlr.v4.runtime.Vocabulary;

final class KotlinNameDictionary extends AntlrNameDictionary {

    KotlinNameDictionary(Vocabulary vocab, String[] ruleNames) {
        super(vocab, ruleNames);
    }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd.impl;

import net.sourceforge.pmd.cpd.CpdLexer;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

/**
 * Base class for a {@link CpdLexer} for a language implemented by a JavaCC tokenizer.
 */
public abstract class JavaccCpdLexer extends CpdLexerBase<JavaccToken> {

}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm.cpd;

import java.io.IOException;

import net.sourceforge.pmd.cpd.impl.JavaccCpdLexer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.vm.ast.VmTokenKinds;

/**
 * <p>Note: This class has been called VmTokenizer in PMD 6</p>.
 */
public class VmCpdLexer extends JavaccCpdLexer {
    @Override
    protected TokenManager<JavaccToken> makeLexerImpl(TextDocument doc) throws IOException {
        return VmTokenKinds.newTokenManager(CharStream.create(doc));
    }
}

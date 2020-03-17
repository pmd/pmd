/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.apache.commons.io.input.CharSequenceReader;

import net.sourceforge.pmd.cpd.internal.JavaCCTokenizer;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.vf.ast.VfTokenManager;
import net.sourceforge.pmd.util.IOUtil;

/**
 * @author sergey.gorbaty
 */
public class VfTokenizer extends JavaCCTokenizer {

    @Override
    protected TokenManager getLexerForSource(SourceCode sourceCode) {
        return new VfTokenManager(IOUtil.skipBOM(new CharSequenceReader(sourceCode.getCodeBuffer())));
    }
}

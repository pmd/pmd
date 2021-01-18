/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.io.Reader;

import net.sourceforge.pmd.lang.ast.SimpleCharStream;

/**
 * @author sergey.gorbaty
 *
 * @deprecated Just use {@link SimpleCharStream} directly. The only difference is
 *             the tabSize. With PMD 6.27.0 the tabSize is set to 1 for all languages, which
 *             simplifies calculation of the positions of AST nodes. PMD Designer also
 *             uses a tabSize of 1. If a mapping from PMD's beginColumn or endColumn to
 *             the text editor coordinates is needed, this needs to be done manually.
 *             Before it would only have been correct, if the editor used a tabSize of 4.
 */
@Deprecated
public class VfSimpleCharStream extends SimpleCharStream {

    public VfSimpleCharStream(Reader dstream) {
        super(dstream);
        tabSize = 4;
    }

}

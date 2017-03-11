/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import java.io.Reader;

import net.sourceforge.pmd.lang.ast.SimpleCharStream;

/**
 * @author sergey.gorbaty
 *
 */
public class VfSimpleCharStream extends SimpleCharStream {

    public VfSimpleCharStream(Reader dstream) {
        super(dstream);
        tabSize = 4;
    }

}

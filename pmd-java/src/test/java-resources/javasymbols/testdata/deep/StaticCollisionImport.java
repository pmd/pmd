/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.deep;

import static javasymbols.testdata.StaticNameCollision.Ola;
import static javasymbols.testdata.Statics.oha;


public class StaticCollisionImport {

    Ola o;

    static {
        oha();
    }

}

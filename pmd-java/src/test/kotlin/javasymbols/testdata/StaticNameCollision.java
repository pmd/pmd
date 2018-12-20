/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata;

/**
 * @author Clément Fournier
 * @since 7.0.0
 */
public class StaticNameCollision {

    public static final int Ola = 0;


    public static int Ola() {
        return 0;
    }


    public static class Ola {

    }
}

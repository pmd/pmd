/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;


/**
 * @author Clément Fournier
 * @since 6.2.0
 */
public enum EnumWithInterfaces implements InterfaceWithBound<int[]> {
    Foo {
        @Override
        public void handle(int[] ints) {
            super.handle(ints);
        }
    };

    @Override
    public void handle(int[] ints) {

    }
}

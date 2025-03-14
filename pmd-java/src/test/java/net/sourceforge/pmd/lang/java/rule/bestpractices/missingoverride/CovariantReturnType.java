/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

public class CovariantReturnType extends AbstractClass {

    @Override
    String fun(String s) {
        return "";
    }
}

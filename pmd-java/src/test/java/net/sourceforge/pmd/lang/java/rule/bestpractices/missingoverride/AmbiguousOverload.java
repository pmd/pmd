/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

import java.util.Comparator;


public class AmbiguousOverload implements Comparator<StringBuilder> {


    // only one of those overloads is an override, and so there's only one bridge,
    // so we can't choose the inherited overload


    @Override
    public int compare(StringBuilder o1, StringBuilder o2) {
        return 0;
    }


    public int compare(String s, String s2) {
        return 0;
    }


}

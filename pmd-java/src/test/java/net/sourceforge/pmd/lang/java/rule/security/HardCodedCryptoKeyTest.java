/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import net.sourceforge.pmd.test.PmdRuleTst;

class HardCodedCryptoKeyTest extends PmdRuleTst {
    // no additional unit tests
    public interface Constants {
        String SOME_STRING = "NOT_SECRET";
        String DYNAMIC_STRING = "ALPHABET".substring((int) (Math.random() * 4));
    }
}

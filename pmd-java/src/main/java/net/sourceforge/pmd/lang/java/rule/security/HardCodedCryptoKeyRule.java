/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;

/**
 * Finds hard coded encryption keys that are passed to
 * javax.crypto.spec.SecretKeySpec(key, algorithm).
 *
 * @author sergeygorbaty
 * @since 6.4.0
 */
public class HardCodedCryptoKeyRule extends HardCodedConstructorArgsBaseRule {

    public HardCodedCryptoKeyRule() {
        super(ASTConstructorCall.class, javax.crypto.spec.SecretKeySpec.class);
    }

}

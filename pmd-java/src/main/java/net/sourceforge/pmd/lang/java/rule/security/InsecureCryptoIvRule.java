/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

/**
 * Finds hardcoded static Initialization Vectors vectors used with cryptographic
 * operations.
 *
 * <code>
 * //bad: byte[] ivBytes = new byte[] {32, 87, -14, 25, 78, -104, 98, 40};
 * //bad: byte[] ivBytes = "hardcoded".getBytes();
 * //bad: byte[] ivBytes = someString.getBytes();
 * </code>
 *
 * <p>{@link javax.crypto.spec.IvParameterSpec} must not be created from a static sources
 *
 * @author sergeygorbaty
 * @since 6.3.0
 *
 */
public class InsecureCryptoIvRule extends HardCodedConstructorArgsBaseRule {

    public InsecureCryptoIvRule() {
        super(javax.crypto.spec.IvParameterSpec.class);
    }
}

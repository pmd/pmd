/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

class InvalidTypeSignatureException extends IllegalArgumentException {

    public InvalidTypeSignatureException(String s) {
        super(s);
    }
}

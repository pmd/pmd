/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.objectweb.asm.signature.SignatureVisitor;

/**
 *
 */
public class SigVisitor extends SignatureVisitor {

    public SigVisitor() {
        super(AsmSymbolResolver.ASM_API_V);
    }

}

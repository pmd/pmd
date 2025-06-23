/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;

import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

class GenericTypeParameterCounter extends SignatureVisitor {
    private int count;

    GenericTypeParameterCounter() {
        super(AsmSymbolResolver.ASM_API_V);
    }

    @Override
    public void visitFormalTypeParameter(String name) {
        count++;
    }

    public int getCount() {
        return count;
    }

    static int determineTypeParameterCount(String signature) {
        if (signature == null) {
            return 0;
        }

        SignatureReader signatureReader = new SignatureReader(signature);
        GenericTypeParameterCounter counter = new GenericTypeParameterCounter();
        signatureReader.accept(counter);
        return counter.getCount();
    }
}

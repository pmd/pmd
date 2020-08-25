/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.internal.asm;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import net.sourceforge.pmd.lang.java.symbols.JFormalParamSymbol;
import net.sourceforge.pmd.lang.java.symbols.internal.asm.ExecutableStub.FormalParamStub;

class MethodInfoVisitor extends MethodVisitor {

    private final ExecutableStub execStub;
    private List<JFormalParamSymbol> params = Collections.emptyList();

    public MethodInfoVisitor(ExecutableStub execStub) {
        super(AsmSymbolResolver.ASM_API_V);
        this.execStub = execStub;
    }

    @Override
    public void visitParameter(String name, int access) {
        if (params.isEmpty()) {
            params = new ArrayList<>();// make it writable
        }
        boolean isFinal = (access & Opcodes.ACC_FINAL) != 0;
        FormalParamStub paramStub = execStub.new FormalParamStub(params.size(), isFinal, name);
        params.add(paramStub);
        super.visitParameter(name, access);
    }

    @Override
    public void visitEnd() {
        execStub.setParams(params);
        super.visitEnd();
    }
}

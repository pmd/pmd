/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

public class VarArgsMethodUseCase {

    public void foo() {
        MethodFirstPhase methods = new MethodFirstPhase();
        methods.stringVarargs("a", "b");
        methods.classVarargs(String.class, VarArgsMethodUseCase.class);
    }
}

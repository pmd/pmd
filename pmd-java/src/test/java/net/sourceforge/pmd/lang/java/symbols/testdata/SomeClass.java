/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.testdata;

@TypeAnnotation
@AnnotWithDefaults(valueNoDefault = "ohio",
                   stringArrayDefault = {})
public class SomeClass {

    @FieldAnnotation
    private int f1;
    
    @ConstructorAnnotation
    public SomeClass() {
        @LocalVarAnnotation
        long local;
    }

    void m1(int a, @ParameterAnnotation final String foo) {

    }

    @MethodAnnotation
    void m4(final int x) {

    }

}

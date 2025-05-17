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
        
    }

    void withAnnotatedParam(int a, @ParameterAnnotation final String foo) {
        
    }

    void withAnnotatedLocal() {
        @LocalVarAnnotation
        long local;
    }
    
    @MethodAnnotation
    void anotatedMethod(final int x) {

    }

}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.testdata;

import net.sourceforge.pmd.lang.java.symbols.internal.asm.TypeAnnotReflectionOnMethodsTest;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside.A;
import net.sourceforge.pmd.lang.java.symbols.testdata.ClassWithTypeAnnotationsInside.B;

/**
 * See {@link TypeAnnotReflectionOnMethodsTest}.
 *
 * @author Clément Fournier
 */
public abstract class ClassWithTypeAnnotationsOnMethods {

    abstract void aOnIntParam(@A int i);

    abstract void aOnStringParam(@A String i);

    abstract @A @B String abOnReturn(@A String i);

    abstract void aOnThrows() throws @A RuntimeException;

    abstract <@A @B T, E extends T> void abOnTypeParm();
    abstract <@A @B T, E extends T> T abOnTypeParm2(T t);
    abstract <@A T, E extends @B T> void bOnTypeParmBound();
    abstract <@A T, E extends @B T> E bOnTypeParmBound(T t);

    static class CtorOwner {

        CtorOwner(@A @B int i) { }
        @A CtorOwner() { }
        CtorOwner(String i) throws @A RuntimeException {}
    }

}

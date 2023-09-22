/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.testdata;

import java.io.Serializable;
import java.util.List;

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

    abstract List<@A String> abOnReturnInArg();

    abstract void aOnThrows() throws @A Exception;

    // tparams
    abstract <@A @B T, E extends T> void abOnTypeParm();

    abstract <@A @B T, E extends T> T abOnTypeParm2(T t);

    // tparam bounds
    abstract <@A T, E extends @B T> E bOnTypeParmBound(T t);

    abstract <@A T, E extends @B Cloneable & @A Serializable> E bOnTypeParmBoundIntersection(T t);


    abstract void abOnReceiver(@A @B ClassWithTypeAnnotationsOnMethods this);

    static class CtorOwner {

        CtorOwner(@A @B int i) { }

        @A CtorOwner() { }

        CtorOwner(String i) throws @A Exception {}
    }

}

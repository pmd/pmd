/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symbols.testdata;

import net.sourceforge.pmd.lang.java.symbols.TypeAnnotReflectionOnMethodsTest;
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

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * This interface captures Java access modifiers.
 */
public interface AccessNode extends Annotatable {

    int PUBLIC = 0x0001;
    int PROTECTED = 0x0002;
    int PRIVATE = 0x0004;
    int ABSTRACT = 0x0008;
    int STATIC = 0x0010;
    int FINAL = 0x0020;
    int SYNCHRONIZED = 0x0040;
    int NATIVE = 0x0080;
    int TRANSIENT = 0x0100;
    int VOLATILE = 0x0200;
    int STRICTFP = 0x1000;
    int DEFAULT = 0x2000;


    int getModifiers();


    boolean isPublic();



    boolean isProtected();


    boolean isPrivate();


    boolean isAbstract();



    boolean isStatic();


    boolean isFinal();



    boolean isSynchronized();



    boolean isNative();


    boolean isTransient();


    boolean isVolatile();


    boolean isStrictfp();


    boolean isPackagePrivate();


    boolean isDefault();
}

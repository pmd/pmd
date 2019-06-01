/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * This interface captures Java access modifiers.
 */
public interface AccessNode extends Node {

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


    @Deprecated
    @InternalApi
    void setModifiers(int modifiers);


    boolean isPublic();


    @Deprecated
    @InternalApi
    void setPublic(boolean isPublic);


    boolean isProtected();


    @Deprecated
    @InternalApi
    void setProtected(boolean isProtected);


    boolean isPrivate();


    @Deprecated
    @InternalApi
    void setPrivate(boolean isPrivate);


    boolean isAbstract();


    @Deprecated
    @InternalApi
    void setAbstract(boolean isAbstract);


    boolean isStatic();


    @Deprecated
    @InternalApi
    void setStatic(boolean isStatic);


    boolean isFinal();


    @Deprecated
    @InternalApi
    void setFinal(boolean isFinal);


    boolean isSynchronized();


    @Deprecated
    @InternalApi
    void setSynchronized(boolean isSynchronized);


    boolean isNative();


    @Deprecated
    @InternalApi
    void setNative(boolean isNative);


    boolean isTransient();


    @Deprecated
    @InternalApi
    void setTransient(boolean isTransient);


    boolean isVolatile();


    @Deprecated
    @InternalApi
    void setVolatile(boolean isVolatile);


    boolean isStrictfp();


    @Deprecated
    @InternalApi
    void setStrictfp(boolean isStrictfp);


    boolean isPackagePrivate();


    @Deprecated
    @InternalApi
    void setDefault(boolean isDefault);


    boolean isDefault();
}

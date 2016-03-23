/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

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
    int TRANSIENT = 0x0100;

    int getModifiers();

    void setModifiers(int modifiers);

    boolean isPublic();

    void setPublic(boolean isPublic);

    boolean isProtected();

    void setProtected(boolean isProtected);

    boolean isPrivate();

    void setPrivate(boolean isPrivate);

    boolean isAbstract();

    void setAbstract(boolean isAbstract);

    boolean isStatic();

    void setStatic(boolean isStatic);

    boolean isFinal();

    void setFinal(boolean isFinal);

    void setDefault(boolean isDefault);

    boolean isDefault();
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.Node;

/**
 * This interface captures access modifiers.
 */
public interface AccessNode extends Node {

    int PUBLIC = 0x0001;
    int PRIVATE = 0x0002;
    int PROTECTED = 0x0004;
    int STATIC = 0x0008;
    int FINAL = 0x0010;
    int TRANSIENT = 0x0080;
    int ABSTRACT = 0x0400;

    int getModifiers();

    boolean isPublic();

    boolean isProtected();

    boolean isPrivate();

    boolean isAbstract();

    boolean isStatic();

    boolean isFinal();

    boolean isTransient();
}

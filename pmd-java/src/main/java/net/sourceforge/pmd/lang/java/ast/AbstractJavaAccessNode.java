/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

abstract class AbstractJavaAccessNode extends AbstractJavaAnnotatableNode implements AccessNode {

    private int modifiers;

    AbstractJavaAccessNode(int i) {
        super(i);
    }

    AbstractJavaAccessNode(JavaParser parser, int i) {
        super(parser, i);
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public boolean isPublic() {
        return isModifier(PUBLIC);
    }

    @Override
    public boolean isProtected() {
        return isModifier(PROTECTED);
    }

    @Override
    public boolean isPrivate() {
        return isModifier(PRIVATE);
    }


    @Override
    public boolean isAbstract() {
        return isModifier(ABSTRACT);
    }


    @Override
    public boolean isStatic() {
        return isModifier(STATIC);
    }


    @Override
    public boolean isFinal() {
        return isModifier(FINAL);
    }

    void setFinal(boolean enable) {
        setModifier(enable, FINAL);
    }

    @Override
    public boolean isSynchronized() {
        return isModifier(SYNCHRONIZED);
    }


    @Override
    public boolean isNative() {
        return isModifier(NATIVE);
    }


    @Override
    public boolean isTransient() {
        return isModifier(TRANSIENT);
    }


    @Override
    public boolean isVolatile() {
        return isModifier(VOLATILE);
    }


    @Override
    public boolean isStrictfp() {
        return isModifier(STRICTFP);
    }


    @Override
    public boolean isDefault() {
        return isModifier(DEFAULT);
    }


    private boolean isModifier(int mask) {
        return (modifiers & mask) == mask;
    }

    void setModifier(boolean enable, int mask) {
        if (enable) {
            this.modifiers |= mask;
        } else {
            this.modifiers &= ~mask;
        }
    }

    @Override
    public boolean isPackagePrivate() {
        return !isPrivate() && !isPublic() && !isProtected();
    }
}


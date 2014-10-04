/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.ast;

public abstract class AbstractJavaAccessNode extends AbstractJavaNode implements AccessNode {

    private int modifiers;

    public AbstractJavaAccessNode(int i) {
        super(i);
    }

    public AbstractJavaAccessNode(JavaParser parser, int i) {
        super(parser, i);
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public boolean isPublic() {
        return isModifier(PUBLIC);
    }

    public void setPublic(boolean isPublic) {
        setModifier(isPublic, PUBLIC);
    }

    public boolean isProtected() {
        return isModifier(PROTECTED);
    }

    public void setProtected(boolean isProtected) {
        setModifier(isProtected, PROTECTED);
    }

    public boolean isPrivate() {
        return isModifier(PRIVATE);
    }

    public void setPrivate(boolean isPrivate) {
        setModifier(isPrivate, PRIVATE);
    }

    public boolean isAbstract() {
        return isModifier(ABSTRACT);
    }

    public void setAbstract(boolean isAbstract) {
        setModifier(isAbstract, ABSTRACT);
    }

    public boolean isStatic() {
        return isModifier(STATIC);
    }

    public void setStatic(boolean isStatic) {
        setModifier(isStatic, STATIC);
    }

    public boolean isFinal() {
        return isModifier(FINAL);
    }

    public void setFinal(boolean isFinal) {
        setModifier(isFinal, FINAL);
    }

    public boolean isSynchronized() {
        return isModifier(SYNCHRONIZED);
    }

    public void setSynchronized(boolean isSynchronized) {
        setModifier(isSynchronized, SYNCHRONIZED);
    }

    public boolean isNative() {
        return isModifier(NATIVE);
    }

    public void setNative(boolean isNative) {
        setModifier(isNative, NATIVE);
    }

    public boolean isTransient() {
        return isModifier(TRANSIENT);
    }

    public void setTransient(boolean isTransient) {
        setModifier(isTransient, TRANSIENT);
    }

    public boolean isVolatile() {
        return isModifier(VOLATILE);
    }

    public void setVolatile(boolean isVolative) {
        setModifier(isVolative, VOLATILE);
    }

    public boolean isStrictfp() {
        return isModifier(STRICTFP);
    }

    public void setStrictfp(boolean isStrictfp) {
        setModifier(isStrictfp, STRICTFP);
    }

    public boolean isDefault() {
        return isModifier(DEFAULT);
    }

    public void setDefault(boolean isDefault) {
        setModifier(isDefault, DEFAULT);
    }

    @SuppressWarnings("PMD.UselessParentheses") //TODO: fix the rule - around binary expressions the parentheses are needed...
    private final boolean isModifier(int mask) {
        return (modifiers & mask) == mask;
    }

    private void setModifier(boolean enable, int mask) {
        if (enable) {
            this.modifiers |= mask;
        } else {
            this.modifiers &= ~mask;
        }
    }

    public boolean isPackagePrivate() {
        return !isPrivate() && !isPublic() && !isProtected();
    }
}

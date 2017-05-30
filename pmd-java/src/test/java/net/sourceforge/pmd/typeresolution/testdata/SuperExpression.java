/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata;

public class SuperExpression extends SuperClass {
    public SuperExpression() {
        SuperClass a = super.s;
    }

    protected SuperExpression b;

    { SuperClass s = super.s; }

    public void foo() {
        SuperClass a = super.s;
    }

    SuperClass a = super.s;

    public class SuperExprNested extends SuperExpression {
        SuperClass a = SuperExpression.super.s;
        SuperExpression b = super.b;
    }

    public static class ThisExprStaticNested extends SuperExpression {
        SuperExpression a = super.b;
    }
}


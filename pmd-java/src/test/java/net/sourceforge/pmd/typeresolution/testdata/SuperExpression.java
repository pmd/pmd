/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;

public class SuperExpression extends SuperClassA {
    public SuperExpression() {
        SuperClassA a = super.s;
    }

    protected SuperExpression b;

    { SuperClassA s = super.s; }

    public void foo() {
        SuperClassA a = super.s;
    }

    SuperClassA a = super.s;

    public class SuperExprNested extends SuperExpression {
        SuperClassA a = SuperExpression.super.s;
        SuperExpression b = super.b;
    }

    public static class ThisExprStaticNested extends SuperExpression {
        SuperExpression a = super.b;
    }
}


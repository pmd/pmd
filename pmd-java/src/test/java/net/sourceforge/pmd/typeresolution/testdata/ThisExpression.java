package net.sourceforge.pmd.typeresolution.testdata;

public class ThisExpression {
    public ThisExpression() { ThisExpression a = this; }

    { ThisExpression a = this; }

    public void foo() {
        ThisExpression a = this;
    }

    ThisExpression a = this;

    public class ThisExprNested {
        ThisExprNested a = this;
    }

    public static class ThisExprStaticNested {
        ThisExprStaticNested a = this;
    }
}


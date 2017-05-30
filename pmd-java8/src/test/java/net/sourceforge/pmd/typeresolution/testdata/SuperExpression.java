package net.sourceforge.pmd.typeresolution.testdata;

public class SuperExpression extends SuperClass {
    public void foo() {
        ((Runnable) (() -> { SuperClass a = super.s; })).run();
    }
}


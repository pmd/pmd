package net.sourceforge.pmd.typeresolution.testdata;

public enum EnumWithAnonymousInnerClass {
    A;
    interface Inner { int get(); }
    private static final Inner VAL = new Inner() {
        @Override public int get() { return 1; }
    };
}

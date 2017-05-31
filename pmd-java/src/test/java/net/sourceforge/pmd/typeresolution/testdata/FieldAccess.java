package net.sourceforge.pmd.typeresolution.testdata;


public class FieldAccess extends SuperClass {
    public int field;
    public FieldAccess f;
    public GenericClass<String> generic;

    public void foo() {
        // "f.f.f.field" goes into a single Name node
        f.f.f.field = 10;

        // f.f.f.field, each goes into a separate Suffix/Name node
        (this).f.f.field = 10;

        // Primary[Prefix["this"], Suffix["field"]]
        this.field = 10;

        // Primary[Prefix["super"], Suffix["field"]]
        super.s = new SuperClass();


        // example of simple field access, the scope has to be searched
        // produces Primary[Prefix["field"]]
        field = 10;

        String field;
        // example of simple field access with shadowed scope
        field = "";

        // example of field access on an arbitrary PrimaryExpression["(this)"]
        (this).field = 10;
    }

    public class Nested {
        // Primary[Prefix["FieldAccess"], Suffix["super"], Suffix["field"]]
        // refers to the enclosing class's immediate super class's field
        SuperClass a = FieldAccess.super.s;

        public void foo() {
            field = 10;
            a = new SuperClass();
        }
    }
}

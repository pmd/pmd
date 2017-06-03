package net.sourceforge.pmd.typeresolution.testdata;


public class FieldAccess extends SuperClass {
    public int field;
    public FieldAccess f;

    public void foo(FieldAccess param) {
        FieldAccess local = null;

        // access through method parameter
        // Primary[Prefix[Name[param.field]]]
        param.field = 10;

        // access through local
        // Primary[Prefix[Name[local.field]]]
        local.field = 10;

        // "f.f.f.field" goes into a single Name node
        // Primary[Prefix[Name["f.f.f.field"]]]
        f.f.f.field = 10;

        // f.f.f.field, each goes into a separate Suffix/Name node
        // Primary[Prefix[Primary[(this)], Suffix[f], Suffix[f], Suffix[field]]]
        (this).f.f.field = 10;

        // field access through super
        // Primary[Prefix["super"], Suffix["field"]]
        super.s = new SuperClass();

        // simple field access
        // Primary[Prefix["field"]]
        field = 10;

        String field;

        // example of simple field access with shadowed scope
        // Primary[Prefix["field"]]
        field = "";

        // field access through this
        // Primary[Prefix["this"], Suffix["field"]]
        this.field = 10;

        // example of field access on an arbitrary PrimaryExpression[(this)]
        // Primary[ Prefix[Primary[(this)]], Suffix[field] ]
        (this).field = 10;
    }

    public class Nested {
        SuperClass a;

        public void foo() {
            // refers to the enclosing class's immediate super class's field
            // Primary[Prefix["FieldAccess"], Suffix["super"], Suffix["field"]]
            FieldAccess.super.s = new SuperClass();

            // access enclosing scope field
            // Primary[Prefix[field]]
            field = 10;

            // access field in nested
            // Primary[Prefix[a]]
            a = new SuperClass();
        }
    }
}

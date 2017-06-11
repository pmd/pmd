/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata;


import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA2;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB;


/*
 * Note: inherited fields of a nested class shadow outer scope variables
 *      Note: only if they are accessible!
 *
 * TODO: test static field access, array types
 */
public class FieldAccess extends SuperClassA {
    public int field;
    public FieldAccess f;

    public void foo(FieldAccess param) {
        FieldAccess local = null;

        // simple super field access
        // Primary[Prefix[Name[s]]]
        s = new SuperClassA();

        // access inherited field through primary
        // Primary[ Prefix[Primary[(this)]], Suffix[s], Suffix[s2] ]
        (this).s.s2 = new SuperClassA2();

        // access inherited field
        // Primary[Prefix[Name[s.s.s2]]]
        s.s.s2 = new SuperClassA2();

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
        super.s = new SuperClassA();

        // simple field access
        // Primary[Prefix["field"]]
        field = 10;

        String field;

        // example of simple field access with shadowed scope
        // Primary[Prefix["field"]]
        field = "shadow";

        // field access through this
        // Primary[Prefix["this"], Suffix["field"]]
        this.field = 10;

        // example of field access on an arbitrary PrimaryExpression[(this)]
        // Primary[ Prefix[Primary[(this)]], Suffix[field] ]
        (this).field = 10;
    }

    Number privateShadow;

    public class NestedShadow extends SuperClassB {
        public void foo() {
            // SuperClassB's "s" field shadows enclosing scope's inherited field
            // Primary[Prefix[Name[s]]]
            s = new SuperClassB();

            // SuperClassB has an inaccessible field "privateShadow", it should not
            // shadow enclosing scope's privateShadow member field
            // Primary[Prefix[Name[privateShadow]]]
            privateShadow = 10;
        }
    }

    public class Nested {
        SuperClassA a;

        public void foo() {
            // access enclosing super field
            // Primary[Prefix[Name[s]]]
            s = new SuperClassA();

            // refers to the enclosing class's immediate super class's field
            // Primary[Prefix["FieldAccess"], Suffix["super"], Suffix["field"]]
            FieldAccess.super.s = new SuperClassA();

            // access enclosing scope field
            // Primary[Prefix[field]]
            field = 10;

            // access field in nested
            // Primary[Prefix[a]]
            a = new SuperClassA();
        }
    }
}

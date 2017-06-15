/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata;


import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;


/*
 * Note: inherited fields of a nested class shadow outer scope variables
 *      Note: only if they are accessible!
 *
 * TODO: test static field access, array types, anonymous class (super type access)
 */
public class FieldAccess extends SuperClassA {
    public int field;
    public FieldAccess f;
    public static FieldAccess staticF;

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

        // simple field access
        // Primary[Prefix["field"]]
        field = 10;
    }
}


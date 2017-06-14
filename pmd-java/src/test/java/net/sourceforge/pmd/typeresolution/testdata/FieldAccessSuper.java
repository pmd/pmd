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
 */
public class FieldAccessSuper extends SuperClassA {
    public void foo() {
        // simple super field access
        // Primary[Prefix[Name[s]]]
        s = new SuperClassA();

        // access inherited field through primary
        // Primary[ Prefix[Primary[(this)]], Suffix[s], Suffix[s2] ]
        (this).s.s2 = new SuperClassA2();

        // access inherited field, second 's' has inherited field 's2'
        // Primary[Prefix[Name[s.s.s2]]]
        s.s.s2 = new SuperClassA2();

        // field access through super
        // Primary[Prefix["super"], Suffix["field"]]
        super.s = new SuperClassA();

        // fully qualified case
        // Primary[Prefix[Name[net...FieldAccessSuper]], Suffix[this], Suffix[s]]
        net.sourceforge.pmd.typeresolution.testdata.FieldAccessSuper.this.s
                = new SuperClassA();
    }

    public class Nested extends SuperClassB {
        SuperClassA a;

        public void foo() {
            // access enclosing super field
            // Primary[Prefix[Name[s]]]
            s = new SuperClassA();

            // access Nested inherited field
            // Primary[Prefix[Name[bs]]]
            bs = new SuperClassB();

            // access super field with fully qualified stuff
            // Primary[Prefix["FieldAccessSuper"], Suffix[Nested],
            //                  Suffix["super"], Suffix["bs"]]
            FieldAccessSuper.Nested.super.bs = new SuperClassB();

            // refers to the enclosing class's immediate super class's field
            // Primary[Prefix["FieldAccessSuper"], Suffix["super"], Suffix["s"]]
            FieldAccessSuper.super.s = new SuperClassA();
        }
    }
}

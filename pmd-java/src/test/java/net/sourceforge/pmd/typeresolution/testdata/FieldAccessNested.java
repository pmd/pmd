/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata;


import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassA;


/*
 * Note: inherited fields of a nested class shadow outer scope variables
 *      Note: only if they are accessible!
 */
public class FieldAccessNested {
    public int field;

    public class Nested {
        SuperClassA a;

        public void foo() {
            // access enclosing scope field
            // Primary[Prefix[field]]
            field = 10;

            // access field in nested
            // Primary[Prefix[a]]
            a = new SuperClassA();

            net.sourceforge.pmd.typeresolution.testdata.FieldAccessNested.Nested.this.a = new SuperClassA();
            FieldAccessNested.Nested.this.a = new SuperClassA();
        }
    }
}

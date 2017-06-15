/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.typeresolution.testdata;


import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.SuperClassB2;


/*
 * Note: inherited fields of a nested class shadow outer scope variables
 *      Note: only if they are accessible!
 */
public class FieldAccessShadow {
    Integer field;
    String s2;

    public void foo() {
        String field;

        // example of simple field access with shadowed scope
        // Primary[Prefix["field"]]
        field = "shadow";

        // field access through this
        // Primary[Prefix["this"], Suffix["field"]]
        this.field = new Integer(10);

        // example of field access on an arbitrary PrimaryExpression[(this)]
        // Primary[ Prefix[Primary[(this)]], Suffix[field] ]
        (this).field = new Integer(10);
    }

    Number privateShadow;

    public class NestedShadow extends SuperClassB {
        public void foo() {
            // SuperClassB's "s2" field shadows enclosing scope's inherited field
            // Primary[Prefix[Name[s2]]]
            s2 = new SuperClassB2();

            // SuperClassB has an inaccessible field "privateShadow", it should not
            // shadow enclosing scope's privateShadow member field
            // Primary[Prefix[Name[privateShadow]]]
            privateShadow = 10;
        }
    }
}

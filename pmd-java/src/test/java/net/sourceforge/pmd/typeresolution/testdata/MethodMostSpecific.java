/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import java.util.AbstractCollection;
import java.util.AbstractList;
import java.util.ArrayList;
import javax.management.relation.RoleList;


public abstract class MethodMostSpecific {
    void test() {
        // Hiararchy: Object, AbstractCollection, AbstractList, ArrayList, RoleList

        String a = moreSpecific((Number) null, (AbstractCollection) null);
        Exception b = moreSpecific((Integer) null, (AbstractList) null);
        int c = moreSpecific((Double) null, (RoleList) null);
    }

    String moreSpecific(Number a, AbstractCollection b) {
        return null;
    }

    Exception moreSpecific(Integer a, AbstractList b) {
        return null;
    }

    int moreSpecific(Number a, ArrayList b) {
        return 0;
    }
}

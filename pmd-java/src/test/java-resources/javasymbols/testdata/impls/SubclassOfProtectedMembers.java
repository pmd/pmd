/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.impls;

import javasymbols.testdata.ProtectedMembers;

/**
 * A subclass of {@link ProtectedMembers} in another package.
 */
public class SubclassOfProtectedMembers extends ProtectedMembers {

    public SubclassOfProtectedMembers() {
        super(0);
    }
}

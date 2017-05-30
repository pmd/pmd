/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.visitor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Signature mask for an operation.
 *
 * @author Clément Fournier
 */
public class OperationSigMask extends SigMask<OperationSignature> {

    private Set<OperationSignature.Role> roleMask = new HashSet<>();
    private boolean isAbstractIncluded = false;

    public void setRoleMask(OperationSignature.Role... roles) {
        roleMask.clear();
        roleMask.addAll(Arrays.asList(roles));
    }

    public void setAbstractIncluded(boolean isAbstractIncluded) {
        this.isAbstractIncluded = isAbstractIncluded;
    }

    public void setAllRoles() {
        roleMask.addAll(Arrays.asList(OperationSignature.Role.values()));
    }

    public void remove(OperationSignature.Role... roles) {
        roleMask.removeAll(Arrays.asList(roles));
    }

    @Override
    public boolean covers(OperationSignature sig) {
        return super.covers(sig) && roleMask.contains(sig.role) && (isAbstractIncluded || !sig.isAbstract);
    }

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.metrics.OperationSignature.Role;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class OperationSigMask extends SigMask<OperationSignature> {

    private Set<Role> roleMask           = new HashSet<>();
    private boolean   isAbstractIncluded = false;
    
    public void setRoleMask(Role... roles) {
        roleMask.clear();
        roleMask.addAll(Arrays.asList(roles));
    }
    
    public void setAbstractIncluded(boolean isAbstractIncluded) {
        this.isAbstractIncluded = isAbstractIncluded;
    }
    
    public void setAllRoles() {
        roleMask.addAll(Arrays.asList(Role.ALL));
    }
    
    public void remove(Role... roles) {
        roleMask.removeAll(Arrays.asList(roles));
    }

    @Override
    public boolean covers(OperationSignature sig) {
        return super.covers(sig) && roleMask.contains(sig.role) && (isAbstractIncluded || !sig.isAbstract);
    }

}

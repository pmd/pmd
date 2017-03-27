/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.metrics.OperationSignature.Role;
import net.sourceforge.pmd.lang.java.metrics.Signature.Visibility;

/**
 * @author Clément Fournier (clement.fournier@insa-rennes.fr)
 *
 */
public class OperationSigMask {

    private Set<Visibility> visMask            = new HashSet<>();
    private Set<Role>       roleMask           = new HashSet<>();
    private boolean         isAbstractIncluded = false;
    
    public void setVisibilityMask(Visibility... visibilities) {
        visMask.clear();
        visMask.addAll(Arrays.asList(visibilities));
    }
    
    public void setRoleMask(Role... roles) {
        roleMask.clear();
        roleMask.addAll(Arrays.asList(roles));
    }
    
    public void setAbstractIncluded(boolean isAbstractIncluded) {
        this.isAbstractIncluded = isAbstractIncluded;
    }
    
    public void setAllVisibility() {
        visMask.addAll(Arrays.asList(Visibility.ALL));
    }

    public void setAllRoles() {
        roleMask.addAll(Arrays.asList(Role.ALL));
    }
    
    public void remove(Visibility... visibilities) {
        visMask.removeAll(Arrays.asList(visibilities));
    }
    
    public void remove(Role... roles) {
        roleMask.removeAll(Arrays.asList(roles));
    }

    public boolean covers(OperationSignature sig) {
        return visMask.contains(sig.visibility) && roleMask.contains(sig.role)
                && (isAbstractIncluded || !sig.isAbstract);
    }

}

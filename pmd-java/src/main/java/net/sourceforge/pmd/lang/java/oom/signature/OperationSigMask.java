/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.oom.signature;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Signature mask for an operation. Newly created masks cover any operation that is not abstract.
 *
 * @author Clément Fournier
 */
public final class OperationSigMask extends SigMask<OperationSignature> {

    private Set<OperationSignature.Role> roleMask = new HashSet<>();
    private boolean coverAbstract = false;

    public OperationSigMask() {
        super();
        coverAllRoles();
    }

    /**
     * Restricts the roles covered by the mask to the parameters.
     *
     * @param roles The roles to cover
     */
    public void restrictRolesTo(OperationSignature.Role... roles) {
        roleMask.clear();
        roleMask.addAll(Arrays.asList(roles));
    }

    /**
     * Sets the mask to cover all roles.
     */
    public void coverAllRoles() {
        roleMask.addAll(Arrays.asList(OperationSignature.Role.values()));
    }

    /**
     * Forbid all mentioned roles.
     *
     * @param roles The roles to forbid
     */
    public void forbid(OperationSignature.Role... roles) {
        roleMask.removeAll(Arrays.asList(roles));
    }

    /**
     * Forbid all mentioned visibilities.
     *
     * @param coverAbstract The visibilities to forbid
     */
    public void coverAbstract(boolean coverAbstract) {
        this.coverAbstract = coverAbstract;
    }

    @Override
    public boolean covers(OperationSignature sig) {
        return super.covers(sig) && roleMask.contains(sig.role) && (coverAbstract
            || !sig.isAbstract);
    }
}

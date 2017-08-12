/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.multifile.signature;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.multifile.signature.JavaOperationSignature.Role;
import net.sourceforge.pmd.lang.java.multifile.signature.JavaSignature.Visibility;

/**
 * Signature mask for an operation. Newly created masks cover any operation that is not abstract.
 *
 * @author Clément Fournier
 */
public final class JavaOperationSigMask extends JavaSigMask<JavaOperationSignature> {

    private Set<JavaOperationSignature.Role> roleMask = EnumSet.allOf(Role.class);
    private boolean coverAbstract = false;


    /**
     * Creates an operation sig mask that covers any non-abstract operation.
     */
    public JavaOperationSigMask() {
        // everything's initialized
    }


    /**
     * Sets the mask to cover all roles.
     */
    public JavaOperationSigMask coverAllRoles() {
        roleMask.addAll(Arrays.asList(JavaOperationSignature.Role.values()));
        return this;
    }


    /**
     * Restricts the roles covered by the mask to the parameters.
     *
     * @param roles The roles to cover
     */
    public JavaOperationSigMask restrictRolesTo(JavaOperationSignature.Role... roles) {
        roleMask.clear();
        roleMask.addAll(Arrays.asList(roles));
        return this;
    }


    /**
     * Forbid all mentioned roles.
     *
     * @param roles The roles to forbid
     */
    public JavaOperationSigMask forbid(JavaOperationSignature.Role... roles) {
        roleMask.removeAll(Arrays.asList(roles));
        return this;
    }


    /**
     * Sets the mask to cover abstract operations.
     */
    public JavaOperationSigMask coverAbstract() {
        this.coverAbstract = true;
        return this;
    }


    /**
     * Forbid abstract operations.
     *
     * @return this
     */
    public JavaOperationSigMask forbidAbstract() {
        coverAbstract = false;
        return this;
    }


    @Override
    public JavaOperationSigMask coverAllVisibilities() {
        super.coverAllVisibilities();
        return this;
    }


    @Override
    public JavaOperationSigMask restrictVisibilitiesTo(Visibility... visibilities) {
        super.restrictVisibilitiesTo(visibilities);
        return this;
    }


    @Override
    public JavaOperationSigMask forbid(Visibility... visibilities) {
        super.forbid(visibilities);
        return this;
    }


    @Override
    public boolean covers(JavaOperationSignature sig) {
        return super.covers(sig) && roleMask.contains(sig.role) && (coverAbstract
            || !sig.isAbstract);
    }
}

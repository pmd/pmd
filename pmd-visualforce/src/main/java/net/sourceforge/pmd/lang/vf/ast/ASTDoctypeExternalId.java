/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTDoctypeExternalId extends AbstractVFNode {

    /**
     * URI of the external entity. Cannot be null.
     */
    private String uri;

    /**
     * Public ID of the external entity. This is optional.
     */
    private String publicId;

    @Deprecated
    @InternalApi
    public ASTDoctypeExternalId(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTDoctypeExternalId(VfParser p, int id) {
        super(p, id);
    }

    public boolean isHasPublicId() {
        return null != publicId;
    }

    public String getUri() {
        return uri;
    }

    @Deprecated
    @InternalApi
    public void setUri(String uri) {
        this.uri = uri;
    }

    /**
     * @return Returns the publicId (or an empty string if there is none for
     *         this external entity id).
     */
    public String getPublicId() {
        return null == publicId ? "" : publicId;
    }

    @Deprecated
    @InternalApi
    public void setPublicId(String publicId) {
        this.publicId = publicId;
    }

    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}

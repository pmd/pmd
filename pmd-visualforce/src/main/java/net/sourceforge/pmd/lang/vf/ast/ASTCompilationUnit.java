/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.properties.PropertySource;

public class ASTCompilationUnit extends AbstractVFNode implements RootNode {
    /**
     * Holds the properties contained in {@code VfParserOptions}. This class acts as an intermediary since it is
     * accessible by {@code VFParser} and {@code VFHandler}. {@code VfHandler} uses this value to initialize the
     * {@code VfExpressionTypeVisitor} for type resolution.
     */
    private PropertySource propertySource;

    @Deprecated
    @InternalApi
    public ASTCompilationUnit(int id) {
        super(id);
    }

    @Deprecated
    @InternalApi
    public ASTCompilationUnit(VfParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(VfParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public PropertySource getPropertySource() {
        return propertySource;
    }

    public void setPropertySource(PropertySource propertySource) {
        this.propertySource = propertySource;
    }
}

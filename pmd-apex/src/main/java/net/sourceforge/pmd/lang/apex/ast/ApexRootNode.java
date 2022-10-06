/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import com.google.summit.ast.declaration.TypeDeclaration;

@Deprecated
@InternalApi
public abstract class ApexRootNode<T extends TypeDeclaration> extends AbstractApexNode.Single<T> implements RootNode {
    @Deprecated
    @InternalApi
    public ApexRootNode(T node) {
        super(node);
    }

    @Override
    void calculateLineNumbers(SourceCodePositioner positioner) {
        super.calculateLineNumbers(positioner);
        
        if (getParent() == null) {
            // For top level classes, the end is the end of file.
            this.endLine = positioner.getLastLine();
            this.endColumn = positioner.getLastLineColumn();
        } else {
            // For nested classes, look for the position of the last child, which has a real location
            for (int i = getNumChildren() - 1; i >= 0; i--) {
                ApexNode<?> child = getChild(i);
                if (child.hasRealLoc()) {
                    this.endLine = child.getEndLine();
                    this.endColumn = child.getEndColumn();
                    break;
                }
            }
        }
    }

    /**
     * Gets the apex version this class has been compiled with.
     * Use {@link Version} to compare, e.g.
     * {@code node.getApexVersion() >= Version.V176.getExternal()}
     * @return the apex version
     */
    public double getApexVersion() {
        // return node.getDefiningType().getCodeUnitDetails().getVersion().getExternal();
        // TODO(b/239648780)
        return 0;
    }

    @Override
    public boolean isFindBoundary() {
        return true;
    }
}

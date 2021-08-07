/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.nio.file.Paths;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

import apex.jorje.semantic.ast.AstNode;
import apex.jorje.services.Version;

@Deprecated
@InternalApi
public abstract class ApexRootNode<T extends AstNode> extends AbstractApexNode<T> implements RootNode {

    private String fileName;

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
        return node.getDefiningType().getCodeUnitDetails().getVersion().getExternal();
    }

    void setFileName(String fileName) {
        // remove prefixed path segments.
        this.fileName = Paths.get(fileName).getFileName().toString();
    }

    /**
     * Returns the name of the file, including its extension. This
     * excludes any segments for containing directories.
     */
    public String getFileName() {
        if (fileName == null) {
            // a nested class
            return getFirstParentOfType(ApexRootNode.class).getFileName();
        }
        return fileName;
    }

    @Override
    public boolean isFindBoundary() {
        return true;
    }
}

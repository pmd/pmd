/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dcd.graph;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import net.sourceforge.pmd.dcd.ClassLoaderUtil;

/**
 * Represents a Class Field in a UsageGraph.
 */
@SuppressWarnings("PMD.OverrideBothEqualsAndHashcode")
public class FieldNode extends MemberNode<FieldNode, Field> {

    private WeakReference<Field> fieldReference;

    public FieldNode(ClassNode classNode, String name, String desc) {
        super(classNode, name, desc);
        getMember();
    }

    @Override
    public Field getMember() {
        Field field = fieldReference == null ? null : fieldReference.get();
        if (field == null) {
            field = ClassLoaderUtil.getField(getClassNode().getType(), name);
            this.fieldReference = new WeakReference<>(field);
        }
        return field;
    }

    @Override
    public int compareTo(FieldNode that) {
        return this.name.compareTo(that.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FieldNode) {
            FieldNode that = (FieldNode) obj;
            return super.equals(that);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.pmd.dcd.graph.MemberNode#hashCode()
     */
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

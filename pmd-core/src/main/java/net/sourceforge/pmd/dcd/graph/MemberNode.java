/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.dcd.graph;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.dcd.DCD;

/**
 * Represents a Class Member in a UsageGraph.
 * @deprecated See {@link DCD}
 */
@Deprecated
public abstract class MemberNode<S extends MemberNode<S, T>, T extends Member>
    implements NodeVisitorAcceptor, Comparable<S> {
    protected final ClassNode classNode;

    protected final String name;

    protected final String desc;

    private List<MemberNode> uses;

    private List<MemberNode> users;

    public MemberNode(ClassNode classNode, String name, String desc) {
        this.classNode = classNode;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public Object accept(NodeVisitor visitor, Object data) {
        visitor.visitUses(this, data);
        visitor.visitUsers(this, data);
        return data;
    }

    public ClassNode getClassNode() {
        return classNode;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public abstract T getMember();

    public void addUse(MemberNode use) {
        if (uses == null) {
            uses = new ArrayList<>(1);
        }
        if (!uses.contains(use)) {
            uses.add(use);
        }
    }

    public List<MemberNode> getUses() {
        return uses != null ? uses : Collections.<MemberNode>emptyList();
    }

    public void addUser(MemberNode user) {
        if (users == null) {
            users = new ArrayList<>(1);
        }
        if (!users.contains(user)) {
            users.add(user);
        }
    }

    public List<MemberNode> getUsers() {
        return users != null ? users : Collections.<MemberNode>emptyList();
    }

    @Override
    public String toString() {
        return name + ' ' + desc;
    }

    public String toStringLong() {
        return getMember().toString();
    }

    @SuppressWarnings("PMD.SuspiciousEqualsMethodName")
    @Deprecated // To be removed with PMD 7.0.0
    public boolean equals(S that) {
        return equals(that.name, that.desc);
    }

    public boolean equals(String name, String desc) {
        return this.name.equals(name) && this.desc.equals(desc);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((desc == null) ? 0 : desc.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MemberNode other = (MemberNode) obj;
        if (desc == null) {
            if (other.desc != null) {
                return false;
            }
        } else if (!desc.equals(other.desc)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }
}

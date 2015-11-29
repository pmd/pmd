/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.graph;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Class Member in a UsageGraph.
 */
public abstract class MemberNode<S extends MemberNode<S, T>, T extends Member> implements NodeVisitorAcceptor,
		Comparable<S> {
	protected final ClassNode classNode;

	protected final String name;

	protected final String desc;

	private List<MemberNode> uses;

	private List<MemberNode> users;

	private Object decoration;

	public MemberNode(ClassNode classNode, String name, String desc) {
		this.classNode = classNode;
		this.name = name;
		this.desc = desc;
	}

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
		return uses != null ? uses : Collections.<MemberNode> emptyList();
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
		return users != null ? users : Collections.<MemberNode> emptyList();
	}

	public String toString() {
		return name + " " + desc;
	}

	public String toStringLong() {
		return getMember().toString();
	}

	public abstract boolean equals(Object that);

	@SuppressWarnings("PMD.SuspiciousEqualsMethodName")
	public boolean equals(S that) {
		return equals(that.name, that.desc);
	}

	public boolean equals(String name, String desc) {
		return this.name.equals(name) && this.desc.equals(desc);
	}

	public int hashCode() {
		return name.hashCode() + desc.hashCode();
	}
}

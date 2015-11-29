/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.dcd.ClassLoaderUtil;
import net.sourceforge.pmd.util.filter.Filter;

/**
 * A UsageGraph tracks usage references between Java classes, based upon
 * a parsing of the class files.  Once the UsageGraph is built, it may be
 * visited to perform additional post-processing, or usage relationship
 * analysis.
 * <p>
 * The UsageGraph is composed of ClassNodes.  Each ClassNode has various
 * MemberNodes, specifically ConstructorNodes, FieldNodes, and MethodNodes.
 * Each of these MemberNodes keeps track of other MemberNodes which it
 * <em>uses</em> and other MemberNodes which are <em>users</em> of it.  In
 * this sense, the graph can navigated bi-directionally across the <em>use</em>
 * relationship between MemberNodes.
 * <p>
 * Great effort is taken to keep the bookkeeping of the UsageGraph as tight
 * as possible, so that rather large code bases can be analyzed.  While nodes
 * can grant access to the underlying Java Reflection APIs (e.g. Class,
 * Constructor, Field, Member), the results are stored using WeakReferences
 * to assist with memory usage.
 * <p>
 * A class Filter can be specified to limit the set of classes on which
 * usage references will be tracked.  This is often done to limit memory
 * usage to interesting classes.  For example, the <code>java.util</code>
 * package is very often used, and tracking usages would require a massive
 * bookkeeping effort which has little value.
 *
 * @see UsageGraphBuilder
 * @see ClassNode
 * @see MemberNode
 * @see ConstructorNode
 * @see FieldNode
 * @see MethodNode
 * @see NodeVisitor
 * @see NodeVisitorAcceptor
 */
public class UsageGraph implements NodeVisitorAcceptor {

	private final List<ClassNode> classNodes = new ArrayList<>();

	protected final Filter<String> classFilter;

	public UsageGraph(Filter<String> classFilter) {
		this.classFilter = classFilter;
	}

	public Object accept(NodeVisitor visitor, Object data) {
		for (ClassNode classNode : classNodes) {
			visitor.visit(classNode, data);
		}
		return data;
	}

	public boolean isClass(String className) {
		checkClassName(className);
		return Collections.binarySearch(classNodes, className, ClassNodeComparator.INSTANCE) >= 0;
	}

	public ClassNode defineClass(String className) {
		checkClassName(className);
		int index = Collections.binarySearch(classNodes, className, ClassNodeComparator.INSTANCE);
		ClassNode classNode;
		if (index >= 0) {
			classNode = classNodes.get(index);
		} else {
			classNode = new ClassNode(className);
			classNodes.add(-(index + 1), classNode);
		}
		return classNode;
	}

	public FieldNode defineField(String className, String name, String desc) {
		ClassNode classNode = defineClass(className);
		return classNode.defineField(name, desc);
	}

	public MemberNode defineConstructor(String className, String name, String desc) {
		ClassNode classNode = defineClass(className);
		return classNode.defineConstructor(name, desc);
	}

	public MemberNode defineMethod(String className, String name, String desc) {
		ClassNode classNode = defineClass(className);
		if (ClassLoaderUtil.CLINIT.equals(name) || ClassLoaderUtil.INIT.equals(name)) {
			return classNode.defineConstructor(name, desc);
		} else {
			return classNode.defineMethod(name, desc);
		}
	}

	public void usageField(String className, String name, String desc, MemberNode usingMemberNode) {
		checkClassName(className);
		if (classFilter.filter(className)) {
			FieldNode fieldNode = defineField(className, name, desc);
			usage(fieldNode, usingMemberNode);
		}
	}

	public void usageMethod(String className, String name, String desc, MemberNode usingMemberNode) {
		checkClassName(className);
		if (classFilter.filter(className)) {
			MemberNode memberNode;
			if (ClassLoaderUtil.CLINIT.equals(name) || ClassLoaderUtil.INIT.equals(name)) {
				memberNode = defineConstructor(className, name, desc);
			} else {
				memberNode = defineMethod(className, name, desc);
			}
			usage(memberNode, usingMemberNode);
		}
	}

	private void usage(MemberNode use, MemberNode user) {
		use.addUser(user);
		user.addUse(use);
	}

	private final void checkClassName(String className) {
		// Make sure it's not in byte code internal format, or file system path.
		if (className.indexOf('/') >= 0 || className.indexOf('\\') >= 0) {
			throw new IllegalArgumentException("Invalid class name: " + className);
		}
	}
}

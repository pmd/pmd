/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.graph;

/**
 * Visitor for nodes in a UsageGraph.
 */
public interface NodeVisitor {

	public Object visit(UsageGraph usageGraph, Object data);

	public Object visit(ClassNode classNode, Object data);

	public Object visitFields(ClassNode classNode, Object data);

	public Object visit(FieldNode fieldNode, Object data);

	public Object visitConstructors(ClassNode classNode, Object data);

	public Object visit(ConstructorNode constructorNode, Object data);

	public Object visitMethods(ClassNode classNode, Object data);

	public Object visit(MethodNode methodNode, Object data);

	public Object visitUses(MemberNode memberNode, Object data);

	public Object visitUse(MemberNode use, Object data);

	public Object visitUsers(MemberNode memberNode, Object data);

	public Object visitUser(MemberNode user, Object data);
}

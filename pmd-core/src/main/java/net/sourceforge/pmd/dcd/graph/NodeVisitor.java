/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.dcd.graph;

/**
 * Visitor for nodes in a UsageGraph.
 */
public interface NodeVisitor {

	Object visit(UsageGraph usageGraph, Object data);

	Object visit(ClassNode classNode, Object data);

	Object visitFields(ClassNode classNode, Object data);

	Object visit(FieldNode fieldNode, Object data);

	Object visitConstructors(ClassNode classNode, Object data);

	Object visit(ConstructorNode constructorNode, Object data);

	Object visitMethods(ClassNode classNode, Object data);

	Object visit(MethodNode methodNode, Object data);

	Object visitUses(MemberNode memberNode, Object data);

	Object visitUse(MemberNode use, Object data);

	Object visitUsers(MemberNode memberNode, Object data);

	Object visitUser(MemberNode user, Object data);
}

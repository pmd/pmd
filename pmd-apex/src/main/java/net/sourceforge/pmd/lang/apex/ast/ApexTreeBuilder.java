/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import net.sourceforge.pmd.lang.ast.Node;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.compilation.UserClass;
import apex.jorje.semantic.ast.member.Method;
import apex.jorje.semantic.ast.visitor.AdditionalPassScope;
import apex.jorje.semantic.ast.visitor.AstVisitor;

public final class ApexTreeBuilder extends AstVisitor<AdditionalPassScope> {

	private static final Map<Class<? extends AstNode>, Constructor<? extends ApexNode<?>>> NODE_TYPE_TO_NODE_ADAPTER_TYPE = new HashMap<>();
	static {
		register(Method.class, ASTMethod.class);
		register(UserClass.class, ASTUserClass.class);
	}

	private static <T extends AstNode> void register(Class<T> nodeType,
			Class<? extends ApexNode<T>> nodeAdapterType) {
		try {
			NODE_TYPE_TO_NODE_ADAPTER_TYPE.put(nodeType,
					nodeAdapterType.getConstructor(nodeType));
		}
		catch (SecurityException e) {
			throw new RuntimeException(e);
		}
		catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	// The nodes having children built.
	private Stack<Node> nodes = new Stack<>();

	// The Apex nodes with children to build.
	private Stack<AstNode> parents = new Stack<>();

	static <T extends AstNode> ApexNode<T> createNodeAdapter(T node) {
		try {
			@SuppressWarnings("unchecked")
			// the register function makes sure only ApexNode<T> can be added,
			// where T is "T extends AstNode".
			Constructor<? extends ApexNode<T>> constructor = (Constructor<? extends ApexNode<T>>) NODE_TYPE_TO_NODE_ADAPTER_TYPE
					.get(node.getClass());
			if (constructor == null) {
				throw new IllegalArgumentException(
						"There is no Node adapter class registered for the Node class: "
								+ node.getClass());
			}
			return constructor.newInstance(node);
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e) {
			throw new RuntimeException(e.getTargetException());
		}
	}

	public <T extends AstNode> ApexNode<T> build(T astNode) {
		// Create a Node
		ApexNode<T> node = createNodeAdapter(astNode);

		// Append to parent
		Node parent = nodes.isEmpty() ? null : nodes.peek();
		if (parent != null) {
			parent.jjtAddChild(node, parent.jjtGetNumChildren());
			node.jjtSetParent(parent);
		}

		// Build the children...
		nodes.push(node);
		parents.push(astNode);
		astNode.traverse(this, null);
		nodes.pop();
		parents.pop();

		return node;
	}

	public boolean visit(AstNode node) {
		if (parents.peek() == node) {
			return true;
		}
		else {
			build(node);
			return false;
		}
	}
}

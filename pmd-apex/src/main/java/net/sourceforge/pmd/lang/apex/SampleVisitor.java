package net.sourceforge.pmd.lang.apex;

import java.util.List;

import apex.jorje.semantic.ast.member.Method;
import apex.jorje.semantic.ast.member.Parameter;
import apex.jorje.semantic.ast.visitor.AdditionalPassScope;
import apex.jorje.semantic.ast.visitor.AstVisitor;
import apex.jorje.semantic.symbol.member.method.MethodInfo;

/**
 * Here is an example of a visitor.
 * 
 * @author nchen
 *
 */
public class SampleVisitor extends AstVisitor<AdditionalPassScope> {

	@Override
	protected boolean defaultVisit() {
		// Returning true signals that it should visit all nodes.
		// You can be more selective and override the individual visit(...)
		// nodes to be more selective. Take a look at the visit(...) method.
		// Each one signifies an AstNode.
		return true;
	}

	@Override
	public void visitEnd(Method method, AdditionalPassScope scope) {
		// The visitEnd(...) method is called *after* the visit(...) method if
		// the visit(...) method returns true;
		MethodInfo methodInfo = method.getMethodInfo();
		List<Parameter> parameters = methodInfo.getParameters();

		// In this example, this is called several times because we have several
		// "hidden" methods that are defined.
		System.out.println(String.format("Saw %d parameters for method: %s", parameters.size(), methodInfo.getName()));
	}
}

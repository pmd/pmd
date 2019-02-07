package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class ArrayInitializationVerbosenessRule extends AbstractJavaRule {

  @Override
  public Object visit(ASTLocalVariableDeclaration node, Object data) {

    if (node.isArray()) {
      if (node.getImage().matches("(new).+\\{"))
        addViolation(data, node);
      return super.visit(node, data);
    }

    return super.visit(node, data);
  }
}

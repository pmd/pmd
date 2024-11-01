/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.bestpractices;

import java.util.List;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTMethodCallExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;

/**
 * Scans classes which implement the `Queueable` interface. If the `public void
 * execute(QueueableContext context)` method does not call the `System.attachFinalizer(Finalizer f)`
 * method, then a violation will be added to the `execute` method.
 *
 * @author mitchspano
 */
public class QueueableShouldAttachFinalizerRule extends AbstractApexRule {

  private static final String EXECUTE = "execute";
  private static final String QUEUEABLE = "queueable";
  private static final String QUEUEABLE_CONTEXT = "queueablecontext";
  private static final String SYSTEM_ATTACH_FINALIZER = "system.attachfinalizer";

  /** Scans the top level class and all inner classes. */
  @Override
  public Object visit(ASTUserClass topLevelClass, Object data) {
    scanClassForViolation(topLevelClass, data);
    for (ASTUserClass innerClass : topLevelClass.descendants(ASTUserClass.class).toList()) {
      scanClassForViolation(innerClass, data);
    }
    return data;
  }

  /**
   * If the class implements the `Queueable` interface and the `execute(QueueableContext context)`
   * does not call the `System.attachFinalizer(Finalizer f)` method, then add a violation.
   */
  private void scanClassForViolation(ASTUserClass theClass, Object data) {
    if (!implementsTheQueueableInterface(theClass)) {
      return;
    }
    for (ASTMethod theMethod : theClass.descendants(ASTMethod.class).toList()) {
      if (isTheExecuteMethodOfTheQueueableInterface(theMethod)
          && !callsTheSystemAttachFinalizerMethod(theMethod)) {
        asCtx(data).addViolation(theMethod);
      }
    }
  }

  /** Determines if the class implements the Queueable interface. */
  private boolean implementsTheQueueableInterface(ASTUserClass theClass) {
    for (String interfaceName : theClass.getInterfaceNames()) {
      if (interfaceName.equalsIgnoreCase(QUEUEABLE)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if the method is the `execute(QueueableContext context)` method. Parameter count is
   * checked to account for method overloading.
   */
  private boolean isTheExecuteMethodOfTheQueueableInterface(ASTMethod theMethod) {
    if (!theMethod.getCanonicalName().equalsIgnoreCase(EXECUTE)) {
      return false;
    }
    List<ASTParameter> parameters = theMethod.descendants(ASTParameter.class).toList();
    return parameters.size() == 1
        && parameters.get(0).getType().equalsIgnoreCase(QUEUEABLE_CONTEXT);
  }

  /** Determines if the method calls the `System.attachFinalizer(Finalizer f)` method. */
  private boolean callsTheSystemAttachFinalizerMethod(ASTMethod theMethod) {
    for (ASTMethodCallExpression methodCallExpression :
        theMethod.descendants(ASTMethodCallExpression.class).toList()) {
      if (methodCallExpression.getFullMethodName().equalsIgnoreCase(SYSTEM_ATTACH_FINALIZER)) {
        return true;
      }
    }
    return false;
  }
}

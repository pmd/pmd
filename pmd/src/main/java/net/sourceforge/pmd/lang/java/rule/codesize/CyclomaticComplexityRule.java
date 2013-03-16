/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.codesize;

import java.util.List;
import java.util.Stack;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * @author Donald A. Leckie,
 *
 * @version $Revision: 5956 $, $Date: 2008-04-04 04:59:25 -0500 (Fri, 04 Apr 2008) $
 * @since January 14, 2003
 */
public class CyclomaticComplexityRule extends AbstractJavaRule {

    public static final IntegerProperty REPORT_LEVEL_DESCRIPTOR = new IntegerProperty("reportLevel",
	    "Cyclomatic Complexity reporting threshold", 1, 30, 10, 1.0f);

    public static final BooleanProperty SHOW_CLASSES_COMPLEXITY_DESCRIPTOR = new BooleanProperty("showClassesComplexity",
	"Add class average violations to the report", true, 2.0f);

    public static final BooleanProperty SHOW_METHODS_COMPLEXITY_DESCRIPTOR = new BooleanProperty("showMethodsComplexity",
	"Add method average violations to the report", true, 3.0f);

  private int reportLevel;
  private boolean showClassesComplexity = true;
  private boolean showMethodsComplexity = true;

  private static class Entry {
    private Node node;
    private int decisionPoints = 1;
    public int highestDecisionPoints;
    public int methodCount;

    private Entry(Node node) {
      this.node = node;
    }

    public void bumpDecisionPoints() {
      decisionPoints++;
    }

    public void bumpDecisionPoints(int size) {
      decisionPoints += size;
    }

    public int getComplexityAverage() {
      return (double) methodCount == 0 ? 1
          : (int) Math.rint( (double) decisionPoints / (double) methodCount );
    }
  }

  private Stack<Entry> entryStack = new Stack<Entry>();

  public CyclomaticComplexityRule() {
      definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
      definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
      definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
  }

  @Override
public Object visit(ASTCompilationUnit node, Object data) {
    reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
    showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
    showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
    super.visit( node, data );
    return data;
  }

  @Override
public Object visit(ASTIfStatement node, Object data) {
    int boolCompIf = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
    // If statement always has a complexity of at least 1
    boolCompIf++;

    entryStack.peek().bumpDecisionPoints( boolCompIf );
    super.visit( node, data );
    return data;
  }

  @Override
public Object visit(ASTCatchStatement node, Object data) {
    entryStack.peek().bumpDecisionPoints();
    super.visit( node, data );
    return data;
  }

  @Override
public Object visit(ASTForStatement node, Object data) {
    int boolCompFor = NPathComplexityRule.sumExpressionComplexity( node.getFirstDescendantOfType( ASTExpression.class ) );
    // For statement always has a complexity of at least 1
    boolCompFor++;

    entryStack.peek().bumpDecisionPoints( boolCompFor );
    super.visit( node, data );
    return data;
  }

  @Override
public Object visit(ASTDoStatement node, Object data) {
    int boolCompDo = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
    // Do statement always has a complexity of at least 1
    boolCompDo++;

    entryStack.peek().bumpDecisionPoints( boolCompDo );
    super.visit( node, data );
    return data;
  }

  @Override
public Object visit(ASTSwitchStatement node, Object data) {
    Entry entry = entryStack.peek();

    int boolCompSwitch = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
    entry.bumpDecisionPoints( boolCompSwitch );

    int childCount = node.jjtGetNumChildren();
    int lastIndex = childCount - 1;
    for ( int n = 0; n < lastIndex; n++ ) {
      Node childNode = node.jjtGetChild( n );
      if ( childNode instanceof ASTSwitchLabel ) {
        // default is generally not considered a decision (same as "else")
        ASTSwitchLabel sl = (ASTSwitchLabel) childNode;
        if ( !sl.isDefault() ) {
          childNode = node.jjtGetChild( n + 1 );
          if ( childNode instanceof ASTBlockStatement ) {
            entry.bumpDecisionPoints();
          }
        }
      }
    }
    super.visit( node, data );
    return data;
  }

  @Override
public Object visit(ASTWhileStatement node, Object data) {
    int boolCompWhile = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
    // While statement always has a complexity of at least 1
    boolCompWhile++;

    entryStack.peek().bumpDecisionPoints( boolCompWhile );
    super.visit( node, data );
    return data;
  }

  @Override
public Object visit(ASTConditionalExpression node, Object data) {
    if ( node.isTernary() ) {
      int boolCompTern = NPathComplexityRule.sumExpressionComplexity( node.getFirstChildOfType( ASTExpression.class ) );
      // Ternary statement always has a complexity of at least 1
      boolCompTern++;

      entryStack.peek().bumpDecisionPoints( boolCompTern );
      super.visit( node, data );
    }
    return data;
  }

  @Override
public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
    if ( node.isInterface() ) {
      return data;
    }

    entryStack.push( new Entry( node ) );
    super.visit( node, data );
    if ( showClassesComplexity ) {
    	Entry classEntry = entryStack.pop();
	    if ( classEntry.getComplexityAverage() >= reportLevel
	        || classEntry.highestDecisionPoints >= reportLevel ) {
	      addViolation( data, node, new String[] {
	          "class",
	          node.getImage(),
	          classEntry.getComplexityAverage() + " (Highest = "
	              + classEntry.highestDecisionPoints + ')' } );
	    }
    }
    return data;
  }

  @Override
public Object visit(ASTMethodDeclaration node, Object data) {
    entryStack.push( new Entry( node ) );
    super.visit( node, data );
	    Entry methodEntry = entryStack.pop();
    if (!isSuppressed(node)) {
	    int methodDecisionPoints = methodEntry.decisionPoints;
	    Entry classEntry = entryStack.peek();
	    classEntry.methodCount++;
	    classEntry.bumpDecisionPoints( methodDecisionPoints );

	    if ( methodDecisionPoints > classEntry.highestDecisionPoints ) {
	      classEntry.highestDecisionPoints = methodDecisionPoints;
	    }

	    ASTMethodDeclarator methodDeclarator = null;
	    for ( int n = 0; n < node.jjtGetNumChildren(); n++ ) {
	      Node childNode = node.jjtGetChild( n );
	      if ( childNode instanceof ASTMethodDeclarator ) {
	        methodDeclarator = (ASTMethodDeclarator) childNode;
	        break;
	      }
	    }

	    if ( showMethodsComplexity && methodEntry.decisionPoints >= reportLevel ) {
	        addViolation( data, node, new String[] { "method",
	            methodDeclarator == null ? "" : methodDeclarator.getImage(),
	            String.valueOf( methodEntry.decisionPoints ) } );
	      }
    }
    return data;
  }

  @Override
public Object visit(ASTEnumDeclaration node, Object data) {
    entryStack.push( new Entry( node ) );
    super.visit( node, data );
    Entry classEntry = entryStack.pop();
    if ( classEntry.getComplexityAverage() >= reportLevel
        || classEntry.highestDecisionPoints >= reportLevel ) {
      addViolation( data, node, new String[] {
          "class",
          node.getImage(),
          classEntry.getComplexityAverage() + "(Highest = "
              + classEntry.highestDecisionPoints + ')' } );
    }
    return data;
  }

  @Override
public Object visit(ASTConstructorDeclaration node, Object data) {
    entryStack.push( new Entry( node ) );
    super.visit( node, data );
    Entry constructorEntry = entryStack.pop();
    if (!isSuppressed(node)) {
    int constructorDecisionPointCount = constructorEntry.decisionPoints;
    Entry classEntry = entryStack.peek();
    classEntry.methodCount++;
    classEntry.decisionPoints += constructorDecisionPointCount;
    if ( constructorDecisionPointCount > classEntry.highestDecisionPoints ) {
      classEntry.highestDecisionPoints = constructorDecisionPointCount;
    }
    if ( showMethodsComplexity && constructorEntry.decisionPoints >= reportLevel ) {
      addViolation( data, node, new String[] { "constructor",
          classEntry.node.getImage(),
          String.valueOf( constructorDecisionPointCount ) } );
    }
    }
    return data;
  }

  private boolean isSuppressed(Node node) {
      boolean result = false;

      ASTClassOrInterfaceBodyDeclaration parent = node.getFirstParentOfType(ASTClassOrInterfaceBodyDeclaration.class);
      List<ASTAnnotation> annotations = parent.findChildrenOfType(ASTAnnotation.class);
      for (ASTAnnotation a : annotations) {
          ASTName name = a.getFirstDescendantOfType(ASTName.class);
          if ("SuppressWarnings".equals(name.getImage())) {
              List<ASTLiteral> literals = a.findDescendantsOfType(ASTLiteral.class);
              for (ASTLiteral l : literals) {
                  if (l.isStringLiteral() && "\"PMD.CyclomaticComplexity\"".equals(l.getImage())) {
                      result = true;
                      break;
                  }
              }
          }
          if (result) {
              break;
          }
      }

      return result;
  }
}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import java.util.Stack;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.apex.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTryCatchFinallyBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTCompilation;
import net.sourceforge.pmd.lang.apex.ast.ASTBooleanExpression;
import net.sourceforge.pmd.lang.apex.ast.ASTConstructorPreambleStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTForEachStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTIfBlockStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTWhileLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTDoLoopStatement;
import net.sourceforge.pmd.lang.apex.ast.ASTTernaryExpression;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Implements the standard cyclomatic complexity rule
 * <p>
 * Standard rules: +1 for each decision point, including case statements but not
 * including boolean operators unlike CyclomaticComplexityRule.
 * 
 * @author ported on Java version of Alan Hohn, based on work by Donald A.
 *         Leckie
 * 
 * @since June 18, 2014
 */
public class StdCyclomaticComplexityRule extends AbstractApexRule {

	public static final IntegerProperty REPORT_LEVEL_DESCRIPTOR = new IntegerProperty("reportLevel",
			"Cyclomatic Complexity reporting threshold", 1, 30, 10, 1.0f);

	public static final BooleanProperty SHOW_CLASSES_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
			"showClassesComplexity", "Add class average violations to the report", true, 2.0f);

	public static final BooleanProperty SHOW_METHODS_COMPLEXITY_DESCRIPTOR = new BooleanProperty(
			"showMethodsComplexity", "Add method average violations to the report", true, 3.0f);

	private int reportLevel;
	private boolean showClassesComplexity = true;
	private boolean showMethodsComplexity = true;

	protected static class Entry {
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
			return (double) methodCount == 0 ? 1 : (int) Math.rint((double) decisionPoints / (double) methodCount);
		}
	}

	protected Stack<Entry> entryStack = new Stack<>();

	public StdCyclomaticComplexityRule() {
		definePropertyDescriptor(REPORT_LEVEL_DESCRIPTOR);
		definePropertyDescriptor(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
		definePropertyDescriptor(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
	}

	@Override
	public Object visit(ASTCompilation node, Object data) {
		reportLevel = getProperty(REPORT_LEVEL_DESCRIPTOR);
		showClassesComplexity = getProperty(SHOW_CLASSES_COMPLEXITY_DESCRIPTOR);
		showMethodsComplexity = getProperty(SHOW_METHODS_COMPLEXITY_DESCRIPTOR);
		super.visit(node, data);
		return data;
	}

	@Override
	public Object visit(ASTIfBlockStatement node, Object data) {
		entryStack.peek().bumpDecisionPoints();
		super.visit(node, data);
		return data;
	}

	@Override
	public Object visit(ASTTryCatchFinallyBlockStatement node, Object data) {
		entryStack.peek().bumpDecisionPoints();
		super.visit(node, data);
		return data;
	}

	@Override
	public Object visit(ASTForLoopStatement node, Object data) {
		entryStack.peek().bumpDecisionPoints();
		super.visit(node, data);
		return data;
	}

	@Override
	public Object visit(ASTForEachStatement node, Object data) {
		entryStack.peek().bumpDecisionPoints();
		super.visit(node, data);
		return data;
	}

	@Override
	public Object visit(ASTWhileLoopStatement node, Object data) {
		entryStack.peek().bumpDecisionPoints();
		super.visit(node, data);
		return data;
	}
	
	@Override
	public Object visit(ASTDoLoopStatement node, Object data) {
		entryStack.peek().bumpDecisionPoints();
		super.visit(node, data);
		return data;
	}
	
	@Override
	public Object visit(ASTTernaryExpression node, Object data) {
		entryStack.peek().bumpDecisionPoints();
		super.visit(node, data);
		return data;
	}

	@Override
	public Object visit(ASTUserInterface node, Object data) {
		return data;
	}

	@Override
	public Object visit(ASTUserClass node, Object data) {
		entryStack.push(new Entry(node));
		super.visit(node, data);
		if (showClassesComplexity) {
			Entry classEntry = entryStack.pop();
			if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
				addViolation(data, node, new String[] { "class", node.getImage(),
						classEntry.getComplexityAverage() + " (Highest = " + classEntry.highestDecisionPoints + ')' });
			}
		}
		return data;
	}

	@Override
	public Object visit(ASTMethod node, Object data) {
		entryStack.push(new Entry(node));
		super.visit(node, data);
		Entry methodEntry = entryStack.pop();
		int methodDecisionPoints = methodEntry.decisionPoints;
		Entry classEntry = entryStack.peek();
		classEntry.methodCount++;
		classEntry.bumpDecisionPoints(methodDecisionPoints);

		if (methodDecisionPoints > classEntry.highestDecisionPoints) {
			classEntry.highestDecisionPoints = methodDecisionPoints;
		}

		if (showMethodsComplexity && methodEntry.decisionPoints >= reportLevel) {
			addViolation(data, node, new String[] { "method", "", String.valueOf(methodEntry.decisionPoints) });
		}
		return data;
	}

	@Override
	public Object visit(ASTUserEnum node, Object data) {
		entryStack.push(new Entry(node));
		super.visit(node, data);
		Entry classEntry = entryStack.pop();
		if (classEntry.getComplexityAverage() >= reportLevel || classEntry.highestDecisionPoints >= reportLevel) {
			addViolation(data, node, new String[] { "class", node.getImage(),
					classEntry.getComplexityAverage() + "(Highest = " + classEntry.highestDecisionPoints + ')' });
		}
		return data;
	}

	public Object visit(ASTConstructorPreambleStatement node, Object data) {
		entryStack.push(new Entry(node));
		super.visit(node, data);
		Entry constructorEntry = entryStack.pop();
		int constructorDecisionPointCount = constructorEntry.decisionPoints;
		Entry classEntry = entryStack.peek();
		classEntry.methodCount++;
		classEntry.decisionPoints += constructorDecisionPointCount;
		if (constructorDecisionPointCount > classEntry.highestDecisionPoints) {
			classEntry.highestDecisionPoints = constructorDecisionPointCount;
		}
		if (showMethodsComplexity && constructorEntry.decisionPoints >= reportLevel) {
			addViolation(data, node, new String[] { "constructor", classEntry.node.getImage(),
					String.valueOf(constructorDecisionPointCount) });
		}
		return data;
	}
}

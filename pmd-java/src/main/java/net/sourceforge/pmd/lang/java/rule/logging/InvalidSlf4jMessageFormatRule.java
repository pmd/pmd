package net.sourceforge.pmd.lang.java.rule.logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaTypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;

import org.apache.commons.lang3.StringUtils;

public class InvalidSlf4jMessageFormatRule extends AbstractJavaRule {

	private static final Set<String> LOGGER_LEVELS;
	private static final String LOOGER_CLASS = "org.slf4j.Logger";
	static {
		LOGGER_LEVELS = Collections.unmodifiableSet(
				new HashSet<String>(Arrays.asList("trace", "debug", "info", "warn", "error")));
	}

	@Override
	public Object visit(final ASTName node, final Object data) {
		final NameDeclaration nameDeclaration = node.getNameDeclaration();
		// ignore imports
		if (nameDeclaration == null) {
			return super.visit(node, data);
		}

		// ignore non slf4j logger
		if (!((ASTVariableDeclaratorId) nameDeclaration.getNode()).getType().getName().equals(LOOGER_CLASS)) {
			return super.visit(node, data);
		}

		// get the node that contains the logger
		final ASTPrimaryExpression parentNode = node.getFirstParentOfType(ASTPrimaryExpression.class);

		// get the log level
		final String method = parentNode.getFirstChildOfType(ASTPrimaryPrefix.class)
				.getFirstChildOfType(ASTName.class).getImage().replace(nameDeclaration.getImage() + ".", "");

		// ignore if not a log level
		if (!LOGGER_LEVELS.contains(method)) {
			return super.visit(node, data);
		}

		// find the arguments
		final List<ASTPrimaryExpression> params = new LinkedList<ASTPrimaryExpression>();
		final List<ASTExpression> argumentList = parentNode.getFirstChildOfType(ASTPrimarySuffix.class)
				.getFirstDescendantOfType(ASTArgumentList.class).findChildrenOfType(ASTExpression.class);
		for (final ASTExpression astExpression : argumentList) {
			params.add(astExpression.getFirstChildOfType(ASTPrimaryExpression.class));
		}

		final ASTPrimaryExpression messageParam = params.get(0);
		//remove the message parameter
		params.remove(0);
		final int expectedArguments = expectedArguments(messageParam);

		if (expectedArguments == 0) {
			// ignore if we are not expecting arguments to format the message
			return super.visit(node, data);
		}

		// Remove throwable param, since it is shown separately.
		removeThrowableParam(params);

		if (params.size() < expectedArguments) {
			addViolationWithMessage(data, node, "Missing arguments," + getExpectedMessage(params, expectedArguments));
		} else if (params.size() > expectedArguments) {
			addViolationWithMessage(data, node, "Too many arguments," + getExpectedMessage(params, expectedArguments));
		}

		return super.visit(node, data);
	}

	private void removeThrowableParam(final List<ASTPrimaryExpression> params) {
		final Iterator<ASTPrimaryExpression> it = params.iterator();
		while (it.hasNext()) {
			final ASTClassOrInterfaceType throwable = it.next().getFirstDescendantOfType(ASTClassOrInterfaceType.class);
			if (throwable != null && Throwable.class.isAssignableFrom(throwable.getType())) {
				it.remove();
			}
		}
	}

	private String getExpectedMessage(final List<ASTPrimaryExpression> params, final int expectedArguments) {
		return " expected " + expectedArguments
				+ (expectedArguments > 1 ? " arguments " : " argument ") + "but have " + params.size();
	}

	private int expectedArguments(@Nonnull final ASTPrimaryExpression node) {
		int count = 0;
		// look if the logger have a literal message
		if (node.getFirstDescendantOfType(ASTLiteral.class) != null) {
			count = countPlaceholders(node);
		} else if (node.getFirstDescendantOfType(ASTName.class) != null) {
			final String variableName = node.getFirstDescendantOfType(ASTName.class).getImage();
			// look if the message is defined locally
			final List<ASTVariableDeclarator> localValiables = node.getFirstParentOfType(ASTMethodDeclaration.class)
					.findDescendantsOfType(ASTVariableDeclarator.class);
			count = getAmountOfExpectedArguments(variableName, localValiables);

			if (count == 0) {
				// look if the message is defined in a field
				final List<ASTFieldDeclaration> fieldlist = node.getFirstParentOfType(ASTClassOrInterfaceBody.class)
						.findDescendantsOfType(ASTFieldDeclaration.class);
				// only look for ASTVariableDeclarator that are Fields
				final List<ASTVariableDeclarator> fields = new LinkedList<ASTVariableDeclarator>();
				for (final ASTFieldDeclaration astFieldDeclaration : fieldlist) {
					fields.add(astFieldDeclaration.getFirstChildOfType(ASTVariableDeclarator.class));
				}
				count = getAmountOfExpectedArguments(variableName, fields);
			}
		}
		return count;
	}

	private int getAmountOfExpectedArguments(final String variableName, final List<ASTVariableDeclarator> variables) {
		for (final ASTVariableDeclarator astVariableDeclarator : variables) {
			if (astVariableDeclarator.getFirstChildOfType(ASTVariableDeclaratorId.class)
					.getImage().equals(variableName)) {
				return countPlaceholders(astVariableDeclarator);
			}
		}
		return 0;
	}

	private int countPlaceholders(final AbstractJavaTypeNode node) {
		return StringUtils.countMatches(node.getFirstDescendantOfType(ASTLiteral.class).getImage(), "{}");
	}
}
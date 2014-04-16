/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.comments;

import java.util.Arrays;

import net.sourceforge.pmd.PropertySource;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.AbstractJavaAccessNode;
import net.sourceforge.pmd.lang.rule.properties.EnumeratedProperty;

/**
 * @author Brian Remedios
 */
public class CommentRequiredRule extends AbstractCommentRule {

	enum CommentRequirement {
		Required("Required"), Ignored("Ignored"), Unwanted("Unwanted");

		private final String label;

		CommentRequirement(String theLabel) {
			label = theLabel;
		}

		public static String[] labels() {
			String[] labels = new String[values().length];
			int i = 0;
			for (CommentRequirement requirement : values()) {
				labels[i++] = requirement.label;
			}
			return labels;
		}
	}

	public static final EnumeratedProperty<CommentRequirement> HEADER_CMT_REQUIREMENT_DESCRIPTOR = new EnumeratedProperty<CommentRequirement>(
			"headerCommentRequirement", "Header comments. Possible values: " + Arrays.toString(CommentRequirement.values()),
			CommentRequirement.labels(), CommentRequirement.values(), 0, 1.0f);

	public static final EnumeratedProperty<CommentRequirement> FIELD_CMT_REQUIREMENT_DESCRIPTOR = new EnumeratedProperty<CommentRequirement>(
			"fieldCommentRequirement", "Field comments. Possible values: " + Arrays.toString(CommentRequirement.values()),
			CommentRequirement.labels(), CommentRequirement.values(), 0, 2.0f);

	public static final EnumeratedProperty<CommentRequirement> PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR = new EnumeratedProperty<CommentRequirement>(
			"publicMethodCommentRequirement", "Public method and constructor comments. Possible values: " + Arrays.toString(CommentRequirement.values()),
			CommentRequirement.labels(), CommentRequirement.values(), 0, 3.0f);

	public static final EnumeratedProperty<CommentRequirement> PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR = new EnumeratedProperty<CommentRequirement>(
			"protectedMethodCommentRequirement", "Protected method constructor comments. Possible values: " + Arrays.toString(CommentRequirement.values()),
			CommentRequirement.labels(), CommentRequirement.values(), 0, 4.0f);

	public static final EnumeratedProperty<CommentRequirement> ENUM_CMT_REQUIREMENT_DESCRIPTOR = new EnumeratedProperty<CommentRequirement>(
			"enumCommentRequirement", "Enum comments. Possible values: " + Arrays.toString(CommentRequirement.values()),
			CommentRequirement.labels(), CommentRequirement.values(), 0, 5.0f);

	public CommentRequiredRule() {
		definePropertyDescriptor(HEADER_CMT_REQUIREMENT_DESCRIPTOR);
		definePropertyDescriptor(FIELD_CMT_REQUIREMENT_DESCRIPTOR);
		definePropertyDescriptor(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
		definePropertyDescriptor(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR);
		definePropertyDescriptor(ENUM_CMT_REQUIREMENT_DESCRIPTOR);
	}

	private CommentRequirement getCommentRequirement(String label) {
		if (CommentRequirement.Ignored.label.equals(label)) {
			return CommentRequirement.Ignored;
		} else if (CommentRequirement.Required.label.equals(label)) {
			return CommentRequirement.Required;
		} else if (CommentRequirement.Unwanted.label.equals(label)) {
			return CommentRequirement.Unwanted;
		} else {
			return null;
		}
	}

	@Override
	public Object visit(ASTClassOrInterfaceDeclaration decl, Object data) {
		CommentRequirement headerRequirement = getCommentRequirement(getProperty(
				HEADER_CMT_REQUIREMENT_DESCRIPTOR).toString());

		if (headerRequirement != CommentRequirement.Ignored) {
			if (headerRequirement == CommentRequirement.Required) {
				if (decl.comment() == null) {
					addViolationWithMessage(data, decl,
							HEADER_CMT_REQUIREMENT_DESCRIPTOR.name() + " "
									+ CommentRequirement.Required,
							decl.getBeginLine(), decl.getEndLine());
				}
			} else {
				if (decl.comment() != null) {
					addViolationWithMessage(data, decl,
							HEADER_CMT_REQUIREMENT_DESCRIPTOR.name() + " "
									+ CommentRequirement.Unwanted,
							decl.getBeginLine(), decl.getEndLine());
				}
			}
		}

		return super.visit(decl, data);
	}

	@Override
	public Object visit(ASTConstructorDeclaration decl, Object data) {
	    checkComment(decl, data);
	    return super.visit(decl, data);
	}

	@Override
	public Object visit(ASTMethodDeclaration decl, Object data) {
	    checkComment(decl, data);
	    return super.visit(decl, data);
	}

	private void checkComment(AbstractJavaAccessNode decl, Object data) {
		CommentRequirement pubMethodRequirement = getCommentRequirement(getProperty(
				PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR).toString());
		CommentRequirement protMethodRequirement = getCommentRequirement(getProperty(
				PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR).toString());

		if (decl.isPublic()) {
			if (pubMethodRequirement != CommentRequirement.Ignored) {
				if (pubMethodRequirement == CommentRequirement.Required) {
					if (decl.comment() == null) {
						addViolationWithMessage(data, decl,
								PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR.name()
										+ " " + CommentRequirement.Required,
								decl.getBeginLine(), decl.getEndLine());
					}
				} else {
					if (decl.comment() != null) {
						addViolationWithMessage(data, decl,
								PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR.name()
										+ " " + CommentRequirement.Unwanted,
								decl.getBeginLine(), decl.getEndLine());
					}
				}
			}
		} else if (decl.isProtected()) {
			if (protMethodRequirement != CommentRequirement.Ignored) {
				if (protMethodRequirement == CommentRequirement.Required) {
					if (decl.comment() == null) {
						addViolationWithMessage(data, decl,
								PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR.name()
										+ " " + CommentRequirement.Required,
								decl.getBeginLine(), decl.getEndLine());
					}
				} else {
					if (decl.comment() != null) {
						addViolationWithMessage(data, decl,
								PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR.name()
										+ " " + CommentRequirement.Unwanted,
								decl.getBeginLine(), decl.getEndLine());
					}
				}
			}
		}
	}

	@Override
	public Object visit(ASTFieldDeclaration decl, Object data) {
		CommentRequirement fieldRequirement = getCommentRequirement(getProperty(
				FIELD_CMT_REQUIREMENT_DESCRIPTOR).toString());

		if (fieldRequirement != CommentRequirement.Ignored) {
			if (fieldRequirement == CommentRequirement.Required) {
				if (decl.comment() == null) {
					addViolationWithMessage(data, decl,
							FIELD_CMT_REQUIREMENT_DESCRIPTOR.name() + " "
									+ CommentRequirement.Required,
							decl.getBeginLine(), decl.getEndLine());
				}
			} else {
				if (decl.comment() != null) {
					addViolationWithMessage(data, decl,
							FIELD_CMT_REQUIREMENT_DESCRIPTOR.name() + " "
									+ CommentRequirement.Unwanted,
							decl.getBeginLine(), decl.getEndLine());
				}
			}
		}

		return super.visit(decl, data);
	}

	@Override
	public Object visit(ASTEnumDeclaration decl, Object data) {
		
		CommentRequirement enumRequirement = getCommentRequirement(getProperty(
				ENUM_CMT_REQUIREMENT_DESCRIPTOR).toString());

		if (enumRequirement != CommentRequirement.Ignored) {
			if (enumRequirement == CommentRequirement.Required) {
				if (decl.comment() == null) {
					addViolationWithMessage(data, decl,
							ENUM_CMT_REQUIREMENT_DESCRIPTOR.name() + " "
									+ CommentRequirement.Required,
							decl.getBeginLine(), decl.getEndLine());
				}
			} else {
				if (decl.comment() != null) {
					addViolationWithMessage(data, decl,
							ENUM_CMT_REQUIREMENT_DESCRIPTOR.name() + " "
									+ CommentRequirement.Unwanted,
							decl.getBeginLine(), decl.getEndLine());
				}
			}
		}

		return super.visit(decl, data);
	}

	@Override
	public Object visit(ASTCompilationUnit cUnit, Object data) {
		assignCommentsToDeclarations(cUnit);

		return super.visit(cUnit, data);
	}

	public boolean allCommentsAreIgnored() {

		return getProperty(HEADER_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored
				&& getProperty(FIELD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored
				&& getProperty(PUB_METHOD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored
				&& getProperty(PROT_METHOD_CMT_REQUIREMENT_DESCRIPTOR) == CommentRequirement.Ignored;
	}

	/**
	 * @see PropertySource#dysfunctionReason()
	 */
	@Override
	public String dysfunctionReason() {
		return allCommentsAreIgnored() ? "All comment types are ignored" : null;
	}
}

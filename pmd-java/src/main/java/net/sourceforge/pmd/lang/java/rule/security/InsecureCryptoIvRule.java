package net.sourceforge.pmd.lang.java.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableInitializer;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Finds hardcoded static Initialization Vectors vectors used with cryptographic
 * operations.
 * 
 * //bad: byte[] ivBytes = new byte[] {32, 87, -14, 25, 78, -104, 98, 40};
 * //bad: byte[] ivBytes = "hardcoded".getBytes(); //bad: byte[] ivBytes =
 * someString.getBytes();
 * 
 * javax.crypto.spec.IvParameterSpec must not be created from a static
 * 
 * @author sergeygorbaty
 *
 */
public class InsecureCryptoIvRule extends AbstractJavaRule {

	public InsecureCryptoIvRule() {

		addRuleChainVisit(ASTCompilationUnit.class);
		addRuleChainVisit(ASTLiteral.class);
	}

	@Override
	public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
		Set<ASTFieldDeclaration> foundFields = new HashSet<>();
		Set<ASTLocalVariableDeclaration> foundLocalVars = new HashSet<>();
		Set<String> passedInIvVarNames = new HashSet<>();

		// byte[] fields
		List<ASTFieldDeclaration> fields = node.findDescendantsOfType(ASTFieldDeclaration.class);
		for (ASTFieldDeclaration field : fields) {
			foundFields.addAll(extractPrimitiveTypes(field));
		}

		List<ASTLocalVariableDeclaration> localVars = node.findDescendantsOfType(ASTLocalVariableDeclaration.class);
		for (ASTLocalVariableDeclaration localVar : localVars) {
			// byte[] local vars
			foundLocalVars.addAll(extractPrimitiveTypes(localVar));

			// find javax.crypto.spec.IvParameterSpec
			ASTClassOrInterfaceType declClassName = localVar.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
			if (declClassName != null) {
				Class<?> foundClass = declClassName.getTypeDefinition() == null ? null
						: declClassName.getTypeDefinition().getType();

				if (foundClass != null && foundClass.equals(javax.crypto.spec.IvParameterSpec.class)) {
					ASTVariableInitializer init = localVar.getFirstDescendantOfType(ASTVariableInitializer.class);
					if (init != null) {
						ASTName name = init.getFirstDescendantOfType(ASTName.class);
						if (name != null) {
							passedInIvVarNames.add(name.getImage());
						}
					}

				}
			}

			for (ASTFieldDeclaration foundField : foundFields) {
				if (passedInIvVarNames.contains(foundField.getVariableName())) {
					validateProperIv(data, foundField.getFirstDescendantOfType(ASTVariableInitializer.class));
				}
			}

			for (ASTLocalVariableDeclaration foundLocalVar : foundLocalVars) {
				if (passedInIvVarNames.contains(foundLocalVar.getVariableName())) {
					validateProperIv(data, foundLocalVar.getFirstDescendantOfType(ASTVariableInitializer.class));
				}
			}

		}

		return data;
	}

	private Set<ASTLocalVariableDeclaration> extractPrimitiveTypes(ASTLocalVariableDeclaration localVar) {
		List<ASTPrimitiveType> types = localVar.findDescendantsOfType(ASTPrimitiveType.class);
		Set<ASTLocalVariableDeclaration> retVal = new HashSet<>();
		extractPrimitiveTypesInner(retVal, localVar, types);

		return retVal;
	}

	private Set<ASTFieldDeclaration> extractPrimitiveTypes(ASTFieldDeclaration field) {
		List<ASTPrimitiveType> types = field.findDescendantsOfType(ASTPrimitiveType.class);
		Set<ASTFieldDeclaration> retVal = new HashSet<>();

		extractPrimitiveTypesInner(retVal, field, types);

		return retVal;
	}

	private <T> void extractPrimitiveTypesInner(Set<T> retVal, T field, List<ASTPrimitiveType> types) {
		for (ASTPrimitiveType type : types) {
			if (type.hasImageEqualTo("byte")) {
				ASTReferenceType parent = type.getFirstParentOfType(ASTReferenceType.class);
				if (parent != null) {
					retVal.add(field);
				}
			}
		}
	}

	private void validateProperIv(Object data, ASTVariableInitializer varInit) {
		// hard coded array
		ASTArrayInitializer arrayInit = varInit.getFirstDescendantOfType(ASTArrayInitializer.class);
		if (arrayInit != null) {
			addViolation(data, varInit);
		}

		// string literal
		ASTLiteral literal = varInit.getFirstDescendantOfType(ASTLiteral.class);
		if (literal != null && literal.isStringLiteral()) {
			addViolation(data, varInit);
		}

	}

}

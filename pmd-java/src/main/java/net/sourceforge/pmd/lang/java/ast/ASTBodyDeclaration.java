/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * Marker interface for declarations that can occur in a {@linkplain ASTTypeBody type body},
 * such as field or method declarations. Some of those can also appear on the
 * {@linkplain ASTTopLevelDeclaration top-level} of a file.
 *
 * <pre class="grammar">
 *
 * BodyDeclaration ::= {@link ASTAnyTypeDeclaration AnyTypeDeclaration}
 *                   | {@link ASTMethodDeclaration MethodDeclaration}
 *                   | {@link ASTConstructorDeclaration ConstructorDeclaration}
 *                   | {@link ASTRecordConstructorDeclaration RecordConstructorDeclaration}
 *                   | {@link ASTInitializer Initializer}
 *                   | {@link ASTFieldDeclaration FieldDeclaration}
 *                   | {@link ASTEnumConstant EnumConstant}
 *                   | {@link ASTEmptyDeclaration EmptyDeclaration}
 *
 * </pre>
 *
 */
public interface ASTBodyDeclaration extends JavaNode {


}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
@file:JvmName("AstTestUtil")

package net.sourceforge.pmd.lang.java.types

import net.sourceforge.pmd.lang.ast.NodeStream
import net.sourceforge.pmd.lang.ast.NodeStream.DescendantNodeStream
import net.sourceforge.pmd.lang.java.ast.*


fun JavaNode.methodDeclarations(): DescendantNodeStream<ASTMethodDeclaration> = descendants(ASTMethodDeclaration::class.java).crossFindBoundaries()
fun JavaNode.typeDeclarations(): DescendantNodeStream<ASTAnyTypeDeclaration> = descendants(ASTAnyTypeDeclaration::class.java).crossFindBoundaries()
fun JavaNode.ctorDeclarations(): DescendantNodeStream<ASTConstructorDeclaration> = descendants(ASTConstructorDeclaration::class.java).crossFindBoundaries()

fun JavaNode.firstTypeSignature(): JClassType = typeDeclarations().firstOrThrow().typeMirror
fun JavaNode.declaredTypeSignatures(): List<JClassType> = typeDeclarations().toList { it.typeMirror }
fun JavaNode.declaredMethodSignatures(): List<JMethodSig> = methodDeclarations().toList { it.genericSignature }

fun JavaNode.methodCalls(): DescendantNodeStream<ASTMethodCall> = descendants(ASTMethodCall::class.java)
fun JavaNode.firstMethodCall() = methodCalls().crossFindBoundaries().firstOrThrow()

fun JavaNode.ctorCalls(): DescendantNodeStream<ASTConstructorCall> = descendants(ASTConstructorCall::class.java)
fun JavaNode.firstCtorCall() = ctorCalls().crossFindBoundaries().firstOrThrow()

fun JavaNode.typeVariables(): MutableList<JTypeVar> = descendants(ASTTypeParameter::class.java).crossFindBoundaries().toList { it.typeMirror }
fun JavaNode.varAccesses(name: String): NodeStream<ASTVariableAccess> = descendants(ASTVariableAccess::class.java).filter { it.name == name }
fun JavaNode.varId(name: String) = descendants(ASTVariableDeclaratorId::class.java).filter { it.name == name }.firstOrThrow()
fun JavaNode.typeVar(name: String) = descendants(ASTTypeParameter::class.java).filter { it.name == name }.firstOrThrow().typeMirror

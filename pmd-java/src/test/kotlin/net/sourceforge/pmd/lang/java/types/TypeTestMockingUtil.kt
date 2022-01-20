/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import io.kotest.matchers.shouldBe
import net.sourceforge.pmd.lang.ast.NodeStream
import net.sourceforge.pmd.lang.ast.NodeStream.*
import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.ast.*
import net.sourceforge.pmd.lang.java.types.internal.infer.ResolutionFailure
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.mockito.Mockito


// kept out of TypeTestUtil so as not to import Mockito there

fun JavaParsingHelper.parseWithTypeInferenceSpy(code: String): Pair<ASTCompilationUnit, TypeInferenceSpy> {
    val spy = spy(typeInfLogger)
    `when`(spy.isNoop).thenReturn(false) // enable log for exceptions though
    val acu = this.logTypeInference(spy).parse(code)
    return Pair(acu, TypeInferenceSpy(spy, acu.typeSystem))
}

/**
 * Spies on the logger.
 *
 * @property spy A [Mockito.spy], you can call methods of mockito there
 */
data class TypeInferenceSpy(private val spy: TypeInferenceLogger, val ts: TypeSystem) {
    private fun shouldHaveNoErrors() {
        // note that inexact method ref selection may call noApplicableCandidates
        // or noCompileTimeDeclaration sometimes I think.
        verify(spy, never()).ambiguityError(any(), any(), any())
        verify(spy, never()).noCompileTimeDeclaration(any())
        verify(spy, never()).fallbackInvocation(any(), any())
    }

    fun shouldBeOk(block: TypeDslMixin.() -> Unit) {
        this.shouldHaveNoErrors()
        TypeDslOf(ts).block()
        this.shouldHaveNoErrors()
    }

    fun shouldTriggerMissingCtDecl(block: TypeDslMixin.() -> Unit) {
        this.shouldHaveNoErrors()
        TypeDslOf(ts).block()
        verify(spy, times(1)).noCompileTimeDeclaration(any())
    }

    fun shouldTriggerNoApplicableMethods(block: TypeDslMixin.() -> Unit) {
        this.shouldHaveNoErrors()
        TypeDslOf(ts).block()
        verify(spy, times(1)).noApplicableCandidates(any())
    }

    fun shouldTriggerNoLambdaCtx(block: TypeDslMixin.() -> Unit) {
        this.shouldHaveNoErrors()
        TypeDslOf(ts).block()
        val captor = ArgumentCaptor.forClass(ResolutionFailure::class.java)
        verify(spy, times(1)).logResolutionFail(captor.capture())
        val failure: ResolutionFailure = captor.value
        failure.reason shouldBe "Missing target type for functional expression"
    }

    fun resetInteractions() {
        reset(spy)
        `when`(spy.isNoop).thenReturn(false) // enable log for exceptions though
    }

    fun shouldBeAmbiguous(block: TypeDslMixin.() -> Unit) {
        this.shouldHaveNoErrors()
        TypeDslOf(ts).block()
        verify(spy, times(1)).ambiguityError(any(), any(), any())
    }
}


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

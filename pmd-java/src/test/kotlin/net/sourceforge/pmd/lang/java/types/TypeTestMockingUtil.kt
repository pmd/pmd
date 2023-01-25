/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types

import net.sourceforge.pmd.lang.java.JavaParsingHelper
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit
import net.sourceforge.pmd.lang.java.ast.FunctionalExpression
import net.sourceforge.pmd.lang.java.ast.InvocationNode
import net.sourceforge.pmd.lang.java.types.internal.infer.TypeInferenceLogger
import org.mockito.Mockito
import org.mockito.Mockito.*


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

    fun shouldHaveMissingCtDecl(node: InvocationNode) {
        verify(spy, times(1))
            .noCompileTimeDeclaration(argThat { it.expr.location == node })
    }

    fun shouldHaveNoApplicableMethods(node: InvocationNode) {
        verify(spy, times(1))
            .noApplicableCandidates(argThat {
                it.expr.location == node
            })
    }

    fun shouldHaveNoLambdaCtx(lambdaOrMref: FunctionalExpression) {
        verify(spy, times(1))
            .logResolutionFail(argThat {
                it.reason == "Missing target type for functional expression"
                        && it.location == lambdaOrMref
            })
    }

    fun shouldBeAmbiguous(node: InvocationNode) {
        verify(spy, times(1))
            .ambiguityError(argThat { it.expr.location==node }, any(), any())
    }
}

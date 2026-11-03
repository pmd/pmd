/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.java.types.JMethodSig;

class ResolutionFailureTest {

    /**
     * {@link ResolutionFailure#UNKNOWN} is shared by all analyses. If it stored the context,
     * it would keep the type system (and everything reachable from it, e.g. open class files)
     * of the last analysis alive forever.
     */
    @Test
    void sharedUnknownFailureShouldNotRetainContext() {
        ResolutionFailure.UNKNOWN.addContext(mock(JMethodSig.class), mock(PolySite.class), MethodResolutionPhase.STRICT);

        assertNull(ResolutionFailure.UNKNOWN.getFailedMethod());
        assertNull(ResolutionFailure.UNKNOWN.getCallSite());
        assertNull(ResolutionFailure.UNKNOWN.getPhase());
    }

    @Test
    void failureShouldKeepContext() {
        ResolutionFailure failure = new ResolutionFailure(null, "reason");
        JMethodSig method = mock(JMethodSig.class);
        PolySite<?> callSite = mock(PolySite.class);

        failure.addContext(method, callSite, MethodResolutionPhase.LOOSE);

        assertSame(method, failure.getFailedMethod());
        assertSame(callSite, failure.getCallSite());
        assertSame(MethodResolutionPhase.LOOSE, failure.getPhase());
    }
}

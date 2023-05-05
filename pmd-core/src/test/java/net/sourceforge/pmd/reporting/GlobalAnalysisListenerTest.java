/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GlobalAnalysisListenerTest {
    @Test
    void teeShouldForwardAllEventsSingleListeners() throws Exception {
        GlobalAnalysisListener mockListener1 = createMockListener();
        GlobalAnalysisListener teed = GlobalAnalysisListener.tee(Arrays.asList(mockListener1));

        teed.initializer();
        teed.startFileAnalysis(null);
        teed.onConfigError(null);
        teed.close();

        verifyMethods(mockListener1);
        Mockito.verifyNoMoreInteractions(mockListener1);
    }

    @Test
    void teeShouldForwardAllEventsMultipleListeners() throws Exception {
        GlobalAnalysisListener mockListener1 = createMockListener();
        GlobalAnalysisListener mockListener2 = createMockListener();
        GlobalAnalysisListener teed = GlobalAnalysisListener.tee(Arrays.asList(mockListener1, mockListener2));

        teed.initializer();
        teed.startFileAnalysis(null);
        teed.onConfigError(null);
        teed.close();

        verifyMethods(mockListener1);
        verifyMethods(mockListener2);
        Mockito.verifyNoMoreInteractions(mockListener1, mockListener2);
    }

    private GlobalAnalysisListener createMockListener() {
        GlobalAnalysisListener mockListener = Mockito.mock(GlobalAnalysisListener.class);
        Mockito.when(mockListener.initializer()).thenReturn(ListenerInitializer.noop());
        Mockito.when(mockListener.startFileAnalysis(Mockito.any())).thenReturn(FileAnalysisListener.noop());
        return mockListener;
    }

    private void verifyMethods(GlobalAnalysisListener listener) throws Exception {
        Mockito.verify(listener, Mockito.times(1)).initializer();
        Mockito.verify(listener, Mockito.times(1)).startFileAnalysis(null);
        Mockito.verify(listener, Mockito.times(1)).onConfigError(null);
        Mockito.verify(listener, Mockito.times(1)).close();
    }
}

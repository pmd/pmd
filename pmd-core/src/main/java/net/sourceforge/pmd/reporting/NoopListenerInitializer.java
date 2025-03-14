/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.reporting;

final class NoopListenerInitializer implements ListenerInitializer {

    static final NoopListenerInitializer INSTANCE = new NoopListenerInitializer();

    private NoopListenerInitializer() {
        // singleton
    }

    @Override
    public String toString() {
        return "Noop";
    }
}

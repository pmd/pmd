/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

public final class ResolutionState {
    private final Watchdog watchdog;
    private boolean constantsOnly;

    private ResolutionState(boolean constantsOnly) {
        watchdog = new Watchdog(1000);
        this.constantsOnly = constantsOnly;
    }

    public static ResolutionState forType() {
        return new ResolutionState(true);
    }

    public static ResolutionState forComponentReference() {
        return new ResolutionState(false);
    }

    void tick() throws Watchdog.CountdownException {
        watchdog.decrement();
    }

    public boolean needRecurseInto(ModelicaComponentDeclaration component) {
        return !constantsOnly || component.getVariability() == ModelicaComponentDeclaration.ComponentVariability.CONSTANT;
    }

    public ResolutionContext createContext() {
        return new ResolutionContext(this);
    }
}

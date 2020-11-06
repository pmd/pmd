/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sourceforge.pmd.annotation.InternalApi;

@InternalApi
public class ResolutionContext {
    private final ResolutionState state;
    private final List<ResolvableEntity> bestCandidates = new ArrayList<>();
    private final List<ResolvableEntity> hiddenCandidates = new ArrayList<>();
    private boolean ttlExceeded = false;
    private boolean isCollectingHidden = false;

    ResolutionContext(ResolutionState ctx) {
        state = ctx;
    }

    public ResolutionState getState() {
        return state;
    }

    public void watchdogTick() throws Watchdog.CountdownException {
        state.tick();
    }

    public void addCandidate(ResolvableEntity candidate) {
        if (isCollectingHidden) {
            hiddenCandidates.add(candidate);
        } else {
            bestCandidates.add(candidate);
        }
    }

    /**
     * Marks the corresponding resolution process as timed out.
     *
     * Usually, this method is called after catching `Watchdog.CountdownException`.
     */
    void markTtlExceeded() {
        ttlExceeded = true;
    }

    /**
     * Mark previously resolved declarations (if any) as more important than the subsequent ones.
     *
     * It is correct to call this method even at the point when nothing is resolved yet.
     * If there is something resolved so far, the subsequent declarations will be considered as hidden.
     * If there is nothing resolved so far, the call is ignored.
     */
    public void markHidingPoint() {
        if (!bestCandidates.isEmpty()) {
            isCollectingHidden = true;
        }
    }

    void accumulate(ResolutionResult result) {
        bestCandidates.addAll(result.getBestCandidates());
        hiddenCandidates.addAll(result.getHiddenCandidates());
    }

    private static class Result<A extends ResolvableEntity> implements ResolutionResult<A> {
        private final List<A> bestCandidates = new ArrayList<>();
        private final List<A> hiddenCandidates = new ArrayList<>();
        private final boolean timedOut;

        Result(Class<A> tpe, List<?> best, List<?> hidden, boolean timedOut) {
            for (Object b: best) {
                if (tpe.isInstance(b)) {
                    bestCandidates.add((A) b);
                }
            }
            for (Object h: hidden) {
                if (tpe.isInstance(h)) {
                    hiddenCandidates.add((A) h);
                }
            }
            this.timedOut = timedOut;
        }

        @Override
        public List<A> getBestCandidates() {
            return Collections.unmodifiableList(bestCandidates);
        }

        @Override
        public List<A> getHiddenCandidates() {
            return Collections.unmodifiableList(hiddenCandidates);
        }

        @Override
        public boolean isUnresolved() {
            return bestCandidates.isEmpty();
        }

        @Override
        public boolean isClashed() {
            return bestCandidates.size() > 1;
        }

        @Override
        public boolean hasHiddenResults() {
            return !hiddenCandidates.isEmpty();
        }

        @Override
        public boolean wasTimedOut() {
            return timedOut;
        }
    }

    public ResolutionResult<ModelicaType> getTypes() {
        return new Result<>(ModelicaType.class, bestCandidates, hiddenCandidates, ttlExceeded);
    }

    public ResolutionResult<ModelicaDeclaration> getDeclaration() {
        return new Result<>(ModelicaDeclaration.class, bestCandidates, hiddenCandidates, ttlExceeded);
    }

    public <T extends ResolvableEntity> ResolutionResult<T> get(Class<T> clazz) {
        return new Result<>(clazz, bestCandidates, hiddenCandidates, ttlExceeded);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resolved[");
        sb.append(bestCandidates.size());
        sb.append('/');
        sb.append(hiddenCandidates.size());
        sb.append(']');
        return sb.toString();
    }
}

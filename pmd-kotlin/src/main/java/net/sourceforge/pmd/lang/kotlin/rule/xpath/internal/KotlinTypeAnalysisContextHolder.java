/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.rule.xpath.internal;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds the active {@link KotlinTypeAnalysisContext} for XPath function evaluation.
 *
 * <p>Thread-local storage takes precedence over the global slot, allowing per-test
 * injection without interfering with other threads.
 *
 * <p>In production, call {@link #setGlobal(KotlinTypeAnalysisContext)} once at startup
 * (e.g., after loading a pre-computed JSON analysis file).
 * In tests, call {@link #set(KotlinTypeAnalysisContext)} before the test and
 * {@link #clear()} in an {@code @AfterEach} to avoid leaking state.
 */
public final class KotlinTypeAnalysisContextHolder {

    private static final AtomicReference<KotlinTypeAnalysisContext> GLOBAL_CONTEXT =
            new AtomicReference<>(KotlinTypeAnalysisContext.empty());
    private static final ThreadLocal<KotlinTypeAnalysisContext> THREAD_CONTEXT = new ThreadLocal<>();

    private KotlinTypeAnalysisContextHolder() {}

    /** Sets the context for the current thread (overrides the global context). */
    public static void set(KotlinTypeAnalysisContext ctx) {
        THREAD_CONTEXT.set(ctx);
    }

    /** Sets the global context used when no thread-local context is active. */
    public static void setGlobal(KotlinTypeAnalysisContext ctx) {
        GLOBAL_CONTEXT.set(ctx);
    }

    /**
     * Returns the active context: thread-local if set, otherwise global.
     * Never returns {@code null}; falls back to the empty no-op context.
     */
    public static KotlinTypeAnalysisContext get() {
        KotlinTypeAnalysisContext ctx = THREAD_CONTEXT.get();
        return ctx != null ? ctx : GLOBAL_CONTEXT.get();
    }

    /** Removes the thread-local context for the current thread. */
    public static void clear() {
        THREAD_CONTEXT.remove();
    }

    /** Resets the global context to the empty no-op context. */
    public static void clearGlobal() {
        GLOBAL_CONTEXT.set(KotlinTypeAnalysisContext.empty());
    }
}

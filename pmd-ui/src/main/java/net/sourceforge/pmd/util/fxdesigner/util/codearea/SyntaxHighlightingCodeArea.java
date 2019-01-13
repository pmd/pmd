/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util.codearea;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.reactfx.EventStream;
import org.reactfx.Subscription;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.util.fxdesigner.util.TextAwareNodeWrapper;

import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.WindowEvent;


/**
 * Code area that can handle syntax highlighting. Syntax highlighting is performed asynchronously
 * by another thread. It can be enabled by providing a {@link SyntaxHighlighter} to
 * {@link #setSyntaxHighlighter(SyntaxHighlighter)}, and disabled by passing a {@code null} reference
 * to that method.
 *
 * @see AvailableSyntaxHighlighters
 * @author Clément Fournier
 * @since 6.0.0
 */
public class SyntaxHighlightingCodeArea extends CodeArea {

    /** Minimum delay between each code highlighting recomputation. Changes are ignored until then. */
    private static final Duration TEXT_CHANGE_DELAY = Duration.ofMillis(30);

    /** Current subscription to syntax highlighting auto-refresh. */
    private final Var<Subscription> syntaxAutoRefresh = Var.newSimpleVar(null);

    /** Current syntax highlighter. Can be absent. */
    private final Var<SyntaxHighlighter> syntaxHighlighter = Var.newSimpleVar(null);

    /** Current highlighting spans. */
    private final Var<StyleSpans<Collection<String>>> currentSyntaxHighlight = Var.newSimpleVar(null);

    /** Read-only view on the current highlighting spans. Can be absent. */
    protected final Val<StyleSpans<Collection<String>>> syntaxHighlight = currentSyntaxHighlight;


    public SyntaxHighlightingCodeArea() {
        // captured in the closure
        final EventHandler<WindowEvent> autoCloseHandler = e -> syntaxAutoRefresh.ifPresent(Subscription::unsubscribe);

        // handles auto shutdown of executor services
        // by attaching a handler to the stage responsible for the control
        Val.wrap(sceneProperty())
           .filter(Objects::nonNull)
           .flatMap(Scene::windowProperty)
           .values()
           .filter(Objects::nonNull)
            .subscribe(c -> c.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, autoCloseHandler));
    }


    /**
     * Enables syntax highlighting if disabled and sets it to use the given highlighter.
     * If the argument is null, then this method disables syntax highlighting.
     */
    public void setSyntaxHighlighter(SyntaxHighlighter highlighter) {

        if (Objects.equals(highlighter, syntaxHighlighter.getValue())) {
            return;
        }

        syntaxHighlighter.ifPresent(previous -> getStyleClass().remove(previous.getLanguageTerseName()));
        syntaxAutoRefresh.ifPresent(Subscription::unsubscribe);

        if (highlighter == null) {
            syntaxAutoRefresh.setValue(null);
            this.setCurrentSyntaxHighlight(null);
            return;
        }

        syntaxHighlighter.setValue(highlighter);

        getStyleClass().add(highlighter.getLanguageTerseName());
        syntaxAutoRefresh.setValue(subscribeSyntaxHighlighting(defaultHighlightingTicks(), highlighter));

        try { // refresh the highlighting once.
            Task<StyleSpans<Collection<String>>> t = computeHighlightingAsync(Executors.newSingleThreadExecutor(), highlighter, getText());
            t.setOnSucceeded(e -> this.setCurrentSyntaxHighlight(t.getValue()));
        } catch (Exception ignored) {
            // nevermind
        }
    }


    public Val<Boolean> syntaxHighlightingEnabledProperty() {
        return syntaxHighlighter.map(Objects::nonNull);
    }


    private EventStream<?> defaultHighlightingTicks() {
        return this.plainTextChanges()
                   .filter(ch -> !ch.isIdentity())
                   .distinct();
    }


    private Subscription subscribeSyntaxHighlighting(EventStream<?> ticks, SyntaxHighlighter highlighter) {
        // captured in the closure, shutdown when unsubscribing
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        return ticks.successionEnds(TEXT_CHANGE_DELAY)
                    .supplyTask(() -> computeHighlightingAsync(executorService, highlighter, this.getText()))
                    .awaitLatest(ticks)
                    .filterMap(t -> {
                        t.ifFailure(Throwable::printStackTrace);
                        return t.toOptional();
                    })
                    .subscribe(this::setCurrentSyntaxHighlight)
                    .and(executorService::shutdownNow);
    }


    private static Task<StyleSpans<Collection<String>>> computeHighlightingAsync(ExecutorService service, SyntaxHighlighter highlighter, String text) {
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() {
                return highlighter.computeHighlighting(text);
            }
        };
        if (!service.isShutdown()) {
            service.execute(task);
        }
        return task;
    }

    /** Removes the current syntax highlighting span. */
    protected void clearSyntaxHighlighting() {
        setCurrentSyntaxHighlight(null);
    }

    /**
     * Update the syntax highlighting to the specified value.
     * If null, syntax highlighting is stripped off.
     */
    private void setCurrentSyntaxHighlight(final StyleSpans<Collection<String>> newSyntax) {
        Optional<StyleSpans<Collection<String>>> oldSyntaxHighlight = currentSyntaxHighlight.getOpt();
        this.currentSyntaxHighlight.setValue(newSyntax);

        setStyleSpans(0, styleSyntaxHighlightChange(oldSyntaxHighlight, newSyntax));
    }


    /**
     * Given the old value of the highlighting spans, and a newly computed value,
     * computes the spans as they should be applied to the codearea. The default behaviour
     * simply returns the newest spans, which works flawlessly when there is no other
     * style layer in the game. Subclasses are free to override, to get a chance to
     * preserve additional style layers.
     */
    protected StyleSpans<Collection<String>> styleSyntaxHighlightChange(final Optional<StyleSpans<Collection<String>>> oldSyntax,
                                                                        final StyleSpans<Collection<String>> newSyntax) {
        return newSyntax;
    }


    /**
     * Forces synchronous updating of the syntax highlighting.
     * This can be done when we suspect the highlighting is outdated
     * but we really need the most up to date one, for example because
     * we want to overlay other spans on it.
     */
    protected void updateSyntaxHighlightingSynchronously() {
        syntaxHighlighter.getOpt().map(h -> h.computeHighlighting(getText())).ifPresent(currentSyntaxHighlight::setValue);
    }


    protected StyleSpans<Collection<String>> emptySpan() {
        return StyleSpans.singleton(Collections.emptyList(), getLength());
    }


    /** Overlay operation that stacks up the style classes of the two overlaid spans. */
    protected static Collection<String> additiveOverlay(Collection<String> style1, Collection<String> style2) {
        // todo using persistent collections here may be beneficial
        if (style1.isEmpty()) {
            return style2;
        } else if (style2.isEmpty()) {
            return style1;
        }
        Set<String> styles = new HashSet<>(style1);
        styles.addAll(style2);
        return styles;
    }


    /** Wraps a node into a convenience layer that can for example provide the rich text associated with it. */
    public TextAwareNodeWrapper wrapNode(Node node) {
        return NodeStyleSpan.fromNode(node, this).snapshot();
    }

}

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.fxdesigner.util;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.reactfx.EventSource;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.collection.LiveList;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.rule.xpath.XPathRuleQuery;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.lang.symboltable.ScopedNode;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import javafx.util.StringConverter;


/**
 * @author Clément Fournier
 * @since 6.0.0
 */
public final class DesignerUtil {


    private static final Path PMD_SETTINGS_DIR = Paths.get(System.getProperty("user.home"), ".pmd");
    private static final File DESIGNER_SETTINGS_FILE = PMD_SETTINGS_DIR.resolve("designer.xml").toFile();
    private static final Pattern JJT_ACCEPT_PATTERN = Pattern.compile("net.sourceforge.pmd.lang.\\w++.ast.AST(\\w+).jjtAccept");

    private static List<LanguageVersion> supportedLanguageVersions;
    private static Map<String, LanguageVersion> extensionsToLanguage;


    private DesignerUtil() {

    }


    public static String defaultXPathVersion() {
        return XPathRuleQuery.XPATH_2_0;
    }


    public static LanguageVersion defaultLanguageVersion() {
        return LanguageRegistry.getDefaultLanguage().getDefaultVersion();
    }


    /**
     * Gets the URL to an fxml file from its simple name.
     *
     * @param simpleName Simple name of the file, i.e. with no directory prefixes
     *
     * @return A URL to an fxml file
     */
    public static URL getFxml(String simpleName) {
        return DesignerUtil.class.getResource("/net/sourceforge/pmd/util/fxdesigner/fxml/" + simpleName);
    }


    /**
     * Name of the designer's settings file.
     *
     * @return The name
     */
    public static File getSettingsFile() {
        return DESIGNER_SETTINGS_FILE;
    }


    public static <T> Callback<ListView<T>, ListCell<T>> simpleListCellFactory(Function<T, String> converter, Function<T, String> toolTipMaker) {
        return collection -> new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    Tooltip.uninstall(this, getTooltip());
                } else {
                    setText(converter.apply(item));
                    Tooltip.install(this, new Tooltip(toolTipMaker.apply(item)));
                }
            }
        };
    }


    public static <T> StringConverter<T> stringConverter(Function<T, String> toString, Function<String, T> fromString) {
        return new StringConverter<T>() {
            @Override
            public String toString(T object) {
                return toString.apply(object);
            }


            @Override
            public T fromString(String string) {
                return fromString.apply(string);
            }
        };
    }


    /**
     * Given a toggle group whose toggles all have user data of type T,
     * maps the selected toggle property to a Var&lt;T>
     */
    @SuppressWarnings("unchecked")
    public static <T> Var<T> mapToggleGroupToUserData(ToggleGroup toggleGroup, Supplier<T> defaultValue) {
        return Var.fromVal(toggleGroup.selectedToggleProperty(), toggleGroup::selectToggle)
                  .mapBidirectional(
                      item -> (T) item.getUserData(),
                      t -> selectFirst(
                          () -> findToggleWithUserData(toggleGroup, t),
                          () -> findToggleWithUserData(toggleGroup, defaultValue.get())
                      )
                          .orElseThrow(() -> new IllegalStateException("Unknown toggle " + t))
                  );
    }


    /** Returns the first non-empty optional in the arguments, or else Optional.empty. */
    @SafeVarargs
    public static <T> Optional<T> selectFirst(Supplier<Optional<T>>... opts) {
        for (Supplier<Optional<T>> optGetter : opts) {
            Optional<T> o = optGetter.get();
            if (o.isPresent()) {
                return o;
            }
        }
        return Optional.empty();
    }


    private static <T> Optional<Toggle> findToggleWithUserData(ToggleGroup toggleGroup, T data) {
        return toggleGroup.getToggles()
                          .stream()
                          .filter(toggle -> toggle.getUserData().equals(data))
                          .findFirst();
    }


    public static StringConverter<LanguageVersion> languageVersionStringConverter() {
        return DesignerUtil.stringConverter(LanguageVersion::getShortName,
            s -> LanguageRegistry.findLanguageVersionByTerseName(s.toLowerCase(Locale.ROOT)));
    }


    private static Map<String, LanguageVersion> getExtensionsToLanguageMap() {
        Map<String, LanguageVersion> result = new HashMap<>();
        getSupportedLanguageVersions().stream()
                                      .map(LanguageVersion::getLanguage)
                                      .distinct()
                                      .collect(Collectors.toMap(Language::getExtensions,
                                                                Language::getDefaultVersion))
                                      .forEach((key, value) -> key.forEach(ext -> result.put(ext, value)));
        return result;
    }


    public static synchronized LanguageVersion getLanguageVersionFromExtension(String filename) {
        if (extensionsToLanguage == null) {
            extensionsToLanguage = getExtensionsToLanguageMap();
        }

        if (filename.indexOf('.') > 0) {
            String[] tokens = filename.split("\\.");
            return extensionsToLanguage.get(tokens[tokens.length - 1]);
        }
        return null;
    }


    public static synchronized List<LanguageVersion> getSupportedLanguageVersions() {
        if (supportedLanguageVersions == null) {
            List<LanguageVersion> languageVersions = new ArrayList<>();
            for (LanguageVersion languageVersion : LanguageRegistry.findAllVersions()) {
                Optional.ofNullable(languageVersion.getLanguageVersionHandler())
                        .map(handler -> handler.getParser(handler.getDefaultParserOptions()))
                        .filter(Parser::canParse)
                        .ifPresent(p -> languageVersions.add(languageVersion));
            }
            supportedLanguageVersions = languageVersions;
        }
        return supportedLanguageVersions;
    }


    /** Like the other overload, using the setter of the ui property. */
    public static <T> void rewireInit(Property<T> underlying, Property<T> ui) {
        rewireInit(underlying, ui, ui::setValue);
    }

    /**
     * Binds the underlying property to a source of values (UI property). The UI
     * property is also initialised using a setter.
     *
     * @param underlying The underlying property
     * @param ui         The property exposed to the user (the one in this wizard)
     * @param setter     Setter to initialise the UI value
     * @param <T>        Type of values
     */
    public static <T> void rewireInit(Property<T> underlying, ObservableValue<? extends T> ui, Consumer<? super T> setter) {
        setter.accept(underlying.getValue());
        rewire(underlying, ui);
    }


    /** Like rewireInit, with no initialisation. */
    public static <T> void rewire(Property<T> underlying, ObservableValue<? extends T> source) {
        underlying.unbind();
        underlying.bind(source); // Bindings are garbage collected after the popup dies
    }


    /**
     * Works out an xpath query that matches the node
     * which was being visited during the failure.
     *
     * <p>The query selects nodes that have exactly the
     * same ancestors than the node in which the last call
     * from the stack trace.
     *
     * @param stackTrace full stack trace
     *
     * @return An xpath expression if possible
     */
    public static Optional<String> stackTraceToXPath(String stackTrace) {
        List<String> lines = Arrays.stream(stackTrace.split("\\n"))
                                   .map(JJT_ACCEPT_PATTERN::matcher)
                                   .filter(Matcher::find)
                                   .map(m -> m.group(1))
                                   .collect(Collectors.toList());

        Collections.reverse(lines);

        return lines.isEmpty() ? Optional.empty() : Optional.of("//" + String.join("/", lines));
    }


    /**
     * Works out an xpath query that matches the node
     * which was being visited during the failure.
     *
     * @param e Exception
     *
     * @return A query, if possible.
     *
     * @see #stackTraceToXPath(String)
     */
    public static Optional<String> stackTraceToXPath(Throwable e) {
        return stackTraceToXPath(ExceptionUtils.getStackTrace(e));
    }


    public static Var<Boolean> booleanVar(BooleanProperty p) {
        return Var.mapBidirectional(p, Boolean::booleanValue, Function.identity());
    }


    // Creating a real function Val<LiveList<T>> => LiveList<T> or LiveList<Val<T>> => LiveList<T> would
    // allow implementing LiveList.flatMap, which is a long-standing feature request in ReactFX
    // These utilities are very inefficient, but sufficient for our use case...
    public static <T> Val<LiveList<T>> flatMapChanges(ObservableList<? extends ObservableValue<T>> listOfObservables) {

        // every time an element changes an invalidation stream
        EventStream<?> invalidations =
            LiveList.map(listOfObservables, EventStreams::valuesOf)
                    .reduce(EventStreams::merge)
                    .values()
                    .filter(Objects::nonNull)
                    .flatMap(Function.identity());

        return Val.create(() -> LiveList.map(listOfObservables, ObservableValue::getValue), invalidations);
    }


    public static <T, U> Val<U> reduceWElts(ObservableList<? extends ObservableValue<T>> list, U zero, BiFunction<U, T, U> mapper) {
        return flatMapChanges(list).map(l -> l.stream().reduce(zero, mapper, (u, v) -> v));
    }


    public static <T> Val<Integer> countMatching(ObservableList<? extends ObservableValue<T>> list, Predicate<? super T> predicate) {
        return reduceWElts(list, 0, (cur, t) -> predicate.test(t) ? cur + 1 : cur);
    }


    public static Val<Integer> countMatching(ObservableList<? extends ObservableValue<Boolean>> list) {
        return countMatching(list, b -> b);
    }


    public static Val<Integer> countNotMatching(ObservableList<? extends ObservableValue<Boolean>> list) {
        return countMatching(list, b -> !b);
    }


    /**
     * Reduces the given stream on the given duration. If reduction of two values is not possible
     * (canReduce returns false), then the last value is emitted and the new one will
     * be tested for reduction with the next ones. If no new event is pushed during the duration, the last reduction
     * result is emitted.
     */
    public static <T> EventStream<T> reduceIfPossible(EventStream<T> input, BiPredicate<T, T> canReduce, BinaryOperator<T> reduction, Duration duration) {
        EventSource<T> source = new EventSource<>();

        input.reduceSuccessions(
            (last, t) -> {
                if (canReduce.test(last, t)) {
                    return reduction.apply(last, t);
                } else {
                    source.push(last);
                    return t;
                }
            }, duration)
             .subscribe(source::push);

        return source;
    }


    /**
     * Like reduce if possible, but can be used if the events to reduce are emitted in extremely close
     * succession, so close that some unrelated events may be mixed up. This reduces each new event
     * with a related event in the pending notification chain instead of just considering the last one
     * as a possible reduction target.
     */
    public static <T> EventStream<T> reduceEntangledIfPossible(EventStream<T> input, BiPredicate<T, T> canReduce, BinaryOperator<T> reduction, Duration duration) {
        EventSource<T> source = new EventSource<>();

        input.reduceSuccessions(
            () -> new ArrayList<>(),
            (List<T> pending, T t) -> {

                for (int i = 0; i < pending.size(); i++) {
                    if (canReduce.test(pending.get(i), t)) {
                        pending.set(i, reduction.apply(pending.get(i), t));
                        return pending;
                    }
                }
                pending.add(t);

                return pending;
            },
            duration
        )
             .subscribe(pending -> {
                 for (T t : pending) {
                     source.push(t);
                 }
             });

        return source;
    }


    /**
     * Returns an event stream that reduces successions of the input stream, and deletes the latest
     * event if a new event that matches the isCancelSignal predicate is recorded during a reduction
     * period. Cancel events are also emitted.
     */
    public static <T> EventStream<T> deleteOnSignal(EventStream<T> input, Predicate<T> isCancelSignal, Duration duration) {
        return reduceIfPossible(input, (last, t) -> isCancelSignal.test(t), (last, t) -> t, duration);
    }


    public static <T, R> EventStream<T> mapFilter(EventStream<T> input, Function<? super T, ? extends R> mapper, Predicate<R> filter) {
        return input.filter(t -> filter.test(mapper.apply(t)));
    }


    public static List<NameOccurrence> getNameOccurrences(ScopedNode node) {

        // For MethodNameDeclaration the scope is the method scope, which is not the scope it is declared
        // in but the scope it declares! That means that getDeclarations().get(declaration) returns null
        // and no name occurrences are found. We thus look in the parent, but ultimately the name occurrence
        // finder is broken since it can't find e.g. the use of a method in another scope. Plus in case of
        // overloads both overloads are reported to have a usage.

        // Plus this is some serious law of Demeter breaking there...

        Set<NameDeclaration> candidates = new HashSet<>(node.getScope().getDeclarations().keySet());

        Optional.ofNullable(node.getScope().getParent())
                .map(Scope::getDeclarations)
                .map(Map::keySet)
                .ifPresent(candidates::addAll);

        return candidates.stream()
                         .filter(nd -> node.equals(nd.getNode()))
                         .findFirst()
                         .map(nd -> {
                             // nd.getScope() != nd.getNode().getScope()?? wtf?

                             List<NameOccurrence> usages = nd.getNode().getScope().getDeclarations().get(nd);

                             if (usages == null) {
                                 usages = nd.getNode().getScope().getParent().getDeclarations().get(nd);
                             }

                             return usages;
                         })
                         .orElse(Collections.emptyList());
    }
}

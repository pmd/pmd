/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;


import static net.sourceforge.pmd.internal.util.AssertionUtil.requireOver1;
import static net.sourceforge.pmd.internal.util.AssertionUtil.requireParamNotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Utility class for working with {@link Predicate}.
 */
public final class PredicateUtil {

    private PredicateUtil() {
        // utility class
    }

    /** A predicate that always returns false. */
    public static <T> Predicate<T> never() {
        return t -> false;
    }

    /** A predicate that always returns true. */
    public static <T> Predicate<T> always() {
        return t -> true;
    }

    /**
     * Returns a case-insensitive predicate for files with the given extensions.
     *
     * @throws NullPointerException If the extensions array is null
     */
    public static Predicate<File> getFileExtensionFilter(String... extensions) {
        requireParamNotNull("extensions", extensions);
        // TODO add first parameter to mandate that. This affects a
        //  constructor of AbstractLanguage and should be done later
        requireOver1("Extension count", extensions.length);
        return new FileExtensionFilter(true, extensions);
    }

    /**
     * Returns a predicate that tests if the name of a file matches the
     * given string filter. The returned predicate normalizes backslashes
     * ({@code '\'}) to {@code '/'} to be more easily cross-platform.
     *
     * @param filter A predicate on the file name
     *
     * @return A predicate on files
     */
    public static Predicate<File> toNormalizedFileFilter(final Predicate<? super String> filter) {
        return file -> {
            String path = file.getPath();
            path = path.replace('\\', '/');
            return filter.test(path);
        };
    }

    /**
     * Given a File Filter, expose as a FilenameFilter.
     *
     * @param filter The File Filter.
     *
     * @return A FilenameFilter.
     */
    public static FilenameFilter toFilenameFilter(final Predicate<? super File> filter) {
        return (dir, name) -> filter.test(new File(dir, name));
    }

    /**
     * Given a FilenameFilter, expose as a File Filter.
     *
     * @param filter The FilenameFilter.
     *
     * @return A File Filter.
     */
    public static Predicate<Path> toFileFilter(final FilenameFilter filter) {
        return path -> filter.accept(path.getParent().toFile(), path.getFileName().toString());
    }

    /**
     * Builds a string filter using a set of include and exclude regular
     * expressions. A string S matches the predicate if either
     * <ul>
     * <li>1. no exclude regex matches S, or
     * <li>2. some include regex matches S
     * </ul>
     * In other words, include patterns override exclude patterns.
     *
     * @param includeRegexes Regular expressions overriding the excludes.
     * @param excludeRegexes Regular expressions filtering strings out.
     *
     * @return A predicate for strings.
     */
    public static Predicate<String> buildRegexFilterIncludeOverExclude(@NonNull Collection<Pattern> includeRegexes,
                                                                       @NonNull Collection<Pattern> excludeRegexes) {
        AssertionUtil.requireParamNotNull("includeRegexes", includeRegexes);
        AssertionUtil.requireParamNotNull("excludeRegexes", includeRegexes);

        return union(excludeRegexes).negate().or(union(includeRegexes));
    }

    private static Predicate<String> union(Collection<Pattern> regexes) {
        return regexes.stream().map(PredicateUtil::matchesRegex).reduce(never(), Predicate::or);
    }

    private static Predicate<String> matchesRegex(Pattern regex) {
        return s -> regex.matcher(s).matches();
    }
}

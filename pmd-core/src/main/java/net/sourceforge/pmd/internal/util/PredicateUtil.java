/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.internal.util;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Utility class for working with {@link Predicate}. Contains builder style methods, apply
 * methods, as well as mechanisms for adapting Filters and FilenameFilters.
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
     * Filter a given Collection.
     *
     * @param <T>        Type of the Collection.
     * @param filter     A Filter upon the Type of objects in the Collection.
     * @param collection The Collection to filter.
     *
     * @return A List containing only those objects for which the Filter
     *     returned <code>true</code>.
     */
    public static <T> List<T> filter(Predicate<? super T> filter, Collection<T> collection) {
        List<T> list = new ArrayList<>();
        for (T obj : collection) {
            if (filter.test(obj)) {
                list.add(obj);
            }
        }
        return list;
    }

    /** Returns a case-insensitive predicate for files with the given extensions. */
    public static Predicate<File> getFileExtensionFilter(String... extensions) {
        return new FileExtensionFilter(extensions);
    }

    /**
     * Get a File Filter for directories or for files with the given extensions,
     * ignoring case.
     *
     * @param extensions The extensions to filter.
     *
     * @return A File Filter.
     */
    public static Predicate<File> getFileExtensionOrDirectoryFilter(String... extensions) {
        return getFileExtensionFilter(extensions).or(File::isDirectory);
    }

    /**
     * Given a String Filter, expose as a File Filter. The File paths are
     * normalized to a standard pattern using <code>/</code> as a path separator
     * which can be used cross platform easily in a regular expression based
     * String Filter.
     *
     * @param filter A String Filter.
     *
     * @return A File Filter.
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
        return new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return filter.test(new File(dir, name));
            }

            @Override
            public String toString() {
                return filter.toString();
            }
        };
    }

    /**
     * Given a FilenameFilter, expose as a File Filter.
     *
     * @param filter The FilenameFilter.
     *
     * @return A File Filter.
     */
    public static Predicate<File> toFileFilter(final FilenameFilter filter) {
        return new Predicate<File>() {
            @Override
            public boolean test(File file) {
                return filter.accept(file.getParentFile(), file.getName());
            }

            @Override
            public String toString() {
                return filter.toString();
            }
        };
    }

    /**
     * Construct a String Filter using set of include and exclude regular
     * expressions. If there are no include regular expressions provide, then a
     * regular expression is added which matches every String by default. A
     * String is included as long as it matches an include regular expression
     * and does not match an exclude regular expression.
     * <p>
     * In other words, exclude patterns override include patterns.
     *
     * @param includeRegexes The include regular expressions. May be <code>null</code>.
     * @param excludeRegexes The exclude regular expressions. May be <code>null</code>.
     *
     * @return A String Filter.
     */
    public static Predicate<String> buildRegexFilterExcludeOverInclude(@NonNull List<Pattern> includeRegexes, List<Pattern> excludeRegexes) {
        Predicate<String> includes = union(includeRegexes, always());
        Predicate<String> excludes = union(excludeRegexes, never());

        return includes.and(excludes.negate());
    }

    /**
     * Construct a String Filter using set of include and exclude regular
     * expressions. If there are no include regular expressions provide, then a
     * regular expression is added which matches every String by default. A
     * String is included as long as the case that there is an include which
     * matches or there is not an exclude which matches.
     * <p>
     * In other words, include patterns override exclude patterns.
     *
     * @param includeRegexes The include regular expressions. May be <code>null</code>.
     * @param excludeRegexes The exclude regular expressions. May be <code>null</code>.
     *
     * @return A String Filter.
     */
    public static Predicate<String> buildRegexFilterIncludeOverExclude(List<Pattern> includeRegexes, List<Pattern> excludeRegexes) {
        Predicate<String> includes = union(includeRegexes, never());
        Predicate<String> excludes = union(excludeRegexes, never());

        return includes.or(excludes.negate());
    }

    private static Predicate<String> union(List<Pattern> regexes, Predicate<String> ifMissing) {
        return union(regexes, PredicateUtil::matchesRegex, ifMissing);
    }

    /**
     * Builds the logical OR of the collection {@code as.map(f)}. If
     * the collection is empty or null, returns the default value.
     */
    private static <A, B> Predicate<B> union(List<? extends A> as,
                                             Function<? super A, ? extends Predicate<B>> f,
                                             Predicate<B> ifMissing) {

        if (as == null || as.isEmpty()) {
            return ifMissing;
        } else {
            Predicate<B> union = never();
            for (A a : as) {
                union = union.or(f.apply(a));
            }
            return union;
        }
    }

    public static Predicate<String> matchesRegex(Pattern regex) {
        return s -> regex.matcher(s).matches();
    }
}

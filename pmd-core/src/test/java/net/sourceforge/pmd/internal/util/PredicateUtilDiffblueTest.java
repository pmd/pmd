package net.sourceforge.pmd.internal.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

class PredicateUtilDiffblueTest {
    /**
     * Method under test: {@link PredicateUtil#always()}
     */
    @Test
    void testAlways() {
        // Arrange and Act
        Predicate<Object> actualAlwaysResult = PredicateUtil.always();

        // Assert
        assertTrue(actualAlwaysResult.test("42"));
    }

    /**
     * Method under test:
     * {@link PredicateUtil#buildRegexFilterIncludeOverExclude(Collection, Collection)}
     */
    @Test
    void testBuildRegexFilterIncludeOverExclude() {
        // Arrange
        ArrayList<Pattern> includeRegexes = new ArrayList<>();

        // Act
        Predicate<String> actualBuildRegexFilterIncludeOverExcludeResult = PredicateUtil
                .buildRegexFilterIncludeOverExclude(includeRegexes, new ArrayList<>());

        // Assert
        assertTrue(actualBuildRegexFilterIncludeOverExcludeResult.test("foo"));
    }

    /**
     * Method under test:
     * {@link PredicateUtil#buildRegexFilterIncludeOverExclude(Collection, Collection)}
     */
    @Test
    void testBuildRegexFilterIncludeOverExclude2() {
        // Arrange
        ArrayList<Pattern> includeRegexes = new ArrayList<>();
        includeRegexes.add(Pattern.compile(".*\\.txt"));

        // Act
        Predicate<String> actualBuildRegexFilterIncludeOverExcludeResult = PredicateUtil
                .buildRegexFilterIncludeOverExclude(includeRegexes, new ArrayList<>());

        // Assert
        assertTrue(actualBuildRegexFilterIncludeOverExcludeResult.test("foo"));
    }

    /**
     * Method under test:
     * {@link PredicateUtil#buildRegexFilterIncludeOverExclude(Collection, Collection)}
     */
    @Test
    void testBuildRegexFilterIncludeOverExclude3() {
        // Arrange
        ArrayList<Pattern> includeRegexes = new ArrayList<>();
        includeRegexes.add(Pattern.compile(".*\\.txt"));
        includeRegexes.add(Pattern.compile(".*\\.txt"));

        // Act
        Predicate<String> actualBuildRegexFilterIncludeOverExcludeResult = PredicateUtil
                .buildRegexFilterIncludeOverExclude(includeRegexes, new ArrayList<>());

        // Assert
        assertTrue(actualBuildRegexFilterIncludeOverExcludeResult.test("foo"));
    }

    /**
     * Method under test:
     * {@link PredicateUtil#buildRegexFilterIncludeOverExclude(Collection, Collection)}
     */
    @Test
    void testBuildRegexFilterIncludeOverExclude4() {
        // Arrange
        ArrayList<Pattern> includeRegexes = new ArrayList<>();

        ArrayList<Pattern> excludeRegexes = new ArrayList<>();
        excludeRegexes.add(Pattern.compile(".*\\.txt"));

        // Act
        Predicate<String> actualBuildRegexFilterIncludeOverExcludeResult = PredicateUtil
                .buildRegexFilterIncludeOverExclude(includeRegexes, excludeRegexes);

        // Assert
        assertTrue(actualBuildRegexFilterIncludeOverExcludeResult.test("foo"));
    }

    /**
     * Method under test:
     * {@link PredicateUtil#buildRegexFilterIncludeOverExclude(Collection, Collection)}
     */
    @Test
    void testBuildRegexFilterIncludeOverExclude5() {
        // Arrange
        ArrayList<Pattern> includeRegexes = new ArrayList<>();

        ArrayList<Pattern> excludeRegexes = new ArrayList<>();
        excludeRegexes.add(Pattern.compile(".*\\.txt"));
        excludeRegexes.add(Pattern.compile(".*\\.txt"));

        // Act
        Predicate<String> actualBuildRegexFilterIncludeOverExcludeResult = PredicateUtil
                .buildRegexFilterIncludeOverExclude(includeRegexes, excludeRegexes);

        // Assert
        assertTrue(actualBuildRegexFilterIncludeOverExcludeResult.test("foo"));
    }

    /**
     * Method under test:
     * {@link PredicateUtil#buildRegexFilterIncludeOverExclude(Collection, Collection)}
     */
    @Test
    void testBuildRegexFilterIncludeOverExclude6() {
        // Arrange
        ArrayList<Pattern> includeRegexes = new ArrayList<>();

        ArrayList<Pattern> excludeRegexes = new ArrayList<>();
        excludeRegexes.add(Pattern.compile("foo"));

        // Act
        Predicate<String> actualBuildRegexFilterIncludeOverExcludeResult = PredicateUtil
                .buildRegexFilterIncludeOverExclude(includeRegexes, excludeRegexes);

        // Assert
        assertFalse(actualBuildRegexFilterIncludeOverExcludeResult.test("foo"));
    }
}

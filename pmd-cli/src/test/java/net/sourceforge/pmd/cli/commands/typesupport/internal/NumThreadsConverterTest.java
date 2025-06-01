/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.typesupport.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

import java.util.Arrays;
import java.util.Collection;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class NumThreadsConverterTest {
    @ParameterizedTest
    @MethodSource
    void convertToThreadCount(String parameter, int expectedThreadCount) {
        NumThreadsConverter converter = new NumThreadsConverter();
        int actualThreadCount = converter.convert(parameter);
        assertEquals(expectedThreadCount, actualThreadCount);
    }

    private static Collection<Arguments> convertToThreadCount() {
        return Arrays.asList(
                of("0", 0),
                of("0C", 0),
                of("1", 1),
                of("1C", Runtime.getRuntime().availableProcessors()),
                of("2C", 2 * Runtime.getRuntime().availableProcessors()),
                of("0.5C", (int) (0.5 * Runtime.getRuntime().availableProcessors())));
    }
}

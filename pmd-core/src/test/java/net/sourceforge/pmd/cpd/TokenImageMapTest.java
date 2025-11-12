/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.RepeatedTest;

class TokenImageMapTest {

    @RepeatedTest(100)
    void testNextIdSynchronization() {
        // Verify that even in a multithreaded context, assigned IDs are
        // all different.

        TokenImageMap map = new TokenImageMap(4);
        List<Integer> assignedIds = Collections.synchronizedList(new ArrayList<>());
        int numValues = 100;
        IntStream.range(1, numValues)
                 // Add many strings in parallel to the map
                 .parallel().map(i -> map.getImageId(String.valueOf(i)))
                 .forEach(assignedIds::add);

        assertEquals(numValues - 1, assignedIds.size());
        assignedIds.sort(Integer::compareTo);
        assertEquals(IntStream.range(1, numValues).boxed().collect(Collectors.toList()), assignedIds);
    }
}

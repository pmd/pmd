package com.cb4.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Predicate;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cb4.common.test.JUnitTestSupport;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MapUtilsTest extends JUnitTestSupport {
    public MapUtilsTest() {
        super();
    }

    @Test
    public void testPutUniqueValuesForNoSource() {
        @SuppressWarnings("unchecked")
        Map<Object, Object>[] maps = new Map[] { null, Collections.emptyMap() };
        for (Map<Object, Object> dst : maps) {
            for (Map<?, ?> src : maps) {
                assertSame("Mismatched result for src=" + src + ", dst=" + dst, dst, MapUtils.putUniqueValues(src, dst));
            }
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testPutUniqueValuesWithDuplicates() {
        Map<Object, Object> dst = new HashMap<>(Collections.singletonMap("test", getCurrentTestName()));
        assertSame("Mismatched result for self update", dst, MapUtils.putUniqueValues(dst, dst));

        for (Map.Entry<?, ?> de : dst.entrySet()) {
            Map<?, ?> result = MapUtils.putUniqueValues(Collections.singletonMap(de.getKey(), de.getValue()), dst);
            fail("Unexpected success for entry=" + de + ": " + result);
        }
    }

    @Test
    public void testPutUniqueValues() {
        final Class<?> anchor = getClass();
        Map<String, String> extra = new TreeMap<String, String>() {
            // Not serializing it
            private static final long serialVersionUID = 1L;

            {
                put("class", anchor.getSimpleName());
                put("package", anchor.getPackage().getName());
                put("test", getCurrentTestName());
            }
        };

        Map<String, String> original = Collections.singletonMap("now", new Date().toString());
        Map<String, String> dest = new HashMap<>(original);
        // NOTE: we use a hash map on purpose so we get a map with different keys order than original
        Map<String, String> merged = MapUtils.putUniqueValues(extra, dest);
        assertSame("Not same destination result", dest, merged);
        assertEquals("Mismatched merged size", extra.size() + original.size(), merged.size());

        for (@SuppressWarnings("unchecked")
        Map<String, String> m : new Map[] { original, extra }) {
            for (Map.Entry<String, String> me : m.entrySet()) {
                String key = me.getKey();
                String expected = me.getValue();
                String actual = merged.get(key);
                assertEquals("Mismatched merged result for key=" + key, expected, actual);
            }
        }
    }

    @Test
    public void testSort() {
        Map<Integer, String> map = new HashMap<>();
        map.put(5, "aa");
        map.put(1, "ss");
        map.put(3, "ww");

        Map<Integer, String> actualMap = MapUtils.sort(map);

        Map<Integer, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put(1, "ss");
        expectedMap.put(3, "ww");
        expectedMap.put(5, "aa");

        assertEquals(expectedMap, actualMap);
    }

    @Test
    public void testAddValuesToMap() {
        Map<Integer, ArrayList<String>> map = new HashMap<>();
        MapUtils.addValueToMapList(map, 5, "aa");
        assertTrue("Missing '5' key", map.containsKey(5));
        assertTrue("Missing 'aa' sub-key", map.get(5).contains("aa"));

        MapUtils.addValueToMapList(map, 5, "bb");
        assertTrue("Missing 2nd 'aa' sub-key", map.get(5).contains("aa"));
        assertTrue("Missing 'bb' sub-key", map.get(5).contains("bb"));
        MapUtils.addValueToMapList(map, 1, "aa");
        assertTrue("Missing 'aa' sub-key of '1'", map.get(1).contains("aa"));
    }

    @Test
    public void testSortKeys() {
        Map<Integer, String> map = new HashMap<>();
        map.put(5, "aa");
        map.put(1, "ss");
        map.put(3, "ww");

        Map<Integer, String> actualMap = MapUtils.sort(map);
        Map<Integer, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put(1, "ss");
        expectedMap.put(3, "ww");
        expectedMap.put(5, "aa");

        assertEquals(expectedMap, actualMap);
    }

    @Test
    public void testValuesSort() {
        Map<Integer, String> map = new HashMap<>();
        map.put(5, "aa");
        map.put(1, "ss");
        map.put(3, "ww");

        Map<Integer, String> actualMap = MapUtils.sort(map);

        Map<Integer, String> expectedMap = new LinkedHashMap<>();
        expectedMap.put(5, "aa");
        expectedMap.put(1, "ss");
        expectedMap.put(3, "ww");

        assertEquals(expectedMap, actualMap);
    }

    @Test
    public void testRandomSortByValue() {
        Random random = new Random(System.currentTimeMillis());
        Map<String, Integer> testMap = new HashMap<>(1000);
        for (int i = 0; i < 1000; ++i) {
            testMap.put("SomeString" + random.nextInt(), random.nextInt());
        }

        testMap = MapUtils.sortByValues(testMap);
        assertEquals(1000, testMap.size());

        Integer previous = null;
        for (Map.Entry<String, Integer> entry : testMap.entrySet()) {
            assertNotNull(entry.getValue());
            if (previous != null) {
                assertTrue(entry.getValue() >= previous);
            }
            previous = entry.getValue();
        }
    }

    @Test
    public void testByKeyComparator() {
        Map<String, Object> original = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER) {
            // not serializing it
            private static final long serialVersionUID = 1L;

            {
                Class<?> anchor = getCurrentTestClass();
                put("class", anchor);
                put("package", anchor.getPackage());
                put("method", getCurrentTestName());
                put("now", new Date());
                put("nanos", Long.valueOf(System.nanoTime()));
                put("pi", Math.PI);
            }
        };
        List<Map.Entry<String, Object>> expected = new ArrayList<>(original.entrySet());

        Map<String, Object> hashed = new HashMap<>(original);
        List<Map.Entry<String, Object>> actual = new ArrayList<>(hashed.entrySet());
        Comparator<Map.Entry<String, Object>> comp = MapUtils.byKeyComparator(String.CASE_INSENSITIVE_ORDER);
        Collections.sort(actual, comp);

        assertListEquals(getCurrentTestName(), expected, actual);
    }

    @Test
    public void testFlip() {
        Map<String, Long> src = new TreeMap<String, Long>() {
            private static final long serialVersionUID = -3686693573082540693L;

            {
                put("sysTime", Long.valueOf(7365L));
                put("nanoTime", Long.valueOf(3777347L));
            }
        };

        Map<Number, CharSequence> dst = MapUtils.flip(false, src, new HashMap<Number, CharSequence>(src.size()));
        assertEquals("Mismatched size", src.size(), dst.size());
        for (Map.Entry<String, Long> se : src.entrySet()) {
            String expected = se.getKey();
            Long value = se.getValue();
            CharSequence actual = dst.remove(value);
            assertSame("Mismatched key for value=" + value, expected, actual);
        }
    }

    @Test
    public void testFlipNullOrEmpty() {
        Map<Object, Object> dst = Collections.unmodifiableMap(new HashMap<>());
        assertSame("Mismatached instance for null source", dst, MapUtils.flip(false, null, dst));
        assertSame("Mismatached instance for empty source", dst, MapUtils.flip(false, Collections.emptyMap(), dst));
    }

    @Test(expected = IllegalStateException.class)
    public void testFlipOnDuplicateKeys() {
        Map<String, String> map = new HashMap<>();
        map.put("testName1", getCurrentTestName());
        map.put("testName2", getCurrentTestName());

        Map<String, String> flipped = MapUtils.flip(false, map, new TreeMap<String, String>());
        fail("Unexpected success: " + flipped);
    }

    @Test
    public void testGetMandatoryValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);

        for (Map.Entry<String, ?> me : map.entrySet()) {
            String key = me.getKey();
            Object expected = me.getValue();
            Object actual = MapUtils.getMandatoryValue(map, key);
            assertSame("Mismatched mandatory value instance for key=" + key, expected, actual);
        }
    }

    @Test(expected = NullPointerException.class)
    public void testGetMandatoryValueWithoutValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", 2);

        Object mandatoryValue = MapUtils.getMandatoryValue(map, getCurrentTestName());
        fail("Unexpected success: " + mandatoryValue);
    }

    @Test(expected = NullPointerException.class)
    public void testGetMandatoryValueWithEmptyMap() {
        Map<String, Object> map = new HashMap<>();

        Object mandatoryValue = MapUtils.getMandatoryValue(map, getCurrentTestName());
        fail("Unexpected success: " + mandatoryValue);
    }

    @Test
    public void testGetMandatoryIntValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("A", 1);
        map.put("B", "2");

        int mandatoryIntValueA = MapUtils.getMandatoryIntValue(map, "A");
        assertEquals("Mismatched value for the 'A' key", 1, mandatoryIntValueA);
        int mandatoryIntValueB = MapUtils.getMandatoryIntValue(map, "B");
        assertEquals("Mismatched value for the 'B' key", 2, mandatoryIntValueB);
    }

    @Test
    public void testGetMandatoryDoubleValue() {
        Map<String, Object> map = new HashMap<>();
        map.put("A", 1.2);
        map.put("B", "2.2");

        double mandatoryIntValueA = MapUtils.getMandatoryDoubleValue(map, "A");
        assertEquals("Mismatched value for the 'A' key", 1.2, mandatoryIntValueA, 0);
        double mandatoryIntValueB = MapUtils.getMandatoryDoubleValue(map, "B");
        assertEquals("Mismatched value for the 'B' key", 2.2, mandatoryIntValueB, 0);
    }

    @Test
    public void testNestedLen() {
        Map<String, Collection<Object>> mapOfColls = new HashMap<>();

        Collection<Object> col1 = new ArrayList<>();
        col1.add(1L);
        col1.add(2L);

        Collection<Object> col2 = new LinkedList<>();
        col2.add(1L);
        col2.add(2L);
        col2.add(3L);

        mapOfColls.put("a", col1);
        mapOfColls.put("b", col2);
        assertEquals("Mismatched nested entries count", 5, MapUtils.nestedLen(mapOfColls));
    }

    @Test
    public void testEmptyNestedLen() {
        assertEquals("Mismatched empty nested entries count", 0, MapUtils.nestedLen(new HashMap<String, Collection<Object>>()));
    }

    @Test
    public void testNullNestedLen() {
        assertEquals("Mismatched null 0 nested entries count", 0, MapUtils.nestedLen((Map<String, Collection<Object>>) null));
    }

    @Test
    public void testClearNullOrEmptyMap() {
        assertNull("Unexpected null map clear result", MapUtils.clear(null));

        @SuppressWarnings("unchecked")
        Map<String, String> expected = Mockito.mock(Map.class);
        Mockito.when(expected.size()).thenReturn(NumberUtils.INTEGER_ZERO);
        Mockito.when(expected.isEmpty()).thenReturn(Boolean.TRUE);
        Mockito.when(expected.toString()).thenReturn(getCurrentTestName());
        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                fail("Unexpected clear method invocation");
                return null;
            }
        }).when(expected).clear();

        Map<String, String> actual = MapUtils.clear(expected);
        assertSame("Mismatched cleared result reference", expected, actual);
    }

    @Test
    public void testClearNonEmptyMap() {
        Map<String, String> expected = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {
            // Not serializing it
            private static final long serialVersionUID = 1L;

            {
                put("testName", getCurrentTestName());
                put("className", SafeUtils.safeShortName(getCurrentTestClass()));
            }
        };
        Map<String, String> actual = MapUtils.clear(expected);
        assertSame("Mismatched cleared result reference", expected, actual);
        assertTrue("Map not cleared", actual.isEmpty());
        assertEquals("Size not zeroed", 0, actual.size());
    }

    @Test
    public void testClearAndReplace() {
        for (@SuppressWarnings("unchecked")
            Map<String, String> expected : new Map[] { null, Collections.emptyMap(), new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {
                // Not serializing it
                private static final long serialVersionUID = 1L;

                {
                    put("testName", getCurrentTestName());
                    put("className", SafeUtils.safeShortName(getCurrentTestClass()));
                }
            } }) {
            Map<String, String> original = new HashMap<String, String>() {
                // Not serializing it
                private static final long serialVersionUID = 1L;

                {
                    put("expected", Objects.toString(expected));
                }
            };

            Map<String, String> actual = MapUtils.clearAndReplace(original, expected);
            assertTrue("Original not cleared: " + original, original.isEmpty());
            assertSame("Mismatched replacement", expected, actual);
        }
    }

    @Test
    public void testKeyExtractor() {
        Map<String, Object> map = new TreeMap<String, Object>() {
            // Not serializing it
            private static final long serialVersionUID = 1L;

            {
                put("testName", getCurrentTestName());
                put("className", getCurrentTestClass());
            }
        };

        Transformer<Map.Entry<String, Object>, String> keyExtractor = MapUtils.keyExtractor();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String expected = entry.getKey();
            String actual = keyExtractor.transform(entry);
            assertSame(expected, actual);
        }
    }

    @Test
    public void testValueExtractor() {
        Map<String, Object> map = new TreeMap<String, Object>() {
            // Not serializing it
            private static final long serialVersionUID = 1L;

            {
                put("testName", getCurrentTestName());
                put("className", getCurrentTestClass());
            }
        };

        Transformer<Map.Entry<String, Object>, Object> valueExtractor = MapUtils.valueExtractor();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object expected = entry.getValue();
            Object actual = valueExtractor.transform(entry);
            assertSame(key, expected, actual);
        }
    }

    @Test
    public void testFilterByKey() {
        String expectedKey = "testName";
        Map<String, Object> map = new HashMap<String, Object>() {
            // Not serializing it
            private static final long serialVersionUID = 1L;

            {
                put(expectedKey, getCurrentTestName());
                put("className", getCurrentTestClass());
            }
        };

        Map.Entry<String, Object> expEntry = Pair.of(expectedKey, map.get(expectedKey));
        NavigableMap<String, Object> actual = MapUtils.filterByKey(map, k -> expectedKey.equals(k), TreeMap::new);
        assertMapEquals(getCurrentTestName(), Collections.singletonMap(expEntry.getKey(), expEntry.getValue()), actual);

        Map.Entry<String, Object> actEntry = SafeUtils.head(actual.entrySet());
        assertSame("Mismatched key instance", expEntry.getKey(), actEntry.getKey());
        assertSame("Mismatched value instance", expEntry.getValue(), actEntry.getValue());
    }

    @Test
    public void testFilterByEntry() {
        Map<Integer, Integer> map = new HashMap<>();
        for (int index = 1; index <= Byte.SIZE; index++) {
            map.put(index, index & 0x01);
        }

        Map<Integer, Integer> expected = new HashMap<>();
        Predicate<Map.Entry<Integer, Integer>> selector = e -> (e.getKey() > 3) && (e.getKey() <= 7) && ((e.getValue() & 0x01) == 0);
        map.forEach((key, value) -> {
            if (selector.test(Pair.of(key, value))) {
                expected.put(key, value);
            }
        });

        Map<Integer, Integer> actual = MapUtils.filterByEntry(map, selector, TreeMap::new);
        assertMapEquals(getCurrentTestName(), expected, actual);
    }

    @Test
    public void testEqualPropertiesCaseSensitiveName() {
        Properties p1 = new Properties() {
            // Not serializing it
            private static final long serialVersionUID = 1L;

            {
                setProperty(getCurrentTestName(), getCurrentTestName());

                Class<?> c = getCurrentTestClass();
                setProperty(c.getSimpleName(), c.getSimpleName());

                Package pkg = c.getPackage();
                setProperty(pkg.getName(), pkg.getName());
            }
        };

        Properties p2 = new Properties();
        for (String name : p1.stringPropertyNames()) {
            String key = shuffleCase(name);
            String value = p1.getProperty(name);
            p2.setProperty(key, value);
        }

        assertFalse("Unexpected case sensitive equality", MapUtils.equalProperties(p1, p2));
        assertTrue("Non-equal case insensitive result", MapUtils.equalProperties(p1, p2, String.CASE_INSENSITIVE_ORDER, Comparator.naturalOrder()));
    }

    @Test
    public void testEqualPropertiesCaseSensitiveValue() {
        Properties p1 = new Properties() {
            // Not serializing it
            private static final long serialVersionUID = 1L;

            {
                setProperty(getCurrentTestName(), getCurrentTestName());

                Class<?> c = getCurrentTestClass();
                setProperty(c.getSimpleName(), c.getSimpleName());

                Package pkg = c.getPackage();
                setProperty(pkg.getName(), pkg.getName());
            }
        };

        Properties p2 = new Properties();
        for (String key : p1.stringPropertyNames()) {
            String value = p1.getProperty(key);
            p2.setProperty(key, shuffleCase(value));
        }

        assertFalse("Unexpected case sensitive equality", MapUtils.equalProperties(p1, p2));
        assertTrue("Non-equal case insensitive result", MapUtils.equalProperties(p1, p2, Comparator.naturalOrder(), String.CASE_INSENSITIVE_ORDER));
    }

    @Test
    public void testSubMapWithEmptyMaps() {
        Map<String, Integer> m1 = new HashMap<>();
        m1.put(getCurrentTestName(), 1);
        Collection<Map<String, Integer>> empties = Arrays.asList(null, Collections.emptyMap());
        for (Map<String, Integer> empty : empties) {
            assertTrue("empty is submap of any map", MapUtils.isSubMap(m1, empty));
            assertFalse("nothing is a submap of empty", MapUtils.isSubMap(empty, m1));
            assertTrue("empty is submap of empty", MapUtils.isSubMap(empty, empty));
        }
    }

    @Test
    public void testSubMapWithSubs() {
        Map<String, Integer> m1 = new HashMap<>();
        m1.put(getCurrentTestName(), 1);
        Map<String, Integer> m2 = new HashMap<>();
        m2.put(getCurrentTestName(), 1);

        assertTrue("equal maps are sub maps", MapUtils.isSubMap(m1, m2));
        assertTrue("equal maps are sub maps", MapUtils.isSubMap(m2, m1));
        m1.put(JAVA_SUFFIX, 2);
        assertTrue("strictly sub", MapUtils.isSubMap(m1, m2));
        assertFalse("non sub", MapUtils.isSubMap(m2, m1));

        int differentValueForKey = m1.get(JAVA_SUFFIX) + 1;
        m2.put(JAVA_SUFFIX, differentValueForKey);

        assertFalse("non sub", MapUtils.isSubMap(m1, m2));
        assertFalse("non sub", MapUtils.isSubMap(m2, m1));
    }

    @Test
    public void testGetSubMap() {
        Map<String, Integer> hashMap = new HashMap<>();
        hashMap.put(getCurrentTestName(), 1);
        hashMap.put(JAVA_SUFFIX, 2);
        hashMap.put(JAVA_TYPE, 3);
        NavigableMap<String, Integer> expectedSingletonMap = MapUtils.getSubMap(hashMap, e -> e.getValue() > 2, TreeMap::new);
        assertEquals(Collections.singletonMap(JAVA_TYPE, 3), expectedSingletonMap);

        Map<String, Integer> treeMap = new TreeMap<>(hashMap);

        Map<String, Integer> expectedEmpty = MapUtils.getSubMap(treeMap, e -> e.getValue() > 3, HashMap::new);
        assertEquals(Collections.emptyMap(), expectedEmpty);

        Map<String, Integer> expectedAllEntries = MapUtils.getSubMap(treeMap, e -> e.getValue() < 10, HashMap::new);
        assertEquals(treeMap, expectedAllEntries);
    }

    @Test
    public void testGetSubMapEmpty() {
        assertEquals(Collections.emptyMap(), MapUtils.getSubMap(Collections.emptyMap(), e -> true, TreeMap::new));
        assertEquals(Collections.emptyMap(), MapUtils.getSubMap(null, e -> true, HashMap::new));
    }
}

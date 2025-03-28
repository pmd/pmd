/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.util.Map;

/**
 * @see <a href="https://openjdk.org/jeps/488">JEP 488: Primitive Types in Patterns, instanceof, and switch (Second Preview)</a> (Java 24)
 */
public class Jep488_PrimitiveTypesInPatternsInstanceofAndSwitch {

    void switchWithPrimitiveTypePatterns() {
        class X {
            int getStatus() { return 0; }
            int getYearlyFlights() { return 1; }
            void noop() {}
            void issueDiscount() {}
            void issueGoldCard() {}
        }
        X x = new X();

        String status = switch (x.getStatus()) {
            case 0 -> "okay";
            case 1 -> "warning";
            case 2 -> "error";
            case int i -> "unknown status: " + i;
        };
        System.out.println("status: " + status);

        switch (x.getYearlyFlights()) {
            case 0 -> x.noop();
            case 1 -> x.noop();
            case 2 -> x.issueDiscount();
            case int i when i >= 100 -> x.issueGoldCard();
            case int i -> x.issueDiscount(); //  ... appropriate action when i > 2 && i < 100 ...
        }
    }

    sealed interface JsonValue {}
    record JsonString(String s) implements JsonValue { }
    record JsonNumber(double d) implements JsonValue { }
    record JsonObject(Map<String, JsonValue> map) implements JsonValue { }
    void recordPatternsWithPrimitiveTypes() {
        var json = new JsonObject(Map.of("name", new JsonString("John"),
                "age",  new JsonNumber(30)));

        if (json instanceof JsonObject(var map)
                && map.get("name") instanceof JsonString(String n)
                && map.get("age")  instanceof JsonNumber(int a)) {
            int age = a;
            System.out.printf("Name: %s Age: %d%n", n, age);
        }
    }

    void patternMatchingForInstanceofWithPrimitiveTypes() {
        class X {
            int getPopulation() { return 0; }
        }

        X x = new X();
        if (x.getPopulation() instanceof float pop) {
            System.out.println("pop: " + pop);
        }
        int i = x.getPopulation();
        if (i instanceof byte b) {
            System.out.println("byte: " + b);
        }
        if (i instanceof byte b1) { // traditional cast required
            System.out.println("byte: ... " + b);
        }
    }

    void expanedPrimitiveSupportInSwitch() {
        class User {
            boolean isLoggedIn() { return false; }
            int id() { return 42; }
        }
        User user = new User();
        int userId = switch(user.isLoggedIn()) {
            case true -> user.id();
            case false -> { System.out.println("Unrecognized user"); yield - 1; }
        };

        long v = 12345L;
        switch(v) {
            case 1L              -> System.out.println("1L");
            case 2L              -> System.out.println("2L");
            case 10_000_000_000L -> System.out.println("10x");
            case 20_000_000_000L -> System.out.println("20x");
            case long x          -> System.out.println("... " + x + " ...");
        }

        float fv = 1.2f;
        fv = switch (fv) {
            case 0f -> 5f;
            case float x when x == 1f -> 6f + x;
            case float x -> 7f + x;
        };

        double dd = 1.2;
        dd = switch (dd) {
            case 0d -> 5;
            case double d when d == 2 -> 6 + d;
            case double d -> 8 + d;
        };
    }

    void instanceofPrecondition() {
        {
            byte b = 42;
            assert b instanceof int;         // true (unconditionally exact)
        }

        {
            int i = 42;
            assert i instanceof byte;        // true (exact)
        }

        {
            int i = 1000;
            assert !(i instanceof byte);        // false (not exact)
        }

        {
            int i = 16_777_217;       // 2^24 + 1
            assert !(i instanceof float);       // false (not exact)
            assert i instanceof double;      // true (unconditionally exact)
            assert i instanceof Integer;     // true (unconditionally exact)
            assert i instanceof Number;      // true (unconditionally exact)
        }

        {
            float f = 1000.0f;
            assert !(f instanceof byte);        // false
            assert f instanceof int;         // true (exact)
            assert f instanceof double;      // true (unconditionally exact)
        }

        {
            double d = 1000.0d;
            assert !(d instanceof byte);        // false
            assert d instanceof int;         // true (exact)
            assert d instanceof float;       // true (exact)
        }

        {
            Integer ii = 1000;
            assert ii instanceof int;        // true (exact)
            assert ii instanceof float;      // true (exact)
            assert ii instanceof double;     // true (exact)
        }

        {
            Integer ii = 16_777_217;
            assert !(ii instanceof float);      // false (not exact)
            assert ii instanceof double;     // true (exact)
        }
    }

    void primitivePatternInSwitch() {
        // Applicability
        int i = 1;
        switch (i) {
            case double d -> System.out.println("double: " + d);
        }

        Byte b = 1;
        switch (b) {             // exhaustive switch
            case int p -> System.out.println("p: " + p);
        }
    }
}

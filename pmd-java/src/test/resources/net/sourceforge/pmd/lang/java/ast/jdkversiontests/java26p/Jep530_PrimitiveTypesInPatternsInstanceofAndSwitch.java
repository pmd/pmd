/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.util.Map;

/**
 * @see <a href="https://openjdk.org/jeps/530">JEP 530: Primitive Types in Patterns, instanceof, and switch (Fourth Preview)</a> (Java 26)
 */
public class Jep530_PrimitiveTypesInPatternsInstanceofAndSwitch {

    /**
     * 1. Extend pattern matching so that primitive type patterns are applicable to a wider range of match candidate
     * types. This will allow expressions such as v instanceof JsonNumber(int age).
     */
    void primitiveTypePatterns() {
        var json = new JsonObject(Map.of("name", new JsonString("John"),
                "age",  new JsonNumber(30)));

        if (json instanceof JsonObject(var map)
                && map.get("name") instanceof JsonString(String n)
                && map.get("age")  instanceof JsonNumber(int age)) {
            System.out.printf("Name: %s Age: %d%n", n, age);
        }
    }
    sealed interface JsonValue {}
    record JsonString(String s) implements JsonValue { }
    record JsonNumber(double d) implements JsonValue { }
    record JsonObject(Map<String, JsonValue> map) implements JsonValue { }

    /**
     * 2. Enhance the instanceof and switch constructs to support primitive type patterns as top level patterns.
     */
    void topLevelPrimitiveTypePatterns() {
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

        if (x.getPopulation() instanceof float p) {
            // ... p ...
        }
        int i = 42;
        if (i instanceof byte b) {
            // ... b ...
        }
    }
    class X {
        int getStatus() { return 0; }
        int getYearlyFlights() { return 1; }
        void noop() {}
        void issueDiscount() {}
        void issueGoldCard() {}
        int getPopulation() { return 1; }
    }

    /**
     * 3. Further enhance the instanceof construct so that, when used for type testing rather than pattern matching,
     * it can test against all types, not just reference types. This will extend instanceof's current role, as the
     * precondition for safe casting on reference types, to apply to all types.
     *
     * <p>More broadly, this means that instanceof can safeguard all conversions, whether the match candidate is
     * having its type tested (e.g., x instanceof int, or y instanceof String) or having its value matched
     * (e.g., x instanceof int i, or y instanceof String s).
     */
    void instanceofWithPrimitives() {
        int i = 42;
        if (i instanceof byte) {
            byte b = (byte) i;
            // ... b ...
        }
    }

    /**
     * 4. Further enhance the switch construct so that it works with all primitive types, not just a subset
     * of the integral primitive types.
     */
    void switchOnAllPrimitives() {
        User user = new User();
        User.startProcessing(User.OrderStatus.NEW, switch (user.isLoggedIn()) {
            case true  -> user.id();
            case false -> { User.log("Unrecognized user"); yield -1; }
        });

        {
            long v = (long) Math.random() * Long.MAX_VALUE;
            switch (v) {
                case 1L -> System.out.println("v is 1L");
                case 2L -> System.out.println("v is 2L");
                case 10_000_000_000L -> System.out.println("v is 10b");
                case 20_000_000_000L -> System.out.println("v is 20b");
                case long x -> System.out.println("v is " + x);
            }
        }

        {
            Byte b = 2;
            switch (b) {             // exhaustive switch
                case int p -> {}
            }
        }

        {
            int x = 42;
            switch (x) {
                case int _ -> {}  // unconditional pattern
                //case float _    -> {}  // error: dominated
            }
        }

        {
            float v = 1.1f;
            float result = switch (v) {
                case 0f -> 5f;
                case float x when x == 1f -> 6f + x;
                case float x -> 7f + x;
            };
        }

        {
            boolean v = true;
            switch (v) {
                case true -> {
                }
                case false -> {
                }
            }
        }

        {
            // Applicability
            int i = 1;
            switch (i) {
                case double d -> System.out.println("double: " + d);
            }
        }
    }
    class User {
        boolean isLoggedIn() { return false; }
        int id() { return 42; }
        static void startProcessing(OrderStatus status, int userId) {}
        static void log(String s) {}
        enum OrderStatus { NEW };
    }

    void instanceofAsThePreconditionForSafeCasting() {
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
            int i = 16_777_217;                 // 2^24 + 1
            assert !(i instanceof float);       // false (not exact)
            assert i instanceof double;         // true (unconditionally exact)
            assert i instanceof Integer;        // true (unconditionally exact)
            assert i instanceof Number;         // true (unconditionally exact)
        }

        {
            float f = 1000.0f;
            assert !(f instanceof byte);        // false
            assert f instanceof int;            // true (exact)
            assert f instanceof double;         // true (unconditionally exact)
        }

        {
            double d = 1000.0d;
            assert !(d instanceof byte);        // false
            assert d instanceof int;            // true (exact)
            assert d instanceof float;          // true (exact)
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
            assert ii instanceof double;        // true (exact)
        }
    }
}

/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * @see <a href="https://openjdk.org/jeps/455">JEP 455: Primitive Types in Patterns, instanceof, and switch (Preview)</a> (Java 23)
 */
public class Jep455_PrimitiveTypesInPatternsInstanceofAndSwitch {
    public static void instanceofWithPrimitiveTypes() {
        byte b = 42;
        if (b instanceof int) { // true (unconditionally exact)
            System.out.println("b = " + b + " is an int");
        }
        int i = 42;
        if (i instanceof byte) { // true (exact)
            System.out.println("i = " + i + " can be converted to byte");
        }
        int bigInt = 1000;
        if (bigInt instanceof byte) { // false (not exact)
            System.out.println("bigInt = " + bigInt);
        } else {
            System.out.println("bigInt = " + bigInt + " cannot be converted to byte");
        }

        int i2 = 16_777_217;       // 2^24 + 1
        System.out.println("i2 as float: " + (i2 instanceof float));     // false (not exact)
        System.out.println("i2 as double: " + (i2 instanceof double));   // true (unconditionally exact)
        System.out.println("i2 as Integer: " + (i2 instanceof Integer)); // true (unconditionally exact)
        System.out.println("i2 as Number: " + (i2 instanceof Number));   // true (unconditionally exact)

        float f = 1000.0f;
        System.out.println("f as byte: " + (f instanceof byte));      // false
        System.out.println("f as int: " + (f instanceof int));        // true (exact)
        System.out.println("f as double: " + (f instanceof double));  // true (unconditionally exact)

        double d = 1000.0d;
        System.out.println("d as byte: " + (d instanceof byte));     // false
        System.out.println("d as int: " + (d instanceof int));       // true (exact)
        System.out.println("d as float: " + (d instanceof float));   // true (exact)

        Integer ii = 1000;
        System.out.println("ii as int: " + (ii instanceof int));        // true (exact)
        System.out.println("ii as float: " + (ii instanceof float));    // true (exact)
        System.out.println("ii as double: " + (ii instanceof double));  // true (exact)

        Integer ii2 = 16_777_217;
        System.out.println("ii2 as float: " + (ii2 instanceof float));    // false (not exact)
        System.out.println("ii2 as double: " + (ii2 instanceof double));  // true (exact)
    }

    public static void primitiveTypePatternsInInstanceof() {
        int i = 1000;
        if (i instanceof byte b) {
            System.out.println("b = " + b);
        } else {
            System.out.println("i cannot be coverted to byte without loss...");
        }
    }

    public static void primitiveTypePatternsInSwitch() {
        int i = 1000;
        switch (i) {
            case double d:
                System.out.println("d = " + d);
                break;
        }
    }

    public static void expandedPrimitiveSupportInSwitch() {
        float v = 1.0f;
        float f = switch (v) {
            case 0f -> 5f;
            case float x when x == 1f -> 6f + x;
            case float x -> 7f + x;
        };
        System.out.println("f = " + f);

        boolean b = true;
        switch (b) {
            case true -> System.out.println("b was true");
            case false -> System.out.println("b was false");
            // Alternatively: case true, false -> ...
        }
    }

    public static void main(String[] args) {
        instanceofWithPrimitiveTypes();
        primitiveTypePatternsInInstanceof();
        primitiveTypePatternsInSwitch();
        expandedPrimitiveSupportInSwitch();
    }
}

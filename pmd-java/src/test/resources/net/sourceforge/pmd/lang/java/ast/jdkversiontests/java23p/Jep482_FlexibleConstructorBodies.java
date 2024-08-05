/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAKey;

/**
 * @see <a href="https://openjdk.org/jeps/447">JEP 447: Statements before super(...) (Preview)</a> (Java 22)
 * @see <a href="https://openjdk.org/jeps/482">JEP 482: Flexible Constructor Bodies (Second Preview)</a> (Java 23)
 */
class Jep482_FlexibleConstructorBodies {
    // To test backwards compatibility - "normal" explicit constructor invocation
    public static class Old {
        public Old() {
            super();
        }
    }

    // Example: Validating superclass constructor arguments
    public static class PositiveBigInteger extends BigInteger {

        public PositiveBigInteger(long value) {
            if (value <= 0)
                throw new IllegalArgumentException("non-positive value");
            final String valueAsString = String.valueOf(value);
            super(valueAsString);
        }
    }

    // Example: Preparing superclass constructor arguments
    public static class Super {
        public Super(byte[] bytes) {}
    }

    public class Sub extends Super {
        public Sub(Certificate certificate) {
            var publicKey = certificate.getPublicKey();
            if (publicKey == null)
                throw new IllegalArgumentException("null certificate");
            final byte[] byteArray = switch (publicKey) {
                case RSAKey rsaKey -> rsaKey.toString().getBytes(StandardCharsets.UTF_8);
                case DSAPublicKey dsaKey -> dsaKey.toString().getBytes(StandardCharsets.UTF_8);
                default -> new byte[0];
            };
            super(byteArray);
        }
    }

    // Example: Sharing superclass constructor arguments
    public static class F {}
    public static class Super2 {
        public Super2(F f1, F f2) {}
    }
    public class Sub2 extends Super2 {
        public Sub2(int i) {
            var f = new F();
            super(f, f);
            // ... i ...
        }
    }

    // Example with records
    public record Range(int lo, int hi) {
        public Range(int lo, int hi, int maxDistance) {
            if (lo > hi)
                throw new IllegalArgumentException(String.format("(%d,%d)", lo, hi));
            if (hi - lo > maxDistance)
                throw new IllegalArgumentException(String.format("(%d,%d,%d", lo, hi, maxDistance));
            this(lo, hi);
        }
    }

    // Example with enum
    public enum Color {
        BLUE(1);
        private Color() {
        }
        private Color(int a) {
            if (a < 0) throw new IllegalArgumentException();
            this();
        };
    }

    // Example for Early assignment to fields (new with Java 23 preview)
    public static class EarlyAssignmentToFieldsSuper {
        EarlyAssignmentToFieldsSuper() { overriddenMethod(); }

        void overriddenMethod() { System.out.println("hello"); }
    }

    public static class EarlyAssignmentToFieldsSub extends EarlyAssignmentToFieldsSuper {
        final int x;

        EarlyAssignmentToFieldsSub(int x) {
            this.x = x;    // Initialize the field
            super();       // Then invoke the Super constructor explicitly
        }

        @Override
        void overriddenMethod() { System.out.println(x); }
    }
}

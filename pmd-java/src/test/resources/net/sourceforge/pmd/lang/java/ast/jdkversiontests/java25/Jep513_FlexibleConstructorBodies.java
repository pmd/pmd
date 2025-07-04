/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

import java.math.BigInteger;
import java.security.cert.Certificate;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAKey;

/**
 * @see <a href="https://openjdk.org/jeps/513">JEP 513: Flexible Constructor Bodies</a> (Java 25)
 */
public class Jep513_FlexibleConstructorBodies {

    // Example: Validating superclass constructor arguments
    public class PositiveBigInteger extends BigInteger {
        public PositiveBigInteger(long value) {
            if (value <= 0) throw new IllegalArgumentException("value must be positive");
            super(String.valueOf(value));
        }
    }

    // Example: Preparing superclass constructor arguments
    public class Super {
        public Super(byte[] bytes) {}
    }
    public class Sub extends Super {
        public Sub(Certificate certificate) {
            var publicKey = certificate.getPublicKey();
            if (publicKey == null) throw new NullPointerException();
            byte[] certBytes = switch (publicKey) {
                case RSAKey rsaKey -> rsaKey.getModulus().toByteArray();
                case DSAPublicKey dsaKey -> dsaKey.getY().toByteArray();
                default -> new byte[0];
            };
            super(certBytes);
        }
    }

    // Example: Sharing superclass constructor arguments
    public class C {
        private final int i;
        public C(int i) {
            this.i = i;
        }
    }
    public class Super2 {
        private final C x;
        private final C y;
        public Super2(C x, C y) {
            this.x = x;
            this.y = y;
        }
    }
    public class Sub2 extends Super2 {
        public Sub2(int i) {
            var x = new C(i);
            super(x, x);
        }
    }

    // Using enclosing instances in early construction contexts
    class Outer {
        int i;
        void hello() { System.out.println("Hello"); }

        class Inner {
            int j;

            Inner() {
                var x = i;             // OK - implicitly refers to field of enclosing instance
                var y = Outer.this.i;  // OK - explicitly refers to field of enclosing instance
                hello();               // OK - implicitly refers to method of enclosing instance
                Outer.this.hello();    // OK - explicitly refers to method of enclosing instance
                super();
            }
        }
    }

    // Early assignment to fields
    class Super3 {
        Super3() { overriddenMethod(); }
        void overriddenMethod() { System.out.println("hello"); }
    }
    class Sub3 extends Super3 {
        final int x;

        Sub3(int x) {
            this.x = x;    // Initialize the field
            super();       // Then invoke the Super constructor explicitly
        }

        @Override
        void overriddenMethod() { System.out.println(x); }
    }


}

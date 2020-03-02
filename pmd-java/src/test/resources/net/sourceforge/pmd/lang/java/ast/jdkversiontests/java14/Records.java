import java.io.IOException;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @see <a href="https://openjdk.java.net/jeps/359">JEP 359: Records (Preview)</a>
 */
public class Records {

    @Target(ElementType.TYPE_USE)
    @interface Nullable { }

    @Target({ElementType.CONSTRUCTOR, ElementType.PARAMETER})
    @interface MyAnnotation { }

    public record MyComplex(int real, @Deprecated int imaginary) {
        // explicit declaration of a canonical constructor
        @MyAnnotation
        public MyComplex(@MyAnnotation int real, int imaginary) {
            if (real > 100) throw new IllegalArgumentException("too big");
            this.real = real;
            this.imaginary = imaginary;
        }
        public record Nested(int a) {}
    }


    public record Range(int lo, int hi) {
        // compact record constructor
        @MyAnnotation
        public Range {
          if (lo > hi)  /* referring here to the implicit constructor parameters */
            throw new IllegalArgumentException(String.format("(%d,%d)", lo, hi));
        }

        public void foo() { }
    }

    public record VarRec(@Nullable @Deprecated String @Nullable ... x) {}

    public record ArrayRec(int x[]) {}

    public record EmptyRec<Type>() {
        public void foo() { }
        public Type bar() { return null; }
        public static void baz() {
            EmptyRec<String> r = new EmptyRec<>();
            System.out.println(r);
        }
    }

    // see https://www.javaspecialists.eu/archive/Issue276.html
    public interface Person {
        String firstName();
        String lastName();
    }
    public record PersonRecord(String firstName, String lastName)
        implements Person, java.io.Serializable {

    }
}

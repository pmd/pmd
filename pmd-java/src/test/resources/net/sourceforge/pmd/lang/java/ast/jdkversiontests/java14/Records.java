import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * @see <a href="https://openjdk.java.net/jeps/359">JEP 359: Records (Preview)</a>
 */
public class Records {

    @Target(ElementType.TYPE_USE)
    @interface Nullable {
    }

    public record MyComplex(int real, @Deprecated int imaginary) {
        public record Nested(int a) {};
    }


    public record Range(int lo, int hi) {
        public Range {
          if (lo > hi)  /* referring here to the implicit constructor parameters */
            throw new IllegalArgumentException(String.format("(%d,%d)", lo, hi));
        }
    }

    public record VarRec(@Nullable @Deprecated String @Nullable ... x) {}

    public record ArrayRec(int x[]) {}

    public record EmptyRec() {}
}

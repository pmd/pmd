/**
 * @see <a href="https://openjdk.java.net/jeps/359">JEP 359: Records (Preview)</a>
 */
public class Records {


    public record MyComplex(int real, int imaginary) {
        public record Nested(int a) {};
    };


    public record Range(int lo, int hi) {
        public Range {
          if (lo > hi)  /* referring here to the implicit constructor parameters */
            throw new IllegalArgumentException(String.format("(%d,%d)", lo, hi));
        }
    }
}

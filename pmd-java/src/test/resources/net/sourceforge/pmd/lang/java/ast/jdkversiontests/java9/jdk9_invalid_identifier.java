/**
 * Using "_" as an identifier is not allowed anymore with java9.
 */
public class Java9Identifier {

    /*
     * see https://bugs.openjdk.java.net/browse/JDK-8061549
     */
    public interface Lambda {
        public int a(int _);
        public default void t(Lambda l) {
            t(_ -> 0);
        }
    }
}

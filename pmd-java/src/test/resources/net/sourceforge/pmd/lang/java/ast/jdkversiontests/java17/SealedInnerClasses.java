/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

public class SealedInnerClasses {
    sealed class Square implements Squircle {
        non-sealed private class OtherSquare extends Square {}
        static non-sealed class StaticClass implements Squircle {}
    }

    sealed interface Squircle permits Square, Square.StaticClass {}
}

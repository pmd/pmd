/**
 * @see <a href="https://openjdk.java.net/jeps/361">JEP 361: Switch Expressions (Standard)</a>
 */
public class YieldStatements {
    {
        int yield = 0;
        yield = 2;  // should be an assignment
        yield (2); // should be a method call
        yield(a,b); // should be a method call


        yield = switch (e) { // must be a switch expr
        case 1 -> {
            yield(a,b); // should be a method call
            yield = 2;  // should be an assignment
            yield (2);  // should be a yield statement
            yield++bar; // should be a yield statement (++bar is an expression)
            yield--bar; // should be a yield statement (--bar is an expression)
            yield++;    // should be an increment (not an error)
            yield--;    // should be a decrement (not an error)

            if (true) yield(2);
            else yield 4;

            yield = switch (foo) { // putting a switch in the middles checks the reset behavior
            case 4 -> {yield(5);} // should be a yield statement
            };

            yield () -> {}; // should be a yield statement
            yield ();       // should be a method call
            yield (2);      // should be a yield statement

            // all of the following should be yield statements:
            yield !true;
            yield ~0;
            yield +2;
            yield -2;
            yield --foo;
            yield ++foo;
            yield void.class;
            yield double.class; yield float.class;
            yield long.class; yield int.class; yield short.class;
            yield char.class; yield byte.class;
            yield boolean.class;
            yield null;
            yield 0x001;
            yield 004;
            yield 2e74;
            yield 0b01;
            yield 0x4P60;
            yield new Object();
            yield (new Object());
            yield switch(foo) {
                default -> 4;
            };
            yield this;
            yield super.field;
            yield this.field;
        }
        };
    }
}

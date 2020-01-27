/**
 *
 * @see <a href="https://openjdk.java.net/jeps/325">JEP 325: Switch Expressions (Preview)</a>
 */
public class SwitchExpressionsYield {
    private static final int MONDAY = 1;
    private static final int TUESDAY = 2;
    private static final int WEDNESDAY = 3;
    private static final int THURSDAY = 4;
    private static final int FRIDAY = 5;
    private static final int SATURDAY = 6;
    private static final int SUNDAY = 7;

    private static final int SIX = 6;

    public static void main(String[] args) {
        int day = FRIDAY;

        var numLetters = switch (day) {
                    case MONDAY, FRIDAY, SUNDAY: yield SwitchExpressionsBreak.SIX;
                    case TUESDAY               : yield 7;
                    case THURSDAY, SATURDAY    : yield 8;
                    case WEDNESDAY             : yield 9;
                    default                    : {
                        int k = day * 2;
                        int result = f(k);
                        yield result;
                    }
                };
        System.out.printf("NumLetters: %d%n", numLetters);
    }

    private static int f(int k) {
        return k*3;
    }
}

/**
 * @see <a href="https://openjdk.java.net/jeps/361">JEP 361: Switch Expressions (Standard)</a>
 */
public class SwitchExpressions {
    private enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    }

    public static final int BAZ = 3;

    public static void main(String[] args) {
        Day day = Day.THURSDAY;

        // SwitchStatement
        switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> System.out.println(6);
            case TUESDAY                -> System.out.println(7);
            case THURSDAY, SATURDAY     -> System.out.println(8);
            case WEDNESDAY              -> System.out.println(9);
        }

        // SwitchExpression
        int numLetters = switch (day) {
            case MONDAY, FRIDAY, SUNDAY -> 6;
            case TUESDAY                -> 7;
            case THURSDAY, SATURDAY     -> 8;
            case WEDNESDAY              -> 9;
        };
        System.out.printf("numLetters=%d%n", numLetters);

        howMany(1);
        howMany(2);
        howMany(3);

        howManyExpr(1);
        howManyExpr(2);
        howManyExpr(3);

        // SwitchExpression
        int j = switch (day) {
            case MONDAY  -> 0;
            case TUESDAY -> 1;
            default      -> {
                int k = day.toString().length();
                int result = f(k);
                yield result;
            }
        };
        System.out.printf("j=%d%n", j);

        String s = "Foo";
        // SwitchExpression
        int result = switch (s) {
            case "Foo": 
                yield 1;
            case "Bar":
                yield 2;
            case "Baz":
                yield SwitchExpressions.BAZ;
            default:
                System.out.println("Neither Foo nor Bar, hmmm...");
                yield 0;
        };
        System.out.printf("result=%d%n", result);
    }

    private static void howMany(int k) {
        // SwitchStatement
        switch (k) {
            case 1  -> System.out.println("one");
            case 2  -> System.out.println("two");
            default -> System.out.println("many");
        }
    }

    private static void howManyExpr(int k) {
        System.out.println(
            // SwitchExpression
            switch (k) {
                case  1 -> "one";
                case  2 -> "two";
                default -> "many";
            }
        );
    }

    private static int f(int k) {
        return k*2;
    }
}
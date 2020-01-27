/**
 *
 *
 * @see <a href="http://openjdk.java.net/jeps/354">JEP 354: Switch Expressions (Preview)</a>
 */
public class SwitchExpressions {
    private enum Day {
        MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    }

    public static void main(String[] args) {
        Day day = Day.FRIDAY;

        int j = switch (day) {
            case MONDAY  -> 0;
            case TUESDAY -> 1;
            default      -> {
                int k = day.toString().length();
                int result = f(k);
                yield result;
            }
        };
        System.out.printf("j = %d%n", j);

        String s = "Bar";
        int result = switch (s) {
            case "Foo":
                yield 1;
            case "Bar":
                yield 2;
            default:
                System.out.println("Neither Foo nor Bar, hmmm...");
                yield 0;
        };
        System.out.printf("result = %d%n", result);
    }

    private static int f(int k) {
        return k+1;
    }
}

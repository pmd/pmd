public class WrongMethodArgument {
    private static void takesInteger(Integer value) {
    }

    public static void run() {
        takesInteger('hello');
    }
}

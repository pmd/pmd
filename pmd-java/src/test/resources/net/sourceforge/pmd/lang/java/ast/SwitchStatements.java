public class SwitchStatements {

    public void myMethod() {
        int a = 1;

        // only fall through, no block statements.
        switch (a) {
            case 1:
            default:
        }

        // empty switch statement
        switch (a) { }

        // last label without block statement
        switch (a) {
        case 1:
            System.out.println("1");
            break;
        default:
        }
    }
}
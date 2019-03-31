public class SwitchWithFallthrough {

    // only fall through, no block statements.
    public void myMethod() {
        int a = 1;
        switch (a) {
            case 1:
            default:
        }
    }
}
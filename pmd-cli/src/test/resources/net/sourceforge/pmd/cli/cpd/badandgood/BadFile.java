public class BadFile {
    public void foo() {
        // this is a bad character � it's U+FFFD REPLACEMENT CHARACTER
        int a�b = 1;
    }
}
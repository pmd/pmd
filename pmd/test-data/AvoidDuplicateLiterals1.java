public class AvoidDuplicateLiterals1 {
 private void bar() {
    buz("Howdy");
    buz("Howdy");
    buz("Howdy");
    buz("Howdy");
 }
 private void buz(String x) {}
}
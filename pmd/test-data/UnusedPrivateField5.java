public class UnusedPrivateField5 {
 public void bar() {
  Runnable r = new Runnable() {
   public void run() {
    String foo = "";
   }
  };
 }
 private String foo;
}

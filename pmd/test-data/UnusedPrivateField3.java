public class UnusedPrivateField3 {

 private String foo;

 public void baz() {
  Runnable r = new Runnable() {
   public void run() {
    String foo = "buz";
   }
  };	
 }
}

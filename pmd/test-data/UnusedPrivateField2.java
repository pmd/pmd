public class UnusedPrivateField2 {
 
 private String foo;
 private String bar = foo;
 
 public void buz() {
  bar = null;
 }
}

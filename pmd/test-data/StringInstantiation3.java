public class StringInstantiation3 {
 public void foo() {
  byte[] bytes = new byte[50];
  String bar = new String(bytes, 0, bytes.length);
 }
}

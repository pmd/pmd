public class StringInstantiation4 {
 public void foo() {
  byte[] bytes = new byte[50];
  String bar = new String(bytes, 0, bytes.length, "some-encoding");
 }
}

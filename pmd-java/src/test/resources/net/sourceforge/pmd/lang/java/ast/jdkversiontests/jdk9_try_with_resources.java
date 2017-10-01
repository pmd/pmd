public class InputJava9TryWithResources {
 public static void main() {
  MyResource resource1 = new MyResource();
  MyResource resource2 = new MyResource();
  try (resource1; resource2) { }
 }
}
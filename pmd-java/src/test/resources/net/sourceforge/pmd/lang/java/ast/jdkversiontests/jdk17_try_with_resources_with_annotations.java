public class InputJava7TryWithResources {
 public static void main() {
  try (@SuppressWarnings("all") final MyResource resource = new MyResource()) { }
 }
}
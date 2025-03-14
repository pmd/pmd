public class InputJava7TryWithResources {
 public static void main() {
  try (MyResource resource = new MyResource()) { }
 }
}
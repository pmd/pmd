public class InputJava7TryWithResources {
 public static void main() {
  try (MyResource resource = new MyResource(); MyResource2 resource2 = new MyResource2()) { }
 }
}
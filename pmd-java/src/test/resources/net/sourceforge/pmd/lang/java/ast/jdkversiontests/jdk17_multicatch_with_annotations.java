public class InputJava7Multicatch {
 public static void main() {
  try { }
  catch (final @SuppressWarnings("all") FileNotFoundException | CustomException e) { }
 }
}
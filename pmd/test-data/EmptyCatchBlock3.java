public class EmptyCatchBlock3 {
 private void foo() {
  try {
  } finally {
   try {
    int x =2;
   } catch (Exception e) {}
  }
 }
}

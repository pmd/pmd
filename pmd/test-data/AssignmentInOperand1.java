public class AssignmentInOperand1 {
 public void bar() {
  int x = 2;
  if ((x = getX()) == 3) {
   System.out.println("3!");
  }
 }
 private int getX() {
  return 3;
 }
}
public class AssignmentInOperand4 {
 public void bar() {
  int x = 2;
  while ( (x = getX()) != 0 ) {}
 }
 private int getX() {return 2;}
}
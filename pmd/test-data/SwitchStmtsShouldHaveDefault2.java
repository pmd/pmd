public class SwitchStmtsShouldHaveDefault2 {
 public void bar() {
  int x = 2;
  switch (x) {
   case 2: int y=8;
   default: int y=8;
  }
 }
}

public class ConstructorCallsOverridableMethodRule5 {
 public ConstructorCallsOverridableMethodRule5() {
  this("Bar");
 }
 private ConstructorCallsOverridableMethodRule5(String bar) {
  foo();
 }
 public void foo() {}
}
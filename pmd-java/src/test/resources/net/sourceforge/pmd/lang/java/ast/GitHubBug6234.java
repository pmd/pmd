import java.util.concurrent.Callable;

class A {
  A(Callable<Boolean> x) {}
}

class B extends A {
  private static final int X = 1;
  B() {
    super(
        () -> {
          return switch (1) {
            case X -> true;
            default -> false;
          };
        }
    );
  }
}

public class AccessorClassGeneration3 {
    public class InnerClass {
      void method(){
        new AccessorClassGeneration3();//Causes generation of accessor
      }
    }
    private AccessorClassGeneration3(){
    }
}

public class ReturnFromFinallyBlock1 {
 public String bugga() {
  try {
   throw new Exception( "My Exception" );
  } catch (Exception e) {
   throw e;
  } finally {
   return "A. O. K."; // Very bad.
  }
 }
}


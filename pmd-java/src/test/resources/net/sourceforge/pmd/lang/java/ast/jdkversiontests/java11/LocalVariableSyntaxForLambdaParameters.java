import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.function.BiFunction;
import java.util.function.Function;

public class LocalVariableSyntaxForLambdaParameters {

    @Target({ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Nonnull { }

    public void createLambdas() {
        //var lambda = (var x, var y) -> x.process(y);

        Function<Integer, String> lambda1 = (var x) -> String.valueOf(x);
        BiFunction<Integer, Integer, Integer> lambda2 = (var x, var y) -> x + y;
    }

    public void createAnnotatedLambdaParameters() {
        //@Nonnull var x = new Foo();
        //(@Nonnull var x, @Nullable var y) -> x.process(y)

        Function<Integer, String> lambda1 = (@Nonnull var x) -> String.valueOf(x);
        BiFunction<Integer, Integer, Integer> lambda2 = (@Nonnull var x, @Nonnull var y) -> x + y;
    }
}
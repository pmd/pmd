/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.cognitivecomplexity;

import java.util.Map;

public class ReproducerFor5084 {
    private Map<Class, Serializer> serializers;

    public ReproducerFor5084() {
        // -- commented out, because that is java9+ api (Map.of): this.serializers = Map.of(
        //         HttpRequest.class, new HttpRequestSerializer(),
        //         HttpResponse.class, new HttpResponseSerializer()
        // );
    }

    public abstract static class Body<T> { }

    public abstract static class BodyWithContentType<T> extends Body<T> { }

    public interface HttpMessage<T extends HttpMessage, B extends Body> { }

    public static class HttpRequest implements HttpMessage<HttpRequest, Body> { }

    public static class HttpResponse implements HttpMessage<HttpResponse, BodyWithContentType> { }

    public interface Serializer<T> {
        String serialize(T t);
    }

    public class HttpRequestSerializer implements Serializer<HttpRequest> {
        @Override
        public String serialize(HttpRequest s) {
            return String.valueOf(s);
        }
    }

    public class HttpResponseSerializer implements Serializer<HttpResponse> {
        @Override
        public String serialize(HttpResponse s) {
            return String.valueOf(s);
        }
    }
}

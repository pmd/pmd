class Example {
    String foo(Object foo) {
        return switch (foo) {
            case char[] array -> new String(array);
            case String string -> string;
            default -> throw new RuntimeException();
        };
    }
}

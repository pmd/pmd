package org.example.unusedPrivateMethod;

import static java.util.Collections.emptySet;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

public class NestedLambdasAndMethodCalls {

    public static void main(String[] args) {
        Library library = new Library(emptySet());
        Map<String, Map<String, String>> map = new Main().run(library);
        System.out.println(map);
    }

    private Map<String, Map<String, String>> run(Library library) {
        return library
            .books()
            .stream()
            .map(book -> book.lenders().stream().collect(Collectors.toMap(Lender::name, lender -> Map.of(book.title(), lender.status()))))
            .reduce(this::reduceBooksAndLenderStatusByLender)
            .orElse(null);
    }

    private Map<String, Map<String, String>> reduceBooksAndLenderStatusByLender(
        Map<String, Map<String, String>> previousMap,
        Map<String, Map<String, String>> nextMap
    ) {
        previousMap.putAll(nextMap);
        return previousMap;
    }
}


record Lender(String name, String status) {}
record Book(String title, Collection<Lender> lenders) {}
record Library(Collection<Book> books) {}
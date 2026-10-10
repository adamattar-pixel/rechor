package ch.epfl.rechor;

import java.util.Set;
import java.util.stream.Collectors;

public record Trip(short routeId,
                   String headSign,
                   Set<String> attributesCh) {
    public Trip {
        headSign = headSign.intern();
        attributesCh = attributesCh.stream()
                .map(String::intern)
                .collect(Collectors.toUnmodifiableSet());
    }
}

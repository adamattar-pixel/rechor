package ch.epfl.rechor;

import java.util.*;
import java.util.stream.Stream;

public final class StringPool {
    private final Map<String, Integer> index;

    public StringPool(Map<String, Integer> index) {
        this.index = Map.copyOf(index);
    }

    public int size() {
        return index.size();
    }

    public short idOf(String string) {
        var i = index.get(string);
        if (i == null) throw new NoSuchElementException();
        return i.shortValue();
    }

    public Stream<String> strings() {
        return index.keySet().stream()
                .sorted(Comparator.comparingInt(index::get));
    }

    public static final class Builder {
        private final SortedSet<String> strings = new TreeSet<>();

        public void add(String string) {
            strings.add(string);
        }

        public void addAll(Collection<String> strings) {
            this.strings.addAll(strings);
        }

        public StringPool build() {
            var index = new HashMap<String, Integer>();
            for (var string : strings) index.put(string, index.size());
            return new StringPool(index);
        }
    }
}

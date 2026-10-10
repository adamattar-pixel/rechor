package ch.epfl.rechor;

import java.util.*;

public record IdValues<T>(List<IdValue<T>> idValues, Map<String, Integer> index) {

    public record IdValue<T>(Set<String> ids, T value) {
        public IdValue {
            ids = Set.copyOf(ids);
        }
    }

    public IdValues {
        idValues = List.copyOf(idValues);
        index = Map.copyOf(index);
    }

    public int size() {
        return idValues.size();
    }

    public boolean containsId(String id) {
        return index.containsKey(id);
    }

    public int maybeIndexOf(String id) {
        var i = index.get(id);
        return i == null ? -1 : i;
    }

    public int indexOf(String id) {
        var i = index.get(id);
        if (i == null) throw new NoSuchElementException();
        return i;
    }

    public T get(int index) {
        return idValues.get(index).value;
    }

    public List<T> values() {
        return idValues.stream()
                .map(IdValue::value)
                .toList();
    }

    public static final class Builder<T> {
        private final List<IdValue<T>> idValues = new ArrayList<>();

        public Builder<T> add(IdValue<T> iv) {
            idValues.add(iv);
            return this;
        }

        public Builder<T> add(Set<String> ids, T value) {
            return add(new IdValue<>(ids, value));
        }

        public Builder<T> add(String id, T value) {
            return add(Set.of(id), value);
        }

        public Builder<T> deduplicate() {
            var valueKeys = new HashMap<T, Set<String>>();

            idValues.forEach(iv ->
                    valueKeys.computeIfAbsent(iv.value(), k -> new HashSet<>())
                            .addAll(iv.ids()));
            idValues.clear();

            valueKeys.entrySet().stream()
                    .map(e -> new IdValue<>(e.getValue(), e.getKey()))
                    .forEach(idValues::add);

            return this;
        }

        public Builder<T> sort(Comparator<T> comparator) {
            idValues.sort(Comparator.comparing(IdValue::value, comparator));
            return this;
        }

        public IdValues<T> build() {
            var index = new HashMap<String, Integer>();
            for (var i = 0; i < idValues.size(); i++)
                for (var id : idValues.get(i).ids()) index.put(id, i);

            return new IdValues<>(idValues, index);
        }
    }
}

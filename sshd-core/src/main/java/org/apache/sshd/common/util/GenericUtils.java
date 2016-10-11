/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.sshd.common.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.management.MBeanException;
import javax.management.ReflectionException;

/**
 * @author <a href="mailto:dev@mina.apache.org">Apache MINA SSHD Project</a>
 */
public final class GenericUtils {

    public static final byte[] EMPTY_BYTE_ARRAY = {};
    public static final String[] EMPTY_STRING_ARRAY = {};
    public static final Object[] EMPTY_OBJECT_ARRAY = {};

    /**
     * A value indicating a {@code null} value - to be used as a placeholder
     * where {@code null}s are not allowed
     */
    public static final Object NULL = new Object();

    /**
     * The complement of {@link String#CASE_INSENSITIVE_ORDER}
     */
    public static final Comparator<String> CASE_SENSITIVE_ORDER = (s1, s2) -> {
        if (s1 == s2) {
            return 0;
        } else {
            return s1.compareTo(s2);
        }
    };

    public static final String QUOTES = "\"'";

    @SuppressWarnings("rawtypes")
    private static final Supplier CASE_INSENSITIVE_MAP_FACTORY = () -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private GenericUtils() {
        throw new UnsupportedOperationException("No instance");
    }

    public static String trimToEmpty(String s) {
        if (s == null) {
            return "";
        } else {
            return s.trim();
        }
    }

    public static int safeCompare(String s1, String s2, boolean caseSensitive) {
        if (s1 == s2) {
            return 0;
        } else if (s1 == null) {
            return +1;    // push null(s) to end
        } else if (s2 == null) {
            return -1;    // push null(s) to end
        } else if (caseSensitive) {
            return s1.compareTo(s2);
        } else {
            return s1.compareToIgnoreCase(s2);
        }
    }

    public static int length(CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }

    public static boolean isEmpty(CharSequence cs) {
        return length(cs) <= 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

    // a List would be better, but we want to be compatible with String.split(...)
    public static String[] split(String s, char ch) {
        if (isEmpty(s)) {
            return EMPTY_STRING_ARRAY;
        }

        int lastPos = 0;
        int curPos = s.indexOf(ch);
        if (curPos < 0) {
            return new String[]{s};
        }

        Collection<String> values = new LinkedList<>();
        do {
            String v = s.substring(lastPos, curPos);
            values.add(v);

            // skip separator
            lastPos = curPos + 1;
            if (lastPos >= s.length()) {
                break;
            }

            curPos = s.indexOf(ch, lastPos);
            if (curPos < lastPos) {
                break;  // no more separators
            }
        } while (curPos < s.length());

        // check if any leftovers
        if (lastPos < s.length()) {
            String v = s.substring(lastPos);
            values.add(v);
        }

        return values.toArray(new String[values.size()]);
    }

    public static <T> String join(T[] values, char ch) {
        return join(isEmpty(values) ? Collections.<T>emptyList() : Arrays.asList(values), ch);
    }

    public static String join(Iterable<?> iter, char ch) {
        return join((iter == null) ? null : iter.iterator(), ch);
    }

    public static String join(Iterator<?> iter, char ch) {
        if ((iter == null) || (!iter.hasNext())) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        do {    // we already asked hasNext...
            Object o = iter.next();
            if (sb.length() > 0) {
                sb.append(ch);
            }
            sb.append(Objects.toString(o));
        } while (iter.hasNext());

        return sb.toString();
    }

    public static <T> String join(T[] values, CharSequence sep) {
        return join(isEmpty(values) ? Collections.<T>emptyList() : Arrays.asList(values), sep);
    }

    public static String join(Iterable<?> iter, CharSequence sep) {
        return join((iter == null) ? null : iter.iterator(), sep);
    }

    public static String join(Iterator<?> iter, CharSequence sep) {
        if ((iter == null) || (!iter.hasNext())) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        do {    // we already asked hasNext...
            Object o = iter.next();
            if (sb.length() > 0) {
                sb.append(sep);
            }
            sb.append(Objects.toString(o));
        } while (iter.hasNext());

        return sb.toString();
    }

    public static int size(Collection<?> c) {
        return c == null ? 0 : c.size();
    }

    public static boolean isEmpty(Collection<?> c) {
        return (c == null) || c.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return !isEmpty(c);
    }

    public static int size(Map<?, ?> m) {
        return m == null ? 0 : m.size();
    }

    public static boolean isEmpty(Map<?, ?> m) {
        return (m == null) || m.isEmpty();
    }

    public static boolean isNotEmpty(Map<?, ?> m) {
        return !isEmpty(m);
    }

    @SafeVarargs
    public static <T> int length(T... a) {
        return a == null ? 0 : a.length;
    }

    public static <T> boolean isEmpty(Iterable<? extends T> iter) {
        if (iter == null) {
            return true;
        } else if (iter instanceof Collection<?>) {
            return isEmpty((Collection<?>) iter);
        } else {
            return isEmpty(iter.iterator());
        }
    }

    public static<T> boolean isNotEmpty(Iterable<? extends T> iter) {
        return !isEmpty(iter);
    }

    public static <T> boolean isEmpty(Iterator<? extends T> iter) {
        return iter == null || !iter.hasNext();
    }

    public static <T> boolean isNotEmpty(Iterator<? extends T> iter) {
        return !isEmpty(iter);
    }

    @SafeVarargs
    public static <T> boolean isEmpty(T... a) {
        return length(a) <= 0;
    }

    @SafeVarargs    // there is no EnumSet.of(...) so we have to provide our own
    public static <E extends Enum<E>> Set<E> of(E... values) {
        return of(isEmpty(values) ? Collections.emptySet() : Arrays.asList(values));
    }

    public static <E extends Enum<E>> Set<E> of(Collection<? extends E> values) {
        if (isEmpty(values)) {
            return Collections.emptySet();
        }

        Set<E> result = null;
        for (E v : values) {
            /*
             * A trick to compensate for the fact that we do not have
             * the enum Class to invoke EnumSet.noneOf
             */
            if (result == null) {
                result = EnumSet.of(v);
            } else {
                result.add(v);
            }
        }

        return result;
    }

    public static <T> void forEach(Iterable<T> values, Consumer<T> consumer) {
        if (isNotEmpty(values)) {
            values.forEach(consumer);
        }
    }

    public static <T, U> List<U> map(Collection<T> values, Function<? super T, ? extends U> mapper) {
        return stream(values).map(mapper).collect(Collectors.toList());
    }

    public static <T, U> SortedSet<U> mapSort(Collection<T> values,
                                              Function<? super T, ? extends U> mapper,
                                              Comparator<U> comparator) {
        return stream(values).map(mapper).collect(toSortedSet(comparator));
    }

    public static <T, K, U> SortedMap<K, U> toSortedMap(
                                Iterable<T> values,
                                Function<? super T, ? extends K> keyMapper,
                                Function<? super T, ? extends U> valueMapper,
                                Comparator<K> comparator) {
        return stream(values).collect(toSortedMap(keyMapper, valueMapper, comparator));
    }

    public static <T, K, U> Collector<T, ?, SortedMap<K, U>> toSortedMap(
                                Function<? super T, ? extends K> keyMapper,
                                Function<? super T, ? extends U> valueMapper,
                                Comparator<K> comparator) {
        return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), () -> new TreeMap<>(comparator));
    }

    private static <T> BinaryOperator<T> throwingMerger() {
        return (u, v) -> {
            throw new IllegalStateException(String.format("Duplicate key %s", u));
        };
    }

    public static <T> Collector<T, ?, SortedSet<T>> toSortedSet(Comparator<T> comparator) {
        return Collectors.toCollection(() -> new TreeSet<>(comparator));
    }

    public static <T> Stream<T> stream(Iterable<T> values) {
        if (isEmpty(values)) {
            return Stream.empty();
        } else if (values instanceof Collection<?>) {
            return ((Collection<T>) values).stream();
        } else {
            return StreamSupport.stream(values.spliterator(), false);
        }
    }

    @SafeVarargs
    public static <T> List<T> unmodifiableList(T ... values) {
        return unmodifiableList(asList(values));
    }

    public static <T> List<T> unmodifiableList(Collection<? extends T> values) {
        if (isEmpty(values)) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(new ArrayList<>(values));
        }
    }

    public static <T> List<T> unmodifiableList(Stream<T> values) {
        return unmodifiableList(values.collect(Collectors.toList()));
    }

    @SafeVarargs
    public static <T> List<T> asList(T ... values) {
        return isEmpty(values) ? Collections.emptyList() : Arrays.asList(values);
    }

    @SafeVarargs
    public static <T> Set<T> asSet(T ... values) {
        return new HashSet<>(asList(values));
    }

    @SafeVarargs
    public static <V extends Comparable<V>> SortedSet<V> asSortedSet(V ... values) {
        return asSortedSet(Comparator.naturalOrder(), values);
    }

    public static <V extends Comparable<V>> SortedSet<V> asSortedSet(Collection<? extends V> values) {
        return asSortedSet(Comparator.naturalOrder(), values);
    }

    /**
     * @param <V>    The element type
     * @param comp   The (non-{@code null}) {@link Comparator} to use
     * @param values The values to be added (ignored if {@code null})
     * @return A {@link SortedSet} containing the values (if any) sorted
     * using the provided comparator
     */
    @SafeVarargs
    public static <V> SortedSet<V> asSortedSet(Comparator<? super V> comp, V ... values) {
        return asSortedSet(comp, isEmpty(values) ? Collections.emptyList() : Arrays.asList(values));
    }

    /**
     * @param <V>    The element type
     * @param comp   The (non-{@code null}) {@link Comparator} to use
     * @param values The values to be added (ignored if {@code null}/empty)
     * @return A {@link SortedSet} containing the values (if any) sorted
     * using the provided comparator
     */
    public static <V> SortedSet<V> asSortedSet(Comparator<? super V> comp, Collection<? extends V> values) {
        SortedSet<V> set = new TreeSet<>(Objects.requireNonNull(comp, "No comparator"));
        if (size(values) > 0) {
            set.addAll(values);
        }
        return set;
    }

    /**
     * @param <V> Type of mapped value
     * @return A {@link Supplier} that returns a <U>new</U> {@link SortedMap}
     * whenever its {@code get()} method is invoked
     */
    @SuppressWarnings("unchecked")
    public static <V> Supplier<SortedMap<String, V>> caseInsensitiveMap() {
        return CASE_INSENSITIVE_MAP_FACTORY;
    }

    public static <K, V> Map<V, K> flipMap(Map<? extends K, ? extends V> map, Supplier<? extends Map<V, K>> mapCreator, boolean allowDuplicates) {
        if (isEmpty(map)) {
            return Collections.emptyMap();
        }

        Map<V, K> result = Objects.requireNonNull(mapCreator.get(), "No map created");
        map.forEach((key, value) -> {
            K prev = result.put(value, key);
            if ((prev != null) && (!allowDuplicates)) {
                ValidateUtils.throwIllegalArgumentException("Multiple values for key=%s: current=%s, previous=%s", value, key, prev);
            }
        });

        return result;
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapValues(
            Function<? super V, ? extends K> keyMapper, Supplier<? extends Map<K, V>> mapCreator, V ... values) {
        return mapValues(keyMapper, mapCreator, isEmpty(values) ? Collections.emptyList() : Arrays.asList(values));
    }

    /**
     * Creates a map out of a group of values
     *
     * @param <K> The key type
     * @param <V> The value type
     * @param keyMapper The {@link Function} that generates a key for a given value.
     * If the returned key is {@code null} then the value is not mapped
     * @param mapCreator The {@link Supplier} used to create/retrieve the result map - provided
     * non-empty group of values
     * @param values The values to be mapped
     * @return The resulting {@link Map} - <B>Note:</B> no validation is made to ensure
     * that 2 (or more) values are not mapped to the same key
     */
    public static <K, V> Map<K, V> mapValues(
            Function<? super V, ? extends K> keyMapper, Supplier<? extends Map<K, V>> mapCreator, Collection<V> values) {
        if (isEmpty(values)) {
            return Collections.emptyMap();
        }

        Map<K, V> map = mapCreator.get();
        for (V v : values) {
            K k = keyMapper.apply(v);
            if (k == null) {
                continue;   // debug breakpoint
            }
            map.put(k, v);
        }

        return map;
    }

    /**
     * Returns a list of all the values that were accepted by a predicate
     *
     * @param <T> The type of value being evaluated
     * @param acceptor The {@link Predicate} to consult whether a member is selected
     * @param values The values to be scanned
     * @return A {@link List} of all the values that were accepted by the predicate
     */
    @SafeVarargs
    public static <T> List<T> selectMatchingMembers(Predicate<? super T> acceptor, T ... values) {
        return selectMatchingMembers(acceptor, isEmpty(values) ? Collections.emptyList() : Arrays.asList(values));
    }

    /**
     * Returns a list of all the values that were accepted by a predicate
     *
     * @param <T> The type of value being evaluated
     * @param acceptor The {@link Predicate} to consult whether a member is selected
     * @param values The values to be scanned
     * @return A {@link List} of all the values that were accepted by the predicate
     */
    public static <T> List<T> selectMatchingMembers(Predicate<? super T> acceptor, Collection<? extends T> values) {
        return GenericUtils.stream(values)
                .filter(acceptor::test)
                .collect(Collectors.toList());
    }

    /**
     * @param s The {@link CharSequence} to be checked
     * @return If the sequence contains any of the {@link #QUOTES}
     * on <U>both</U> ends, then they are stripped, otherwise
     * nothing is done
     * @see #stripDelimiters(CharSequence, char)
     */
    public static CharSequence stripQuotes(CharSequence s) {
        if (isEmpty(s)) {
            return s;
        }

        for (int index = 0; index < QUOTES.length(); index++) {
            char delim = QUOTES.charAt(index);
            CharSequence v = stripDelimiters(s, delim);
            if (v != s) {   // if stripped one don't continue
                return v;
            }
        }

        return s;
    }

    /**
     * @param s     The {@link CharSequence} to be checked
     * @param delim The expected delimiter
     * @return If the sequence contains the delimiter on <U>both</U> ends,
     * then it is are stripped, otherwise nothing is done
     */
    public static CharSequence stripDelimiters(CharSequence s, char delim) {
        if (isEmpty(s) || (s.length() < 2)) {
            return s;
        }

        int lastPos = s.length() - 1;
        if ((s.charAt(0) != delim) || (s.charAt(lastPos) != delim)) {
            return s;
        } else {
            return s.subSequence(1, lastPos);
        }
    }

    /**
     * Attempts to get to the &quot;effective&quot; exception being thrown,
     * by taking care of some known exceptions that wrap the original thrown
     * one.
     *
     * @param t The original {@link Throwable} - ignored if {@code null}
     * @return The effective exception - same as input if not a wrapper
     */
    public static Throwable peelException(Throwable t) {
        if (t == null) {
            return t;
        } else if (t instanceof UndeclaredThrowableException) {
            Throwable wrapped = ((UndeclaredThrowableException) t).getUndeclaredThrowable();
            // according to the Javadoc it may be null, in which case 'getCause'
            // might contain the information we need
            if (wrapped != null) {
                return peelException(wrapped);
            }

            wrapped = t.getCause();
            if (wrapped != t) {     // make sure it is a real cause
                return peelException(wrapped);
            }
        } else if (t instanceof InvocationTargetException) {
            Throwable target = ((InvocationTargetException) t).getTargetException();
            if (target != null) {
                return peelException(target);
            }
        } else if (t instanceof ReflectionException) {
            Throwable target = ((ReflectionException) t).getTargetException();
            if (target != null) {
                return peelException(target);
            }
        } else if (t instanceof MBeanException) {
            Throwable target = ((MBeanException) t).getTargetException();
            if (target != null) {
                return peelException(target);
            }
        }

        return t;   // no special handling required or available
    }
    /**
     * @param t The original {@link Throwable} - ignored if {@code null}
     * @return If {@link Throwable#getCause()} is non-{@code null} then
     * the cause, otherwise the original exception - {@code null} if
     * the original exception was {@code null}
     */
    public static Throwable resolveExceptionCause(Throwable t) {
        if (t == null) {
            return t;
        }

        Throwable c = t.getCause();
        if (c == null) {
            return t;
        } else {
            return c;
        }
    }

    /**
     * Used to &quot;accumulate&quot; exceptions of the <U>same type</U>. If the
     * current exception is {@code null} then the new one becomes the current,
     * otherwise the new one is added as a <U>suppressed</U> exception to the
     * current one
     *
     * @param <T>     The exception type
     * @param current The current exception
     * @param extra   The extra/new exception
     * @return The resolved exception
     * @see Throwable#addSuppressed(Throwable)
     */
    public static <T extends Throwable> T accumulateException(T current, T extra) {
        if (current == null) {
            return extra;
        }

        if ((extra == null) || (extra == current)) {
            return current;
        }

        current.addSuppressed(extra);
        return current;
    }

    /**
     * Wraps a value into a {@link Supplier}
     * @param <T> Type of value being supplied
     * @param value The value to be supplied
     * @return The supplier wrapper
     */
    public static <T> Supplier<T> supplierOf(T value) {
        return () -> value;
    }

    /**
     * Resolves to an always non-{@code null} iterator
     *
     * @param <T> Type of value being iterated
     * @param iterable The {@link Iterable} instance
     * @return A non-{@code null} iterator which may be empty if no iterable
     * instance or no iterator returned from it
     * @see #iteratorOf(Iterator)
     */
    public static <T> Iterator<T> iteratorOf(Iterable<T> iterable) {
        return iteratorOf((iterable == null) ? null : iterable.iterator());
    }

    /**
     * Resolves to an always non-{@code null} iterator
     *
     * @param <T> Type of value being iterated
     * @param iter The {@link Iterator} instance
     * @return  A non-{@code null} iterator which may be empty if no iterator instance
     * @see Collections#emptyIterator()
     */
    public static <T> Iterator<T> iteratorOf(Iterator<T> iter) {
        return (iter == null) ? Collections.emptyIterator() : iter;
    }

    public static <U, V> Iterable<V> wrapIterable(Iterable<? extends U> iter, Function<U, V> mapper) {
        return () -> wrapIterator(iteratorOf(iter), mapper);
    }

    public static <U, V> Iterator<V> wrapIterator(Iterator<? extends U> iter, Function<U, V> mapper) {
        Iterator<? extends U> iterator = iteratorOf(iter);
        return new Iterator<V>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }
            @Override
            public V next() {
                return mapper.apply(iterator.next());
            }
        };
    }

    /**
     * Wraps a group of {@link Supplier}s of {@link Iterable} instances into a &quot;unified&quot;
     * {@link Iterable} of their values, in the same order as the suppliers - i.e., once the values
     * from a specific supplier are exhausted, the next one is consulted, and so on, until all
     * suppliers have been consulted
     *
     * @param <T> Type of value being iterated
     * @param providers The providers - ignored if {@code null} (i.e., return an empty iterable instance)
     * @return The wrapping instance
     */
    public static <T> Iterable<T> multiIterableSuppliers(Iterable<? extends Supplier<? extends Iterable<? extends T>>> providers) {
        return (providers == null) ? Collections.emptyList() : () -> new Iterator<T>() {
            private final Iterator<? extends Supplier<? extends Iterable<? extends T>>> iter = iteratorOf(providers);
            private Iterator<? extends T> current = nextIterator();

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                if (current == null) {
                    throw new NoSuchElementException("No more elements");
                }

                T value = current.next();
                if (!current.hasNext()) {
                    current = nextIterator();
                }

                return value;
            }

            private Iterator<? extends T> nextIterator() {
                while (iter.hasNext()) {
                    Supplier<? extends Iterable<? extends T>> supplier = iter.next();
                    Iterator<? extends T> values = iteratorOf((supplier == null) ? null : supplier.get());
                    if (values.hasNext()) {
                        return values;
                    }
                }

                return null;
            }
        };
    }

    public static <K, V> MapBuilder<K, V> mapBuilder() {
        return new MapBuilder<>();
    }

    public static <K, V> MapBuilder<K, V> mapBuilder(Comparator<K> comparator) {
        return new MapBuilder<>(comparator);
    }

    public static class MapBuilder<K, V> {
        private Map<K, V> map;

        public MapBuilder() {
            this.map = new LinkedHashMap<>();
        }

        public MapBuilder(Comparator<K> comparator) {
            this.map = new TreeMap<>(comparator);
        }

        public MapBuilder<K, V> put(K k, V v) {
            map.put(k, v);
            return this;
        }

        public Map<K, V> build() {
            return map;
        }

        public Map<K, V> immutable() {
            return Collections.unmodifiableMap(map);
        }
    }
}

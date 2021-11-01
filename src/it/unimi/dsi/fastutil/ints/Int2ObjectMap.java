/*
	* Copyright (C) 2002-2020 Sebastiano Vigna
	*
	* Licensed under the Apache License, Version 2.0 (the "License");
	* you may not use this file except in compliance with the License.
	* You may obtain a copy of the License at
	*
	*     http://www.apache.org/licenses/LICENSE-2.0
	*
	* Unless required by applicable law or agreed to in writing, software
	* distributed under the License is distributed on an "AS IS" BASIS,
	* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	* See the License for the specific language governing permissions and
	* limitations under the License.
	*/
package it.unimi.dsi.fastutil.ints;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.function.Consumer;
import java.util.Map;
/**
 * A type-specific {@link Map}; provides some additional methods that use
 * polymorphism to avoid (un)boxing, and handling of a default return value.
 *
 * <p>
 * Besides extending the corresponding type-specific
 * {@linkplain it.unimi.dsi.fastutil.Function function}, this interface
 * strengthens {@link Map#entrySet()}, {@link #keySet()} and {@link #values()}.
 * Moreover, a number of methods, such as {@link #size()},
 * {@link #defaultReturnValue()}, etc., are un-defaulted as their function
 * default do not make sense for a map. Maps returning entry sets of type
 * {@link FastEntrySet} support also fast iteration.
 *
 * <p>
 * A submap or subset may or may not have an independent default return value
 * (which however must be initialized to the default return value of the
 * originator).
 *
 * @see Map
 */
public interface Int2ObjectMap<V> extends Int2ObjectFunction<V>, Map<Integer, V> {
	/**
	 * An entry set providing fast iteration.
	 *
	 * <p>
	 * In some cases (e.g., hash-based classes) iteration over an entry set requires
	 * the creation of a large number of {@link java.util.Map.Entry} objects. Some
	 * {@code fastutil} maps might return {@linkplain Map#entrySet() entry set}
	 * objects of type {@code FastEntrySet}: in this case, {@link #fastIterator()
	 * fastIterator()} will return an iterator that is guaranteed not to create a
	 * large number of objects, <em>possibly by returning always the same entry</em>
	 * (of course, mutated), and {@link #fastForEach(Consumer)} will apply the
	 * provided consumer to all elements of the entry set, <em>which might be
	 * represented always by the same entry</em> (of course, mutated).
	 */
	interface FastEntrySet<V> extends ObjectSet<Int2ObjectMap.Entry<V>> {
		/**
		 * Returns a fast iterator over this entry set; the iterator might return always
		 * the same entry instance, suitably mutated.
		 *
		 * @return a fast iterator over this entry set; the iterator might return always
		 *         the same {@link java.util.Map.Entry} instance, suitably mutated.
		 */
		ObjectIterator<Int2ObjectMap.Entry<V>> fastIterator();
		/**
		 * Iterates quickly over this entry set; the iteration might happen always on
		 * the same entry instance, suitably mutated.
		 *
		 * <p>
		 * This default implementation just delegates to {@link #forEach(Consumer)}.
		 *
		 * @param consumer
		 *            a consumer that will by applied to the entries of this set; the
		 *            entries might be represented by the same entry instance, suitably
		 *            mutated.
		 * @since 8.1.0
		 */
		default void fastForEach(final Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
			forEach(consumer);
		}
	}
	/**
	 * Returns the number of key/value mappings in this map. If the map contains
	 * more than {@link Integer#MAX_VALUE} elements, returns
	 * {@link Integer#MAX_VALUE}.
	 *
	 * @return the number of key-value mappings in this map.
	 * @see it.unimi.dsi.fastutil.Size64
	 */
	@Override
	int size();
	/**
	 * Removes all of the mappings from this map (optional operation). The map will
	 * be empty after this call returns.
	 *
	 * @throws UnsupportedOperationException
	 *             if the <code>clear()</code> operation is not supported by this
	 *             map
	 */
	@Override
	default void clear() {
		throw new UnsupportedOperationException();
	}
	/**
	 * Sets the default return value (optional operation).
	 *
	 * This value must be returned by type-specific versions of {@code get()},
	 * {@code put()} and {@code remove()} to denote that the map does not contain
	 * the specified key. It must be {@code null} by default.
	 *
	 * <p>
	 * <strong>Warning</strong>: Changing this to a non-null value can have
	 * unforeseen consequences. Especially, it breaks compatibility with the
	 * specifications of Java's {@link java.util.Map} interface. It has to be used
	 * with great care and thorough study of all method comments is recommended.
	 *
	 * @param rv
	 *            the new default return value.
	 * @see #defaultReturnValue()
	 */
	@Override
	void defaultReturnValue(V rv);
	/**
	 * Gets the default return value.
	 *
	 * @return the current default return value.
	 */
	@Override
	V defaultReturnValue();
	/**
	 * Returns a type-specific set view of the mappings contained in this map.
	 *
	 * <p>
	 * This method is necessary because there is no inheritance along type
	 * parameters: it is thus impossible to strengthen {@link Map#entrySet()} so
	 * that it returns an {@link it.unimi.dsi.fastutil.objects.ObjectSet} of
	 * type-specific entries (the latter makes it possible to access keys and values
	 * with type-specific methods).
	 *
	 * @return a type-specific set view of the mappings contained in this map.
	 * @see Map#entrySet()
	 */
	ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet();
	/**
	 * Returns a set view of the mappings contained in this map.
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link Map#entrySet()}.
	 *
	 * @return a set view of the mappings contained in this map.
	 * @see Map#entrySet()
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	@SuppressWarnings({"unchecked", "rawtypes"})
	default ObjectSet<Map.Entry<Integer, V>> entrySet() {
		return (ObjectSet) int2ObjectEntrySet();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding
	 * type-specific&ndash;{@linkplain it.unimi.dsi.fastutil.Function function}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V put(final Integer key, final V value) {
		return Int2ObjectFunction.super.put(key, value);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding
	 * type-specific&ndash;{@linkplain it.unimi.dsi.fastutil.Function function}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V get(final Object key) {
		return Int2ObjectFunction.super.get(key);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding
	 * type-specific&ndash;{@linkplain it.unimi.dsi.fastutil.Function function}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V remove(final Object key) {
		return Int2ObjectFunction.super.remove(key);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link Map#keySet()}.
	 * 
	 * @return a set view of the keys contained in this map.
	 * @see Map#keySet()
	 */
	@Override
	IntSet keySet();
	/**
	 * {@inheritDoc}
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link Map#values()}.
	 * 
	 * @return a set view of the values contained in this map.
	 * @see Map#values()
	 */
	@Override
	ObjectCollection<V> values();
	/**
	 * Returns true if this function contains a mapping for the specified key.
	 *
	 * @param key
	 *            the key.
	 * @return true if this function associates a value to {@code key}.
	 * @see Map#containsKey(Object)
	 */
	@Override
	boolean containsKey(int key);
	/**
	 * Returns true if this function contains a mapping for the specified key.
	 * <p>
	 * This default implementation just delegates to the corresponding
	 * type-specific&ndash;{@linkplain it.unimi.dsi.fastutil.Function function}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean containsKey(final Object key) {
		return Int2ObjectFunction.super.containsKey(key);
	}
	// Defaultable methods
	/**
	 * Returns the value to which the specified key is mapped, or the
	 * {@code defaultValue} if this map contains no mapping for the key.
	 *
	 * @param key
	 *            the key.
	 * @param defaultValue
	 *            the default mapping of the key.
	 *
	 * @return the value to which the specified key is mapped, or the
	 *         {@code defaultValue} if this map contains no mapping for the key.
	 *
	 * @see java.util.Map#getOrDefault(Object, Object)
	 * @since 8.0.0
	 */
	default V getOrDefault(final int key, final V defaultValue) {
		final V v;
		return ((v = get(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
	}
	/**
	 * If the specified key is not already associated with a value, associates it
	 * with the given value and returns the {@linkplain #defaultReturnValue()
	 * default return value}, else returns the current value.
	 *
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param value
	 *            value to be associated with the specified key.
	 *
	 * @return the previous value associated with the specified key, or the
	 *         {@linkplain #defaultReturnValue() default return value} if there was
	 *         no mapping for the key.
	 *
	 * @see java.util.Map#putIfAbsent(Object, Object)
	 * @since 8.0.0
	 */
	default V putIfAbsent(final int key, final V value) {
		final V v = get(key), drv = defaultReturnValue();
		if (v != drv || containsKey(key))
			return v;
		put(key, value);
		return drv;
	}
	/**
	 * Removes the entry for the specified key only if it is currently mapped to the
	 * specified value.
	 *
	 * @param key
	 *            key with which the specified value is associated.
	 * @param value
	 *            value expected to be associated with the specified key.
	 *
	 * @return {@code true} if the value was removed.
	 *
	 * @see java.util.Map#remove(Object, Object)
	 * @since 8.0.0
	 */
	default boolean remove(final int key, final Object value) {
		final V curValue = get(key);
		if (!java.util.Objects.equals(curValue, value) || (curValue == defaultReturnValue() && !containsKey(key)))
			return false;
		remove(key);
		return true;
	}
	/**
	 * Replaces the entry for the specified key only if currently mapped to the
	 * specified value.
	 *
	 * @param key
	 *            key with which the specified value is associated.
	 * @param oldValue
	 *            value expected to be associated with the specified key.
	 * @param newValue
	 *            value to be associated with the specified key.
	 *
	 * @return {@code true} if the value was replaced.
	 *
	 * @see java.util.Map#replace(Object, Object, Object)
	 * @since 8.0.0
	 */
	default boolean replace(final int key, final V oldValue, final V newValue) {
		final V curValue = get(key);
		if (!java.util.Objects.equals(curValue, oldValue) || (curValue == defaultReturnValue() && !containsKey(key)))
			return false;
		put(key, newValue);
		return true;
	}
	/**
	 * Replaces the entry for the specified key only if it is currently mapped to
	 * some value.
	 *
	 * @param key
	 *            key with which the specified value is associated.
	 * @param value
	 *            value to be associated with the specified key.
	 *
	 * @return the previous value associated with the specified key, or the
	 *         {@linkplain #defaultReturnValue() default return value} if there was
	 *         no mapping for the key.
	 *
	 * @see java.util.Map#replace(Object, Object)
	 * @since 8.0.0
	 */
	default V replace(final int key, final V value) {
		return containsKey(key) ? put(key, value) : defaultReturnValue();
	}
	/**
	 * If the specified key is not already associated with a value, attempts to
	 * compute its value using the given mapping function and enters it into this
	 * map.
	 *
	 * <p>
	 * Note that contrarily to the default
	 * {@linkplain java.util.Map#computeIfAbsent(Object, java.util.function.Function)
	 * computeIfAbsent()}, it is not possible to not add a value for a given key,
	 * since the {@code mappingFunction} cannot return {@code null}. If such a
	 * behavior is needed, please use the corresponding <em>nullable</em> version.
	 *
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param mappingFunction
	 *            the function to compute a value.
	 *
	 * @return the current (existing or computed) value associated with the
	 *         specified key.
	 *
	 * @see java.util.Map#computeIfAbsent(Object, java.util.function.Function)
	 * @since 8.0.0
	 */
	default V computeIfAbsent(final int key, final java.util.function.IntFunction<? extends V> mappingFunction) {
		java.util.Objects.requireNonNull(mappingFunction);
		final V v = get(key);
		if (v != defaultReturnValue() || containsKey(key))
			return v;
		V newValue = mappingFunction.apply(key);
		put(key, newValue);
		return newValue;
	}
	/**
	 * If the specified key is not already associated with a value, attempts to
	 * compute its value using the given mapping function and enters it into this
	 * map, unless the key is not present in the given mapping function.
	 *
	 * <p>
	 * This version of
	 * {@linkplain java.util.Map#computeIfAbsent(Object, java.util.function.Function)
	 * computeIfAbsent()} uses a type-specific version of <code>fastutil</code>'s
	 * {@link it.unimi.dsi.fastutil.Function Function}. Since
	 * {@link it.unimi.dsi.fastutil.Function Function} has a
	 * {@link it.unimi.dsi.fastutil.Function#containsKey(Object) containsKey()}
	 * method, it is possible to avoid adding a key by having {@code containsKey()}
	 * return {@code false} for that key.
	 *
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param mappingFunction
	 *            the function to compute a value.
	 *
	 * @return the current (existing or computed) value associated with the
	 *         specified key.
	 *
	 * @see java.util.Map#computeIfAbsent(Object, java.util.function.Function)
	 * @since 8.0.0
	 */
	default V computeIfAbsentPartial(final int key, final Int2ObjectFunction<? extends V> mappingFunction) {
		java.util.Objects.requireNonNull(mappingFunction);
		final V v = get(key), drv = defaultReturnValue();
		if (v != drv || containsKey(key))
			return v;
		if (!mappingFunction.containsKey(key))
			return drv;
		V newValue = mappingFunction.get(key);
		put(key, newValue);
		return newValue;
	}
	/**
	 * If the value for the specified key is present, attempts to compute a new
	 * mapping given the key and its current mapped value.
	 *
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param remappingFunction
	 *            the function to compute a value.
	 *
	 * @return the new value associated with the specified key, or the
	 *         {@linkplain #defaultReturnValue() default return value} if none.
	 *
	 * @see java.util.Map#computeIfPresent(Object, java.util.function.BiFunction)
	 * @since 8.0.0
	 */
	default V computeIfPresent(final int key,
			final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		java.util.Objects.requireNonNull(remappingFunction);
		final V oldValue = get(key), drv = defaultReturnValue();
		if (oldValue == drv && !containsKey(key))
			return drv;
		final V newValue = remappingFunction.apply(Integer.valueOf(key), (oldValue));
		if (newValue == null) {
			remove(key);
			return drv;
		}
		put(key, newValue);
		return newValue;
	}
	/**
	 * Attempts to compute a mapping for the specified key and its current mapped
	 * value (or {@code null} if there is no current mapping).
	 *
	 * <p>
	 * If the function returns {@code null}, the mapping is removed (or remains
	 * absent if initially absent). If the function itself throws an (unchecked)
	 * exception, the exception is rethrown, and the current mapping is left
	 * unchanged.
	 *
	 * @param key
	 *            key with which the specified value is to be associated.
	 * @param remappingFunction
	 *            the function to compute a value.
	 *
	 * @return the new value associated with the specified key, or the
	 *         {@linkplain #defaultReturnValue() default return value} if none.
	 *
	 * @see java.util.Map#compute(Object, java.util.function.BiFunction)
	 * @since 8.0.0
	 */
	default V compute(final int key,
			final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		java.util.Objects.requireNonNull(remappingFunction);
		final V oldValue = get(key), drv = defaultReturnValue();
		final boolean contained = oldValue != drv || containsKey(key);
		final V newValue = remappingFunction.apply(Integer.valueOf(key), contained ? (oldValue) : null);
		if (newValue == null) {
			if (contained)
				remove(key);
			return drv;
		}
		put(key, newValue);
		return newValue;
	}
	/**
	 * If the specified key is not already associated with a value or is associated
	 * with null, associates it with the given non-null {@code value}. Otherwise,
	 * replaces the associated value with the results of the given remapping
	 * function, or removes if the result is {@code null}.
	 *
	 * @param key
	 *            key with which the resulting value is to be associated.
	 * @param value
	 *            the non-{@code null} value to be merged with the existing value
	 *            associated with the key or, if no existing value is associated
	 *            with the key, to be associated with the key.
	 * @param remappingFunction
	 *            the function to recompute a value if present.
	 *
	 * @return the new value associated with the specified key, or the
	 *         {@linkplain #defaultReturnValue() default return value} if no value
	 *         is associated with the key.
	 *
	 * @see java.util.Map#merge(Object, Object, java.util.function.BiFunction)
	 * @since 8.0.0
	 */
	default V merge(final int key, final V value,
			final java.util.function.BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		java.util.Objects.requireNonNull(remappingFunction);
		java.util.Objects.requireNonNull(value);
		final V oldValue = get(key), drv = defaultReturnValue();
		final V newValue;
		if (oldValue != drv || containsKey(key)) {
			final V mergedValue = remappingFunction.apply((oldValue), (value));
			if (mergedValue == null) {
				remove(key);
				return drv;
			}
			newValue = (mergedValue);
		} else {
			newValue = value;
		}
		put(key, newValue);
		return newValue;
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding {@link Map}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V getOrDefault(final Object key, final V defaultValue) {
		return Map.super.getOrDefault(key, defaultValue);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding {@link Map}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V putIfAbsent(final Integer key, final V value) {
		return Map.super.putIfAbsent(key, value);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding {@link Map}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean remove(final Object key, final Object value) {
		return Map.super.remove(key, value);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding {@link Map}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean replace(final Integer key, final V oldValue, final V newValue) {
		return Map.super.replace(key, oldValue, newValue);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding {@link Map}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V replace(final Integer key, final V value) {
		return Map.super.replace(key, value);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding {@link Map}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V computeIfAbsent(final Integer key,
			final java.util.function.Function<? super Integer, ? extends V> mappingFunction) {
		return Map.super.computeIfAbsent(key, mappingFunction);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding {@link Map}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V computeIfPresent(final Integer key,
			final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		return Map.super.computeIfPresent(key, remappingFunction);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding {@link Map}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V compute(final Integer key,
			final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
		return Map.super.compute(key, remappingFunction);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation just delegates to the corresponding {@link Map}
	 * method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V merge(final Integer key, final V value,
			final java.util.function.BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
		return Map.super.merge(key, value, remappingFunction);
	}
	/**
	 * A type-specific {@link java.util.Map.Entry}; provides some additional methods
	 * that use polymorphism to avoid (un)boxing.
	 *
	 * @see java.util.Map.Entry
	 */
	interface Entry<V> extends Map.Entry<Integer, V> {
		/**
		 * Returns the key corresponding to this entry.
		 * 
		 * @see java.util.Map.Entry#getKey()
		 */
		int getIntKey();
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		default Integer getKey() {
			return Integer.valueOf(getIntKey());
		}
	}
}

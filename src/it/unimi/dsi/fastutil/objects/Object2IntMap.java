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
package it.unimi.dsi.fastutil.objects;
import it.unimi.dsi.fastutil.ints.IntCollection;
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
public interface Object2IntMap<K> extends Object2IntFunction<K>, Map<K, Integer> {
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
	interface FastEntrySet<K> extends ObjectSet<Object2IntMap.Entry<K>> {
		/**
		 * Returns a fast iterator over this entry set; the iterator might return always
		 * the same entry instance, suitably mutated.
		 *
		 * @return a fast iterator over this entry set; the iterator might return always
		 *         the same {@link java.util.Map.Entry} instance, suitably mutated.
		 */
		ObjectIterator<Object2IntMap.Entry<K>> fastIterator();
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
		default void fastForEach(final Consumer<? super Object2IntMap.Entry<K>> consumer) {
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
	 * the specified key. It must be 0/{@code false} by default.
	 *
	 * @param rv
	 *            the new default return value.
	 * @see #defaultReturnValue()
	 */
	@Override
	void defaultReturnValue(int rv);
	/**
	 * Gets the default return value.
	 *
	 * @return the current default return value.
	 */
	@Override
	int defaultReturnValue();
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
	ObjectSet<Object2IntMap.Entry<K>> object2IntEntrySet();
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
	default ObjectSet<Map.Entry<K, Integer>> entrySet() {
		return (ObjectSet) object2IntEntrySet();
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
	default Integer put(final K key, final Integer value) {
		return Object2IntFunction.super.put(key, value);
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
	default Integer get(final Object key) {
		return Object2IntFunction.super.get(key);
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
	default Integer remove(final Object key) {
		return Object2IntFunction.super.remove(key);
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
	ObjectSet<K> keySet();
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
	IntCollection values();
	/**
	 * Returns true if this function contains a mapping for the specified key.
	 *
	 * @param key
	 *            the key.
	 * @return true if this function associates a value to {@code key}.
	 * @see Map#containsKey(Object)
	 */
	@Override
	boolean containsKey(Object key);
	/**
	 * Returns {@code true} if this map maps one or more keys to the specified
	 * value.
	 * 
	 * @see Map#containsValue(Object)
	 */
	boolean containsValue(int value);
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean containsValue(final Object value) {
		return value == null ? false : containsValue(((Integer) (value)).intValue());
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
	default int getOrDefault(final Object key, final int defaultValue) {
		final int v;
		return ((v = getInt(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
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
	default int putIfAbsent(final K key, final int value) {
		final int v = getInt(key), drv = defaultReturnValue();
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
	default boolean remove(final Object key, final int value) {
		final int curValue = getInt(key);
		if (!((curValue) == (value)) || (curValue == defaultReturnValue() && !containsKey(key)))
			return false;
		removeInt(key);
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
	default boolean replace(final K key, final int oldValue, final int newValue) {
		final int curValue = getInt(key);
		if (!((curValue) == (oldValue)) || (curValue == defaultReturnValue() && !containsKey(key)))
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
	default int replace(final K key, final int value) {
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
	default int computeIntIfAbsent(final K key, final java.util.function.ToIntFunction<? super K> mappingFunction) {
		java.util.Objects.requireNonNull(mappingFunction);
		final int v = getInt(key);
		if (v != defaultReturnValue() || containsKey(key))
			return v;
		int newValue = mappingFunction.applyAsInt(key);
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
	default int computeIntIfAbsentPartial(final K key, final Object2IntFunction<? super K> mappingFunction) {
		java.util.Objects.requireNonNull(mappingFunction);
		final int v = getInt(key), drv = defaultReturnValue();
		if (v != drv || containsKey(key))
			return v;
		if (!mappingFunction.containsKey(key))
			return drv;
		int newValue = mappingFunction.getInt(key);
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
	default int computeIntIfPresent(final K key,
			final java.util.function.BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
		java.util.Objects.requireNonNull(remappingFunction);
		final int oldValue = getInt(key), drv = defaultReturnValue();
		if (oldValue == drv && !containsKey(key))
			return drv;
		final Integer newValue = remappingFunction.apply((key), Integer.valueOf(oldValue));
		if (newValue == null) {
			removeInt(key);
			return drv;
		}
		int newVal = (newValue).intValue();
		put(key, newVal);
		return newVal;
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
	default int computeInt(final K key,
			final java.util.function.BiFunction<? super K, ? super Integer, ? extends Integer> remappingFunction) {
		java.util.Objects.requireNonNull(remappingFunction);
		final int oldValue = getInt(key), drv = defaultReturnValue();
		final boolean contained = oldValue != drv || containsKey(key);
		final Integer newValue = remappingFunction.apply((key), contained ? Integer.valueOf(oldValue) : null);
		if (newValue == null) {
			if (contained)
				removeInt(key);
			return drv;
		}
		final int newVal = (newValue).intValue();
		put(key, newVal);
		return newVal;
	}
	/**
	 * If the specified key is not already associated with a value, associates it
	 * with the given {@code value}. Otherwise, replaces the associated value with
	 * the results of the given remapping function, or removes if the result is
	 * {@code null}.
	 *
	 * @param key
	 *            key with which the resulting value is to be associated.
	 * @param value
	 *            the value to be merged with the existing value associated with the
	 *            key or, if no existing value is associated with the key, to be
	 *            associated with the key.
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
	default int mergeInt(final K key, final int value,
			final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
		java.util.Objects.requireNonNull(remappingFunction);
		final int oldValue = getInt(key), drv = defaultReturnValue();
		final int newValue;
		if (oldValue != drv || containsKey(key)) {
			final Integer mergedValue = remappingFunction.apply(Integer.valueOf(oldValue), Integer.valueOf(value));
			if (mergedValue == null) {
				removeInt(key);
				return drv;
			}
			newValue = (mergedValue).intValue();
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
	default Integer getOrDefault(final Object key, final Integer defaultValue) {
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
	default Integer putIfAbsent(final K key, final Integer value) {
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
	default boolean replace(final K key, final Integer oldValue, final Integer newValue) {
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
	default Integer replace(final K key, final Integer value) {
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
	default Integer merge(final K key, final Integer value,
			final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
		return Map.super.merge(key, value, remappingFunction);
	}
	/**
	 * A type-specific {@link java.util.Map.Entry}; provides some additional methods
	 * that use polymorphism to avoid (un)boxing.
	 *
	 * @see java.util.Map.Entry
	 */
	interface Entry<K> extends Map.Entry<K, Integer> {
		/**
		 * Returns the value corresponding to this entry.
		 * 
		 * @see java.util.Map.Entry#getValue()
		 */
		int getIntValue();
		/**
		 * Replaces the value corresponding to this entry with the specified value
		 * (optional operation).
		 * 
		 * @see java.util.Map.Entry#setValue(Object)
		 */
		int setValue(final int value);
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		default Integer getValue() {
			return Integer.valueOf(getIntValue());
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		default Integer setValue(final Integer value) {
			return Integer.valueOf(setValue((value).intValue()));
		}
	}
}

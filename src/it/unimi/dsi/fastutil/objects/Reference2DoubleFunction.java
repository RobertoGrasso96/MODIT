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
import it.unimi.dsi.fastutil.Function;
/**
 * A type-specific {@link Function}; provides some additional methods that use
 * polymorphism to avoid (un)boxing.
 *
 * <p>
 * Type-specific versions of {@code get()}, {@code put()} and {@code remove()}
 * cannot rely on {@code null} to denote absence of a key. Rather, they return a
 * {@linkplain #defaultReturnValue() default return value}, which is set to
 * 0/false at creation, but can be changed using the
 * {@code defaultReturnValue()} method.
 *
 * <p>
 * For uniformity reasons, even functions returning objects implement the
 * default return value (of course, in this case the default return value is
 * initialized to {@code null}).
 *
 * <p>
 * The default implementation of optional operations just throw an
 * {@link UnsupportedOperationException}, except for the type-specific {@code
* containsKey()}, which return true. Generic versions of accessors delegate to
 * the corresponding type-specific counterparts following the interface rules.
 *
 * <p>
 * <strong>Warning:</strong> to fall in line as much as possible with the
 * {@linkplain java.util.Map standard map interface}, it is required that
 * standard versions of {@code get()}, {@code put()} and {@code remove()} for
 * maps with primitive-type keys or values <em>return {@code null} to denote
 * missing keys </em> rather than wrap the default return value in an object. In
 * case both keys and values are reference types, the default return value must
 * be returned instead, thus violating the {@linkplain java.util.Map standard
 * map interface} when the default return value is not {@code null}.
 *
 * @see Function
 */
@FunctionalInterface
public interface Reference2DoubleFunction<K> extends Function<K, Double>, java.util.function.ToDoubleFunction<K> {
	/**
	 * {@inheritDoc}
	 * 
	 * @since 8.0.0
	 */
	@Override
	default double applyAsDouble(K operand) {
		return getDouble(operand);
	}
	/**
	 * Adds a pair to the map (optional operation).
	 *
	 * @param key
	 *            the key.
	 * @param value
	 *            the value.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default
	 *         return value} if no value was present for the given key.
	 * @see Function#put(Object,Object)
	 */
	default double put(final K key, final double value) {
		throw new UnsupportedOperationException();
	}
	/**
	 * Returns the value to which the given key is mapped.
	 *
	 * @param key
	 *            the key.
	 * @return the corresponding value, or the {@linkplain #defaultReturnValue()
	 *         default return value} if no value was present for the given key.
	 * @see Function#get(Object)
	 */
	double getDouble(Object key);
	/**
	 * Removes the mapping with the given key (optional operation).
	 * 
	 * @param key
	 *            the key.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default
	 *         return value} if no value was present for the given key.
	 * @see Function#remove(Object)
	 */
	default double removeDouble(final Object key) {
		throw new UnsupportedOperationException();
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Double put(final K key, final Double value) {
		final K k = (key);
		final boolean containsKey = containsKey(k);
		final double v = put(k, (value).doubleValue());
		return containsKey ? Double.valueOf(v) : null;
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Double get(final Object key) {
		final Object k = (key);
		final double v = getDouble(k);
		return (v != defaultReturnValue() || containsKey(k)) ? Double.valueOf(v) : null;
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Double remove(final Object key) {
		final Object k = (key);
		return containsKey(k) ? Double.valueOf(removeDouble(k)) : null;
	}
	/**
	 * Sets the default return value (optional operation).
	 *
	 * This value must be returned by type-specific versions of {@code get()},
	 * {@code put()} and {@code remove()} to denote that the map does not contain
	 * the specified key. It must be 0/{@code false}/{@code null} by default.
	 *
	 * @param rv
	 *            the new default return value.
	 * @see #defaultReturnValue()
	 */
	default void defaultReturnValue(double rv) {
		throw new UnsupportedOperationException();
	}
	/**
	 * Gets the default return value.
	 *
	 * <p>
	 * This default implementation just return the default null value of the type
	 * ({@code null} for objects, 0 for scalars, false for Booleans).
	 *
	 * @return the current default return value.
	 */
	default double defaultReturnValue() {
		return (0);
	}
	default it.unimi.dsi.fastutil.objects.Reference2ByteFunction<K> andThen(
			it.unimi.dsi.fastutil.doubles.Double2ByteFunction after) {
		return k -> after.get(getDouble(k));
	}
	default it.unimi.dsi.fastutil.bytes.Byte2DoubleFunction compose(
			it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction<K> before) {
		return k -> getDouble(before.get(k));
	}
	default it.unimi.dsi.fastutil.objects.Reference2ShortFunction<K> andThen(
			it.unimi.dsi.fastutil.doubles.Double2ShortFunction after) {
		return k -> after.get(getDouble(k));
	}
	default it.unimi.dsi.fastutil.shorts.Short2DoubleFunction compose(
			it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction<K> before) {
		return k -> getDouble(before.get(k));
	}
	default it.unimi.dsi.fastutil.objects.Reference2IntFunction<K> andThen(
			it.unimi.dsi.fastutil.doubles.Double2IntFunction after) {
		return k -> after.get(getDouble(k));
	}
	default it.unimi.dsi.fastutil.ints.Int2DoubleFunction compose(
			it.unimi.dsi.fastutil.ints.Int2ReferenceFunction<K> before) {
		return k -> getDouble(before.get(k));
	}
	default it.unimi.dsi.fastutil.objects.Reference2LongFunction<K> andThen(
			it.unimi.dsi.fastutil.doubles.Double2LongFunction after) {
		return k -> after.get(getDouble(k));
	}
	default it.unimi.dsi.fastutil.longs.Long2DoubleFunction compose(
			it.unimi.dsi.fastutil.longs.Long2ReferenceFunction<K> before) {
		return k -> getDouble(before.get(k));
	}
	default it.unimi.dsi.fastutil.objects.Reference2CharFunction<K> andThen(
			it.unimi.dsi.fastutil.doubles.Double2CharFunction after) {
		return k -> after.get(getDouble(k));
	}
	default it.unimi.dsi.fastutil.chars.Char2DoubleFunction compose(
			it.unimi.dsi.fastutil.chars.Char2ReferenceFunction<K> before) {
		return k -> getDouble(before.get(k));
	}
	default it.unimi.dsi.fastutil.objects.Reference2FloatFunction<K> andThen(
			it.unimi.dsi.fastutil.doubles.Double2FloatFunction after) {
		return k -> after.get(getDouble(k));
	}
	default it.unimi.dsi.fastutil.floats.Float2DoubleFunction compose(
			it.unimi.dsi.fastutil.floats.Float2ReferenceFunction<K> before) {
		return k -> getDouble(before.get(k));
	}
	default it.unimi.dsi.fastutil.objects.Reference2DoubleFunction<K> andThen(
			it.unimi.dsi.fastutil.doubles.Double2DoubleFunction after) {
		return k -> after.get(getDouble(k));
	}
	default it.unimi.dsi.fastutil.doubles.Double2DoubleFunction compose(
			it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction<K> before) {
		return k -> getDouble(before.get(k));
	}
	default <T> it.unimi.dsi.fastutil.objects.Reference2ObjectFunction<K, T> andThen(
			it.unimi.dsi.fastutil.doubles.Double2ObjectFunction<T> after) {
		return k -> after.get(getDouble(k));
	}
	default <T> it.unimi.dsi.fastutil.objects.Object2DoubleFunction<T> compose(
			it.unimi.dsi.fastutil.objects.Object2ReferenceFunction<T, K> before) {
		return k -> getDouble(before.get(k));
	}
	default <T> it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction<K, T> andThen(
			it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction<T> after) {
		return k -> after.get(getDouble(k));
	}
	default <T> it.unimi.dsi.fastutil.objects.Reference2DoubleFunction<T> compose(
			it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction<T, K> before) {
		return k -> getDouble(before.get(k));
	}
}

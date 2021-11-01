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
package it.unimi.dsi.fastutil.shorts;
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
public interface Short2FloatFunction extends Function<Short, Float>, java.util.function.IntToDoubleFunction {
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * In this default implementation, the key gets narrowed down to the actual key
	 * type, throwing an exception if the given key can't be represented in the
	 * restricted domain. This is done for interoperability with the Java 8 function
	 * environment. Its use is discouraged, as unexpected errors can occur. Instead,
	 * the corresponding classes should be used (e.g.,
	 * {@link it.unimi.dsi.fastutil.ints.Int2IntFunction} instead of
	 * {@link it.unimi.dsi.fastutil.shorts.Short2IntFunction}).
	 *
	 * @throws IllegalArgumentException
	 *             If the given operand is not an element of the key domain.
	 * @since 8.0.0
	 * @deprecated Please use primitive types which don't have to be widened as
	 *             keys.
	 */
	@Deprecated
	@Override
	default double applyAsDouble(int operand) {
		return get(it.unimi.dsi.fastutil.SafeMath.safeIntToShort(operand));
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
	default float put(final short key, final float value) {
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
	float get(short key);
	/**
	 * Removes the mapping with the given key (optional operation).
	 * 
	 * @param key
	 *            the key.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default
	 *         return value} if no value was present for the given key.
	 * @see Function#remove(Object)
	 */
	default float remove(final short key) {
		throw new UnsupportedOperationException();
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Float put(final Short key, final Float value) {
		final short k = (key).shortValue();
		final boolean containsKey = containsKey(k);
		final float v = put(k, (value).floatValue());
		return containsKey ? Float.valueOf(v) : null;
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Float get(final Object key) {
		if (key == null)
			return null;
		final short k = ((Short) (key)).shortValue();
		final float v = get(k);
		return (v != defaultReturnValue() || containsKey(k)) ? Float.valueOf(v) : null;
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Float remove(final Object key) {
		if (key == null)
			return null;
		final short k = ((Short) (key)).shortValue();
		return containsKey(k) ? Float.valueOf(remove(k)) : null;
	}
	/**
	 * Returns true if this function contains a mapping for the specified key.
	 *
	 * <p>
	 * Note that for some kind of functions (e.g., hashes) this method will always
	 * return true. In particular, this default implementation always returns true.
	 *
	 * @param key
	 *            the key.
	 * @return true if this function associates a value to {@code key}.
	 * @see Function#containsKey(Object)
	 */
	default boolean containsKey(short key) {
		return true;
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean containsKey(final Object key) {
		return key == null ? false : containsKey(((Short) (key)).shortValue());
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
	default void defaultReturnValue(float rv) {
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
	default float defaultReturnValue() {
		return (0);
	}
	default it.unimi.dsi.fastutil.shorts.Short2ByteFunction andThen(
			it.unimi.dsi.fastutil.floats.Float2ByteFunction after) {
		return k -> after.get(get(k));
	}
	default it.unimi.dsi.fastutil.bytes.Byte2FloatFunction compose(
			it.unimi.dsi.fastutil.bytes.Byte2ShortFunction before) {
		return k -> get(before.get(k));
	}
	default it.unimi.dsi.fastutil.shorts.Short2ShortFunction andThen(
			it.unimi.dsi.fastutil.floats.Float2ShortFunction after) {
		return k -> after.get(get(k));
	}
	default it.unimi.dsi.fastutil.shorts.Short2FloatFunction compose(
			it.unimi.dsi.fastutil.shorts.Short2ShortFunction before) {
		return k -> get(before.get(k));
	}
	default it.unimi.dsi.fastutil.shorts.Short2IntFunction andThen(
			it.unimi.dsi.fastutil.floats.Float2IntFunction after) {
		return k -> after.get(get(k));
	}
	default it.unimi.dsi.fastutil.ints.Int2FloatFunction compose(it.unimi.dsi.fastutil.ints.Int2ShortFunction before) {
		return k -> get(before.get(k));
	}
	default it.unimi.dsi.fastutil.shorts.Short2LongFunction andThen(
			it.unimi.dsi.fastutil.floats.Float2LongFunction after) {
		return k -> after.get(get(k));
	}
	default it.unimi.dsi.fastutil.longs.Long2FloatFunction compose(
			it.unimi.dsi.fastutil.longs.Long2ShortFunction before) {
		return k -> get(before.get(k));
	}
	default it.unimi.dsi.fastutil.shorts.Short2CharFunction andThen(
			it.unimi.dsi.fastutil.floats.Float2CharFunction after) {
		return k -> after.get(get(k));
	}
	default it.unimi.dsi.fastutil.chars.Char2FloatFunction compose(
			it.unimi.dsi.fastutil.chars.Char2ShortFunction before) {
		return k -> get(before.get(k));
	}
	default it.unimi.dsi.fastutil.shorts.Short2FloatFunction andThen(
			it.unimi.dsi.fastutil.floats.Float2FloatFunction after) {
		return k -> after.get(get(k));
	}
	default it.unimi.dsi.fastutil.floats.Float2FloatFunction compose(
			it.unimi.dsi.fastutil.floats.Float2ShortFunction before) {
		return k -> get(before.get(k));
	}
	default it.unimi.dsi.fastutil.shorts.Short2DoubleFunction andThen(
			it.unimi.dsi.fastutil.floats.Float2DoubleFunction after) {
		return k -> after.get(get(k));
	}
	default it.unimi.dsi.fastutil.doubles.Double2FloatFunction compose(
			it.unimi.dsi.fastutil.doubles.Double2ShortFunction before) {
		return k -> get(before.get(k));
	}
	default <T> it.unimi.dsi.fastutil.shorts.Short2ObjectFunction<T> andThen(
			it.unimi.dsi.fastutil.floats.Float2ObjectFunction<T> after) {
		return k -> after.get(get(k));
	}
	default <T> it.unimi.dsi.fastutil.objects.Object2FloatFunction<T> compose(
			it.unimi.dsi.fastutil.objects.Object2ShortFunction<T> before) {
		return k -> get(before.getShort(k));
	}
	default <T> it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction<T> andThen(
			it.unimi.dsi.fastutil.floats.Float2ReferenceFunction<T> after) {
		return k -> after.get(get(k));
	}
	default <T> it.unimi.dsi.fastutil.objects.Reference2FloatFunction<T> compose(
			it.unimi.dsi.fastutil.objects.Reference2ShortFunction<T> before) {
		return k -> get(before.getShort(k));
	}
}

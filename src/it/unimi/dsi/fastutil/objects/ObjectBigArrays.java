/*
	* Copyright (C) 2009-2020 Sebastiano Vigna
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
	*
	*
	*
	* Copyright (C) 1999 CERN - European Organization for Nuclear Research.
	*
	*   Permission to use, copy, modify, distribute and sell this software and
	*   its documentation for any purpose is hereby granted without fee,
	*   provided that the above copyright notice appear in all copies and that
	*   both that copyright notice and this permission notice appear in
	*   supporting documentation. CERN makes no representations about the
	*   suitability of this software for any purpose. It is provided "as is"
	*   without expressed or implied warranty.
	*/
package it.unimi.dsi.fastutil.objects;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import static it.unimi.dsi.fastutil.BigArrays.ensureLength;
import static it.unimi.dsi.fastutil.BigArrays.start;
import static it.unimi.dsi.fastutil.BigArrays.segment;
import static it.unimi.dsi.fastutil.BigArrays.displacement;
import static it.unimi.dsi.fastutil.BigArrays.SEGMENT_MASK;
import static it.unimi.dsi.fastutil.BigArrays.SEGMENT_SHIFT;
import static it.unimi.dsi.fastutil.BigArrays.SEGMENT_SIZE;
import java.util.Comparator;
/**
 * A class providing static methods and objects that do useful things with
 * {@linkplain BigArrays big arrays}.
 *
 * <p>
 * Since version 8.4.0, many methods previously in this class(e.g.,
 * <code>length()</code>) have been deprecated in favor of those in
 * {@link BigArrays}, which can be imported statically.
 *
 * <p>
 * Note that {@link it.unimi.dsi.fastutil.io.BinIO} and
 * {@link it.unimi.dsi.fastutil.io.TextIO} contain several methods make it
 * possible to load and save big arrays of primitive types as sequences of
 * elements in {@link java.io.DataInput} format (i.e., not as objects) or as
 * sequences of lines of text.
 *
 * <p>
 * <strong>Warning:</strong> creating arrays using
 * {@linkplain java.lang.reflect.Array#newInstance(Class,int) reflection}, as it
 * happens in {@link #ensureCapacity(Object[][],long,long)} and
 * {@link #grow(Object[][],long,long)}, is <em>significantly slower</em> than
 * using {@code new}. This phenomenon is particularly evident in the first
 * growth phases of an array reallocated with doubling (or similar) logic.
 *
 * @see BigArrays
 */
public final class ObjectBigArrays {
	private ObjectBigArrays() {
	}
	/** A static, final, empty big array. */
	public static final Object[][] EMPTY_BIG_ARRAY = {};
	/**
	 * A static, final, empty big array to be used as default big array in
	 * allocations. An object distinct from {@link #EMPTY_BIG_ARRAY} makes it
	 * possible to have different behaviors depending on whether the user required
	 * an empty allocation, or we are just lazily delaying allocation.
	 *
	 * @see java.util.ArrayList
	 */
	public static final Object[][] DEFAULT_EMPTY_BIG_ARRAY = {};
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array
	 *            a big array.
	 * @param index
	 *            a position in the big array.
	 * @return the element of the big array at the specified position.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> K get(final K[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}
	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array
	 *            a big array.
	 * @param index
	 *            a position in the big array.
	 * @param value
	 *            the new value for the array element at the specified position.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void set(final K[][] array, final long index, K value) {
		array[segment(index)][displacement(index)] = value;
	}
	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array
	 *            a big array.
	 * @param first
	 *            a position in the big array.
	 * @param second
	 *            a position in the big array.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void swap(final K[][] array, final long first, final long second) {
		final K t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}
	/**
	 * Returns the length of the given big array.
	 *
	 * @param array
	 *            a big array.
	 * @return the length of the given big array.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> long length(final K[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}
	/**
	 * Copies a big array from the specified source big array, beginning at the
	 * specified position, to the specified position of the destination big array.
	 * Handles correctly overlapping regions of the same big array.
	 *
	 * @param srcArray
	 *            the source big array.
	 * @param srcPos
	 *            the starting position in the source big array.
	 * @param destArray
	 *            the destination big array.
	 * @param destPos
	 *            the starting position in the destination data.
	 * @param length
	 *            the number of elements to be copied.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void copy(final K[][] srcArray, final long srcPos, final K[][] destArray, final long destPos,
			long length) {
		BigArrays.copy(srcArray, srcPos, destArray, destPos, length);
	}
	/**
	 * Copies a big array from the specified source big array, beginning at the
	 * specified position, to the specified position of the destination array.
	 *
	 * @param srcArray
	 *            the source big array.
	 * @param srcPos
	 *            the starting position in the source big array.
	 * @param destArray
	 *            the destination array.
	 * @param destPos
	 *            the starting position in the destination data.
	 * @param length
	 *            the number of elements to be copied.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void copyFromBig(final K[][] srcArray, final long srcPos, final K[] destArray, int destPos,
			int length) {
		BigArrays.copyFromBig(srcArray, srcPos, destArray, destPos, length);
	}
	/**
	 * Copies an array from the specified source array, beginning at the specified
	 * position, to the specified position of the destination big array.
	 *
	 * @param srcArray
	 *            the source array.
	 * @param srcPos
	 *            the starting position in the source array.
	 * @param destArray
	 *            the destination big array.
	 * @param destPos
	 *            the starting position in the destination data.
	 * @param length
	 *            the number of elements to be copied.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void copyToBig(final K[] srcArray, int srcPos, final K[][] destArray, final long destPos,
			long length) {
		BigArrays.copyToBig(srcArray, srcPos, destArray, destPos, length);
	}
	/**
	 * Creates a new big array using the given one as prototype.
	 *
	 * <p>
	 * This method returns a new big array of the given length whose element are of
	 * the same class as of those of {@code prototype}. In case of an empty big
	 * array, it tries to return {@link #EMPTY_BIG_ARRAY}, if possible.
	 *
	 * @param prototype
	 *            a big array that will be used to type the new one.
	 * @param length
	 *            the length of the new big array.
	 * @return a new big array of given type and length.
	 */
	@SuppressWarnings("unchecked")
	public static <K> K[][] newBigArray(final K[][] prototype, final long length) {
		return (K[][]) newBigArray(prototype.getClass().getComponentType(), length);
	}
	/**
	 * Creates a new big array using a given component type.
	 *
	 * <p>
	 * This method returns a new big array whose segments are of class
	 * {@code componentType}. In case of an empty big array, it tries to return
	 * {@link #EMPTY_BIG_ARRAY}, if possible.
	 *
	 * @param componentType
	 *            a class representing the type of segments of the array to be
	 *            created.
	 * @param length
	 *            the length of the new big array.
	 * @return a new big array of given type and length.
	 */
	public static Object[][] newBigArray(Class<?> componentType, final long length) {
		if (length == 0 && componentType == Object[].class)
			return EMPTY_BIG_ARRAY;
		ensureLength(length);
		final int baseLength = (int) ((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		Object[][] base = (Object[][]) java.lang.reflect.Array.newInstance(componentType, baseLength);
		final int residual = (int) (length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = 0; i < baseLength - 1; i++)
				base[i] = (Object[]) java.lang.reflect.Array.newInstance(componentType.getComponentType(),
						SEGMENT_SIZE);
			base[baseLength - 1] = (Object[]) java.lang.reflect.Array.newInstance(componentType.getComponentType(),
					residual);
		} else
			for (int i = 0; i < baseLength; i++)
				base[i] = (Object[]) java.lang.reflect.Array.newInstance(componentType.getComponentType(),
						SEGMENT_SIZE);
		return base;
	}
	/**
	 * Creates a new big array.
	 *
	 * @param length
	 *            the length of the new big array.
	 * @return a new big array of given length.
	 */
	public static Object[][] newBigArray(final long length) {
		if (length == 0)
			return EMPTY_BIG_ARRAY;
		ensureLength(length);
		final int baseLength = (int) ((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		Object[][] base = new Object[baseLength][];
		final int residual = (int) (length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = 0; i < baseLength - 1; i++)
				base[i] = new Object[SEGMENT_SIZE];
			base[baseLength - 1] = new Object[residual];
		} else
			for (int i = 0; i < baseLength; i++)
				base[i] = new Object[SEGMENT_SIZE];
		return base;
	}
	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original
	 * array.
	 *
	 * @param array
	 *            an array.
	 * @return a new big array with the same length and content of {@code array}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <K> K[][] wrap(final K[] array) {
		return BigArrays.wrap(array);
	}
	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged,
	 * you should probably use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments
	 * of the original array, which must be considered read-only after calling this
	 * method.
	 *
	 * @param array
	 *            a big array.
	 * @param length
	 *            the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more;
	 *         otherwise, a big array with {@code length} entries whose first
	 *         {@code length(array)} entries are the same as those of {@code array}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> K[][] ensureCapacity(final K[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}
	/**
	 * Forces a big array to contain the given number of entries, preserving just a
	 * part of the big array.
	 *
	 * <p>
	 * This method returns a new big array of the given length whose element are of
	 * the same class as of those of {@code array}.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments
	 * of the original array, which must be considered read-only after calling this
	 * method.
	 *
	 * @param array
	 *            a big array.
	 * @param length
	 *            the new minimum length for this big array.
	 * @param preserve
	 *            the number of elements of the big array that must be preserved in
	 *            case a new allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <K> K[][] forceCapacity(final K[][] array, final long length, final long preserve) {
		return BigArrays.forceCapacity(array, length, preserve);
	}
	/**
	 * Ensures that a big array can contain the given number of entries, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * This method returns a new big array of the given length whose element are of
	 * the same class as of those of {@code array}.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments
	 * of the original array, which must be considered read-only after calling this
	 * method.
	 *
	 * @param array
	 *            a big array.
	 * @param length
	 *            the new minimum length for this big array.
	 * @param preserve
	 *            the number of elements of the big array that must be preserved in
	 *            case a new allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more;
	 *         otherwise, a big array with {@code length} entries whose first
	 *         {@code preserve} entries are the same as those of {@code array}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	@SuppressWarnings("unchecked")
	public static <K> K[][] ensureCapacity(final K[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}
	/**
	 * Grows the given big array to the maximum between the given length and the
	 * current length increased by 50%, provided that the given length is larger
	 * than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments
	 * of the original array, which must be considered read-only after calling this
	 * method.
	 *
	 * @param array
	 *            a big array.
	 * @param length
	 *            the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a
	 *         big array with max({@code length},{@code length(array)}/&phi;)
	 *         entries whose first {@code length(array)} entries are the same as
	 *         those of {@code array}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> K[][] grow(final K[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}
	/**
	 * Grows the given big array to the maximum between the given length and the
	 * current length increased by 50%, provided that the given length is larger
	 * than the current length, preserving just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments
	 * of the original array, which must be considered read-only after calling this
	 * method.
	 *
	 * @param array
	 *            a big array.
	 * @param length
	 *            the new minimum length for this big array.
	 * @param preserve
	 *            the number of elements of the big array that must be preserved in
	 *            case a new allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a
	 *         big array with max({@code length},{@code length(array)}/&phi;)
	 *         entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> K[][] grow(final K[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength
				? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve)
				: array;
	}
	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments
	 * of the original array, which must be considered read-only after calling this
	 * method.
	 *
	 * @param array
	 *            a big array.
	 * @param length
	 *            the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less;
	 *         otherwise, a big array with {@code length} entries whose entries are
	 *         the same as the first {@code length} entries of {@code array}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> K[][] trim(final K[][] array, final long length) {
		return BigArrays.trim(array, length);
	}
	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments
	 * of the original array, which must be considered read-only after calling this
	 * method.
	 *
	 * @param array
	 *            a big array.
	 * @param length
	 *            the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries;
	 *         otherwise, if it contains <em>more</em> than {@code length} entries,
	 *         a big array with {@code length} entries whose entries are the same as
	 *         the first {@code length} entries of {@code array}; otherwise, a big
	 *         array with {@code length} entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> K[][] setLength(final K[][] array, final long length) {
		return BigArrays.setLength(array, length);
	}
	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array
	 *            a big array.
	 * @param offset
	 *            the first element to copy.
	 * @param length
	 *            the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array}
	 *         starting at {@code offset}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> K[][] copy(final K[][] array, final long offset, final long length) {
		return BigArrays.copy(array, offset, length);
	}
	/**
	 * Returns a copy of a big array.
	 *
	 * @param array
	 *            a big array.
	 * @return a copy of {@code array}.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> K[][] copy(final K[][] array) {
		return BigArrays.copy(array);
	}
	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the
	 * corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array
	 *            a big array.
	 * @param value
	 *            the new value for all elements of the big array.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void fill(final K[][] array, final K value) {
		for (int i = array.length; i-- != 0;)
			Arrays.fill(array[i], value);
	}
	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In
	 * this case, it is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array
	 *            a big array.
	 * @param from
	 *            the starting index of the portion to fill.
	 * @param to
	 *            the end index of the portion to fill.
	 * @param value
	 *            the new value for all elements of the specified portion of the big
	 *            array.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void fill(final K[][] array, final long from, long to, final K value) {
		BigArrays.fill(array, from, to, value);
	}
	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the
	 * corresponding method in {@link java.util.Arrays}.
	 *
	 * @param a1
	 *            a big array.
	 * @param a2
	 *            another big array.
	 * @return true if the two big arrays are of the same length, and their elements
	 *         are equal.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> boolean equals(final K[][] a1, final K a2[][]) {
		return BigArrays.equals(a1, a2);
	}
	/*
	 * Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements,
	 * enclosed in square brackets ("[]"). Adjacent elements are separated by the
	 * characters ", " (a comma followed by a space). Returns "null" if {@code a} is
	 * null.
	 * 
	 * @param a the big array whose string representation to return.
	 * 
	 * @return the string representation of {@code a}.
	 * 
	 * @deprecated Please use the version in {@link
	 * it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> String toString(final K[][] a) {
		return BigArrays.toString(a);
	}
	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive)
	 * elements fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a
	 *            a big array.
	 * @param from
	 *            a start index (inclusive).
	 * @param to
	 *            an end index (inclusive).
	 * @throws IllegalArgumentException
	 *             if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if {@code from} or {@code to} are greater than the big array
	 *             length or negative.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void ensureFromTo(final K[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}
	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a
	 *            a big array.
	 * @param offset
	 *            a start index.
	 * @param length
	 *            a length (the number of elements in the range).
	 * @throws IllegalArgumentException
	 *             if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if {@code offset} is negative or {@code offset}+{@code length} is
	 *             greater than the big array length.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void ensureOffsetLength(final K[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}
	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a
	 *            a big array.
	 * @param b
	 *            another big array.
	 * @throws IllegalArgumentException
	 *             if the two argument arrays are not of the same length.
	 * @deprecated Please use the version in
	 *             {@link it.unimi.dsi.fastutil.BigArrays}.
	 */
	@Deprecated
	public static <K> void ensureSameLength(final K[][] a, final K[][] b) {
		if (length(a) != length(b))
			throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}
	/** A type-specific content-based hash strategy for big arrays. */
	private static final class BigArrayHashStrategy<K> implements Hash.Strategy<K[][]>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		@Override
		public int hashCode(final K[][] o) {
			return java.util.Arrays.deepHashCode(o);
		}
		@Override
		public boolean equals(final K[][] a, final K[][] b) {
			return ObjectBigArrays.equals(a, b);
		}
	}
	/**
	 * A type-specific content-based hash strategy for big arrays.
	 *
	 * <p>
	 * This hash strategy may be used in custom hash collections whenever keys are
	 * big arrays, and they must be considered equal by content. This strategy will
	 * handle {@code null} correctly, and it is serializable.
	 */
	@SuppressWarnings({"rawtypes"})
	public static final Hash.Strategy HASH_STRATEGY = new BigArrayHashStrategy();
	private static final int QUICKSORT_NO_REC = 7;
	private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
	private static final int MEDIUM = 40;
	private static <K> void swap(final K[][] x, long a, long b, final long n) {
		for (int i = 0; i < n; i++, a++, b++)
			BigArrays.swap(x, a, b);
	}
	private static <K> long med3(final K x[][], final long a, final long b, final long c, Comparator<K> comp) {
		int ab = comp.compare(BigArrays.get(x, a), BigArrays.get(x, b));
		int ac = comp.compare(BigArrays.get(x, a), BigArrays.get(x, c));
		int bc = comp.compare(BigArrays.get(x, b), BigArrays.get(x, c));
		return (ab < 0 ? (bc < 0 ? b : ac < 0 ? c : a) : (bc > 0 ? b : ac > 0 ? c : a));
	}
	private static <K> void selectionSort(final K[][] a, final long from, final long to, final Comparator<K> comp) {
		for (long i = from; i < to - 1; i++) {
			long m = i;
			for (long j = i + 1; j < to; j++)
				if (comp.compare(BigArrays.get(a, j), BigArrays.get(a, m)) < 0)
					m = j;
			if (m != i)
				BigArrays.swap(a, i, m);
		}
	}
	/**
	 * Sorts the specified range of elements according to the order induced by the
	 * specified comparator using quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * @param x
	 *            the big array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 */
	public static <K> void quickSort(final K[][] x, final long from, final long to, final Comparator<K> comp) {
		final long len = to - from;
		// Selection sort on smallest arrays
		if (len < QUICKSORT_NO_REC) {
			selectionSort(x, from, to, comp);
			return;
		}
		// Choose a partition element, v
		long m = from + len / 2; // Small arrays, middle element
		if (len > QUICKSORT_NO_REC) {
			long l = from;
			long n = to - 1;
			if (len > MEDIUM) { // Big arrays, pseudomedian of 9
				long s = len / 8;
				l = med3(x, l, l + s, l + 2 * s, comp);
				m = med3(x, m - s, m, m + s, comp);
				n = med3(x, n - 2 * s, n - s, n, comp);
			}
			m = med3(x, l, m, n, comp); // Mid-size, med of 3
		}
		final K v = BigArrays.get(x, m);
		// Establish Invariant: v* (<v)* (>v)* v*
		long a = from, b = a, c = to - 1, d = c;
		while (true) {
			int comparison;
			while (b <= c && (comparison = comp.compare(BigArrays.get(x, b), v)) <= 0) {
				if (comparison == 0)
					BigArrays.swap(x, a++, b);
				b++;
			}
			while (c >= b && (comparison = comp.compare(BigArrays.get(x, c), v)) >= 0) {
				if (comparison == 0)
					BigArrays.swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			BigArrays.swap(x, b++, c--);
		}
		// Swap partition elements back to middle
		long s, n = to;
		s = Math.min(a - from, b - a);
		swap(x, from, b - s, s);
		s = Math.min(d - c, n - d - 1);
		swap(x, b, n - s, s);
		// Recursively sort non-partition-elements
		if ((s = b - a) > 1)
			quickSort(x, from, from + s, comp);
		if ((s = d - c) > 1)
			quickSort(x, n - s, n, comp);
	}
	@SuppressWarnings("unchecked")
	private static <K> long med3(final K x[][], final long a, final long b, final long c) {
		int ab = (((Comparable<K>) (BigArrays.get(x, a))).compareTo(BigArrays.get(x, b)));
		int ac = (((Comparable<K>) (BigArrays.get(x, a))).compareTo(BigArrays.get(x, c)));
		int bc = (((Comparable<K>) (BigArrays.get(x, b))).compareTo(BigArrays.get(x, c)));
		return (ab < 0 ? (bc < 0 ? b : ac < 0 ? c : a) : (bc > 0 ? b : ac > 0 ? c : a));
	}
	@SuppressWarnings("unchecked")
	private static <K> void selectionSort(final K[][] a, final long from, final long to) {
		for (long i = from; i < to - 1; i++) {
			long m = i;
			for (long j = i + 1; j < to; j++)
				if ((((Comparable<K>) (BigArrays.get(a, j))).compareTo(BigArrays.get(a, m)) < 0))
					m = j;
			if (m != i)
				BigArrays.swap(a, i, m);
		}
	}
	/**
	 * Sorts the specified big array according to the order induced by the specified
	 * comparator using quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * @param x
	 *            the big array to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 *
	 */
	public static <K> void quickSort(final K[][] x, final Comparator<K> comp) {
		quickSort(x, 0, BigArrays.length(x), comp);
	}
	/**
	 * Sorts the specified range of elements according to the natural ascending
	 * order using quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * @param x
	 *            the big array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 */
	@SuppressWarnings("unchecked")
	public static <K> void quickSort(final K[][] x, final long from, final long to) {
		final long len = to - from;
		// Selection sort on smallest arrays
		if (len < QUICKSORT_NO_REC) {
			selectionSort(x, from, to);
			return;
		}
		// Choose a partition element, v
		long m = from + len / 2; // Small arrays, middle element
		if (len > QUICKSORT_NO_REC) {
			long l = from;
			long n = to - 1;
			if (len > MEDIUM) { // Big arrays, pseudomedian of 9
				long s = len / 8;
				l = med3(x, l, l + s, l + 2 * s);
				m = med3(x, m - s, m, m + s);
				n = med3(x, n - 2 * s, n - s, n);
			}
			m = med3(x, l, m, n); // Mid-size, med of 3
		}
		final K v = BigArrays.get(x, m);
		// Establish Invariant: v* (<v)* (>v)* v*
		long a = from, b = a, c = to - 1, d = c;
		while (true) {
			int comparison;
			while (b <= c && (comparison = (((Comparable<K>) (BigArrays.get(x, b))).compareTo(v))) <= 0) {
				if (comparison == 0)
					BigArrays.swap(x, a++, b);
				b++;
			}
			while (c >= b && (comparison = (((Comparable<K>) (BigArrays.get(x, c))).compareTo(v))) >= 0) {
				if (comparison == 0)
					BigArrays.swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			BigArrays.swap(x, b++, c--);
		}
		// Swap partition elements back to middle
		long s, n = to;
		s = Math.min(a - from, b - a);
		swap(x, from, b - s, s);
		s = Math.min(d - c, n - d - 1);
		swap(x, b, n - s, s);
		// Recursively sort non-partition-elements
		if ((s = b - a) > 1)
			quickSort(x, from, from + s);
		if ((s = d - c) > 1)
			quickSort(x, n - s, n);
	}
	/**
	 * Sorts the specified big array according to the natural ascending order using
	 * quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * @param x
	 *            the big array to be sorted.
	 */
	public static <K> void quickSort(final K[][] x) {
		quickSort(x, 0, BigArrays.length(x));
	}
	protected static class ForkJoinQuickSort<K> extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final long from;
		private final long to;
		private final K[][] x;
		public ForkJoinQuickSort(final K[][] x, final long from, final long to) {
			this.from = from;
			this.to = to;
			this.x = x;
		}
		@Override
		@SuppressWarnings("unchecked")
		protected void compute() {
			final K[][] x = this.x;
			final long len = to - from;
			if (len < PARALLEL_QUICKSORT_NO_FORK) {
				quickSort(x, from, to);
				return;
			}
			// Choose a partition element, v
			long m = from + len / 2;
			long l = from;
			long n = to - 1;
			long s = len / 8;
			l = med3(x, l, l + s, l + 2 * s);
			m = med3(x, m - s, m, m + s);
			n = med3(x, n - 2 * s, n - s, n);
			m = med3(x, l, m, n);
			final K v = BigArrays.get(x, m);
			// Establish Invariant: v* (<v)* (>v)* v*
			long a = from, b = a, c = to - 1, d = c;
			while (true) {
				int comparison;
				while (b <= c && (comparison = (((Comparable<K>) (BigArrays.get(x, b))).compareTo(v))) <= 0) {
					if (comparison == 0)
						BigArrays.swap(x, a++, b);
					b++;
				}
				while (c >= b && (comparison = (((Comparable<K>) (BigArrays.get(x, c))).compareTo(v))) >= 0) {
					if (comparison == 0)
						BigArrays.swap(x, c, d--);
					c--;
				}
				if (b > c)
					break;
				BigArrays.swap(x, b++, c--);
			}
			// Swap partition elements back to middle
			long t;
			s = Math.min(a - from, b - a);
			swap(x, from, b - s, s);
			s = Math.min(d - c, to - d - 1);
			swap(x, b, to - s, s);
			// Recursively sort non-partition-elements
			s = b - a;
			t = d - c;
			if (s > 1 && t > 1)
				invokeAll(new ForkJoinQuickSort<>(x, from, from + s), new ForkJoinQuickSort<>(x, to - t, to));
			else if (s > 1)
				invokeAll(new ForkJoinQuickSort<>(x, from, from + s));
			else
				invokeAll(new ForkJoinQuickSort<>(x, to - t, to));
		}
	}
	/**
	 * Sorts the specified range of elements according to the natural ascending
	 * order using a parallel quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This implementation uses the {@link ForkJoinPool#commonPool() common pool}.
	 *
	 * @param x
	 *            the big array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 */
	public static <K> void parallelQuickSort(final K[][] x, final long from, final long to) {
		if (to - from < PARALLEL_QUICKSORT_NO_FORK || ForkJoinPool.getCommonPoolParallelism() == 1)
			quickSort(x, from, to);
		else {
			final ForkJoinPool pool = ForkJoinPool.commonPool();;
			pool.invoke(new ForkJoinQuickSort<>(x, from, to));
			pool.shutdown();
		}
	}
	/**
	 * Sorts a big array according to the natural ascending order using a parallel
	 * quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This implementation uses the {@link ForkJoinPool#commonPool() common pool}.
	 *
	 * @param x
	 *            the big array to be sorted.
	 */
	public static <K> void parallelQuickSort(final K[][] x) {
		parallelQuickSort(x, 0, BigArrays.length(x));
	}
	protected static class ForkJoinQuickSortComp<K> extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final long from;
		private final long to;
		private final K[][] x;
		private final Comparator<K> comp;
		public ForkJoinQuickSortComp(final K[][] x, final long from, final long to, final Comparator<K> comp) {
			this.from = from;
			this.to = to;
			this.x = x;
			this.comp = comp;
		}
		@Override
		protected void compute() {
			final K[][] x = this.x;
			final long len = to - from;
			if (len < PARALLEL_QUICKSORT_NO_FORK) {
				quickSort(x, from, to, comp);
				return;
			}
			// Choose a partition element, v
			long m = from + len / 2;
			long l = from;
			long n = to - 1;
			long s = len / 8;
			l = med3(x, l, l + s, l + 2 * s, comp);
			m = med3(x, m - s, m, m + s, comp);
			n = med3(x, n - 2 * s, n - s, n, comp);
			m = med3(x, l, m, n, comp);
			final K v = BigArrays.get(x, m);
			// Establish Invariant: v* (<v)* (>v)* v*
			long a = from, b = a, c = to - 1, d = c;
			while (true) {
				int comparison;
				while (b <= c && (comparison = comp.compare(BigArrays.get(x, b), v)) <= 0) {
					if (comparison == 0)
						BigArrays.swap(x, a++, b);
					b++;
				}
				while (c >= b && (comparison = comp.compare(BigArrays.get(x, c), v)) >= 0) {
					if (comparison == 0)
						BigArrays.swap(x, c, d--);
					c--;
				}
				if (b > c)
					break;
				BigArrays.swap(x, b++, c--);
			}
			// Swap partition elements back to middle
			long t;
			s = Math.min(a - from, b - a);
			swap(x, from, b - s, s);
			s = Math.min(d - c, to - d - 1);
			swap(x, b, to - s, s);
			// Recursively sort non-partition-elements
			s = b - a;
			t = d - c;
			if (s > 1 && t > 1)
				invokeAll(new ForkJoinQuickSortComp<>(x, from, from + s, comp),
						new ForkJoinQuickSortComp<>(x, to - t, to, comp));
			else if (s > 1)
				invokeAll(new ForkJoinQuickSortComp<>(x, from, from + s, comp));
			else
				invokeAll(new ForkJoinQuickSortComp<>(x, to - t, to, comp));
		}
	}
	/**
	 * Sorts the specified range of elements according to the order induced by the
	 * specified comparator using a parallel quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This implementation uses the {@link ForkJoinPool#commonPool() common pool}.
	 *
	 * @param x
	 *            the big array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 */
	public static <K> void parallelQuickSort(final K[][] x, final long from, final long to, final Comparator<K> comp) {
		if (to - from < PARALLEL_QUICKSORT_NO_FORK || ForkJoinPool.getCommonPoolParallelism() == 1)
			quickSort(x, from, to, comp);
		else {
			final ForkJoinPool pool = ForkJoinPool.commonPool();;
			pool.invoke(new ForkJoinQuickSortComp<>(x, from, to, comp));
			pool.shutdown();
		}
	}
	/**
	 * Sorts a big array according to the order induced by the specified comparator
	 * using a parallel quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This implementation uses the {@link ForkJoinPool#commonPool() common pool}.
	 *
	 * @param x
	 *            the big array to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 */
	public static <K> void parallelQuickSort(final K[][] x, final Comparator<K> comp) {
		parallelQuickSort(x, 0, BigArrays.length(x), comp);
	}
	/**
	 * Searches a range of the specified big array for the specified value using the
	 * binary search algorithm. The range must be sorted prior to making this call.
	 * If it is not sorted, the results are undefined. If the range contains
	 * multiple elements with the specified value, there is no guarantee which one
	 * will be found.
	 *
	 * @param a
	 *            the big array to be searched.
	 * @param from
	 *            the index of the first element (inclusive) to be searched.
	 * @param to
	 *            the index of the last element (exclusive) to be searched.
	 * @param key
	 *            the value to be searched for.
	 * @return index of the search key, if it is contained in the big array;
	 *         otherwise, <code>(-(<i>insertion point</i>) - 1)</code>. The
	 *         <i>insertion point</i> is defined as the the point at which the value
	 *         would be inserted into the big array: the index of the first element
	 *         greater than the key, or the length of the big array, if all elements
	 *         in the big array are less than the specified key. Note that this
	 *         guarantees that the return value will be &gt;= 0 if and only if the
	 *         key is found.
	 * @see java.util.Arrays
	 */
	@SuppressWarnings("unchecked")
	public static <K> long binarySearch(final K[][] a, long from, long to, final K key) {
		K midVal;
		to--;
		while (from <= to) {
			final long mid = (from + to) >>> 1;
			midVal = BigArrays.get(a, mid);
			final int cmp = ((Comparable<? super K>) midVal).compareTo(key);
			if (cmp < 0)
				from = mid + 1;
			else if (cmp > 0)
				to = mid - 1;
			else
				return mid;
		}
		return -(from + 1);
	}
	/**
	 * Searches a big array for the specified value using the binary search
	 * algorithm. The range must be sorted prior to making this call. If it is not
	 * sorted, the results are undefined. If the range contains multiple elements
	 * with the specified value, there is no guarantee which one will be found.
	 *
	 * @param a
	 *            the big array to be searched.
	 * @param key
	 *            the value to be searched for.
	 * @return index of the search key, if it is contained in the big array;
	 *         otherwise, <code>(-(<i>insertion point</i>) - 1)</code>. The
	 *         <i>insertion point</i> is defined as the the point at which the value
	 *         would be inserted into the big array: the index of the first element
	 *         greater than the key, or the length of the big array, if all elements
	 *         in the big array are less than the specified key. Note that this
	 *         guarantees that the return value will be &gt;= 0 if and only if the
	 *         key is found.
	 * @see java.util.Arrays
	 */
	public static <K> long binarySearch(final K[][] a, final Object key) {
		return binarySearch(a, 0, BigArrays.length(a), key);
	}
	/**
	 * Searches a range of the specified big array for the specified value using the
	 * binary search algorithm and a specified comparator. The range must be sorted
	 * following the comparator prior to making this call. If it is not sorted, the
	 * results are undefined. If the range contains multiple elements with the
	 * specified value, there is no guarantee which one will be found.
	 *
	 * @param a
	 *            the big array to be searched.
	 * @param from
	 *            the index of the first element (inclusive) to be searched.
	 * @param to
	 *            the index of the last element (exclusive) to be searched.
	 * @param key
	 *            the value to be searched for.
	 * @param c
	 *            a comparator.
	 * @return index of the search key, if it is contained in the big array;
	 *         otherwise, <code>(-(<i>insertion point</i>) - 1)</code>. The
	 *         <i>insertion point</i> is defined as the the point at which the value
	 *         would be inserted into the big array: the index of the first element
	 *         greater than the key, or the length of the big array, if all elements
	 *         in the big array are less than the specified key. Note that this
	 *         guarantees that the return value will be &gt;= 0 if and only if the
	 *         key is found.
	 * @see java.util.Arrays
	 */
	public static <K> long binarySearch(final K[][] a, long from, long to, final K key, final Comparator<K> c) {
		K midVal;
		to--;
		while (from <= to) {
			final long mid = (from + to) >>> 1;
			midVal = BigArrays.get(a, mid);
			final int cmp = c.compare(midVal, key);
			if (cmp < 0)
				from = mid + 1;
			else if (cmp > 0)
				to = mid - 1;
			else
				return mid; // key found
		}
		return -(from + 1);
	}
	/**
	 * Searches a big array for the specified value using the binary search
	 * algorithm and a specified comparator. The range must be sorted following the
	 * comparator prior to making this call. If it is not sorted, the results are
	 * undefined. If the range contains multiple elements with the specified value,
	 * there is no guarantee which one will be found.
	 *
	 * @param a
	 *            the big array to be searched.
	 * @param key
	 *            the value to be searched for.
	 * @param c
	 *            a comparator.
	 * @return index of the search key, if it is contained in the big array;
	 *         otherwise, <code>(-(<i>insertion point</i>) - 1)</code>. The
	 *         <i>insertion point</i> is defined as the the point at which the value
	 *         would be inserted into the big array: the index of the first element
	 *         greater than the key, or the length of the big array, if all elements
	 *         in the big array are less than the specified key. Note that this
	 *         guarantees that the return value will be &gt;= 0 if and only if the
	 *         key is found.
	 * @see java.util.Arrays
	 */
	public static <K> long binarySearch(final K[][] a, final K key, final Comparator<K> c) {
		return binarySearch(a, 0, BigArrays.length(a), key, c);
	}
	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom
	 * number generator.
	 *
	 * @param a
	 *            the big array to be shuffled.
	 * @param from
	 *            the index of the first element (inclusive) to be shuffled.
	 * @param to
	 *            the index of the last element (exclusive) to be shuffled.
	 * @param random
	 *            a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static <K> K[][] shuffle(final K[][] a, final long from, final long to, final Random random) {
		return BigArrays.shuffle(a, from, to, random);
	}
	/**
	 * Shuffles the specified big array using the specified pseudorandom number
	 * generator.
	 *
	 * @param a
	 *            the big array to be shuffled.
	 * @param random
	 *            a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static <K> K[][] shuffle(final K[][] a, final Random random) {
		return BigArrays.shuffle(a, random);
	}
}

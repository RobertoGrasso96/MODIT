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
	*
	*
	*
	* For the sorting and binary search code:
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
package it.unimi.dsi.fastutil.booleans;
import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Hash;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import it.unimi.dsi.fastutil.ints.IntArrays;
/**
 * A class providing static methods and objects that do useful things with
 * type-specific arrays.
 *
 * <p>
 * In particular, the {@code forceCapacity()}, {@code ensureCapacity()},
 * {@code grow()}, {@code trim()} and {@code setLength()} methods allow to
 * handle arrays much like array lists. This can be very useful when efficiency
 * (or syntactic simplicity) reasons make array lists unsuitable.
 *
 * <p>
 * Note that {@link it.unimi.dsi.fastutil.io.BinIO} and
 * {@link it.unimi.dsi.fastutil.io.TextIO} contain several methods make it
 * possible to load and save arrays of primitive types as sequences of elements
 * in {@link java.io.DataInput} format (i.e., not as objects) or as sequences of
 * lines of text.
 *
 * <h2>Sorting</h2>
 *
 * <p>
 * There are several sorting methods available. The main theme is that of
 * letting you choose the sorting algorithm you prefer (i.e., trading stability
 * of mergesort for no memory allocation in quicksort). Some algorithms provide
 * a parallel version that will use the {@linkplain ForkJoinPool#commonPool()
 * common pool}. Some algorithms also provide an explicit <em>indirect</em>
 * sorting facility, which makes it possible to sort an array using the values
 * in another array as comparator.
 *
 * <p>
 * However, if you wish to let the implementation choose an algorithm for you,
 * both {@link #stableSort} and {@link #unstableSort} methods are available,
 * which dynamically chooses an algorithm based on unspecified criteria (but
 * most likely stability, array size, and array element type).
 *
 * <p>
 * All comparison-based algorithm have an implementation based on a
 * type-specific comparator.
 *
 * <p>
 * As a general rule, sequential radix sort is significantly faster than
 * quicksort or mergesort, in particular on random-looking data. In the parallel
 * case, up to a few cores parallel radix sort is still the fastest, but at some
 * point quicksort exploits parallelism better.
 *
 * <p>
 * If you are fine with not knowing exactly which algorithm will be run (in
 * particular, not knowing exactly whether a support array will be allocated),
 * the dual-pivot parallel sorts in {@link java.util.Arrays} are about 50%
 * faster than the classical single-pivot implementation used here.
 *
 * <p>
 * In any case, if sorting time is important I suggest that you benchmark your
 * sorting load with your data distribution and on your architecture.
 *
 * @see java.util.Arrays
 */
public final class BooleanArrays {
	private BooleanArrays() {
	}
	/** A static, final, empty array. */
	public static final boolean[] EMPTY_ARRAY = {};
	/**
	 * A static, final, empty array to be used as default array in allocations. An
	 * object distinct from {@link #EMPTY_ARRAY} makes it possible to have different
	 * behaviors depending on whether the user required an empty allocation, or we
	 * are just lazily delaying allocation.
	 *
	 * @see java.util.ArrayList
	 */
	public static final boolean[] DEFAULT_EMPTY_ARRAY = {};
	/**
	 * Forces an array to contain the given number of entries, preserving just a
	 * part of the array.
	 *
	 * @param array
	 *            an array.
	 * @param length
	 *            the new minimum length for this array.
	 * @param preserve
	 *            the number of elements of the array that must be preserved in case
	 *            a new allocation is necessary.
	 * @return an array with {@code length} entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static boolean[] forceCapacity(final boolean[] array, final int length, final int preserve) {
		final boolean t[] = new boolean[length];
		System.arraycopy(array, 0, t, 0, preserve);
		return t;
	}
	/**
	 * Ensures that an array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this array will need again to be enlarged, you
	 * should probably use {@code grow()} instead.
	 *
	 * @param array
	 *            an array.
	 * @param length
	 *            the new minimum length for this array.
	 * @return {@code array}, if it contains {@code length} entries or more;
	 *         otherwise, an array with {@code length} entries whose first
	 *         {@code array.length} entries are the same as those of {@code array}.
	 */
	public static boolean[] ensureCapacity(final boolean[] array, final int length) {
		return ensureCapacity(array, length, array.length);
	}
	/**
	 * Ensures that an array can contain the given number of entries, preserving
	 * just a part of the array.
	 *
	 * @param array
	 *            an array.
	 * @param length
	 *            the new minimum length for this array.
	 * @param preserve
	 *            the number of elements of the array that must be preserved in case
	 *            a new allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more;
	 *         otherwise, an array with {@code length} entries whose first
	 *         {@code preserve} entries are the same as those of {@code array}.
	 */
	public static boolean[] ensureCapacity(final boolean[] array, final int length, final int preserve) {
		return length > array.length ? forceCapacity(array, length, preserve) : array;
	}
	/**
	 * Grows the given array to the maximum between the given length and the current
	 * length increased by 50%, provided that the given length is larger than the
	 * current length.
	 *
	 * <p>
	 * If you want complete control on the array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * @param array
	 *            an array.
	 * @param length
	 *            the new minimum length for this array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise,
	 *         an array with max({@code length},{@code array.length}/&phi;) entries
	 *         whose first {@code array.length} entries are the same as those of
	 *         {@code array}.
	 */
	public static boolean[] grow(final boolean[] array, final int length) {
		return grow(array, length, array.length);
	}
	/**
	 * Grows the given array to the maximum between the given length and the current
	 * length increased by 50%, provided that the given length is larger than the
	 * current length, preserving just a part of the array.
	 *
	 * <p>
	 * If you want complete control on the array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * @param array
	 *            an array.
	 * @param length
	 *            the new minimum length for this array.
	 * @param preserve
	 *            the number of elements of the array that must be preserved in case
	 *            a new allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise,
	 *         an array with max({@code length},{@code array.length}/&phi;) entries
	 *         whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static boolean[] grow(final boolean[] array, final int length, final int preserve) {
		if (length > array.length) {
			final int newLength = (int) Math
					.max(Math.min((long) array.length + (array.length >> 1), Arrays.MAX_ARRAY_SIZE), length);
			final boolean t[] = new boolean[newLength];
			System.arraycopy(array, 0, t, 0, preserve);
			return t;
		}
		return array;
	}
	/**
	 * Trims the given array to the given length.
	 *
	 * @param array
	 *            an array.
	 * @param length
	 *            the new maximum length for the array.
	 * @return {@code array}, if it contains {@code length} entries or less;
	 *         otherwise, an array with {@code length} entries whose entries are the
	 *         same as the first {@code length} entries of {@code array}.
	 *
	 */
	public static boolean[] trim(final boolean[] array, final int length) {
		if (length >= array.length)
			return array;
		final boolean t[] = length == 0 ? EMPTY_ARRAY : new boolean[length];
		System.arraycopy(array, 0, t, 0, length);
		return t;
	}
	/**
	 * Sets the length of the given array.
	 *
	 * @param array
	 *            an array.
	 * @param length
	 *            the new length for the array.
	 * @return {@code array}, if it contains exactly {@code length} entries;
	 *         otherwise, if it contains <em>more</em> than {@code length} entries,
	 *         an array with {@code length} entries whose entries are the same as
	 *         the first {@code length} entries of {@code array}; otherwise, an
	 *         array with {@code length} entries whose first {@code array.length}
	 *         entries are the same as those of {@code array}.
	 *
	 */
	public static boolean[] setLength(final boolean[] array, final int length) {
		if (length == array.length)
			return array;
		if (length < array.length)
			return trim(array, length);
		return ensureCapacity(array, length);
	}
	/**
	 * Returns a copy of a portion of an array.
	 *
	 * @param array
	 *            an array.
	 * @param offset
	 *            the first element to copy.
	 * @param length
	 *            the number of elements to copy.
	 * @return a new array containing {@code length} elements of {@code array}
	 *         starting at {@code offset}.
	 */
	public static boolean[] copy(final boolean[] array, final int offset, final int length) {
		ensureOffsetLength(array, offset, length);
		final boolean[] a = length == 0 ? EMPTY_ARRAY : new boolean[length];
		System.arraycopy(array, offset, a, 0, length);
		return a;
	}
	/**
	 * Returns a copy of an array.
	 *
	 * @param array
	 *            an array.
	 * @return a copy of {@code array}.
	 */
	public static boolean[] copy(final boolean[] array) {
		return array.clone();
	}
	/**
	 * Fills the given array with the given value.
	 *
	 * @param array
	 *            an array.
	 * @param value
	 *            the new value for all elements of the array.
	 * @deprecated Please use the corresponding {@link java.util.Arrays} method.
	 */
	@Deprecated
	public static void fill(final boolean[] array, final boolean value) {
		int i = array.length;
		while (i-- != 0)
			array[i] = value;
	}
	/**
	 * Fills a portion of the given array with the given value.
	 *
	 * @param array
	 *            an array.
	 * @param from
	 *            the starting index of the portion to fill (inclusive).
	 * @param to
	 *            the end index of the portion to fill (exclusive).
	 * @param value
	 *            the new value for all elements of the specified portion of the
	 *            array.
	 * @deprecated Please use the corresponding {@link java.util.Arrays} method.
	 */
	@Deprecated
	public static void fill(final boolean[] array, final int from, int to, final boolean value) {
		ensureFromTo(array, from, to);
		if (from == 0)
			while (to-- != 0)
				array[to] = value;
		else
			for (int i = from; i < to; i++)
				array[i] = value;
	}
	/**
	 * Returns true if the two arrays are elementwise equal.
	 *
	 * @param a1
	 *            an array.
	 * @param a2
	 *            another array.
	 * @return true if the two arrays are of the same length, and their elements are
	 *         equal.
	 * @deprecated Please use the corresponding {@link java.util.Arrays} method,
	 *             which is intrinsified in recent JVMs.
	 */
	@Deprecated
	public static boolean equals(final boolean[] a1, final boolean a2[]) {
		int i = a1.length;
		if (i != a2.length)
			return false;
		while (i-- != 0)
			if (!((a1[i]) == (a2[i])))
				return false;
		return true;
	}
	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive)
	 * elements fits an array.
	 *
	 * <p>
	 * This method may be used whenever an array range check is needed.
	 *
	 * @param a
	 *            an array.
	 * @param from
	 *            a start index (inclusive).
	 * @param to
	 *            an end index (exclusive).
	 * @throws IllegalArgumentException
	 *             if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if {@code from} or {@code to} are greater than the array length
	 *             or negative.
	 */
	public static void ensureFromTo(final boolean[] a, final int from, final int to) {
		Arrays.ensureFromTo(a.length, from, to);
	}
	/**
	 * Ensures that a range given by an offset and a length fits an array.
	 *
	 * <p>
	 * This method may be used whenever an array range check is needed.
	 *
	 * @param a
	 *            an array.
	 * @param offset
	 *            a start index.
	 * @param length
	 *            a length (the number of elements in the range).
	 * @throws IllegalArgumentException
	 *             if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException
	 *             if {@code offset} is negative or {@code offset}+{@code length} is
	 *             greater than the array length.
	 */
	public static void ensureOffsetLength(final boolean[] a, final int offset, final int length) {
		Arrays.ensureOffsetLength(a.length, offset, length);
	}
	/**
	 * Ensures that two arrays are of the same length.
	 *
	 * @param a
	 *            an array.
	 * @param b
	 *            another array.
	 * @throws IllegalArgumentException
	 *             if the two argument arrays are not of the same length.
	 */
	public static void ensureSameLength(final boolean[] a, final boolean[] b) {
		if (a.length != b.length)
			throw new IllegalArgumentException("Array size mismatch: " + a.length + " != " + b.length);
	}
	private static final int QUICKSORT_NO_REC = 16;
	private static final int PARALLEL_QUICKSORT_NO_FORK = 8192;
	private static final int QUICKSORT_MEDIAN_OF_9 = 128;
	private static final int MERGESORT_NO_REC = 16;
	/**
	 * Swaps two elements of an anrray.
	 *
	 * @param x
	 *            an array.
	 * @param a
	 *            a position in {@code x}.
	 * @param b
	 *            another position in {@code x}.
	 */
	public static void swap(final boolean x[], final int a, final int b) {
		final boolean t = x[a];
		x[a] = x[b];
		x[b] = t;
	}
	/**
	 * Swaps two sequences of elements of an array.
	 *
	 * @param x
	 *            an array.
	 * @param a
	 *            a position in {@code x}.
	 * @param b
	 *            another position in {@code x}.
	 * @param n
	 *            the number of elements to exchange starting at {@code a} and
	 *            {@code b}.
	 */
	public static void swap(final boolean[] x, int a, int b, final int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, a, b);
	}
	private static int med3(final boolean x[], final int a, final int b, final int c, BooleanComparator comp) {
		final int ab = comp.compare(x[a], x[b]);
		final int ac = comp.compare(x[a], x[c]);
		final int bc = comp.compare(x[b], x[c]);
		return (ab < 0 ? (bc < 0 ? b : ac < 0 ? c : a) : (bc > 0 ? b : ac > 0 ? c : a));
	}
	private static void selectionSort(final boolean[] a, final int from, final int to, final BooleanComparator comp) {
		for (int i = from; i < to - 1; i++) {
			int m = i;
			for (int j = i + 1; j < to; j++)
				if (comp.compare(a[j], a[m]) < 0)
					m = j;
			if (m != i) {
				final boolean u = a[i];
				a[i] = a[m];
				a[m] = u;
			}
		}
	}
	private static void insertionSort(final boolean[] a, final int from, final int to, final BooleanComparator comp) {
		for (int i = from; ++i < to;) {
			boolean t = a[i];
			int j = i;
			for (boolean u = a[j - 1]; comp.compare(t, u) < 0; u = a[--j - 1]) {
				a[j] = u;
				if (from == j - 1) {
					--j;
					break;
				}
			}
			a[j] = t;
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
	 * <p>
	 * Note that this implementation does not allocate any object, contrarily to the
	 * implementation used to sort primitive types in {@link java.util.Arrays},
	 * which switches to mergesort on large inputs.
	 *
	 * @param x
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 *
	 */
	public static void quickSort(final boolean[] x, final int from, final int to, final BooleanComparator comp) {
		final int len = to - from;
		// Selection sort on smallest arrays
		if (len < QUICKSORT_NO_REC) {
			selectionSort(x, from, to, comp);
			return;
		}
		// Choose a partition element, v
		int m = from + len / 2;
		int l = from;
		int n = to - 1;
		if (len > QUICKSORT_MEDIAN_OF_9) { // Big arrays, pseudomedian of 9
			int s = len / 8;
			l = med3(x, l, l + s, l + 2 * s, comp);
			m = med3(x, m - s, m, m + s, comp);
			n = med3(x, n - 2 * s, n - s, n, comp);
		}
		m = med3(x, l, m, n, comp); // Mid-size, med of 3
		final boolean v = x[m];
		// Establish Invariant: v* (<v)* (>v)* v*
		int a = from, b = a, c = to - 1, d = c;
		while (true) {
			int comparison;
			while (b <= c && (comparison = comp.compare(x[b], v)) <= 0) {
				if (comparison == 0)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && (comparison = comp.compare(x[c], v)) >= 0) {
				if (comparison == 0)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}
		// Swap partition elements back to middle
		int s;
		s = Math.min(a - from, b - a);
		swap(x, from, b - s, s);
		s = Math.min(d - c, to - d - 1);
		swap(x, b, to - s, s);
		// Recursively sort non-partition-elements
		if ((s = b - a) > 1)
			quickSort(x, from, from + s, comp);
		if ((s = d - c) > 1)
			quickSort(x, to - s, to, comp);
	}
	/**
	 * Sorts an array according to the order induced by the specified comparator
	 * using quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * Note that this implementation does not allocate any object, contrarily to the
	 * implementation used to sort primitive types in {@link java.util.Arrays},
	 * which switches to mergesort on large inputs.
	 *
	 * @param x
	 *            the array to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 *
	 */
	public static void quickSort(final boolean[] x, final BooleanComparator comp) {
		quickSort(x, 0, x.length, comp);
	}
	protected static class ForkJoinQuickSortComp extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final int from;
		private final int to;
		private final boolean[] x;
		private final BooleanComparator comp;
		public ForkJoinQuickSortComp(final boolean[] x, final int from, final int to, final BooleanComparator comp) {
			this.from = from;
			this.to = to;
			this.x = x;
			this.comp = comp;
		}
		@Override
		protected void compute() {
			final boolean[] x = this.x;
			final int len = to - from;
			if (len < PARALLEL_QUICKSORT_NO_FORK) {
				quickSort(x, from, to, comp);
				return;
			}
			// Choose a partition element, v
			int m = from + len / 2;
			int l = from;
			int n = to - 1;
			int s = len / 8;
			l = med3(x, l, l + s, l + 2 * s, comp);
			m = med3(x, m - s, m, m + s, comp);
			n = med3(x, n - 2 * s, n - s, n, comp);
			m = med3(x, l, m, n, comp);
			final boolean v = x[m];
			// Establish Invariant: v* (<v)* (>v)* v*
			int a = from, b = a, c = to - 1, d = c;
			while (true) {
				int comparison;
				while (b <= c && (comparison = comp.compare(x[b], v)) <= 0) {
					if (comparison == 0)
						swap(x, a++, b);
					b++;
				}
				while (c >= b && (comparison = comp.compare(x[c], v)) >= 0) {
					if (comparison == 0)
						swap(x, c, d--);
					c--;
				}
				if (b > c)
					break;
				swap(x, b++, c--);
			}
			// Swap partition elements back to middle
			int t;
			s = Math.min(a - from, b - a);
			swap(x, from, b - s, s);
			s = Math.min(d - c, to - d - 1);
			swap(x, b, to - s, s);
			// Recursively sort non-partition-elements
			s = b - a;
			t = d - c;
			if (s > 1 && t > 1)
				invokeAll(new ForkJoinQuickSortComp(x, from, from + s, comp),
						new ForkJoinQuickSortComp(x, to - t, to, comp));
			else if (s > 1)
				invokeAll(new ForkJoinQuickSortComp(x, from, from + s, comp));
			else
				invokeAll(new ForkJoinQuickSortComp(x, to - t, to, comp));
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
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 */
	public static void parallelQuickSort(final boolean[] x, final int from, final int to,
			final BooleanComparator comp) {
		if (to - from < PARALLEL_QUICKSORT_NO_FORK || ForkJoinPool.getCommonPoolParallelism() == 1)
			quickSort(x, from, to, comp);
		else {
			final ForkJoinPool pool = ForkJoinPool.commonPool();
			pool.invoke(new ForkJoinQuickSortComp(x, from, to, comp));
			pool.shutdown();
		}
	}
	/**
	 * Sorts an array according to the order induced by the specified comparator
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
	 *            the array to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 */
	public static void parallelQuickSort(final boolean[] x, final BooleanComparator comp) {
		parallelQuickSort(x, 0, x.length, comp);
	}

	private static int med3(final boolean x[], final int a, final int b, final int c) {
		final int ab = (Boolean.compare((x[a]), (x[b])));
		final int ac = (Boolean.compare((x[a]), (x[c])));
		final int bc = (Boolean.compare((x[b]), (x[c])));
		return (ab < 0 ? (bc < 0 ? b : ac < 0 ? c : a) : (bc > 0 ? b : ac > 0 ? c : a));
	}

	private static void selectionSort(final boolean[] a, final int from, final int to) {
		for (int i = from; i < to - 1; i++) {
			int m = i;
			for (int j = i + 1; j < to; j++)
				if ((!(a[j]) && (a[m])))
					m = j;
			if (m != i) {
				final boolean u = a[i];
				a[i] = a[m];
				a[m] = u;
			}
		}
	}

	private static void insertionSort(final boolean[] a, final int from, final int to) {
		for (int i = from; ++i < to;) {
			boolean t = a[i];
			int j = i;
			for (boolean u = a[j - 1]; (!(t) && (u)); u = a[--j - 1]) {
				a[j] = u;
				if (from == j - 1) {
					--j;
					break;
				}
			}
			a[j] = t;
		}
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
	 * <p>
	 * Note that this implementation does not allocate any object, contrarily to the
	 * implementation used to sort primitive types in {@link java.util.Arrays},
	 * which switches to mergesort on large inputs.
	 *
	 * @param x
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 */

	public static void quickSort(final boolean[] x, final int from, final int to) {
		final int len = to - from;
		// Selection sort on smallest arrays
		if (len < QUICKSORT_NO_REC) {
			selectionSort(x, from, to);
			return;
		}
		// Choose a partition element, v
		int m = from + len / 2;
		int l = from;
		int n = to - 1;
		if (len > QUICKSORT_MEDIAN_OF_9) { // Big arrays, pseudomedian of 9
			int s = len / 8;
			l = med3(x, l, l + s, l + 2 * s);
			m = med3(x, m - s, m, m + s);
			n = med3(x, n - 2 * s, n - s, n);
		}
		m = med3(x, l, m, n); // Mid-size, med of 3
		final boolean v = x[m];
		// Establish Invariant: v* (<v)* (>v)* v*
		int a = from, b = a, c = to - 1, d = c;
		while (true) {
			int comparison;
			while (b <= c && (comparison = (Boolean.compare((x[b]), (v)))) <= 0) {
				if (comparison == 0)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && (comparison = (Boolean.compare((x[c]), (v)))) >= 0) {
				if (comparison == 0)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}
		// Swap partition elements back to middle
		int s;
		s = Math.min(a - from, b - a);
		swap(x, from, b - s, s);
		s = Math.min(d - c, to - d - 1);
		swap(x, b, to - s, s);
		// Recursively sort non-partition-elements
		if ((s = b - a) > 1)
			quickSort(x, from, from + s);
		if ((s = d - c) > 1)
			quickSort(x, to - s, to);
	}
	/**
	 * Sorts an array according to the natural ascending order using quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * Note that this implementation does not allocate any object, contrarily to the
	 * implementation used to sort primitive types in {@link java.util.Arrays},
	 * which switches to mergesort on large inputs.
	 *
	 * @param x
	 *            the array to be sorted.
	 *
	 */
	public static void quickSort(final boolean[] x) {
		quickSort(x, 0, x.length);
	}
	protected static class ForkJoinQuickSort extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final int from;
		private final int to;
		private final boolean[] x;
		public ForkJoinQuickSort(final boolean[] x, final int from, final int to) {
			this.from = from;
			this.to = to;
			this.x = x;
		}
		@Override

		protected void compute() {
			final boolean[] x = this.x;
			final int len = to - from;
			if (len < PARALLEL_QUICKSORT_NO_FORK) {
				quickSort(x, from, to);
				return;
			}
			// Choose a partition element, v
			int m = from + len / 2;
			int l = from;
			int n = to - 1;
			int s = len / 8;
			l = med3(x, l, l + s, l + 2 * s);
			m = med3(x, m - s, m, m + s);
			n = med3(x, n - 2 * s, n - s, n);
			m = med3(x, l, m, n);
			final boolean v = x[m];
			// Establish Invariant: v* (<v)* (>v)* v*
			int a = from, b = a, c = to - 1, d = c;
			while (true) {
				int comparison;
				while (b <= c && (comparison = (Boolean.compare((x[b]), (v)))) <= 0) {
					if (comparison == 0)
						swap(x, a++, b);
					b++;
				}
				while (c >= b && (comparison = (Boolean.compare((x[c]), (v)))) >= 0) {
					if (comparison == 0)
						swap(x, c, d--);
					c--;
				}
				if (b > c)
					break;
				swap(x, b++, c--);
			}
			// Swap partition elements back to middle
			int t;
			s = Math.min(a - from, b - a);
			swap(x, from, b - s, s);
			s = Math.min(d - c, to - d - 1);
			swap(x, b, to - s, s);
			// Recursively sort non-partition-elements
			s = b - a;
			t = d - c;
			if (s > 1 && t > 1)
				invokeAll(new ForkJoinQuickSort(x, from, from + s), new ForkJoinQuickSort(x, to - t, to));
			else if (s > 1)
				invokeAll(new ForkJoinQuickSort(x, from, from + s));
			else
				invokeAll(new ForkJoinQuickSort(x, to - t, to));
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
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 */
	public static void parallelQuickSort(final boolean[] x, final int from, final int to) {
		if (to - from < PARALLEL_QUICKSORT_NO_FORK || ForkJoinPool.getCommonPoolParallelism() == 1)
			quickSort(x, from, to);
		else {
			final ForkJoinPool pool = ForkJoinPool.commonPool();
			pool.invoke(new ForkJoinQuickSort(x, from, to));
			pool.shutdown();
		}
	}
	/**
	 * Sorts an array according to the natural ascending order using a parallel
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
	 *            the array to be sorted.
	 *
	 */
	public static void parallelQuickSort(final boolean[] x) {
		parallelQuickSort(x, 0, x.length);
	}

	private static int med3Indirect(final int perm[], final boolean x[], final int a, final int b, final int c) {
		final boolean aa = x[perm[a]];
		final boolean bb = x[perm[b]];
		final boolean cc = x[perm[c]];
		final int ab = (Boolean.compare((aa), (bb)));
		final int ac = (Boolean.compare((aa), (cc)));
		final int bc = (Boolean.compare((bb), (cc)));
		return (ab < 0 ? (bc < 0 ? b : ac < 0 ? c : a) : (bc > 0 ? b : ac > 0 ? c : a));
	}

	private static void insertionSortIndirect(final int[] perm, final boolean[] a, final int from, final int to) {
		for (int i = from; ++i < to;) {
			int t = perm[i];
			int j = i;
			for (int u = perm[j - 1]; (!(a[t]) && (a[u])); u = perm[--j - 1]) {
				perm[j] = u;
				if (from == j - 1) {
					--j;
					break;
				}
			}
			perm[j] = t;
		}
	}
	/**
	 * Sorts the specified range of elements according to the natural ascending
	 * order using indirect quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This method implement an <em>indirect</em> sort. The elements of {@code perm}
	 * (which must be exactly the numbers in the interval {@code [0..perm.length)})
	 * will be permuted so that {@code x[perm[i]] &le; x[perm[i + 1]]}.
	 *
	 * <p>
	 * Note that this implementation does not allocate any object, contrarily to the
	 * implementation used to sort primitive types in {@link java.util.Arrays},
	 * which switches to mergesort on large inputs.
	 *
	 * @param perm
	 *            a permutation array indexing {@code x}.
	 * @param x
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 */

	public static void quickSortIndirect(final int[] perm, final boolean[] x, final int from, final int to) {
		final int len = to - from;
		// Selection sort on smallest arrays
		if (len < QUICKSORT_NO_REC) {
			insertionSortIndirect(perm, x, from, to);
			return;
		}
		// Choose a partition element, v
		int m = from + len / 2;
		int l = from;
		int n = to - 1;
		if (len > QUICKSORT_MEDIAN_OF_9) { // Big arrays, pseudomedian of 9
			int s = len / 8;
			l = med3Indirect(perm, x, l, l + s, l + 2 * s);
			m = med3Indirect(perm, x, m - s, m, m + s);
			n = med3Indirect(perm, x, n - 2 * s, n - s, n);
		}
		m = med3Indirect(perm, x, l, m, n); // Mid-size, med of 3
		final boolean v = x[perm[m]];
		// Establish Invariant: v* (<v)* (>v)* v*
		int a = from, b = a, c = to - 1, d = c;
		while (true) {
			int comparison;
			while (b <= c && (comparison = (Boolean.compare((x[perm[b]]), (v)))) <= 0) {
				if (comparison == 0)
					IntArrays.swap(perm, a++, b);
				b++;
			}
			while (c >= b && (comparison = (Boolean.compare((x[perm[c]]), (v)))) >= 0) {
				if (comparison == 0)
					IntArrays.swap(perm, c, d--);
				c--;
			}
			if (b > c)
				break;
			IntArrays.swap(perm, b++, c--);
		}
		// Swap partition elements back to middle
		int s;
		s = Math.min(a - from, b - a);
		IntArrays.swap(perm, from, b - s, s);
		s = Math.min(d - c, to - d - 1);
		IntArrays.swap(perm, b, to - s, s);
		// Recursively sort non-partition-elements
		if ((s = b - a) > 1)
			quickSortIndirect(perm, x, from, from + s);
		if ((s = d - c) > 1)
			quickSortIndirect(perm, x, to - s, to);
	}
	/**
	 * Sorts an array according to the natural ascending order using indirect
	 * quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This method implement an <em>indirect</em> sort. The elements of {@code perm}
	 * (which must be exactly the numbers in the interval {@code [0..perm.length)})
	 * will be permuted so that {@code x[perm[i]] &le; x[perm[i + 1]]}.
	 *
	 * <p>
	 * Note that this implementation does not allocate any object, contrarily to the
	 * implementation used to sort primitive types in {@link java.util.Arrays},
	 * which switches to mergesort on large inputs.
	 *
	 * @param perm
	 *            a permutation array indexing {@code x}.
	 * @param x
	 *            the array to be sorted.
	 */
	public static void quickSortIndirect(final int perm[], final boolean[] x) {
		quickSortIndirect(perm, x, 0, x.length);
	}
	protected static class ForkJoinQuickSortIndirect extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final int from;
		private final int to;
		private final int[] perm;
		private final boolean[] x;
		public ForkJoinQuickSortIndirect(final int perm[], final boolean[] x, final int from, final int to) {
			this.from = from;
			this.to = to;
			this.x = x;
			this.perm = perm;
		}
		@Override

		protected void compute() {
			final boolean[] x = this.x;
			final int len = to - from;
			if (len < PARALLEL_QUICKSORT_NO_FORK) {
				quickSortIndirect(perm, x, from, to);
				return;
			}
			// Choose a partition element, v
			int m = from + len / 2;
			int l = from;
			int n = to - 1;
			int s = len / 8;
			l = med3Indirect(perm, x, l, l + s, l + 2 * s);
			m = med3Indirect(perm, x, m - s, m, m + s);
			n = med3Indirect(perm, x, n - 2 * s, n - s, n);
			m = med3Indirect(perm, x, l, m, n);
			final boolean v = x[perm[m]];
			// Establish Invariant: v* (<v)* (>v)* v*
			int a = from, b = a, c = to - 1, d = c;
			while (true) {
				int comparison;
				while (b <= c && (comparison = (Boolean.compare((x[perm[b]]), (v)))) <= 0) {
					if (comparison == 0)
						IntArrays.swap(perm, a++, b);
					b++;
				}
				while (c >= b && (comparison = (Boolean.compare((x[perm[c]]), (v)))) >= 0) {
					if (comparison == 0)
						IntArrays.swap(perm, c, d--);
					c--;
				}
				if (b > c)
					break;
				IntArrays.swap(perm, b++, c--);
			}
			// Swap partition elements back to middle
			int t;
			s = Math.min(a - from, b - a);
			IntArrays.swap(perm, from, b - s, s);
			s = Math.min(d - c, to - d - 1);
			IntArrays.swap(perm, b, to - s, s);
			// Recursively sort non-partition-elements
			s = b - a;
			t = d - c;
			if (s > 1 && t > 1)
				invokeAll(new ForkJoinQuickSortIndirect(perm, x, from, from + s),
						new ForkJoinQuickSortIndirect(perm, x, to - t, to));
			else if (s > 1)
				invokeAll(new ForkJoinQuickSortIndirect(perm, x, from, from + s));
			else
				invokeAll(new ForkJoinQuickSortIndirect(perm, x, to - t, to));
		}
	}
	/**
	 * Sorts the specified range of elements according to the natural ascending
	 * order using a parallel indirect quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This method implement an <em>indirect</em> sort. The elements of {@code perm}
	 * (which must be exactly the numbers in the interval {@code [0..perm.length)})
	 * will be permuted so that {@code x[perm[i]] &le; x[perm[i + 1]]}.
	 *
	 * <p>
	 * This implementation uses the {@link ForkJoinPool#commonPool() common pool}.
	 *
	 * @param perm
	 *            a permutation array indexing {@code x}.
	 * @param x
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 */
	public static void parallelQuickSortIndirect(final int[] perm, final boolean[] x, final int from, final int to) {
		if (to - from < PARALLEL_QUICKSORT_NO_FORK || ForkJoinPool.getCommonPoolParallelism() == 1)
			quickSortIndirect(perm, x, from, to);
		else {
			final ForkJoinPool pool = ForkJoinPool.commonPool();
			pool.invoke(new ForkJoinQuickSortIndirect(perm, x, from, to));
			pool.shutdown();
		}
	}
	/**
	 * Sorts an array according to the natural ascending order using a parallel
	 * indirect quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This method implement an <em>indirect</em> sort. The elements of {@code perm}
	 * (which must be exactly the numbers in the interval {@code [0..perm.length)})
	 * will be permuted so that {@code x[perm[i]] &le; x[perm[i + 1]]}.
	 *
	 * <p>
	 * This implementation uses the {@link ForkJoinPool#commonPool() common pool}.
	 *
	 * @param perm
	 *            a permutation array indexing {@code x}.
	 * @param x
	 *            the array to be sorted.
	 *
	 */
	public static void parallelQuickSortIndirect(final int perm[], final boolean[] x) {
		parallelQuickSortIndirect(perm, x, 0, x.length);
	}
	/**
	 * Stabilizes a permutation.
	 *
	 * <p>
	 * This method can be used to stabilize the permutation generated by an indirect
	 * sorting, assuming that initially the permutation array was in ascending order
	 * (e.g., the identity, as usually happens). This method scans the permutation,
	 * and for each non-singleton block of elements with the same associated values
	 * in {@code x}, permutes them in ascending order. The resulting permutation
	 * corresponds to a stable sort.
	 *
	 * <p>
	 * Usually combining an unstable indirect sort and this method is more efficient
	 * than using a stable sort, as most stable sort algorithms require a support
	 * array.
	 *
	 * <p>
	 * More precisely, assuming that {@code x[perm[i]] &le; x[perm[i + 1]]}, after
	 * stabilization we will also have that {@code x[perm[i]] = x[perm[i + 1]]}
	 * implies {@code perm[i] &le; perm[i + 1]}.
	 *
	 * @param perm
	 *            a permutation array indexing {@code x} so that it is sorted.
	 * @param x
	 *            the sorted array to be stabilized.
	 * @param from
	 *            the index of the first element (inclusive) to be stabilized.
	 * @param to
	 *            the index of the last element (exclusive) to be stabilized.
	 */
	public static void stabilize(final int perm[], final boolean[] x, final int from, final int to) {
		int curr = from;
		for (int i = from + 1; i < to; i++) {
			if (x[perm[i]] != x[perm[curr]]) {
				if (i - curr > 1)
					IntArrays.parallelQuickSort(perm, curr, i);
				curr = i;
			}
		}
		if (to - curr > 1)
			IntArrays.parallelQuickSort(perm, curr, to);
	}
	/**
	 * Stabilizes a permutation.
	 *
	 * <p>
	 * This method can be used to stabilize the permutation generated by an indirect
	 * sorting, assuming that initially the permutation array was in ascending order
	 * (e.g., the identity, as usually happens). This method scans the permutation,
	 * and for each non-singleton block of elements with the same associated values
	 * in {@code x}, permutes them in ascending order. The resulting permutation
	 * corresponds to a stable sort.
	 *
	 * <p>
	 * Usually combining an unstable indirect sort and this method is more efficient
	 * than using a stable sort, as most stable sort algorithms require a support
	 * array.
	 *
	 * <p>
	 * More precisely, assuming that {@code x[perm[i]] &le; x[perm[i + 1]]}, after
	 * stabilization we will also have that {@code x[perm[i]] = x[perm[i + 1]]}
	 * implies {@code perm[i] &le; perm[i + 1]}.
	 *
	 * @param perm
	 *            a permutation array indexing {@code x} so that it is sorted.
	 * @param x
	 *            the sorted array to be stabilized.
	 */
	public static void stabilize(final int perm[], final boolean[] x) {
		stabilize(perm, x, 0, perm.length);
	}

	private static int med3(final boolean x[], final boolean[] y, final int a, final int b, final int c) {
		int t;
		final int ab = (t = (Boolean.compare((x[a]), (x[b])))) == 0 ? (Boolean.compare((y[a]), (y[b]))) : t;
		final int ac = (t = (Boolean.compare((x[a]), (x[c])))) == 0 ? (Boolean.compare((y[a]), (y[c]))) : t;
		final int bc = (t = (Boolean.compare((x[b]), (x[c])))) == 0 ? (Boolean.compare((y[b]), (y[c]))) : t;
		return (ab < 0 ? (bc < 0 ? b : ac < 0 ? c : a) : (bc > 0 ? b : ac > 0 ? c : a));
	}
	private static void swap(final boolean x[], final boolean[] y, final int a, final int b) {
		final boolean t = x[a];
		final boolean u = y[a];
		x[a] = x[b];
		y[a] = y[b];
		x[b] = t;
		y[b] = u;
	}
	private static void swap(final boolean[] x, final boolean[] y, int a, int b, final int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, y, a, b);
	}

	private static void selectionSort(final boolean[] a, final boolean[] b, final int from, final int to) {
		for (int i = from; i < to - 1; i++) {
			int m = i, u;
			for (int j = i + 1; j < to; j++)
				if ((u = (Boolean.compare((a[j]), (a[m])))) < 0 || u == 0 && (!(b[j]) && (b[m])))
					m = j;
			if (m != i) {
				boolean t = a[i];
				a[i] = a[m];
				a[m] = t;
				t = b[i];
				b[i] = b[m];
				b[m] = t;
			}
		}
	}
	/**
	 * Sorts the specified range of elements of two arrays according to the natural
	 * lexicographical ascending order using quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This method implements a <em>lexicographical</em> sorting of the arguments.
	 * Pairs of elements in the same position in the two provided arrays will be
	 * considered a single key, and permuted accordingly. In the end, either
	 * {@code x[i] &lt; x[i + 1]} or <code>x[i]
	 * == x[i + 1]</code> and {@code y[i] &le; y[i + 1]}.
	 *
	 * @param x
	 *            the first array to be sorted.
	 * @param y
	 *            the second array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 */

	public static void quickSort(final boolean[] x, final boolean[] y, final int from, final int to) {
		final int len = to - from;
		if (len < QUICKSORT_NO_REC) {
			selectionSort(x, y, from, to);
			return;
		}
		// Choose a partition element, v
		int m = from + len / 2;
		int l = from;
		int n = to - 1;
		if (len > QUICKSORT_MEDIAN_OF_9) { // Big arrays, pseudomedian of 9
			int s = len / 8;
			l = med3(x, y, l, l + s, l + 2 * s);
			m = med3(x, y, m - s, m, m + s);
			n = med3(x, y, n - 2 * s, n - s, n);
		}
		m = med3(x, y, l, m, n); // Mid-size, med of 3
		final boolean v = x[m], w = y[m];
		// Establish Invariant: v* (<v)* (>v)* v*
		int a = from, b = a, c = to - 1, d = c;
		while (true) {
			int comparison, t;
			while (b <= c && (comparison = (t = (Boolean.compare((x[b]), (v)))) == 0
					? (Boolean.compare((y[b]), (w)))
					: t) <= 0) {
				if (comparison == 0)
					swap(x, y, a++, b);
				b++;
			}
			while (c >= b && (comparison = (t = (Boolean.compare((x[c]), (v)))) == 0
					? (Boolean.compare((y[c]), (w)))
					: t) >= 0) {
				if (comparison == 0)
					swap(x, y, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, y, b++, c--);
		}
		// Swap partition elements back to middle
		int s;
		s = Math.min(a - from, b - a);
		swap(x, y, from, b - s, s);
		s = Math.min(d - c, to - d - 1);
		swap(x, y, b, to - s, s);
		// Recursively sort non-partition-elements
		if ((s = b - a) > 1)
			quickSort(x, y, from, from + s);
		if ((s = d - c) > 1)
			quickSort(x, y, to - s, to);
	}
	/**
	 * Sorts two arrays according to the natural lexicographical ascending order
	 * using quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This method implements a <em>lexicographical</em> sorting of the arguments.
	 * Pairs of elements in the same position in the two provided arrays will be
	 * considered a single key, and permuted accordingly. In the end, either
	 * {@code x[i] &lt; x[i + 1]} or <code>x[i]
	 * == x[i + 1]</code> and {@code y[i] &le; y[i + 1]}.
	 *
	 * @param x
	 *            the first array to be sorted.
	 * @param y
	 *            the second array to be sorted.
	 */
	public static void quickSort(final boolean[] x, final boolean[] y) {
		ensureSameLength(x, y);
		quickSort(x, y, 0, x.length);
	}
	protected static class ForkJoinQuickSort2 extends RecursiveAction {
		private static final long serialVersionUID = 1L;
		private final int from;
		private final int to;
		private final boolean[] x, y;
		public ForkJoinQuickSort2(final boolean[] x, final boolean[] y, final int from, final int to) {
			this.from = from;
			this.to = to;
			this.x = x;
			this.y = y;
		}
		@Override

		protected void compute() {
			final boolean[] x = this.x;
			final boolean[] y = this.y;
			final int len = to - from;
			if (len < PARALLEL_QUICKSORT_NO_FORK) {
				quickSort(x, y, from, to);
				return;
			}
			// Choose a partition element, v
			int m = from + len / 2;
			int l = from;
			int n = to - 1;
			int s = len / 8;
			l = med3(x, y, l, l + s, l + 2 * s);
			m = med3(x, y, m - s, m, m + s);
			n = med3(x, y, n - 2 * s, n - s, n);
			m = med3(x, y, l, m, n);
			final boolean v = x[m], w = y[m];
			// Establish Invariant: v* (<v)* (>v)* v*
			int a = from, b = a, c = to - 1, d = c;
			while (true) {
				int comparison, t;
				while (b <= c && (comparison = (t = (Boolean.compare((x[b]), (v)))) == 0
						? (Boolean.compare((y[b]), (w)))
						: t) <= 0) {
					if (comparison == 0)
						swap(x, y, a++, b);
					b++;
				}
				while (c >= b && (comparison = (t = (Boolean.compare((x[c]), (v)))) == 0
						? (Boolean.compare((y[c]), (w)))
						: t) >= 0) {
					if (comparison == 0)
						swap(x, y, c, d--);
					c--;
				}
				if (b > c)
					break;
				swap(x, y, b++, c--);
			}
			// Swap partition elements back to middle
			int t;
			s = Math.min(a - from, b - a);
			swap(x, y, from, b - s, s);
			s = Math.min(d - c, to - d - 1);
			swap(x, y, b, to - s, s);
			s = b - a;
			t = d - c;
			// Recursively sort non-partition-elements
			if (s > 1 && t > 1)
				invokeAll(new ForkJoinQuickSort2(x, y, from, from + s), new ForkJoinQuickSort2(x, y, to - t, to));
			else if (s > 1)
				invokeAll(new ForkJoinQuickSort2(x, y, from, from + s));
			else
				invokeAll(new ForkJoinQuickSort2(x, y, to - t, to));
		}
	}
	/**
	 * Sorts the specified range of elements of two arrays according to the natural
	 * lexicographical ascending order using a parallel quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This method implements a <em>lexicographical</em> sorting of the arguments.
	 * Pairs of elements in the same position in the two provided arrays will be
	 * considered a single key, and permuted accordingly. In the end, either
	 * {@code x[i] &lt; x[i + 1]} or <code>x[i]
	 * == x[i + 1]</code> and {@code y[i] &le; y[i + 1]}.
	 *
	 * <p>
	 * This implementation uses the {@link ForkJoinPool#commonPool() common pool}.
	 *
	 * @param x
	 *            the first array to be sorted.
	 * @param y
	 *            the second array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 */
	public static void parallelQuickSort(final boolean[] x, final boolean[] y, final int from, final int to) {
		if (to - from < PARALLEL_QUICKSORT_NO_FORK || ForkJoinPool.getCommonPoolParallelism() == 1)
			quickSort(x, y, from, to);
		final ForkJoinPool pool = ForkJoinPool.commonPool();
		pool.invoke(new ForkJoinQuickSort2(x, y, from, to));
		pool.shutdown();
	}
	/**
	 * Sorts two arrays according to the natural lexicographical ascending order
	 * using a parallel quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M.
	 * Douglas McIlroy, &ldquo;Engineering a Sort Function&rdquo;, <i>Software:
	 * Practice and Experience</i>, 23(11), pages 1249&minus;1265, 1993.
	 *
	 * <p>
	 * This method implements a <em>lexicographical</em> sorting of the arguments.
	 * Pairs of elements in the same position in the two provided arrays will be
	 * considered a single key, and permuted accordingly. In the end, either
	 * {@code x[i] &lt; x[i + 1]} or <code>x[i]
	 * == x[i + 1]</code> and {@code y[i] &le; y[i + 1]}.
	 *
	 * <p>
	 * This implementation uses the {@link ForkJoinPool#commonPool() common pool}.
	 *
	 * @param x
	 *            the first array to be sorted.
	 * @param y
	 *            the second array to be sorted.
	 */
	public static void parallelQuickSort(final boolean[] x, final boolean[] y) {
		ensureSameLength(x, y);
		parallelQuickSort(x, y, 0, x.length);
	}
	/**
	 * Sorts an array according to the natural ascending order, potentially
	 * dynamically choosing an appropriate algorithm given the type and size of the
	 * array. The sort will be stable unless it is provable that it would be
	 * impossible for there to be any difference between a stable and unstable sort
	 * for the given type, in which case stability is meaningless and thus
	 * unspecified.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @since 8.3.0
	 */
	public static void unstableSort(final boolean a[], final int from, final int to) {
		quickSort(a, from, to);
	}
	/**
	 * Sorts the specified range of elements according to the natural ascending
	 * order potentially dynamically choosing an appropriate algorithm given the
	 * type and size of the array. No assurance is made of the stability of the
	 * sort.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @since 8.3.0
	 */
	public static void unstableSort(final boolean a[]) {
		unstableSort(a, 0, a.length);
	}
	/**
	 * Sorts the specified range of elements according to the order induced by the
	 * specified comparator, potentially dynamically choosing an appropriate
	 * algorithm given the type and size of the array. No assurance is made of the
	 * stability of the sort.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 * @since 8.3.0
	 */
	public static void unstableSort(final boolean a[], final int from, final int to, BooleanComparator comp) {
		quickSort(a, from, to, comp);
	}
	/**
	 * Sorts an array according to the order induced by the specified comparator,
	 * potentially dynamically choosing an appropriate algorithm given the type and
	 * size of the array. No assurance is made of the stability of the sort.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 * @since 8.3.0
	 */
	public static void unstableSort(final boolean a[], BooleanComparator comp) {
		unstableSort(a, 0, a.length, comp);
	}
	/**
	 * Sorts the specified range of elements according to the natural ascending
	 * order using mergesort, using a given pre-filled support array.
	 *
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be
	 * reordered as a result of the sort. Moreover, no support arrays will be
	 * allocated.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @param supp
	 *            a support array containing at least {@code to} elements, and whose
	 *            entries are identical to those of {@code a} in the specified
	 *            range. It can be {@code null}, in which case {@code a} will be
	 *            cloned.
	 */

	public static void mergeSort(final boolean a[], final int from, final int to, boolean supp[]) {
		int len = to - from;
		// Insertion sort on smallest arrays
		if (len < MERGESORT_NO_REC) {
			insertionSort(a, from, to);
			return;
		}
		if (supp == null)
			supp = java.util.Arrays.copyOf(a, to);
		// Recursively sort halves of a into supp
		final int mid = (from + to) >>> 1;
		mergeSort(supp, from, mid, a);
		mergeSort(supp, mid, to, a);
		// If list is already sorted, just copy from supp to a. This is an
		// optimization that results in faster sorts for nearly ordered lists.
		if ((!(supp[mid - 1]) || (supp[mid]))) {
			System.arraycopy(supp, from, a, from, len);
			return;
		}
		// Merge sorted halves (now in supp) into a
		for (int i = from, p = from, q = mid; i < to; i++) {
			if (q >= to || p < mid && (!(supp[p]) || (supp[q])))
				a[i] = supp[p++];
			else
				a[i] = supp[q++];
		}
	}
	/**
	 * Sorts the specified range of elements according to the natural ascending
	 * order using mergesort.
	 *
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be
	 * reordered as a result of the sort. An array as large as {@code a} will be
	 * allocated by this method.
	 * 
	 * @param a
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 */
	public static void mergeSort(final boolean a[], final int from, final int to) {
		mergeSort(a, from, to, (boolean[]) null);
	}
	/**
	 * Sorts an array according to the natural ascending order using mergesort.
	 *
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be
	 * reordered as a result of the sort. An array as large as {@code a} will be
	 * allocated by this method.
	 * 
	 * @param a
	 *            the array to be sorted.
	 */
	public static void mergeSort(final boolean a[]) {
		mergeSort(a, 0, a.length);
	}
	/**
	 * Sorts the specified range of elements according to the order induced by the
	 * specified comparator using mergesort, using a given pre-filled support array.
	 *
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be
	 * reordered as a result of the sort. Moreover, no support arrays will be
	 * allocated.
	 * 
	 * @param a
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 * @param supp
	 *            a support array containing at least {@code to} elements, and whose
	 *            entries are identical to those of {@code a} in the specified
	 *            range. It can be {@code null}, in which case {@code a} will be
	 *            cloned.
	 */
	public static void mergeSort(final boolean a[], final int from, final int to, BooleanComparator comp,
			boolean supp[]) {
		int len = to - from;
		// Insertion sort on smallest arrays
		if (len < MERGESORT_NO_REC) {
			insertionSort(a, from, to, comp);
			return;
		}
		if (supp == null)
			supp = java.util.Arrays.copyOf(a, to);
		// Recursively sort halves of a into supp
		final int mid = (from + to) >>> 1;
		mergeSort(supp, from, mid, comp, a);
		mergeSort(supp, mid, to, comp, a);
		// If list is already sorted, just copy from supp to a. This is an
		// optimization that results in faster sorts for nearly ordered lists.
		if (comp.compare(supp[mid - 1], supp[mid]) <= 0) {
			System.arraycopy(supp, from, a, from, len);
			return;
		}
		// Merge sorted halves (now in supp) into a
		for (int i = from, p = from, q = mid; i < to; i++) {
			if (q >= to || p < mid && comp.compare(supp[p], supp[q]) <= 0)
				a[i] = supp[p++];
			else
				a[i] = supp[q++];
		}
	}
	/**
	 * Sorts the specified range of elements according to the order induced by the
	 * specified comparator using mergesort.
	 *
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be
	 * reordered as a result of the sort. An array as large as {@code a} will be
	 * allocated by this method.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 */
	public static void mergeSort(final boolean a[], final int from, final int to, BooleanComparator comp) {
		mergeSort(a, from, to, comp, (boolean[]) null);
	}
	/**
	 * Sorts an array according to the order induced by the specified comparator
	 * using mergesort.
	 *
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be
	 * reordered as a result of the sort. An array as large as {@code a} will be
	 * allocated by this method.
	 * 
	 * @param a
	 *            the array to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 */
	public static void mergeSort(final boolean a[], BooleanComparator comp) {
		mergeSort(a, 0, a.length, comp);
	}
	/**
	 * Sorts an array according to the natural ascending order, potentially
	 * dynamically choosing an appropriate algorithm given the type and size of the
	 * array. The sort will be stable unless it is provable that it would be
	 * impossible for there to be any difference between a stable and unstable sort
	 * for the given type, in which case stability is meaningless and thus
	 * unspecified.
	 *
	 * <p>
	 * An array as large as {@code a} may be allocated by this method.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @since 8.3.0
	 */
	public static void stableSort(final boolean a[], final int from, final int to) {
		// For non-floating point primitive types, when comparing naturally,
		// it is impossible to tell the difference between a stable and not-stable sort.
		// So just use the probably faster unstable sort.
		unstableSort(a, from, to);
	}
	/**
	 * Sorts the specified range of elements according to the natural ascending
	 * order potentially dynamically choosing an appropriate algorithm given the
	 * type and size of the array. The sort will be stable unless it is provable
	 * that it would be impossible for there to be any difference between a stable
	 * and unstable sort for the given type, in which case stability is meaningless
	 * and thus unspecified.
	 *
	 * <p>
	 * An array as large as {@code a} may be allocated by this method.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @since 8.3.0
	 */
	public static void stableSort(final boolean a[]) {
		stableSort(a, 0, a.length);
	}
	/**
	 * Sorts the specified range of elements according to the order induced by the
	 * specified comparator, potentially dynamically choosing an appropriate
	 * algorithm given the type and size of the array. The sort will be stable
	 * unless it is provable that it would be impossible for there to be any
	 * difference between a stable and unstable sort for the given type, in which
	 * case stability is meaningless and thus unspecified.
	 *
	 * <p>
	 * An array as large as {@code a} may be allocated by this method.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @param from
	 *            the index of the first element (inclusive) to be sorted.
	 * @param to
	 *            the index of the last element (exclusive) to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 * @since 8.3.0
	 */
	public static void stableSort(final boolean a[], final int from, final int to, BooleanComparator comp) {
		mergeSort(a, from, to, comp);
	}
	/**
	 * Sorts an array according to the order induced by the specified comparator,
	 * potentially dynamically choosing an appropriate algorithm given the type and
	 * size of the array. The sort will be stable unless it is provable that it
	 * would be impossible for there to be any difference between a stable and
	 * unstable sort for the given type, in which case stability is meaningless and
	 * thus unspecified.
	 *
	 * <p>
	 * An array as large as {@code a} may be allocated by this method.
	 *
	 * @param a
	 *            the array to be sorted.
	 * @param comp
	 *            the comparator to determine the sorting order.
	 * @since 8.3.0
	 */
	public static void stableSort(final boolean a[], BooleanComparator comp) {
		stableSort(a, 0, a.length, comp);
	}
	/**
	 * Shuffles the specified array fragment using the specified pseudorandom number
	 * generator.
	 *
	 * @param a
	 *            the array to be shuffled.
	 * @param from
	 *            the index of the first element (inclusive) to be shuffled.
	 * @param to
	 *            the index of the last element (exclusive) to be shuffled.
	 * @param random
	 *            a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static boolean[] shuffle(final boolean[] a, final int from, final int to, final Random random) {
		for (int i = to - from; i-- != 0;) {
			final int p = random.nextInt(i + 1);
			final boolean t = a[from + i];
			a[from + i] = a[from + p];
			a[from + p] = t;
		}
		return a;
	}
	/**
	 * Shuffles the specified array using the specified pseudorandom number
	 * generator.
	 *
	 * @param a
	 *            the array to be shuffled.
	 * @param random
	 *            a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static boolean[] shuffle(final boolean[] a, final Random random) {
		for (int i = a.length; i-- != 0;) {
			final int p = random.nextInt(i + 1);
			final boolean t = a[i];
			a[i] = a[p];
			a[p] = t;
		}
		return a;
	}
	/**
	 * Reverses the order of the elements in the specified array.
	 *
	 * @param a
	 *            the array to be reversed.
	 * @return {@code a}.
	 */
	public static boolean[] reverse(final boolean[] a) {
		final int length = a.length;
		for (int i = length / 2; i-- != 0;) {
			final boolean t = a[length - i - 1];
			a[length - i - 1] = a[i];
			a[i] = t;
		}
		return a;
	}
	/**
	 * Reverses the order of the elements in the specified array fragment.
	 *
	 * @param a
	 *            the array to be reversed.
	 * @param from
	 *            the index of the first element (inclusive) to be reversed.
	 * @param to
	 *            the index of the last element (exclusive) to be reversed.
	 * @return {@code a}.
	 */
	public static boolean[] reverse(final boolean[] a, final int from, final int to) {
		final int length = to - from;
		for (int i = length / 2; i-- != 0;) {
			final boolean t = a[from + length - i - 1];
			a[from + length - i - 1] = a[from + i];
			a[from + i] = t;
		}
		return a;
	}
	/** A type-specific content-based hash strategy for arrays. */
	private static final class ArrayHashStrategy implements Hash.Strategy<boolean[]>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		@Override
		public int hashCode(final boolean[] o) {
			return java.util.Arrays.hashCode(o);
		}
		@Override
		public boolean equals(final boolean[] a, final boolean[] b) {
			return java.util.Arrays.equals(a, b);
		}
	}
	/**
	 * A type-specific content-based hash strategy for arrays.
	 *
	 * <p>
	 * This hash strategy may be used in custom hash collections whenever keys are
	 * arrays, and they must be considered equal by content. This strategy will
	 * handle {@code null} correctly, and it is serializable.
	 */
	public static final Hash.Strategy<boolean[]> HASH_STRATEGY = new ArrayHashStrategy();
}

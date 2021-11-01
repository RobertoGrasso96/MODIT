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
import java.util.List;
/**
 * A type-specific {@link List}; provides some additional methods that use
 * polymorphism to avoid (un)boxing.
 *
 * <p>
 * Note that this type-specific interface extends {@link Comparable}: it is
 * expected that implementing classes perform a lexicographical comparison using
 * the standard operator "less then" for primitive types, and the usual
 * {@link Comparable#compareTo(Object) compareTo()} method for objects.
 *
 * <p>
 * Additionally, this interface strengthens {@link #listIterator()},
 * {@link #listIterator(int)} and {@link #subList(int,int)}.
 *
 * <p>
 * Besides polymorphic methods, this interfaces specifies methods to copy into
 * an array or remove contiguous sublists. Although the abstract implementation
 * of this interface provides simple, one-by-one implementations of these
 * methods, it is expected that concrete implementation override them with
 * optimized versions.
 *
 * @see List
 */
public interface ObjectList<K> extends List<K>, Comparable<List<? extends K>>, ObjectCollection<K> {
	/**
	 * Returns a type-specific iterator on the elements of this list.
	 *
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link List#iterator()}. It would not be normally necessary, but
	 * {@link java.lang.Iterable#iterator()} is bizarrily re-specified in
	 * {@link List}.
	 *
	 * @return an iterator on the elements of this list.
	 */
	@Override
	ObjectListIterator<K> iterator();
	/**
	 * Returns a type-specific list iterator on the list.
	 *
	 * @see List#listIterator()
	 */
	@Override
	ObjectListIterator<K> listIterator();
	/**
	 * Returns a type-specific list iterator on the list starting at a given index.
	 *
	 * @see List#listIterator(int)
	 */
	@Override
	ObjectListIterator<K> listIterator(int index);
	/**
	 * Returns a type-specific view of the portion of this list from the index
	 * {@code from}, inclusive, to the index {@code to}, exclusive.
	 *
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link List#subList(int,int)}.
	 *
	 * @see List#subList(int,int)
	 */
	@Override
	ObjectList<K> subList(int from, int to);
	/**
	 * Sets the size of this list.
	 *
	 * <p>
	 * If the specified size is smaller than the current size, the last elements are
	 * discarded. Otherwise, they are filled with 0/{@code null}/{@code false}.
	 *
	 * @param size
	 *            the new size.
	 */
	void size(int size);
	/**
	 * Copies (hopefully quickly) elements of this type-specific list into the given
	 * array.
	 *
	 * @param from
	 *            the start index (inclusive).
	 * @param a
	 *            the destination array.
	 * @param offset
	 *            the offset into the destination array where to store the first
	 *            element copied.
	 * @param length
	 *            the number of elements to be copied.
	 */
	void getElements(int from, Object a[], int offset, int length);
	/**
	 * Removes (hopefully quickly) elements of this type-specific list.
	 *
	 * @param from
	 *            the start index (inclusive).
	 * @param to
	 *            the end index (exclusive).
	 */
	void removeElements(int from, int to);
	/**
	 * Add (hopefully quickly) elements to this type-specific list.
	 *
	 * @param index
	 *            the index at which to add elements.
	 * @param a
	 *            the array containing the elements.
	 */
	void addElements(int index, K a[]);
	/**
	 * Add (hopefully quickly) elements to this type-specific list.
	 *
	 * @param index
	 *            the index at which to add elements.
	 * @param a
	 *            the array containing the elements.
	 * @param offset
	 *            the offset of the first element to add.
	 * @param length
	 *            the number of elements to add.
	 */
	void addElements(int index, K a[], int offset, int length);
	/**
	 * Set (hopefully quickly) elements to match the array given.
	 * 
	 * @param a
	 *            the array containing the elements.
	 * @since 8.3.0
	 */
	default void setElements(K a[]) {
		setElements(0, a);
	}
	/**
	 * Set (hopefully quickly) elements to match the array given.
	 * 
	 * @param index
	 *            the index at which to start setting elements.
	 * @param a
	 *            the array containing the elements.
	 * @since 8.3.0
	 */
	default void setElements(int index, K a[]) {
		setElements(index, a, 0, a.length);
	}
	/**
	 * Set (hopefully quickly) elements to match the array given.
	 *
	 * Sets each in this list to the corresponding elements in the array, as if by
	 * 
	 * <pre>
	 * <code>
	 * ListIterator iter = listIterator(index);
	 * int i = 0;
	 * while (i &lt; length) {
	 *   iter.next();
	 *   iter.set(a[offset + i++]);
	 * }
	 * </code>
	 * </pre>
	 * 
	 * However, the exact implementation may be more efficient, taking into account
	 * whether random access is faster or not, or at the discretion of subclasses,
	 * abuse internals.
	 *
	 * @param index
	 *            the index at which to start setting elements.
	 * @param a
	 *            the array containing the elements
	 * @param offset
	 *            the offset of the first element to add.
	 * @param length
	 *            the number of elements to add.
	 * @since 8.3.0
	 */
	default void setElements(int index, K a[], int offset, int length) {
		// We can't use AbstractList#ensureIndex, sadly.
		if (index < 0)
			throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
		if (index > size())
			throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + (size()) + ")");
		ObjectArrays.ensureOffsetLength(a, offset, length);
		if (index + length > size())
			throw new IndexOutOfBoundsException(
					"End index (" + (index + length) + ") is greater than list size (" + size() + ")");
		ObjectListIterator<K> iter = listIterator(index);
		int i = 0;
		while (i < length) {
			iter.next();
			iter.set(a[offset + i++]);
		}
	}
	/**
	 * Sorts this list using a sort not assured to be stable. This differs from
	 * {@link List#sort(java.util.Comparator)} in that the results are not assured
	 * to be stable, but may be a bit faster.
	 *
	 * <p>
	 * Unless a subclass specifies otherwise, the results of the method if the list
	 * is concurrently modified during the sort are unspecified.
	 *
	 * @implSpec The default implementation dumps the elements into an array using
	 *           {@link #toArray()}, sorts the array, then replaces all elements
	 *           using the {@link #setElements} function.
	 *
	 * @since 8.3.0
	 */
	@SuppressWarnings("unchecked")
	default void unstableSort(final java.util.Comparator<? super K> comparator) {
		K[] elements = (K[]) toArray();
		if (comparator == null) {
			ObjectArrays.unstableSort(elements);
		} else {
			ObjectArrays.unstableSort(elements, comparator);
		}
		setElements(elements);
	}
}

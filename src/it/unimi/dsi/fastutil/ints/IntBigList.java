/*
	* Copyright (C) 2010-2020 Sebastiano Vigna
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
import java.util.List;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;
/**
 * A type-specific {@link BigList}; provides some additional methods that use
 * polymorphism to avoid (un)boxing.
 *
 * <p>
 * Additionally, this interface strengthens {@link #iterator()},
 * {@link #listIterator()}, {@link #listIterator(long)} and
 * {@link #subList(long,long)}.
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
public interface IntBigList extends BigList<Integer>, IntCollection, Size64, Comparable<BigList<? extends Integer>> {
	/**
	 * Returns a type-specific iterator on the elements of this list.
	 *
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link java.util.Collection#iterator()}.
	 * 
	 * @see java.util.Collection#iterator()
	 */
	@Override
	IntBigListIterator iterator();
	/**
	 * Returns a type-specific big-list iterator on this type-specific big list.
	 *
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link BigList#listIterator()}.
	 * 
	 * @see BigList#listIterator()
	 */
	@Override
	IntBigListIterator listIterator();
	/**
	 * Returns a type-specific list iterator on this type-specific big list starting
	 * at a given index.
	 *
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link BigList#listIterator(long)}.
	 * 
	 * @see BigList#listIterator(long)
	 */
	@Override
	IntBigListIterator listIterator(long index);
	/**
	 * Returns a type-specific view of the portion of this type-specific big list
	 * from the index {@code from}, inclusive, to the index {@code to}, exclusive.
	 *
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link BigList#subList(long,long)}.
	 *
	 * @see BigList#subList(long,long)
	 */
	@Override
	IntBigList subList(long from, long to);
	/**
	 * Copies (hopefully quickly) elements of this type-specific big list into the
	 * given big array.
	 *
	 * @param from
	 *            the start index (inclusive).
	 * @param a
	 *            the destination big array.
	 * @param offset
	 *            the offset into the destination big array where to store the first
	 *            element copied.
	 * @param length
	 *            the number of elements to be copied.
	 */
	void getElements(long from, int a[][], long offset, long length);
	/**
	 * Removes (hopefully quickly) elements of this type-specific big list.
	 *
	 * @param from
	 *            the start index (inclusive).
	 * @param to
	 *            the end index (exclusive).
	 */
	void removeElements(long from, long to);
	/**
	 * Add (hopefully quickly) elements to this type-specific big list.
	 *
	 * @param index
	 *            the index at which to add elements.
	 * @param a
	 *            the big array containing the elements.
	 */
	void addElements(long index, int a[][]);
	/**
	 * Add (hopefully quickly) elements to this type-specific big list.
	 *
	 * @param index
	 *            the index at which to add elements.
	 * @param a
	 *            the big array containing the elements.
	 * @param offset
	 *            the offset of the first element to add.
	 * @param length
	 *            the number of elements to add.
	 */
	void addElements(long index, int a[][], long offset, long length);
	/**
	 * Inserts the specified element at the specified position in this type-specific
	 * big list (optional operation).
	 * 
	 * @see BigList#add(long,Object)
	 */
	void add(long index, int key);
	/**
	 * Inserts all of the elements in the specified type-specific collection into
	 * this type-specific big list at the specified position (optional operation).
	 * 
	 * @see List#addAll(int,java.util.Collection)
	 */
	boolean addAll(long index, IntCollection c);
	/**
	 * Inserts all of the elements in the specified type-specific big list into this
	 * type-specific big list at the specified position (optional operation).
	 * 
	 * @see List#addAll(int,java.util.Collection)
	 */
	boolean addAll(long index, IntBigList c);
	/**
	 * Appends all of the elements in the specified type-specific big list to the
	 * end of this type-specific big list (optional operation).
	 * 
	 * @see List#addAll(int,java.util.Collection)
	 */
	boolean addAll(IntBigList c);
	/**
	 * Returns the element at the specified position.
	 * 
	 * @see BigList#get(long)
	 */
	int getInt(long index);
	/**
	 * Removes the element at the specified position.
	 * 
	 * @see BigList#remove(long)
	 */
	int removeInt(long index);
	/**
	 * Replaces the element at the specified position in this big list with the
	 * specified element (optional operation).
	 * 
	 * @see BigList#set(long,Object)
	 */
	int set(long index, int k);
	/**
	 * Returns the index of the first occurrence of the specified element in this
	 * type-specific big list, or -1 if this big list does not contain the element.
	 * 
	 * @see BigList#indexOf(Object)
	 */
	long indexOf(int k);
	/**
	 * Returns the index of the last occurrence of the specified element in this
	 * type-specific big list, or -1 if this big list does not contain the element.
	 * 
	 * @see BigList#lastIndexOf(Object)
	 */
	long lastIndexOf(int k);
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	void add(long index, Integer key);
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	Integer get(long index);
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	long indexOf(Object o);
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	long lastIndexOf(Object o);
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	Integer remove(long index);
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	Integer set(long index, Integer k);
}

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
import java.util.Collection;
/**
 * A type-specific {@link Collection}; provides some additional methods that use
 * polymorphism to avoid (un)boxing.
 *
 * <p>
 * Additionally, this class defines strengthens (again) {@link #iterator()}.
 *
 * @see Collection
 */
public interface IntCollection extends Collection<Integer>, IntIterable {
	/**
	 * Returns a type-specific iterator on the elements of this collection.
	 *
	 * <p>
	 * Note that this specification strengthens the one given in
	 * {@link java.lang.Iterable#iterator()}, which was already strengthened in the
	 * corresponding type-specific class, but was weakened by the fact that this
	 * interface extends {@link Collection}.
	 *
	 * @return a type-specific iterator on the elements of this collection.
	 */
	@Override
	IntIterator iterator();
	/**
	 * Ensures that this collection contains the specified element (optional
	 * operation).
	 * 
	 * @see Collection#add(Object)
	 */
	boolean add(int key);
	/**
	 * Returns {@code true} if this collection contains the specified element.
	 * 
	 * @see Collection#contains(Object)
	 */
	boolean contains(int key);
	/**
	 * Removes a single instance of the specified element from this collection, if
	 * it is present (optional operation).
	 *
	 * <p>
	 * Note that this method should be called
	 * {@link java.util.Collection#remove(Object) remove()}, but the clash with the
	 * similarly named index-based method in the {@link java.util.List} interface
	 * forces us to use a distinguished name. For simplicity, the set interfaces
	 * reinstates {@code remove()}.
	 *
	 * @see Collection#remove(Object)
	 */
	boolean rem(int key);
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean add(final Integer key) {
		return add((key).intValue());
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean contains(final Object key) {
		if (key == null)
			return false;
		return contains(((Integer) (key)).intValue());
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use (and implement) the {@code rem()} method instead.
	 */
	@Deprecated
	@Override
	default boolean remove(final Object key) {
		if (key == null)
			return false;
		return rem(((Integer) (key)).intValue());
	}
	/**
	 * Returns a primitive type array containing the items of this collection.
	 * 
	 * @return a primitive type array containing the items of this collection.
	 * @see Collection#toArray()
	 */
	int[] toIntArray();
	/**
	 * Returns a primitive type array containing the items of this collection.
	 *
	 * <p>
	 * Note that, contrarily to {@link Collection#toArray(Object[])}, this methods
	 * just writes all elements of this collection: no special value will be added
	 * after the last one.
	 *
	 * @param a
	 *            if this array is big enough, it will be used to store this
	 *            collection.
	 * @return a primitive type array containing the items of this collection.
	 * @see Collection#toArray(Object[])
	 * @deprecated Please use {@code toArray()} instead&mdash;this method is
	 *             redundant and will be removed in the future.
	 */
	@Deprecated
	int[] toIntArray(int a[]);
	/**
	 * Returns an array containing all of the elements in this collection; the
	 * runtime type of the returned array is that of the specified array.
	 *
	 * <p>
	 * Note that, contrarily to {@link Collection#toArray(Object[])}, this methods
	 * just writes all elements of this collection: no special value will be added
	 * after the last one.
	 *
	 * @param a
	 *            if this array is big enough, it will be used to store this
	 *            collection.
	 * @return a primitive type array containing the items of this collection.
	 * @see Collection#toArray(Object[])
	 */
	int[] toArray(int a[]);
	/**
	 * Adds all elements of the given type-specific collection to this collection.
	 *
	 * @param c
	 *            a type-specific collection.
	 * @see Collection#addAll(Collection)
	 * @return {@code true} if this collection changed as a result of the call.
	 */
	boolean addAll(IntCollection c);
	/**
	 * Checks whether this collection contains all elements from the given
	 * type-specific collection.
	 *
	 * @param c
	 *            a type-specific collection.
	 * @see Collection#containsAll(Collection)
	 * @return {@code true} if this collection contains all elements of the
	 *         argument.
	 */
	boolean containsAll(IntCollection c);
	/**
	 * Remove from this collection all elements in the given type-specific
	 * collection.
	 *
	 * @param c
	 *            a type-specific collection.
	 * @see Collection#removeAll(Collection)
	 * @return {@code true} if this collection changed as a result of the call.
	 */
	boolean removeAll(IntCollection c);
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean removeIf(final java.util.function.Predicate<? super Integer> filter) {
		return removeIf((java.util.function.IntPredicate) key -> filter.test(Integer.valueOf(key)));
	}
	/**
	 * Remove from this collection all elements which satisfy the given predicate.
	 *
	 * @param filter
	 *            a predicate which returns {@code true} for elements to be removed.
	 * @see Collection#removeIf(java.util.function.Predicate)
	 * @return {@code true} if any elements were removed.
	 */
	@SuppressWarnings("overloads")
	default boolean removeIf(final java.util.function.IntPredicate filter) {
		java.util.Objects.requireNonNull(filter);
		boolean removed = false;
		final IntIterator each = iterator();
		while (each.hasNext()) {
			if (filter.test(each.nextInt())) {
				each.remove();
				removed = true;
			}
		}
		return removed;
	}
	/**
	 * Retains in this collection only elements from the given type-specific
	 * collection.
	 *
	 * @param c
	 *            a type-specific collection.
	 * @see Collection#retainAll(Collection)
	 * @return {@code true} if this collection changed as a result of the call.
	 */
	boolean retainAll(IntCollection c);
}

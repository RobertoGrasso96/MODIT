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
import it.unimi.dsi.fastutil.Stack;
import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.NoSuchElementException;
/**
 * An abstract class providing basic methods for lists implementing a
 * type-specific list interface.
 *
 * <p>
 * As an additional bonus, this class implements on top of the list operations a
 * type-specific stack.
 */
public abstract class AbstractObjectList<K> extends AbstractObjectCollection<K> implements ObjectList<K>, Stack<K> {
	protected AbstractObjectList() {
	}
	/**
	 * Ensures that the given index is nonnegative and not greater than the list
	 * size.
	 *
	 * @param index
	 *            an index.
	 * @throws IndexOutOfBoundsException
	 *             if the given index is negative or greater than the list size.
	 */
	protected void ensureIndex(final int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
		if (index > size())
			throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + (size()) + ")");
	}
	/**
	 * Ensures that the given index is nonnegative and smaller than the list size.
	 *
	 * @param index
	 *            an index.
	 * @throws IndexOutOfBoundsException
	 *             if the given index is negative or not smaller than the list size.
	 */
	protected void ensureRestrictedIndex(final int index) {
		if (index < 0)
			throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
		if (index >= size())
			throw new IndexOutOfBoundsException(
					"Index (" + index + ") is greater than or equal to list size (" + (size()) + ")");
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public void add(final int index, final K k) {
		throw new UnsupportedOperationException();
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This implementation delegates to the type-specific version of
	 * {@link List#add(int, Object)}.
	 */
	@Override
	public boolean add(final K k) {
		add(size(), k);
		return true;
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public K remove(final int i) {
		throw new UnsupportedOperationException();
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public K set(final int index, final K k) {
		throw new UnsupportedOperationException();
	}
	/**
	 * Adds all of the elements in the specified collection to this list (optional
	 * operation).
	 */
	@Override
	public boolean addAll(int index, final Collection<? extends K> c) {
		ensureIndex(index);
		final Iterator<? extends K> i = c.iterator();
		final boolean retVal = i.hasNext();
		while (i.hasNext())
			add(index++, (i.next()));
		return retVal;
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This implementation delegates to the type-specific version of
	 * {@link List#addAll(int, Collection)}.
	 */
	@Override
	public boolean addAll(final Collection<? extends K> c) {
		return addAll(size(), c);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This implementation delegates to {@link #listIterator()}.
	 */
	@Override
	public ObjectListIterator<K> iterator() {
		return listIterator();
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This implementation delegates to {@link #listIterator(int) listIterator(0)}.
	 */
	@Override
	public ObjectListIterator<K> listIterator() {
		return listIterator(0);
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation is based on the random-access methods.
	 */
	@Override
	public ObjectListIterator<K> listIterator(final int index) {
		ensureIndex(index);
		return new ObjectListIterator<K>() {
			int pos = index, last = -1;
			@Override
			public boolean hasNext() {
				return pos < AbstractObjectList.this.size();
			}
			@Override
			public boolean hasPrevious() {
				return pos > 0;
			}
			@Override
			public K next() {
				if (!hasNext())
					throw new NoSuchElementException();
				return AbstractObjectList.this.get(last = pos++);
			}
			@Override
			public K previous() {
				if (!hasPrevious())
					throw new NoSuchElementException();
				return AbstractObjectList.this.get(last = --pos);
			}
			@Override
			public int nextIndex() {
				return pos;
			}
			@Override
			public int previousIndex() {
				return pos - 1;
			}
			@Override
			public void add(final K k) {
				AbstractObjectList.this.add(pos++, k);
				last = -1;
			}
			@Override
			public void set(final K k) {
				if (last == -1)
					throw new IllegalStateException();
				AbstractObjectList.this.set(last, k);
			}
			@Override
			public void remove() {
				if (last == -1)
					throw new IllegalStateException();
				AbstractObjectList.this.remove(last);
				/*
				 * If the last operation was a next(), we are removing an element *before* us,
				 * and we must decrease pos correspondingly.
				 */
				if (last < pos)
					pos--;
				last = -1;
			}
		};
	}
	/**
	 * Returns true if this list contains the specified element.
	 * <p>
	 * This implementation delegates to {@code indexOf()}.
	 * 
	 * @see List#contains(Object)
	 */
	@Override
	public boolean contains(final Object k) {
		return indexOf(k) >= 0;
	}
	@Override
	public int indexOf(final Object k) {
		final ObjectListIterator<K> i = listIterator();
		K e;
		while (i.hasNext()) {
			e = i.next();
			if (java.util.Objects.equals(k, e))
				return i.previousIndex();
		}
		return -1;
	}
	@Override
	public int lastIndexOf(final Object k) {
		ObjectListIterator<K> i = listIterator(size());
		K e;
		while (i.hasPrevious()) {
			e = i.previous();
			if (java.util.Objects.equals(k, e))
				return i.nextIndex();
		}
		return -1;
	}
	@Override
	public void size(final int size) {
		int i = size();
		if (size > i)
			while (i++ < size)
				add((null));
		else
			while (i-- != size)
				remove(i);
	}
	@Override
	public ObjectList<K> subList(final int from, final int to) {
		ensureIndex(from);
		ensureIndex(to);
		if (from > to)
			throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
		return new ObjectSubList<>(this, from, to);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This is a trivial iterator-based implementation. It is expected that
	 * implementations will override this method with a more optimized version.
	 */
	@Override
	public void removeElements(final int from, final int to) {
		ensureIndex(to);
		ObjectListIterator<K> i = listIterator(from);
		int n = to - from;
		if (n < 0)
			throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
		while (n-- != 0) {
			i.next();
			i.remove();
		}
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This is a trivial iterator-based implementation. It is expected that
	 * implementations will override this method with a more optimized version.
	 */
	@Override
	public void addElements(int index, final K a[], int offset, int length) {
		ensureIndex(index);
		if (offset < 0)
			throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
		if (offset + length > a.length)
			throw new ArrayIndexOutOfBoundsException(
					"End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
		while (length-- != 0)
			add(index++, a[offset++]);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This implementation delegates to the analogous method for array fragments.
	 */
	@Override
	public void addElements(final int index, final K a[]) {
		addElements(index, a, 0, a.length);
	}
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This is a trivial iterator-based implementation. It is expected that
	 * implementations will override this method with a more optimized version.
	 */
	@Override
	public void getElements(final int from, final Object a[], int offset, int length) {
		ObjectListIterator<K> i = listIterator(from);
		if (offset < 0)
			throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
		if (offset + length > a.length)
			throw new ArrayIndexOutOfBoundsException(
					"End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
		if (from + length > size())
			throw new IndexOutOfBoundsException(
					"End index (" + (from + length) + ") is greater than list size (" + size() + ")");
		while (length-- != 0)
			a[offset++] = i.next();
	}
	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation delegates to {@link #removeElements(int, int)}.
	 */
	@Override
	public void clear() {
		removeElements(0, size());
	}
	private boolean valEquals(final Object a, final Object b) {
		return a == null ? b == null : a.equals(b);
	}
	/**
	 * Returns the hash code for this list, which is identical to
	 * {@link java.util.List#hashCode()}.
	 *
	 * @return the hash code for this list.
	 */
	@Override
	public int hashCode() {
		ObjectIterator<K> i = iterator();
		int h = 1, s = size();
		while (s-- != 0) {
			K k = i.next();
			h = 31 * h + ((k) == null ? 0 : (k).hashCode());
		}
		return h;
	}
	@Override
	public boolean equals(final Object o) {
		if (o == this)
			return true;
		if (!(o instanceof List))
			return false;
		final List<?> l = (List<?>) o;
		int s = size();
		if (s != l.size())
			return false;
		final ListIterator<?> i1 = listIterator(), i2 = l.listIterator();
		while (s-- != 0)
			if (!valEquals(i1.next(), i2.next()))
				return false;
		return true;
	}
	/**
	 * Compares this list to another object. If the argument is a
	 * {@link java.util.List}, this method performs a lexicographical comparison;
	 * otherwise, it throws a {@code ClassCastException}.
	 *
	 * @param l
	 *            a list.
	 * @return if the argument is a {@link java.util.List}, a negative integer,
	 *         zero, or a positive integer as this list is lexicographically less
	 *         than, equal to, or greater than the argument.
	 * @throws ClassCastException
	 *             if the argument is not a list.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(final List<? extends K> l) {
		if (l == this)
			return 0;
		if (l instanceof ObjectList) {
			final ObjectListIterator<K> i1 = listIterator(), i2 = ((ObjectList<K>) l).listIterator();
			int r;
			K e1, e2;
			while (i1.hasNext() && i2.hasNext()) {
				e1 = i1.next();
				e2 = i2.next();
				if ((r = (((Comparable<K>) (e1)).compareTo(e2))) != 0)
					return r;
			}
			return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
		}
		ListIterator<? extends K> i1 = listIterator(), i2 = l.listIterator();
		int r;
		while (i1.hasNext() && i2.hasNext()) {
			if ((r = ((Comparable<? super K>) i1.next()).compareTo(i2.next())) != 0)
				return r;
		}
		return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
	}
	@Override
	public void push(final K o) {
		add(o);
	}
	@Override
	public K pop() {
		if (isEmpty())
			throw new NoSuchElementException();
		return remove(size() - 1);
	}
	@Override
	public K top() {
		if (isEmpty())
			throw new NoSuchElementException();
		return get(size() - 1);
	}
	@Override
	public K peek(final int i) {
		return get(size() - 1 - i);
	}
	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final ObjectIterator<K> i = iterator();
		int n = size();
		K k;
		boolean first = true;
		s.append("[");
		while (n-- != 0) {
			if (first)
				first = false;
			else
				s.append(", ");
			k = i.next();
			if (this == k)
				s.append("(this list)");
			else
				s.append(String.valueOf(k));
		}
		s.append("]");
		return s.toString();
	}
	/** A class implementing a sublist view. */
	public static class ObjectSubList<K> extends AbstractObjectList<K> implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		/** The list this sublist restricts. */
		protected final ObjectList<K> l;
		/** Initial (inclusive) index of this sublist. */
		protected final int from;
		/** Final (exclusive) index of this sublist. */
		protected int to;
		public ObjectSubList(final ObjectList<K> l, final int from, final int to) {
			this.l = l;
			this.from = from;
			this.to = to;
		}
		private boolean assertRange() {
			assert from <= l.size();
			assert to <= l.size();
			assert to >= from;
			return true;
		}
		@Override
		public boolean add(final K k) {
			l.add(to, k);
			to++;
			assert assertRange();
			return true;
		}
		@Override
		public void add(final int index, final K k) {
			ensureIndex(index);
			l.add(from + index, k);
			to++;
			assert assertRange();
		}
		@Override
		public boolean addAll(final int index, final Collection<? extends K> c) {
			ensureIndex(index);
			to += c.size();
			return l.addAll(from + index, c);
		}
		@Override
		public K get(final int index) {
			ensureRestrictedIndex(index);
			return l.get(from + index);
		}
		@Override
		public K remove(final int index) {
			ensureRestrictedIndex(index);
			to--;
			return l.remove(from + index);
		}
		@Override
		public K set(final int index, final K k) {
			ensureRestrictedIndex(index);
			return l.set(from + index, k);
		}
		@Override
		public int size() {
			return to - from;
		}
		@Override
		public void getElements(final int from, final Object[] a, final int offset, final int length) {
			ensureIndex(from);
			if (from + length > size())
				throw new IndexOutOfBoundsException(
						"End index (" + from + length + ") is greater than list size (" + size() + ")");
			l.getElements(this.from + from, a, offset, length);
		}
		@Override
		public void removeElements(final int from, final int to) {
			ensureIndex(from);
			ensureIndex(to);
			l.removeElements(this.from + from, this.from + to);
			this.to -= (to - from);
			assert assertRange();
		}
		@Override
		public void addElements(int index, final K a[], int offset, int length) {
			ensureIndex(index);
			l.addElements(this.from + index, a, offset, length);
			this.to += length;
			assert assertRange();
		}
		@Override
		public ObjectListIterator<K> listIterator(final int index) {
			ensureIndex(index);
			return new ObjectListIterator<K>() {
				int pos = index, last = -1;
				@Override
				public boolean hasNext() {
					return pos < size();
				}
				@Override
				public boolean hasPrevious() {
					return pos > 0;
				}
				@Override
				public K next() {
					if (!hasNext())
						throw new NoSuchElementException();
					return l.get(from + (last = pos++));
				}
				@Override
				public K previous() {
					if (!hasPrevious())
						throw new NoSuchElementException();
					return l.get(from + (last = --pos));
				}
				@Override
				public int nextIndex() {
					return pos;
				}
				@Override
				public int previousIndex() {
					return pos - 1;
				}
				@Override
				public void add(K k) {
					if (last == -1)
						throw new IllegalStateException();
					ObjectSubList.this.add(pos++, k);
					last = -1;
					assert assertRange();
				}
				@Override
				public void set(K k) {
					if (last == -1)
						throw new IllegalStateException();
					ObjectSubList.this.set(last, k);
				}
				@Override
				public void remove() {
					if (last == -1)
						throw new IllegalStateException();
					ObjectSubList.this.remove(last);
					/*
					 * If the last operation was a next(), we are removing an element *before* us,
					 * and we must decrease pos correspondingly.
					 */
					if (last < pos)
						pos--;
					last = -1;
					assert assertRange();
				}
			};
		}
		@Override
		public ObjectList<K> subList(final int from, final int to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to)
				throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			return new ObjectSubList<>(this, from, to);
		}
	}
}

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
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectCollections;
import java.util.Map;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.FastEntrySet;
/**
 * A class providing static methods and objects that do useful things with
 * type-specific maps.
 *
 * @see java.util.Collections
 */
public final class Int2ObjectMaps {
	private Int2ObjectMaps() {
	}
	/**
	 * Returns an iterator that will be {@linkplain FastEntrySet fast}, if possible,
	 * on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map
	 *            a map from which we will try to extract a (fast) iterator on the
	 *            entry set.
	 * @return an iterator on the entry set of the given map that will be fast, if
	 *         possible.
	 * @since 8.0.0
	 */
	@SuppressWarnings("unchecked")
	public static <V> ObjectIterator<Int2ObjectMap.Entry<V>> fastIterator(Int2ObjectMap<V> map) {
		final ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
		return entries instanceof Int2ObjectMap.FastEntrySet
				? ((Int2ObjectMap.FastEntrySet<V>) entries).fastIterator()
				: entries.iterator();
	}
	/**
	 * Iterates {@linkplain FastEntrySet#fastForEach(Consumer) quickly}, if
	 * possible, on the {@linkplain Map#entrySet() entry set} of the provided
	 * {@code map}.
	 * 
	 * @param map
	 *            a map on which we will try to iterate
	 *            {@linkplain FastEntrySet#fastForEach(Consumer) quickly}.
	 * @param consumer
	 *            the consumer that will be passed to
	 *            {@link FastEntrySet#fastForEach(Consumer)}, if possible, or to
	 *            {@link Iterable#forEach(Consumer)}.
	 * @since 8.1.0
	 */
	@SuppressWarnings("unchecked")
	public static <V> void fastForEach(Int2ObjectMap<V> map, final Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
		final ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
		if (entries instanceof Int2ObjectMap.FastEntrySet)
			((Int2ObjectMap.FastEntrySet<V>) entries).fastForEach(consumer);
		else
			entries.forEach(consumer);
	}
	/**
	 * Returns an iterable yielding an iterator that will be
	 * {@linkplain FastEntrySet fast}, if possible, on the
	 * {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map
	 *            a map from which we will try to extract an iterable yielding a
	 *            (fast) iterator on the entry set.
	 * @return an iterable yielding an iterator on the entry set of the given map
	 *         that will be fast, if possible.
	 * @since 8.0.0
	 */
	@SuppressWarnings("unchecked")
	public static <V> ObjectIterable<Int2ObjectMap.Entry<V>> fastIterable(Int2ObjectMap<V> map) {
		final ObjectSet<Int2ObjectMap.Entry<V>> entries = map.int2ObjectEntrySet();
		return entries instanceof Int2ObjectMap.FastEntrySet ? new ObjectIterable<Int2ObjectMap.Entry<V>>() {
			public ObjectIterator<Int2ObjectMap.Entry<V>> iterator() {
				return ((Int2ObjectMap.FastEntrySet<V>) entries).fastIterator();
			}
			public void forEach(final Consumer<? super Int2ObjectMap.Entry<V>> consumer) {
				((Int2ObjectMap.FastEntrySet<V>) entries).fastForEach(consumer);
			}
		} : entries;
	}
	/**
	 * An immutable class representing an empty type-specific map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a
	 * type-specific map.
	 */
	public static class EmptyMap<V> extends Int2ObjectFunctions.EmptyFunction<V>
			implements
				Int2ObjectMap<V>,
				java.io.Serializable,
				Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected EmptyMap() {
		}
		@Override
		public boolean containsValue(final Object v) {
			return false;
		}
		@Override
		public void putAll(final Map<? extends Integer, ? extends V> m) {
			throw new UnsupportedOperationException();
		}
		@SuppressWarnings("unchecked")
		@Override
		public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
			return ObjectSets.EMPTY_SET;
		}

		@Override
		public IntSet keySet() {
			return IntSets.EMPTY_SET;
		}
		@SuppressWarnings("unchecked")
		@Override
		public ObjectCollection<V> values() {
			return ObjectSets.EMPTY_SET;
		}
		@Override
		public Object clone() {
			return EMPTY_MAP;
		}
		@Override
		public boolean isEmpty() {
			return true;
		}
		@Override
		public int hashCode() {
			return 0;
		}
		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Map))
				return false;
			return ((Map<?, ?>) o).isEmpty();
		}
		@Override
		public String toString() {
			return "{}";
		}
	}
	/**
	 * An empty type-specific map (immutable). It is serializable and cloneable.
	 */
	@SuppressWarnings("rawtypes")
	public static final EmptyMap EMPTY_MAP = new EmptyMap();
	/**
	 * Returns an empty map (immutable). It is serializable and cloneable.
	 *
	 * <p>
	 * This method provides a typesafe access to {@link #EMPTY_MAP}.
	 * 
	 * @return an empty map (immutable).
	 */
	@SuppressWarnings("unchecked")
	public static <V> Int2ObjectMap<V> emptyMap() {
		return EMPTY_MAP;
	}
	/**
	 * An immutable class representing a type-specific singleton map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a
	 * type-specific map.
	 */
	public static class Singleton<V> extends Int2ObjectFunctions.Singleton<V>
			implements
				Int2ObjectMap<V>,
				java.io.Serializable,
				Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected transient ObjectSet<Int2ObjectMap.Entry<V>> entries;
		protected transient IntSet keys;
		protected transient ObjectCollection<V> values;
		protected Singleton(final int key, final V value) {
			super(key, value);
		}
		@Override
		public boolean containsValue(final Object v) {
			return java.util.Objects.equals(value, v);
		}
		@Override
		public void putAll(final Map<? extends Integer, ? extends V> m) {
			throw new UnsupportedOperationException();
		}
		@Override
		public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
			if (entries == null)
				entries = ObjectSets.singleton(new AbstractInt2ObjectMap.BasicEntry<>(key, value));
			return entries;
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({"rawtypes", "unchecked"})
		public ObjectSet<Map.Entry<Integer, V>> entrySet() {
			return (ObjectSet) int2ObjectEntrySet();
		}
		@Override
		public IntSet keySet() {
			if (keys == null)
				keys = IntSets.singleton(key);
			return keys;
		}
		@Override
		public ObjectCollection<V> values() {
			if (values == null)
				values = ObjectSets.singleton(value);
			return values;
		}
		@Override
		public boolean isEmpty() {
			return false;
		}
		@Override
		public int hashCode() {
			return (key) ^ ((value) == null ? 0 : (value).hashCode());
		}
		@Override
		public boolean equals(final Object o) {
			if (o == this)
				return true;
			if (!(o instanceof Map))
				return false;
			Map<?, ?> m = (Map<?, ?>) o;
			if (m.size() != 1)
				return false;
			return m.entrySet().iterator().next().equals(entrySet().iterator().next());
		}
		@Override
		public String toString() {
			return "{" + key + "=>" + value + "}";
		}
	}
	/**
	 * Returns a type-specific immutable map containing only the specified pair. The
	 * returned map is serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned map is immutable, its default return value may
	 * be changed.
	 *
	 * @param key
	 *            the only key of the returned map.
	 * @param value
	 *            the only value of the returned map.
	 * @return a type-specific immutable map containing just the pair
	 *         {@code &lt;key,value&gt;}.
	 */
	public static <V> Int2ObjectMap<V> singleton(final int key, V value) {
		return new Singleton<>(key, value);
	}
	/**
	 * Returns a type-specific immutable map containing only the specified pair. The
	 * returned map is serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned map is immutable, its default return value may
	 * be changed.
	 *
	 * @param key
	 *            the only key of the returned map.
	 * @param value
	 *            the only value of the returned map.
	 * @return a type-specific immutable map containing just the pair
	 *         {@code &lt;key,value&gt;}.
	 */
	public static <V> Int2ObjectMap<V> singleton(final Integer key, final V value) {
		return new Singleton<>((key).intValue(), (value));
	}
	/** A synchronized wrapper class for maps. */
	public static class SynchronizedMap<V> extends Int2ObjectFunctions.SynchronizedFunction<V>
			implements
				Int2ObjectMap<V>,
				java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Int2ObjectMap<V> map;
		protected transient ObjectSet<Int2ObjectMap.Entry<V>> entries;
		protected transient IntSet keys;
		protected transient ObjectCollection<V> values;
		protected SynchronizedMap(final Int2ObjectMap<V> m, final Object sync) {
			super(m, sync);
			this.map = m;
		}
		protected SynchronizedMap(final Int2ObjectMap<V> m) {
			super(m);
			this.map = m;
		}
		@Override
		public boolean containsValue(final Object v) {
			synchronized (sync) {
				return map.containsValue(v);
			}
		}
		@Override
		public void putAll(final Map<? extends Integer, ? extends V> m) {
			synchronized (sync) {
				map.putAll(m);
			}
		}
		@Override
		public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
			synchronized (sync) {
				if (entries == null)
					entries = ObjectSets.synchronize(map.int2ObjectEntrySet(), sync);
				return entries;
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({"unchecked", "rawtypes"})
		public ObjectSet<Map.Entry<Integer, V>> entrySet() {
			return (ObjectSet) int2ObjectEntrySet();
		}
		@Override
		public IntSet keySet() {
			synchronized (sync) {
				if (keys == null)
					keys = IntSets.synchronize(map.keySet(), sync);
				return keys;
			}
		}
		@Override
		public ObjectCollection<V> values() {
			synchronized (sync) {
				if (values == null)
					return ObjectCollections.synchronize(map.values(), sync);
				return values;
			}
		}
		@Override
		public boolean isEmpty() {
			synchronized (sync) {
				return map.isEmpty();
			}
		}
		@Override
		public int hashCode() {
			synchronized (sync) {
				return map.hashCode();
			}
		}
		@Override
		public boolean equals(final Object o) {
			if (o == this)
				return true;
			synchronized (sync) {
				return map.equals(o);
			}
		}
		private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
			synchronized (sync) {
				s.defaultWriteObject();
			}
		}
		// Defaultable methods
		@Override
		public V getOrDefault(final int key, final V defaultValue) {
			synchronized (sync) {
				return map.getOrDefault(key, defaultValue);
			}
		}
		@Override
		public void forEach(final java.util.function.BiConsumer<? super Integer, ? super V> action) {
			synchronized (sync) {
				map.forEach(action);
			}
		}
		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> function) {
			synchronized (sync) {
				map.replaceAll(function);
			}
		}
		@Override
		public V putIfAbsent(final int key, final V value) {
			synchronized (sync) {
				return map.putIfAbsent(key, value);
			}
		}
		@Override
		public boolean remove(final int key, final Object value) {
			synchronized (sync) {
				return map.remove(key, value);
			}
		}
		@Override
		public V replace(final int key, final V value) {
			synchronized (sync) {
				return map.replace(key, value);
			}
		}
		@Override
		public boolean replace(final int key, final V oldValue, final V newValue) {
			synchronized (sync) {
				return map.replace(key, oldValue, newValue);
			}
		}
		@Override
		public V computeIfAbsent(final int key, final java.util.function.IntFunction<? extends V> mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}
		@Override
		public V computeIfAbsentPartial(final int key, final Int2ObjectFunction<? extends V> mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsentPartial(key, mappingFunction);
			}
		}
		@Override
		public V computeIfPresent(final int key,
				final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
			synchronized (sync) {
				return map.computeIfPresent(key, remappingFunction);
			}
		}
		@Override
		public V compute(final int key,
				final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
			synchronized (sync) {
				return map.compute(key, remappingFunction);
			}
		}
		@Override
		public V merge(final int key, final V value,
				final java.util.function.BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			synchronized (sync) {
				return map.merge(key, value, remappingFunction);
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V getOrDefault(final Object key, final V defaultValue) {
			synchronized (sync) {
				return map.getOrDefault(key, defaultValue);
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean remove(final Object key, final Object value) {
			synchronized (sync) {
				return map.remove(key, value);
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V replace(final Integer key, final V value) {
			synchronized (sync) {
				return map.replace(key, value);
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean replace(final Integer key, final V oldValue, final V newValue) {
			synchronized (sync) {
				return map.replace(key, oldValue, newValue);
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V putIfAbsent(final Integer key, final V value) {
			synchronized (sync) {
				return map.putIfAbsent(key, value);
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V computeIfAbsent(final Integer key,
				final java.util.function.Function<? super Integer, ? extends V> mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V computeIfPresent(final Integer key,
				final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
			synchronized (sync) {
				return map.computeIfPresent(key, remappingFunction);
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V compute(final Integer key,
				final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
			synchronized (sync) {
				return map.compute(key, remappingFunction);
			}
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V merge(final Integer key, final V value,
				final java.util.function.BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			synchronized (sync) {
				return map.merge(key, value, remappingFunction);
			}
		}
	}
	/**
	 * Returns a synchronized type-specific map backed by the given type-specific
	 * map.
	 *
	 * @param m
	 *            the map to be wrapped in a synchronized map.
	 * @return a synchronized view of the specified map.
	 * @see java.util.Collections#synchronizedMap(Map)
	 */
	public static <V> Int2ObjectMap<V> synchronize(final Int2ObjectMap<V> m) {
		return new SynchronizedMap<>(m);
	}
	/**
	 * Returns a synchronized type-specific map backed by the given type-specific
	 * map, using an assigned object to synchronize.
	 *
	 * @param m
	 *            the map to be wrapped in a synchronized map.
	 * @param sync
	 *            an object that will be used to synchronize the access to the map.
	 * @return a synchronized view of the specified map.
	 * @see java.util.Collections#synchronizedMap(Map)
	 */
	public static <V> Int2ObjectMap<V> synchronize(final Int2ObjectMap<V> m, final Object sync) {
		return new SynchronizedMap<>(m, sync);
	}
	/** An unmodifiable wrapper class for maps. */
	public static class UnmodifiableMap<V> extends Int2ObjectFunctions.UnmodifiableFunction<V>
			implements
				Int2ObjectMap<V>,
				java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Int2ObjectMap<V> map;
		protected transient ObjectSet<Int2ObjectMap.Entry<V>> entries;
		protected transient IntSet keys;
		protected transient ObjectCollection<V> values;
		protected UnmodifiableMap(final Int2ObjectMap<V> m) {
			super(m);
			this.map = m;
		}
		@Override
		public boolean containsValue(final Object v) {
			return map.containsValue(v);
		}
		@Override
		public void putAll(final Map<? extends Integer, ? extends V> m) {
			throw new UnsupportedOperationException();
		}
		@Override
		public ObjectSet<Int2ObjectMap.Entry<V>> int2ObjectEntrySet() {
			if (entries == null)
				entries = ObjectSets.unmodifiable(map.int2ObjectEntrySet());
			return entries;
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({"unchecked", "rawtypes"})
		public ObjectSet<Map.Entry<Integer, V>> entrySet() {
			return (ObjectSet) int2ObjectEntrySet();
		}
		@Override
		public IntSet keySet() {
			if (keys == null)
				keys = IntSets.unmodifiable(map.keySet());
			return keys;
		}
		@Override
		public ObjectCollection<V> values() {
			if (values == null)
				return ObjectCollections.unmodifiable(map.values());
			return values;
		}
		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}
		@Override
		public int hashCode() {
			return map.hashCode();
		}
		@Override
		public boolean equals(final Object o) {
			if (o == this)
				return true;
			return map.equals(o);
		}
		// Defaultable methods
		@Override
		public V getOrDefault(final int key, final V defaultValue) {
			return map.getOrDefault(key, defaultValue);
		}
		@Override
		public void forEach(final java.util.function.BiConsumer<? super Integer, ? super V> action) {
			map.forEach(action);
		}
		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> function) {
			throw new UnsupportedOperationException();
		}
		@Override
		public V putIfAbsent(final int key, final V value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public boolean remove(final int key, final Object value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public V replace(final int key, final V value) {
			throw new UnsupportedOperationException();
		}
		@Override
		public boolean replace(final int key, final V oldValue, final V newValue) {
			throw new UnsupportedOperationException();
		}
		@Override
		public V computeIfAbsent(final int key, final java.util.function.IntFunction<? extends V> mappingFunction) {
			throw new UnsupportedOperationException();
		}
		@Override
		public V computeIfAbsentPartial(final int key, final Int2ObjectFunction<? extends V> mappingFunction) {
			throw new UnsupportedOperationException();
		}
		@Override
		public V computeIfPresent(final int key,
				final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}
		@Override
		public V compute(final int key,
				final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}
		@Override
		public V merge(final int key, final V value,
				final java.util.function.BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V getOrDefault(final Object key, final V defaultValue) {
			return map.getOrDefault(key, defaultValue);
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean remove(final Object key, final Object value) {
			throw new UnsupportedOperationException();
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V replace(final Integer key, final V value) {
			throw new UnsupportedOperationException();
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean replace(final Integer key, final V oldValue, final V newValue) {
			throw new UnsupportedOperationException();
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V putIfAbsent(final Integer key, final V value) {
			throw new UnsupportedOperationException();
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V computeIfAbsent(final Integer key,
				final java.util.function.Function<? super Integer, ? extends V> mappingFunction) {
			throw new UnsupportedOperationException();
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V computeIfPresent(final Integer key,
				final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V compute(final Integer key,
				final java.util.function.BiFunction<? super Integer, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}
		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public V merge(final Integer key, final V value,
				final java.util.function.BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
			throw new UnsupportedOperationException();
		}
	}
	/**
	 * Returns an unmodifiable type-specific map backed by the given type-specific
	 * map.
	 *
	 * @param m
	 *            the map to be wrapped in an unmodifiable map.
	 * @return an unmodifiable view of the specified map.
	 * @see java.util.Collections#unmodifiableMap(Map)
	 */
	public static <V> Int2ObjectMap<V> unmodifiable(final Int2ObjectMap<V> m) {
		return new UnmodifiableMap<>(m);
	}
}

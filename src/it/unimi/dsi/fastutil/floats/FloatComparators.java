/*
	* Copyright (C) 2003-2020 Paolo Boldi and Sebastiano Vigna
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
package it.unimi.dsi.fastutil.floats;
import java.util.Comparator;
/**
 * A class providing static methods and objects that do useful things with
 * comparators.
 */
public final class FloatComparators {
	private FloatComparators() {
	}
	/** A type-specific comparator mimicking the natural order. */
	protected static class NaturalImplicitComparator implements FloatComparator, java.io.Serializable {
		private static final long serialVersionUID = 1L;
		@Override
		public final int compare(final float a, final float b) {
			return (Float.compare((a), (b)));
		}
		@Override
		public FloatComparator reversed() {
			return OPPOSITE_COMPARATOR;
		}
		private Object readResolve() {
			return NATURAL_COMPARATOR;
		}
	};

	public static final FloatComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();
	/** A type-specific comparator mimicking the opposite of the natural order. */
	protected static class OppositeImplicitComparator implements FloatComparator, java.io.Serializable {
		private static final long serialVersionUID = 1L;
		@Override
		public final int compare(final float a, final float b) {
			return -(Float.compare((a), (b)));
		}
		@Override
		public FloatComparator reversed() {
			return NATURAL_COMPARATOR;
		}
		private Object readResolve() {
			return OPPOSITE_COMPARATOR;
		}
	};

	public static final FloatComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();
	protected static class OppositeComparator implements FloatComparator, java.io.Serializable {
		private static final long serialVersionUID = 1L;
		final FloatComparator comparator;
		protected OppositeComparator(final FloatComparator c) {
			comparator = c;
		}
		@Override
		public final int compare(final float a, final float b) {
			return comparator.compare(b, a);
		}
		@Override
		public final FloatComparator reversed() {
			return comparator;
		}
	};
	/**
	 * Returns a comparator representing the opposite order of the given comparator.
	 *
	 * @param c
	 *            a comparator.
	 * @return a comparator representing the opposite order of {@code c}.
	 */
	public static FloatComparator oppositeComparator(final FloatComparator c) {
		if (c instanceof OppositeComparator)
			return ((OppositeComparator) c).comparator;
		return new OppositeComparator(c);
	}
	/**
	 * Returns a type-specific comparator that is equivalent to the given
	 * comparator.
	 *
	 * @param c
	 *            a comparator, or {@code null}.
	 * @return a type-specific comparator representing the order of {@code c}.
	 */
	public static FloatComparator asFloatComparator(final Comparator<? super Float> c) {
		if (c == null || c instanceof FloatComparator)
			return (FloatComparator) c;
		return new FloatComparator() {
			@Override
			public int compare(float x, float y) {
				return c.compare(Float.valueOf(x), Float.valueOf(y));
			}
			@SuppressWarnings("deprecation")
			@Override
			public int compare(Float x, Float y) {
				return c.compare(x, y);
			}
		};
	}
}

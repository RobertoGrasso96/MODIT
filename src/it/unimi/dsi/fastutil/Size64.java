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

package it.unimi.dsi.fastutil;

import java.util.Collection;

/** An interface for data structures whose size can exceed {@link Integer#MAX_VALUE}.
 *
 * <p>The only methods specified by this interfaces are {@link #size64()}, and
 * a deprecated {@link #size()} identical to {@link Collection#size()},
 * but with a default implementation. Implementations
 * can work around the type problem of {@link java.util.Collection#size()}
 * (e.g., not being able to return more than {@link Integer#MAX_VALUE}) by implementing this
 * interface. Callers interested in large structures
 * can use a reflective call to {@code instanceof} to check for the presence of {@link #size64()}.
 */

public interface Size64 {
	/** Returns the size of this data structure as a long.
	 *
	 * @return the size of this data structure.
	 */
	long size64();

	/** Returns the size of this data structure, minimized with {@link Integer#MAX_VALUE}.
	 *
	 * <p>This default implementation follows the definition above, which is compatible
	 * with {@link Collection#size()}.
	 *
	 * @return the size of this data structure, minimized with {@link Integer#MAX_VALUE}.
	 * @see java.util.Collection#size()
	 * @deprecated Use {@link #size64()} instead.
	 */
	@Deprecated
	default int size() {
		return (int)Math.min(Integer.MAX_VALUE, size64());
	}
}

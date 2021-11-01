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
/**
 * A type-specific {@link Iterable} that further strengthens the specification
 * of {@link Iterable#iterator()}.
 */
public interface ObjectBidirectionalIterable<K> extends ObjectIterable<K> {
	/**
	 * Returns a type-specific {@link it.unimi.dsi.fastutil.BidirectionalIterator}.
	 *
	 * @return a type-specific bidirectional iterator.
	 */
	@Override
	ObjectBidirectionalIterator<K> iterator();
}

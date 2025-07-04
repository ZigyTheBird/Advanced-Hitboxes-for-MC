/*
 * MIT License
 *
 * Copyright (c) 2024 GeckoThePecko
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.zigythebird.advanced_hitboxes.geckolib.constant.dataticket;

import java.util.Map;
import java.util.Objects;

/**
 * Ticket object to define a typed data object
 */
public class DataTicket<D> {
	private final String id;
	private final Class<? extends D> objectType;

	public DataTicket(String id, Class<? extends D> objectType) {
		this.id = id;
		this.objectType = objectType;
	}

	public String id() {
		return this.id;
	}

	public Class<? extends D> objectType() {
		return this.objectType;
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id, this.objectType);
	}

	/**
	 * Reverse getter function for consistent operation of ticket data retrieval
	 *
	 * @param dataMap The data map to retrieve the data from
	 * @return The data from the map, or null if the data hasn't been stored
	 */
	public <D> D getData(Map<? extends DataTicket<?>, ?> dataMap) {
		return (D)dataMap.get(this);
	}
}

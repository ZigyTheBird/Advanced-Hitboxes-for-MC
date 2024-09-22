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

package com.zigythebird.advanced_hitboxes.geckolib.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.StringJoiner;

/**
 * Nestable {@link Exception} that allows for a stacktrace to additively append context to a singular exception without needing to re-build the stack trace for each wrapper.
 * <p>
 * This allows for faster stacked exceptions without having errors land sporadically throughout the log as tasks are asynchronously completed.
 */
public class CompoundException extends RuntimeException {
    private final List<String> messages = new ObjectArrayList<>();

    public CompoundException(String message) {
        this.messages.add(message);
    }

    /**
     * Add a message to the stack.
     * The message will be given its own line in the log, displayed <u>before</u> any lines already added
     *
     * @param message The message to add
     * @return this
     */
    public CompoundException withMessage(String message) {
        this.messages.add(message);

        return this;
    }

    @Override
    public String getLocalizedMessage() {
        final StringJoiner joiner = new StringJoiner("\n");
        final int count = this.messages.size() - 1;

        for (int i = count; i >= 0; i--) {
            joiner.add((i == count ? "" : "\t".repeat(Math.max(0, count - i)) + "-> ") + this.messages.get(i));
        }

        return joiner.toString();
    }

    @Override
    public String toString() {
        final String name = "Geckolib.CompoundException";
        final String message = getLocalizedMessage();

        return message != null ? name + ": " + message : name;
    }
}

/*
 * This file is a part of the Chameleon Framework, licensed under the MIT License.
 *
 * Copyright (c) 2021-2022 The Chameleon Framework Authors.
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
package dev.hypera.chameleon.command.objects;

import dev.hypera.chameleon.command.Command;
import dev.hypera.chameleon.command.context.Context;
import java.util.Optional;
import java.util.function.Predicate;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus.NonExtendable;
import org.jetbrains.annotations.NotNull;

/**
 * {@link Command} condition.
 */
@NonExtendable
public interface Condition {

    /**
     * Condition test.
     *
     * @param context {@link Command} execution context.
     *
     * @return {@code true} if condition passed, otherwise {@code false}.
     */
    boolean test(@NotNull Context context);

    /**
     * Get error message.
     *
     * @return error message if available, otherwise {@code null}.
     */
    default @NotNull Optional<Component> getErrorMessage() {
        return Optional.empty();
    }


    /**
     * Create new {@link Condition}.
     *
     * @param test Command condition test.
     *
     * @return New {@link Condition} instance.
     */
    static @NotNull Condition of(@NotNull Predicate<Context> test) {
        return test::test;
    }

    /**
     * Create new {@link Condition} with an error message.
     *
     * @param test         Command condition test.
     * @param errorMessage Error message {@link Component}.
     *
     * @return New {@link Condition} instance.
     */
    static @NotNull Condition of(@NotNull Predicate<Context> test, @NotNull Component errorMessage) {
        return new Condition() {

            @Override
            public boolean test(@NotNull Context context) {
                return test.test(context);
            }

            @Override
            public @NotNull Optional<Component> getErrorMessage() {
                return Optional.of(errorMessage);
            }

        };
    }

}

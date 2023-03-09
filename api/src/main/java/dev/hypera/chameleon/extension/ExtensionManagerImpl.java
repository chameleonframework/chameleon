/*
 * This file is a part of the Chameleon Framework, licensed under the MIT License.
 *
 * Copyright (c) 2021-2023 The Chameleon Framework Authors.
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
package dev.hypera.chameleon.extension;

import dev.hypera.chameleon.Chameleon;
import dev.hypera.chameleon.exception.extension.ChameleonExtensionException;
import dev.hypera.chameleon.util.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;

/**
 * Extension manager implementation.
 *
 * @see ExtensionManager
 */
@Internal
public final class ExtensionManagerImpl implements ExtensionManager {

    private final @NotNull Chameleon chameleon;
    private final @NotNull Collection<? super ChameleonExtension<?>> loadedExtensions;

    /**
     * Extension manager constructor.
     *
     * @param chameleon  Chameleon instance.
     * @param loadedExtensions Extensions.
     */
    @Internal
    public ExtensionManagerImpl(@NotNull Chameleon chameleon, @NotNull Collection<? super ChameleonExtension<?>> loadedExtensions) {
        this.chameleon = chameleon;
        this.loadedExtensions = loadedExtensions;
    }

    /**
     * Load a Chameleon extension.
     *
     * @param factory The factory to create the Chameleon extension.
     * @param <T>     Chameleon extension type.
     *
     * @return new Chameleon extension.
     * @throws ChameleonExtensionException if something goes wrong while loading the extension.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <P, T extends ChameleonExtension<P>> @NotNull P loadExtension(@NotNull ChameleonExtensionFactory<T> factory) throws ChameleonExtensionException {
        T extension = factory.create(this.chameleon.getPlatform());
        Preconditions.checkNotNullState("extension", extension);

        // Check if this extension has already been loaded, if so, return the loaded instance.
        return getExtension((Class<T>) extension.getClass()).orElseGet(() -> {
            // If not, load the extension.
            extension.init(this.chameleon.getLogger(), this.chameleon.getEventBus());
            extension.load(this.chameleon);
            this.loadedExtensions.add(extension);
            return extension.getPlatform();
        });
    }

    /**
     * Get a loaded Chameleon extension.
     *
     * @param clazz Chameleon extension class.
     * @param <T>   Chameleon extension type.
     *
     * @return an optional containing the loaded Chameleon extension, if loaded, otherwise an empty
     *     optional.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <P, T extends ChameleonExtension<P>> @NotNull Optional<P> getExtension(@NotNull Class<T> clazz) {
        return this.loadedExtensions.parallelStream().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> ((T) e).getPlatform()).findFirst();
    }

    /**
     * Get all loaded Chameleon extensions.
     *
     * @return loaded Chameleon extensions.
     */
    @Override
    @SuppressWarnings("unchecked")
    public @NotNull Collection<ChameleonExtension<?>> getExtensions() {
        return (Collection<ChameleonExtension<?>>) Collections.unmodifiableCollection(this.loadedExtensions);
    }

}

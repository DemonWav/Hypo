/*
 * Hypo, an extensible and pluggable Java bytecode analytical model.
 *
 * Copyright (C) 2023  Kyle Wood (DenWav)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License only.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.denwav.hypo.types;

import org.jetbrains.annotations.NotNull;

/**
 * A type which can be bound to a {@link dev.denwav.hypo.types.sig.param.TypeParameter TypeParameter}, or may contain
 * nested objects which themselves may satisfy that scenario. The intended pattern is for objects which fall in the
 * latter category to simply recursively call this method on all matching nested values.
 *
 * <p>Type variable binding only applies to {@link dev.denwav.hypo.types.sig.TypeSignature TypeSignature}-based types.
 */
public interface TypeBindable {

    /**
     * Return a new instance of {@code this} (possibly as a different type) where all unbound type variables have been
     * bound to their associated type parameters using the provided {@code binder}. All valid type definitions must be
     * able to be bound, so failure to find a valid type parameter definition for an unbound type variable will result
     * in an {@link IllegalStateException}.
     *
     * @param binder The {@link TypeVariableBinder} to bind with.
     * @return A new instance of {@code this} (possibly as a different type) where all unbound type variables have been
     *         bound to their associated type parameters
     * @throws IllegalStateException If a type variable could not be bound to an appropriate type parameter.
     */
    @NotNull TypeBindable bind(final @NotNull TypeVariableBinder binder);
}

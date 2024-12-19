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
 * A value which represents a type in Java. "types" include 5 major categories:
 * <ul>
 *     <li>{@link dev.denwav.hypo.types.desc.TypeDescriptor TypeDescriptor}</li>
 *     <li>{@link dev.denwav.hypo.types.desc.MethodDescriptor MethodDescriptor}</li>
 *     <li>{@link dev.denwav.hypo.types.sig.TypeSignature TypeSignature}</li>
 *     <li>{@link dev.denwav.hypo.types.sig.MethodSignature MethodSignature}</li>
 *     <li>{@link dev.denwav.hypo.types.sig.ClassSignature ClassSignature}</li>
 * </ul>
 *
 * <p>All types can be represented in two different ways, {@link #asReadable()}, and {@link #asInternal()}. Generally
 * "readable" will be in source code format, as would appear in a Java source file. Internal format matches exactly what
 * is present in compiled Java bytecode for the given type.
 *
 * <p>Implementations of this interface should have their {@link Object#toString() toString()} method defer to
 * {@link #asReadable()} to assist with debugging. {@link #asInternal()} should be used for serialization, as it matches
 * 1:1 with corresponding {@code parse()} methods for each type.
 */
public interface TypeRepresentable {

    /**
     * Print this type name as a human-readable name to the given {@link StringBuilder}.
     *
     * @param sb The {@link StringBuilder} to print this type's human-readable name to.
     */
    void asReadable(final @NotNull StringBuilder sb);

    /**
     * Print this type name as an internal JVM name to the given {@link StringBuilder}.
     *
     * @param sb The {@link StringBuilder} to print this type's internal JVM name to.
     */
    void asInternal(final @NotNull StringBuilder sb);

    /**
     * Returns this type's human-readable name.
     * @return This type's human-readable name.
     */
    default @NotNull String asReadable() {
        final StringBuilder sb = new StringBuilder();
        this.asReadable(sb);
        return sb.toString();
    }

    /**
     * Returns this type's internal JVM name.
     * @return This type's internal JVM name.
     */
    default @NotNull String asInternal() {
        final StringBuilder sb = new StringBuilder();
        this.asInternal(sb);
        return sb.toString();
    }
}

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

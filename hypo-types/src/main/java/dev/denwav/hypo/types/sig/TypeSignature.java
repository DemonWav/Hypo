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

package dev.denwav.hypo.types.sig;

import dev.denwav.hypo.types.TypeBindable;
import dev.denwav.hypo.types.TypeRepresentable;
import dev.denwav.hypo.types.TypeVariableBinder;
import dev.denwav.hypo.types.desc.TypeDescriptor;
import dev.denwav.hypo.types.parsing.JvmTypeParser;
import org.jetbrains.annotations.NotNull;

public interface TypeSignature extends TypeBindable, TypeRepresentable {

    @NotNull TypeDescriptor asDescriptor();

    static @NotNull TypeSignature parse(final @NotNull String text) {
        return parse(text, 0);
    }
    static @NotNull TypeSignature parse(final @NotNull String text, final int from) {
        return JvmTypeParser.parseTypeSignature(text, from);
    }

    @Override
    @NotNull TypeSignature bind(final @NotNull TypeVariableBinder binder);
}

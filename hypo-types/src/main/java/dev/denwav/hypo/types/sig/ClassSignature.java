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

import com.google.errorprone.annotations.Immutable;
import dev.denwav.hypo.types.Intern;
import dev.denwav.hypo.types.TypeRepresentable;
import dev.denwav.hypo.types.TypeVariableBinder;
import dev.denwav.hypo.types.parsing.JvmTypeParseFailureException;
import dev.denwav.hypo.types.parsing.JvmTypeParser;
import dev.denwav.hypo.types.sig.param.TypeParameter;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

@Immutable
public final class ClassSignature extends Intern<ClassSignature> implements TypeRepresentable {

    @SuppressWarnings("Immutable")
    private final @NotNull List<? extends TypeParameter> typeParameters;
    private final @NotNull ClassTypeSignature superClass;
    @SuppressWarnings("Immutable")
    private final @NotNull List<? extends ClassTypeSignature> superInterfaces;

    public static @NotNull ClassSignature of(
        final @NotNull List<? extends TypeParameter> typeParameters,
        final @NotNull ClassTypeSignature superClass,
        final @NotNull List<? extends ClassTypeSignature> superInterfaces
    ) {
        return new ClassSignature(typeParameters, superClass, superInterfaces).intern();
    }

    public static @NotNull ClassSignature parse(final String text) throws JvmTypeParseFailureException {
        return parse(text, 0);
    }
    public static @NotNull ClassSignature parse(final String text, final int from) throws JvmTypeParseFailureException {
        return JvmTypeParser.parseClassSignature(text, from);
    }

    private ClassSignature(
        final @NotNull List<? extends TypeParameter> typeParameters,
        final @NotNull ClassTypeSignature superClass,
        final @NotNull List<? extends ClassTypeSignature> superInterfaces
    ) {
        this.typeParameters = List.copyOf(typeParameters);
        this.superClass = superClass;
        this.superInterfaces = List.copyOf(superInterfaces);
    }

    @Override
    public void asReadable(final @NotNull StringBuilder sb) {
        if (!this.typeParameters.isEmpty()) {
            sb.append('<');
            for (int i = 0; i < this.typeParameters.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                this.typeParameters.get(i).asReadable(sb);
            }
            sb.append("> ");
        }

        sb.append("extends ");
        this.superClass.asReadable(sb);

        if (!this.superInterfaces.isEmpty()) {
            sb.append(" implements ");
        }
        for (int i = 0; i < this.superInterfaces.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            this.superInterfaces.get(i).asReadable(sb);
        }
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        if (!this.typeParameters.isEmpty()) {
            sb.append('<');
            for (final TypeParameter param : this.typeParameters) {
                param.asInternal(sb);
            }
            sb.append('>');
        }
        this.superClass.asInternal(sb);
        for (final TypeSignature superInt : this.superInterfaces) {
            superInt.asInternal(sb);
        }
    }

    public @NotNull ClassSignature bind(final @NotNull TypeVariableBinder binder) {
        return ClassSignature.of(
            this.typeParameters.stream()
                .map(t -> t.bind(binder))
                .collect(Collectors.toList()),
            this.superClass.bind(binder),
            this.superInterfaces.stream()
                .map(t -> t.bind(binder))
                .collect(Collectors.toList())
        );
    }

    public @NotNull List<? extends TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }

    public @NotNull ClassTypeSignature getSuperClass() {
        return this.superClass;
    }

    public @NotNull List<? extends ClassTypeSignature> getSuperInterfaces() {
        return this.superInterfaces;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ClassSignature)) {
            return false;
        }
        final ClassSignature that = (ClassSignature) o;
        return Objects.equals(this.typeParameters, that.typeParameters)
            && Objects.equals(this.superClass, that.superClass)
            && Objects.equals(this.superInterfaces, that.superInterfaces);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.typeParameters, this.superClass, this.superInterfaces);
    }

    @Override
    public String toString() {
        return this.asReadable();
    }
}

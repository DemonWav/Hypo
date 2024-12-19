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

package dev.denwav.hypo.types.sig.param;

import com.google.errorprone.annotations.Immutable;
import dev.denwav.hypo.types.Intern;
import dev.denwav.hypo.types.TypeRepresentable;
import dev.denwav.hypo.types.TypeVariableBinder;
import dev.denwav.hypo.types.sig.ClassTypeSignature;
import dev.denwav.hypo.types.sig.ReferenceTypeSignature;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Immutable
public final class TypeParameter extends Intern<TypeParameter> implements TypeRepresentable {

    private final @NotNull String name;
    private final @Nullable ReferenceTypeSignature classBound;
    @SuppressWarnings("Immutable")
    private final @NotNull List<? extends ReferenceTypeSignature> interfaceBounds;

    public static @NotNull TypeParameter of(
        final @NotNull String name,
        final @Nullable ReferenceTypeSignature classBound,
        final @NotNull List<? extends ReferenceTypeSignature> interfaceBounds
    ) {
        return new TypeParameter(name, classBound, interfaceBounds).intern();
    }

    public static @NotNull TypeParameter of(
        final @NotNull String name,
        final @NotNull ReferenceTypeSignature classBound
    ) {
        return new TypeParameter(name, classBound, Collections.emptyList()).intern();
    }

    public static @NotNull TypeParameter of(
        final @NotNull String name
    ) {
        return new TypeParameter(name, ClassTypeSignature.of("java/lang/Object"), Collections.emptyList()).intern();
    }

    private TypeParameter(
        final @NotNull String name,
        final @Nullable ReferenceTypeSignature classBound,
        final @NotNull List<? extends ReferenceTypeSignature> interfaceBounds
    ) {
        this.name = name;
        this.classBound = classBound;
        this.interfaceBounds = List.copyOf(interfaceBounds);

        if (classBound == null && this.interfaceBounds.isEmpty()) {
            throw new IllegalArgumentException(
                "Cannot construct a type with empty classBound and interfaceBounds, at least one must be set."
            );
        }
    }

    @Override
    public void asReadable(final @NotNull StringBuilder sb) {
        sb.append(this.name);

        if (this.classBound != null || !this.interfaceBounds.isEmpty()) {
            sb.append(" extends ");
        }

        if (this.classBound != null) {
            this.classBound.asReadable(sb);
        }

        for (int i = 0; i < this.interfaceBounds.size(); i++) {
            if (i > 0 || this.classBound != null) {
                sb.append(" & ");
            }
            this.interfaceBounds.get(i).asReadable(sb);
        }
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        sb.append(this.name);

        sb.append(':');
        if (this.classBound != null) {
            this.classBound.asInternal(sb);
        }

        for (final ReferenceTypeSignature interfaceBound : this.interfaceBounds) {
            sb.append(':');
            interfaceBound.asInternal(sb);
        }
    }

    public TypeParameter bind(final @NotNull TypeVariableBinder binder) {
        return TypeParameter.of(
            this.name,
            binder.bind(this.classBound),
            this.interfaceBounds.stream().map(binder::bind).collect(Collectors.toList())
        );
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @Nullable ReferenceTypeSignature getClassBound() {
        return this.classBound;
    }

    public @NotNull List<? extends ReferenceTypeSignature> getInterfaceBounds() {
        return this.interfaceBounds;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof TypeParameter)) {
            return false;
        }
        final TypeParameter that = (TypeParameter) o;
        return Objects.equals(this.name, that.name)
            && Objects.equals(this.classBound, that.classBound)
            && Objects.equals(this.interfaceBounds, that.interfaceBounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name, this.classBound, this.interfaceBounds);
    }

    @Override
    public String toString() {
        return this.asReadable();
    }
}

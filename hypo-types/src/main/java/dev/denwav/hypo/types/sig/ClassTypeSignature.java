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

import dev.denwav.hypo.types.Intern;
import dev.denwav.hypo.types.TypeVariableBinder;
import dev.denwav.hypo.types.desc.ClassTypeDescriptor;
import dev.denwav.hypo.types.desc.TypeDescriptor;
import dev.denwav.hypo.types.sig.param.TypeArgument;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClassTypeSignature
    extends Intern<ClassTypeSignature>
    implements ReferenceTypeSignature, ThrowsSignature {

    private final @Nullable ClassTypeSignature parentClass;
    private final @NotNull String name;
    private final @NotNull List<? extends TypeArgument> typeArguments;

    public static @NotNull ClassTypeSignature of(
        final @NotNull String name
    ) {
        return ClassTypeSignature.of(null, name, null);
    }

    public static @NotNull ClassTypeSignature of(
        final @NotNull String name,
        final @Nullable List<? extends TypeArgument> typeArguments
    ) {
        return ClassTypeSignature.of(null, name, typeArguments);
    }

    public static @NotNull ClassTypeSignature of(
        final @Nullable ClassTypeSignature parentClass,
        final @NotNull String name,
        final @Nullable List<? extends TypeArgument> typeArguments
    ) {
        return new ClassTypeSignature(parentClass, name, typeArguments).intern();
    }

    private ClassTypeSignature(
        final @Nullable ClassTypeSignature parentClass,
        final @NotNull String name,
        final @Nullable List<? extends TypeArgument> typeArguments
    ) {
        this.parentClass = parentClass;
        this.name = name;
        if (typeArguments == null) {
            this.typeArguments = Collections.emptyList();
        } else {
            this.typeArguments = List.copyOf(typeArguments);
        }
    }

    @Override
    public void asReadable(final @NotNull StringBuilder sb) {
        if (this.parentClass != null) {
            this.parentClass.asReadable(sb);
            sb.append('.');
        }

        sb.append(this.name);
        if (!this.typeArguments.isEmpty()) {
            sb.append("<");
            for (int i = 0; i < this.typeArguments.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                this.typeArguments.get(i).asReadable(sb);
            }
            sb.append('>');
        }
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        if (this.parentClass != null) {
            this.parentClass.asInternal(sb);
            // Parent class will include a `;` at the end
            sb.setLength(sb.length() - 1);
            sb.append('.');
        } else {
            sb.append('L');
        }

        sb.append(this.name);
        if (!this.typeArguments.isEmpty()) {
            sb.append("<");
            for (final TypeArgument typeArg : this.typeArguments) {
                typeArg.asInternal(sb);
            }
            sb.append('>');
        }

        sb.append(';');
    }

    @Override
    public @NotNull TypeDescriptor asDescriptor() {
        return ClassTypeDescriptor.of(this.name);
    }

    @Override
    public @NotNull ClassTypeSignature bind(final @NotNull TypeVariableBinder binder) {
        ClassTypeSignature newParent;
        if (this.parentClass == null) {
            newParent = null;
        } else {
            newParent = this.parentClass.bind(binder);
        }
        return ClassTypeSignature.of(
            newParent,
            this.name,
            this.typeArguments.stream()
                .map(t -> (TypeArgument) t.bind(binder))
                .collect(Collectors.toList())
        );
    }

    public @Nullable ClassTypeSignature getParentClass() {
        return this.parentClass;
    }

    public @NotNull String getName() {
        return this.name;
    }

    public @NotNull List<? extends TypeArgument> getTypeArguments() {
        return this.typeArguments;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ClassTypeSignature)) {
            return false;
        }
        final ClassTypeSignature that = (ClassTypeSignature) o;
        return Objects.equals(this.parentClass, that.parentClass)
            && Objects.equals(this.name, that.name)
            && Objects.equals(this.typeArguments, that.typeArguments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.parentClass, this.name, this.typeArguments);
    }

    @Override
    public String toString() {
        return this.asReadable();
    }
}

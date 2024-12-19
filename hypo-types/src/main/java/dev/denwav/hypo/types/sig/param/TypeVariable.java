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
import dev.denwav.hypo.types.desc.ClassTypeDescriptor;
import dev.denwav.hypo.types.desc.TypeDescriptor;
import dev.denwav.hypo.types.sig.ReferenceTypeSignature;
import dev.denwav.hypo.types.sig.ThrowsSignature;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

@Immutable
public final class TypeVariable
    extends Intern<TypeVariable>
    implements ReferenceTypeSignature, TypeRepresentable, ThrowsSignature {

    private final @NotNull TypeParameter definition;

    public static TypeVariable of(final @NotNull TypeParameter definition) {
        return new TypeVariable(definition).intern();
    }

    public static Unbound unbound(final @NotNull String name) {
        return new TypeVariable.Unbound(name).intern();
    }

    private TypeVariable(final @NotNull TypeParameter definition) {
        this.definition = definition;
    }

    @Override
    public void asReadable(final @NotNull StringBuilder sb) {
        sb.append(this.definition.getName());
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        sb.append('T');
        sb.append(this.definition.getName());
        sb.append(';');
    }

    @Override
    public @NotNull TypeDescriptor asDescriptor() {
        // TODO
        {
            final ReferenceTypeSignature bound = this.definition.getClassBound();
            if (bound != null) {
                return bound.asDescriptor();
            }
        }
        for (final ReferenceTypeSignature bound : this.definition.getInterfaceBounds()) {
            return bound.asDescriptor();
        }
        return ClassTypeDescriptor.of("java/lang/Object");
    }

    @Override
    public @NotNull TypeVariable bind(final @NotNull TypeVariableBinder binder) {
        return this;
    }

    public @NotNull TypeParameter getDefinition() {
        return this.definition;
    }

    public @NotNull String getName() {
        return this.definition.getName();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof TypeVariable)) {
            return false;
        }
        final TypeVariable that = (TypeVariable) o;
        return Objects.equals(this.definition, that.definition);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.definition);
    }

    @Override
    public String toString() {
        return this.asReadable();
    }

    public static final class Unbound
        extends Intern<Unbound>
        implements ReferenceTypeSignature, TypeRepresentable, ThrowsSignature {

        private final @NotNull String name;

        public Unbound(final @NotNull String name) {
            this.name = name;
        }

        public @NotNull String getName() {
            return this.name;
        }

        @Override
        public @NotNull TypeVariable bind(final @NotNull TypeVariableBinder binder) {
            final TypeParameter param = binder.bindingFor(this.name);
            if (param == null) {
                throw new IllegalStateException("TypeParameter not found for name: " + this.name);
            }
            return TypeVariable.of(param);
        }

        @Override
        public void asReadable(final @NotNull StringBuilder sb) {
            sb.append(this.name);
        }

        @Override
        public void asInternal(final @NotNull StringBuilder sb) {
            sb.append('T');
            sb.append(this.name);
            sb.append(';');
        }

        @Override
        public @NotNull TypeDescriptor asDescriptor() {
            throw new IllegalStateException(
                "TypeVariable must be bounded by calling bind() in order to create a TypeDescriptor"
            );
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof Unbound)) {
                return false;
            }
            final Unbound that = (Unbound) o;
            return Objects.equals(this.name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.name);
        }

        @Override
        public String toString() {
            return this.asReadable();
        }
    }
}

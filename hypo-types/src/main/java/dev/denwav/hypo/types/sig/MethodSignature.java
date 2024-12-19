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
import dev.denwav.hypo.types.TypeRepresentable;
import dev.denwav.hypo.types.TypeVariableBinder;
import dev.denwav.hypo.types.desc.MethodDescriptor;
import dev.denwav.hypo.types.desc.TypeDescriptor;
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

public final class MethodSignature extends Intern<MethodSignature> implements TypeRepresentable {

    private final @NotNull List<? extends TypeParameter> typeParameters;
    private final @NotNull List<? extends TypeSignature> parameters;
    private final @NotNull TypeSignature returnType;
    private final @NotNull List<? extends ThrowsSignature> throwsSignatures;

    public static @NotNull MethodSignature of(
        final @NotNull List<? extends TypeParameter> typeParameters,
        final @NotNull List<? extends TypeSignature> parameters,
        final @NotNull TypeSignature returnType,
        final @NotNull List<? extends ThrowsSignature> throwsSignatures
    ) {
        return new MethodSignature(typeParameters, parameters, returnType, throwsSignatures).intern();
    }

    private MethodSignature(
        final @NotNull List<? extends TypeParameter> typeParameters,
        final @NotNull List<? extends TypeSignature> parameters,
        final @NotNull TypeSignature returnType,
        final @NotNull List<? extends ThrowsSignature> throwsSignatures
    ) {
        this.typeParameters = List.copyOf(typeParameters);
        this.parameters = List.copyOf(parameters);
        this.returnType = returnType;
        this.throwsSignatures = List.copyOf(throwsSignatures);
    }

    public static @NotNull MethodSignature parse(final @NotNull String text) throws JvmTypeParseFailureException {
        return parse(text, 0);
    }
    public static @NotNull MethodSignature parse(final @NotNull String text, final int from) throws JvmTypeParseFailureException {
        if (text.length() > 1 && from == 0) {
            final MethodSignature r = Intern.tryFind(MethodSignature.class, text);
            if (r != null) {
                return r;
            }
        }
        return JvmTypeParser.parseMethodSignature(text, from);
    }

    public @NotNull MethodSignature bind(final TypeVariableBinder binder) {
        final List<TypeParameter> newTypeParams = this.typeParameters.stream()
            .map(t -> t.bind(binder))
            .collect(Collectors.toList());

        final List<TypeSignature> newParams = this.parameters.stream()
            .map(t -> t.bind(binder))
            .collect(Collectors.toList());

        final TypeSignature newReturnType = this.returnType.bind(binder);

        final List<ThrowsSignature> newThrows = this.throwsSignatures.stream()
            .map(t -> t.bind(binder))
            .collect(Collectors.toList());

        return MethodSignature.of(newTypeParams, newParams, newReturnType, newThrows);
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

        this.returnType.asReadable(sb);
        sb.append(" (");

        for (int i = 0; i < this.parameters.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            this.parameters.get(i).asReadable(sb);
        }

        sb.append(')');

        if (!this.throwsSignatures.isEmpty()) {
            sb.append(" throws ");

            for (int i = 0; i < this.throwsSignatures.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                this.throwsSignatures.get(i).asReadable(sb);
            }
        }
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        if (!this.typeParameters.isEmpty()) {
            sb.append('<');
            for (final TypeParameter typeParam : this.typeParameters) {
                typeParam.asInternal(sb);
            }
            sb.append('>');
        }

        sb.append('(');
        for (final TypeSignature param : this.parameters) {
            param.asInternal(sb);
        }
        sb.append(')');

        this.returnType.asInternal(sb);

        for (final ThrowsSignature typeSig : this.throwsSignatures) {
            sb.append('^');
            typeSig.asInternal(sb);
        }
    }

    public @NotNull MethodDescriptor asDescriptor() {
        final List<TypeDescriptor> descParams = this.parameters.stream()
            .map(TypeSignature::asDescriptor)
            .collect(Collectors.toList());
        return MethodDescriptor.of(descParams, this.returnType.asDescriptor());
    }

    public @NotNull List<? extends TypeParameter> getTypeParameters() {
        return this.typeParameters;
    }

    public @NotNull List<? extends TypeSignature> getParameters() {
        return this.parameters;
    }

    public @NotNull TypeSignature getReturnType() {
        return this.returnType;
    }

    public @NotNull List<? extends ThrowsSignature> getThrowsSignatures() {
        return this.throwsSignatures;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MethodSignature)) {
            return false;
        }
        final MethodSignature that = (MethodSignature) o;
        return Objects.equals(this.typeParameters, that.typeParameters)
            && Objects.equals(this.parameters, that.parameters)
            && Objects.equals(this.returnType, that.returnType)
            && Objects.equals(this.throwsSignatures, that.throwsSignatures);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.typeParameters, this.parameters, this.returnType, this.throwsSignatures);
    }

    @Override
    public String toString() {
        return this.asReadable();
    }
}

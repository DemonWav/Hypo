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

package dev.denwav.hypo.types.desc;

import dev.denwav.hypo.types.Intern;
import dev.denwav.hypo.types.TypeRepresentable;
import dev.denwav.hypo.types.parsing.JvmTypeParser;
import dev.denwav.hypo.types.sig.MethodSignature;
import dev.denwav.hypo.types.sig.TypeSignature;
import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public final class MethodDescriptor extends Intern<MethodDescriptor> implements TypeRepresentable {

    private static final WeakHashMap<MethodDescriptor, WeakReference<MethodDescriptor>> internment =
        new WeakHashMap<>();

    final @NotNull List<? extends TypeDescriptor> parameters;
    final @NotNull TypeDescriptor returnType;

    public static @NotNull MethodDescriptor of(
        final @NotNull List<? extends TypeDescriptor> parameters,
        final @NotNull TypeDescriptor returnType
    ) {
        return new MethodDescriptor(parameters, returnType).intern();
    }

    public static @NotNull MethodDescriptor parse(final String text) {
        return parse(text, 0);
    }
    public static @NotNull MethodDescriptor parse(final String text, final int from) {
        return JvmTypeParser.parseMethodDescriptor(text, from);
    }

    private MethodDescriptor(
        final @NotNull List<? extends TypeDescriptor> parameters,
        final @NotNull TypeDescriptor returnType
    ) {
        super(internment);
        this.parameters = List.copyOf(parameters);
        this.returnType = returnType;
    }

    @Override
    public void asReadable(final @NotNull StringBuilder sb) {
        this.returnType.asReadable(sb);
        sb.append(" (");
        for (int i = 0; i < this.parameters.size(); i++) {
            this.parameters.get(i).asReadable(sb);
            if (i < this.parameters.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(')');
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        sb.append('(');
        for (final TypeDescriptor param : this.parameters) {
            param.asInternal(sb);
        }
        sb.append(')');
        this.returnType.asInternal(sb);
    }

    public @NotNull MethodSignature asSignature() {
        final List<TypeSignature> sigParams = this.parameters.stream()
            .map(TypeDescriptor::asSignature)
            .collect(Collectors.toList());
        return MethodSignature.of(
            Collections.emptyList(),
            sigParams,
            this.returnType.asSignature(),
            Collections.emptyList()
        );
    }

    public @NotNull List<? extends TypeDescriptor> getParameters() {
        return this.parameters;
    }

    public @NotNull TypeDescriptor getReturnType() {
        return this.returnType;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MethodDescriptor)) {
            return false;
        }
        final MethodDescriptor that = (MethodDescriptor) o;
        return Objects.equals(this.parameters, that.parameters)
            && Objects.equals(this.returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.parameters, this.returnType);
    }

    @Override
    public String toString() {
        return this.asReadable();
    }
}

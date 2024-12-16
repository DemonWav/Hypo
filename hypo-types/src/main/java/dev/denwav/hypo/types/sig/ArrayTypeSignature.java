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
import dev.denwav.hypo.types.desc.ArrayTypeDescriptor;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.WeakHashMap;
import org.jetbrains.annotations.NotNull;

public final class ArrayTypeSignature
    extends Intern<ArrayTypeSignature>
    implements ReferenceTypeSignature {

    private static final WeakHashMap<ArrayTypeSignature, WeakReference<ArrayTypeSignature>> internment =
        new WeakHashMap<>();

    private final int dimension;
    private final @NotNull TypeSignature baseType;

    public static @NotNull ArrayTypeSignature of(final int dimension, final @NotNull TypeSignature baseType) {
        return new ArrayTypeSignature(dimension, baseType).intern();
    }

    private ArrayTypeSignature(final int dimension, final @NotNull TypeSignature baseType) {
        super(internment);
        this.dimension = dimension;
        this.baseType = baseType;
    }

    @Override
    public void asReadable(final @NotNull StringBuilder sb) {
        this.baseType.asReadable(sb);
        sb.ensureCapacity(sb.length() + 2 * this.dimension);
        //noinspection StringRepeatCanBeUsed
        for (int i = 0; i < this.dimension; i++) {
            sb.append("[]");
        }
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        sb.ensureCapacity(sb.length() + this.dimension);
        //noinspection StringRepeatCanBeUsed
        for (int i = 0; i < this.dimension; i++) {
            sb.append("[");
        }
        this.baseType.asInternal(sb);
    }

    @Override
    public @NotNull ArrayTypeDescriptor asDescriptor() {
        return ArrayTypeDescriptor.of(this.dimension, this.baseType.asDescriptor());
    }

    @Override
    public @NotNull ArrayTypeSignature bind(final @NotNull TypeVariableBinder binder) {
        return ArrayTypeSignature.of(this.dimension, this.baseType.bind(binder));
    }

    public int getDimension() {
        return this.dimension;
    }

    public @NotNull TypeSignature getBaseType() {
        return this.baseType;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ArrayTypeSignature)) {
            return false;
        }
        final ArrayTypeSignature that = (ArrayTypeSignature) o;
        return this.dimension == that.dimension
            && Objects.equals(this.baseType, that.baseType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.dimension, this.baseType);
    }

    @Override
    public String toString() {
        return this.asReadable();
    }
}

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
import dev.denwav.hypo.types.sig.ArrayTypeSignature;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public final class ArrayTypeDescriptor extends Intern<ArrayTypeDescriptor> implements TypeDescriptor {

    private final int dimension;
    private final @NotNull TypeDescriptor baseType;

    public static @NotNull ArrayTypeDescriptor of(int dimension, @NotNull TypeDescriptor baseType) {
        return new ArrayTypeDescriptor(dimension, baseType).intern();
    }

    private ArrayTypeDescriptor(
        final int dimension,
        final @NotNull TypeDescriptor baseType
    ) {
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
            sb.append('[');
        }
        this.baseType.asInternal(sb);
    }

    @Override
    public @NotNull ArrayTypeSignature asSignature() {
        return ArrayTypeSignature.of(this.dimension, this.baseType.asSignature());
    }

    public int getDimension() {
        return this.dimension;
    }

    public @NotNull TypeDescriptor getBaseType() {
        return this.baseType;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ArrayTypeDescriptor)) {
            return false;
        }
        final ArrayTypeDescriptor arrayTypeDescriptor = (ArrayTypeDescriptor) o;
        return this.dimension == arrayTypeDescriptor.dimension
            && Objects.equals(this.baseType, arrayTypeDescriptor.baseType);
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

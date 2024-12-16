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

import dev.denwav.hypo.types.Intern;
import dev.denwav.hypo.types.TypeRepresentable;
import dev.denwav.hypo.types.TypeVariableBinder;
import dev.denwav.hypo.types.sig.TypeSignature;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

public final class BoundedTypeArgument extends Intern<BoundedTypeArgument> implements TypeArgument, TypeRepresentable {

    private final @NotNull WildcardBound bounds;
    private final @NotNull TypeSignature signature;

    public static @NotNull BoundedTypeArgument of(
        final @NotNull WildcardBound bounds,
        final @NotNull TypeSignature signature
    ) {
        return new BoundedTypeArgument(bounds, signature).intern();
    }

    private BoundedTypeArgument(
        final @NotNull WildcardBound bounds,
        final @NotNull TypeSignature signature
    ) {
        this.bounds = bounds;
        this.signature = signature;
    }

    @Override
    public void asReadable(final @NotNull StringBuilder sb) {
        this.bounds.asReadable(sb);
        sb.append(' ');

        this.signature.asReadable(sb);
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        this.bounds.asInternal(sb);
        this.signature.asInternal(sb);
    }

    @Override
    public @NotNull BoundedTypeArgument bind(final @NotNull TypeVariableBinder binder) {
        return BoundedTypeArgument.of(this.bounds, this.signature.bind(binder));
    }

    public @NotNull WildcardBound getBounds() {
        return this.bounds;
    }

    public @NotNull TypeSignature getSignature() {
        return this.signature;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BoundedTypeArgument)) {
            return false;
        }
        final BoundedTypeArgument that = (BoundedTypeArgument) o;
        return this.bounds == that.bounds
            && Objects.equals(this.signature, that.signature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.bounds, this.signature);
    }

    @Override
    public String toString() {
        return this.asReadable();
    }
}

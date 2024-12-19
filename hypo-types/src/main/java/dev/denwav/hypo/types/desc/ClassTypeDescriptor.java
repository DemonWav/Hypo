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

import dev.denwav.hypo.types.HypoTypesUtil;
import dev.denwav.hypo.types.Intern;
import dev.denwav.hypo.types.sig.ClassTypeSignature;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link TypeDescriptor} representing a class type. Class (or reference) types follow the internal format of
 * {@code L<class_name>;}.
 */
public final class ClassTypeDescriptor extends Intern<ClassTypeDescriptor> implements TypeDescriptor {

    private final @NotNull String name;

    /**
     * Create a {@link ClassTypeDescriptor} instance.
     *
     * @param name The class name for the new type.
     * @return The new {@link ClassTypeDescriptor}.
     */
    public static @NotNull ClassTypeDescriptor of(final @NotNull String name) {
        return new ClassTypeDescriptor(HypoTypesUtil.normalizedClassName(name)).intern();
    }

    private ClassTypeDescriptor(final @NotNull String name) {
        this.name = name;
    }

    @Override
    public void asReadable(final @NotNull StringBuilder sb) {
        sb.append(this.name.replace('.', '/'));
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        sb.append('L');
        sb.append(this.name);
        sb.append(';');
    }

    @Override
    public @NotNull ClassTypeSignature asSignature() {
        return ClassTypeSignature.of(null, this.name, null);
    }

    /**
     * Get the class name for this type. The name does not include the {@code L} and {@code ;} format characters as seen
     * in the internal name format.
     *
     * @return The class name for this type.
     */
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ClassTypeDescriptor)) {
            return false;
        }
        final ClassTypeDescriptor classTypeDescriptor = (ClassTypeDescriptor) o;
        return Objects.equals(this.name, classTypeDescriptor.name);
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

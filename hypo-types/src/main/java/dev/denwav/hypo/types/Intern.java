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

package dev.denwav.hypo.types;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;
import org.jetbrains.annotations.NotNull;

public abstract class Intern<T extends Intern<T>> implements TypeRepresentable {

    private final Object lock = new Object();
    private final @NotNull WeakHashMap<T, WeakReference<T>> internment;

    protected Intern(final @NotNull WeakHashMap<T, WeakReference<T>> internment) {
        this.internment = internment;
    }

    public final T intern() {
        synchronized (this.lock) {
            final T t = HypoTypesUtil.cast(this);
            WeakReference<T> ref = this.internment.get(t);
            if (ref != null) {
                return ref.get();
            }
            this.internment.put(t, new WeakReference<>(t));
            return t;
        }
    }
}

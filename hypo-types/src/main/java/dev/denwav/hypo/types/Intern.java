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

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Intern<T extends Intern<T>> implements TypeRepresentable {

    private static final IdentityHashMap<ConcurrentHashMap<String, WeakReference<?>>, AtomicLong> interns = new IdentityHashMap<>();
    private static final List<ConcurrentHashMap<String, WeakReference<?>>> newInterns = new ArrayList<>();
    static {
        final Thread t = new Thread(() -> {
            while (true) {
                try {
                    //noinspection BusyWait
                    Thread.sleep(500);
                } catch (final InterruptedException e) {
                    break;
                }
                for (final var entry : interns.entrySet()) {
                    final var map = entry.getKey();
                    final AtomicLong lastSize = entry.getValue();
                    if (Math.abs(map.mappingCount() - lastSize.get()) < 10_000) {
                        System.out.println("SKIPPED");
                        continue;
                    }
                    map.values().removeIf(r -> r.get() == null);
                    lastSize.set(map.mappingCount());
                }

                // Prevent CME
                if (!newInterns.isEmpty()) {
                    synchronized (newInterns) {
                        for (final var newIntern : newInterns) {
                            interns.put(newIntern, new AtomicLong(0));
                        }
                        newInterns.clear();
                    }
                }
            }
        }, "Interning Cleanup");
        t.setDaemon(true);
        t.start();
    }

    private static final class InternClassValue extends ClassValue<ConcurrentHashMap<String, WeakReference<?>>> {
        @Override
        protected ConcurrentHashMap<String, WeakReference<?>> computeValue(final @NotNull Class<?> type) {
            final ConcurrentHashMap<String, WeakReference<?>> map = new ConcurrentHashMap<>();
            synchronized (newInterns) {
                newInterns.add(map);
            }
            return map;
        }
    }

    private static final InternClassValue internment = new InternClassValue();

    private static final boolean interningDisabled = Boolean.getBoolean("hypo.interning.disabled");

    public final T intern() {
        if (interningDisabled) {
            return HypoTypesUtil.cast(this);
        }

        final T t = HypoTypesUtil.cast(this);
        try {
            final ConcurrentHashMap<String, WeakReference<?>> map = internment.get(this.getClass());
            final String key = t.asInternal();
            final WeakReference<T> ref = new WeakReference<>(t);
            final T res = HypoTypesUtil.cast(map.computeIfAbsent(key, k -> ref).get());
            if (res != null) {
                return res;
            }

            map.put(key, ref);
            return t;
        } finally {
            Reference.reachabilityFence(t);
        }
    }

    @SuppressWarnings("TypeParameterUnusedInFormals")
    public static <T> @Nullable T tryFind(final @NotNull Class<?> c, final @NotNull String key) {
        final WeakReference<?> ref = internment.get(c).get(key);
        if (ref == null) {
            return null;
        }
        final Object r = ref.get();
        try {
            if (r != null) {
                return HypoTypesUtil.cast(r);
            }
            return null;
        } finally {
            Reference.reachabilityFence(r);
        }
    }

    public static long internmentSize(final @NotNull Class<?> c) {
        return internment.get(c).mappingCount();
    }
}

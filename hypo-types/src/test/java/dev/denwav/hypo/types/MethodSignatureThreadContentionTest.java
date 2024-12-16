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

import dev.denwav.hypo.types.sig.MethodSignature;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MethodSignatureThreadContentionTest {

    private static final VarHandle methodSigInternment;
    static {
        try {
            methodSigInternment = MethodHandles.privateLookupIn(MethodSignature.class, MethodHandles.lookup())
                .findStaticVarHandle(MethodSignature.class, "internment", WeakHashMap.class);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new LinkageError();
        }
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    @Test
    void testThreadContention() {
        final AtomicInteger counter = new AtomicInteger(0);

        try (final ExecutorService pool = Executors.newWorkStealingPool()) {
            for (int i = 0; i < 64; i++) {
                pool.submit(() -> {
                    for (int j = 0; j < 1_000_000; j++) {
                        final MethodSignature sig = MethodSignature.parse("()V");
                        Assertions.assertEquals(sig, MethodSignature.parse(sig.asInternal()), "Internal " + sig.asInternal());

                        final int count = counter.incrementAndGet();
                        if (count % 1_000_000 == 0) {
                            System.out.printf("Tested %,d iterations...%n", count);
                            System.out.printf("MethodSignature internment: %,d %n", ((Map<?, ?>) methodSigInternment.get()).size());
                        }
                    }
                });
            }
        }
    }

}

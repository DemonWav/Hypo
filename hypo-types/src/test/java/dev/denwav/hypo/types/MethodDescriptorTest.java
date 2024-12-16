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

import dev.denwav.hypo.types.desc.ArrayTypeDescriptor;
import dev.denwav.hypo.types.desc.ClassTypeDescriptor;
import dev.denwav.hypo.types.desc.MethodDescriptor;
import dev.denwav.hypo.types.desc.TypeDescriptor;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("MethodDescriptor Tests")
class MethodDescriptorTest {

    private static final VarHandle methodDescInternment;
    static {
        try {
            methodDescInternment = MethodHandles.privateLookupIn(MethodDescriptor.class, MethodHandles.lookup())
                .findStaticVarHandle(MethodDescriptor.class, "internment", WeakHashMap.class);
        } catch (final NoSuchFieldException | IllegalAccessException e) {
            throw new LinkageError();
        }
    }

    @Test
    @DisplayName("Test MethodDescriptor.parse() and MethodDescriptor.asInternal()")
    void testParse() {
        final TypeDescriptor[] testTypes = Arrays.stream(new TypeDescriptor[]{
            PrimitiveType.CHAR,
            PrimitiveType.BYTE,
            PrimitiveType.SHORT,
            PrimitiveType.INT,
            PrimitiveType.LONG,
            PrimitiveType.FLOAT,
            PrimitiveType.DOUBLE,
            PrimitiveType.BOOLEAN,
            ClassTypeDescriptor.of("java/lang/Object"),
            ClassTypeDescriptor.of("java/lang/String"),
        }).flatMap(t -> {
            return Stream.of(t, ArrayTypeDescriptor.of(1, t), ArrayTypeDescriptor.of(2, t));
        }).toArray(TypeDescriptor[]::new);

        final ArrayList<TypeDescriptor> returnTypeList = new ArrayList<>(Arrays.asList(testTypes));
        returnTypeList.add(VoidType.INSTANCE);
        final TypeDescriptor[] returnTypes = returnTypeList.toArray(new TypeDescriptor[0]);

        final TypeDescriptor[] params = new TypeDescriptor[4];
        final List<TypeDescriptor> paramList = Arrays.asList(params);

        int counter = 0;

        for (final TypeDescriptor param0 : testTypes) {
            for (final TypeDescriptor param1 : testTypes) {
                for (final TypeDescriptor param2 : testTypes) {
                    for (final TypeDescriptor param3 : testTypes) {
                        params[0] = param0;
                        params[1] = param1;
                        params[2] = param2;
                        params[3] = param3;

                        for (final TypeDescriptor returnType : returnTypes) {
                            final MethodDescriptor dec = MethodDescriptor.of(paramList, returnType);
                            Assertions.assertEquals(dec, MethodDescriptor.parse(dec.asInternal()));

                            counter++;
                            if (counter % 1_000_000 == 0) {
                                System.out.printf("Tested %,d permutations...%n", counter);
                                System.out.printf("MethodDescriptor internment: %,d %n", ((Map<?, ?>) methodDescInternment.get()).size());
                            }
                        }
                    }
                }
            }
        }
    }
}

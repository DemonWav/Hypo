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

import dev.denwav.hypo.types.desc.ClassTypeDescriptor;
import dev.denwav.hypo.types.desc.TypeDescriptor;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.WeakHashMap;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class InternTest {

    @Test
    void testIntern() {
        final TypeDescriptor first = TypeDescriptor.parse("Ljava/lang/String;");
        final TypeDescriptor second = TypeDescriptor.parse("Ljava/lang/String;");
        final TypeDescriptor third = ClassTypeDescriptor.of("java/lang/String");

        Assertions.assertSame(first, second);
        Assertions.assertSame(first, third);
        Assertions.assertEquals(1, Intern.internmentSize(ClassTypeDescriptor.class));
    }
}

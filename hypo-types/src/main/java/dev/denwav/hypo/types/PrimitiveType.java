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
import dev.denwav.hypo.types.sig.ClassTypeSignature;
import dev.denwav.hypo.types.sig.TypeSignature;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PrimitiveType implements TypeDescriptor, TypeSignature {
    CHAR("char", 'C', "java/lang/Character"),
    BYTE("byte", 'B', "java/lang/Byte"),
    SHORT("short", 'S', "java/lang/Short"),
    INT("int", 'I', "java/lang/Integer"),
    LONG("long", 'J', "java/lang/Long"),
    FLOAT("float", 'F', "java/lang/Float"),
    DOUBLE("double", 'D', "java/lang/Double"),
    BOOLEAN("boolean", 'Z', "java/lang/Boolean"),
    ;

    private final @NotNull String readableName;
    private final char internalName;
    private final @NotNull String wrapperType;

    PrimitiveType(
        final @NotNull String readableName,
        final char internalName,
        final @NotNull String wrapperType
    ) {
        this.readableName = readableName;
        this.internalName = internalName;
        this.wrapperType = wrapperType;
    }

    @Override
    public void asReadable(final @NotNull StringBuilder sb) {
        sb.append(this.readableName);
    }

    @Override
    public void asInternal(final @NotNull StringBuilder sb) {
        sb.append(this.internalName);
    }

    @Override
    public @NotNull PrimitiveType asSignature() {
        return this;
    }

    @Override
    public @NotNull PrimitiveType bind(final @NotNull TypeVariableBinder binder) {
        return this;
    }

    @Override
    public @NotNull PrimitiveType asDescriptor() {
        return this;
    }

    public @NotNull String getReadableName() {
        return this.readableName;
    }

    public char getInternalName() {
        return this.internalName;
    }

    public @NotNull String getWrapperType() {
        return this.wrapperType;
    }

    public static @Nullable PrimitiveType fromChar(final char c) {
        switch (c) {
            case 'C':
                return PrimitiveType.CHAR;
            case 'B':
                return PrimitiveType.BYTE;
            case 'S':
                return PrimitiveType.SHORT;
            case 'I':
                return PrimitiveType.INT;
            case 'J':
                return PrimitiveType.LONG;
            case 'F':
                return PrimitiveType.FLOAT;
            case 'D':
                return PrimitiveType.DOUBLE;
            case 'Z':
                return PrimitiveType.BOOLEAN;
            default:
                return null;
        }
    }

    public @NotNull ClassTypeDescriptor getWrapperTypeDescriptor() {
        switch (this) {
            case CHAR:
                return ClassTypeDescriptor.of("java/lang/Character");
            case BYTE:
                return ClassTypeDescriptor.of("java/lang/Byte");
            case SHORT:
                return ClassTypeDescriptor.of("java/lang/Short");
            case INT:
                return ClassTypeDescriptor.of("java/lang/Int");
            case LONG:
                return ClassTypeDescriptor.of("java/lang/Long");
            case FLOAT:
                return ClassTypeDescriptor.of("java/lang/Float");
            case DOUBLE:
                return ClassTypeDescriptor.of("java/lang/Double");
            case BOOLEAN:
                return ClassTypeDescriptor.of("java/lang/Boolean");
            default:
                throw new IllegalStateException("Unknown enum value: " + this);
        }
    }

    public @NotNull ClassTypeSignature getWrapperTypeSignature() {
        return this.getWrapperTypeDescriptor().asSignature();
    }

    @Override
    public String toString() {
        return this.asReadable();
    }
}

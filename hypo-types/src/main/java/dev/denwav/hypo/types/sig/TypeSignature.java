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
import dev.denwav.hypo.types.TypeBindable;
import dev.denwav.hypo.types.TypeRepresentable;
import dev.denwav.hypo.types.TypeVariableBinder;
import dev.denwav.hypo.types.desc.TypeDescriptor;
import dev.denwav.hypo.types.parsing.JvmTypeParseFailureException;
import dev.denwav.hypo.types.parsing.JvmTypeParser;
import org.jetbrains.annotations.NotNull;

/**
 * A <a href="https://docs.oracle.com/javase/specs/jvms/se23/html/jvms-4.html#jvms-4.7.9.1">JVM type signature</a>.
 *
 * <p>A type signature can be one of four different kinds:
 * <ul>
 *     <li>{@link dev.denwav.hypo.types.PrimitiveType PrimitiveType}</li>
 *     <li>{@link ClassTypeSignature}</li>
 *     <li>{@link ArrayTypeSignature}</li>
 *     <li>{@link dev.denwav.hypo.types.VoidType VoidType}</li>
 * </ul>
 *
 * <p>Type signatures are used by the Java compiler to enforce generic type checks at compile time. They are not used
 * during runtime on the JVM, though they are present to allow for compiling against and debugging generic code.
 *
 * <p>All implementations of this interface must be immutable.
 *
 * @see MethodSignature
 * @see TypeDescriptor
 */
public interface TypeSignature extends TypeBindable, TypeRepresentable {

    /**
     * Return a possibly erased version of this signature as a {@link TypeDescriptor}. This is a lossy process as type
     * descriptors cannot represent all components of type signatures - all generic type information will be lost.
     *
     * <p>This method will throw {@link IllegalStateException} if it contains an
     * {@link dev.denwav.hypo.types.sig.param.TypeVariable.Unbound unbound tpye variable}. Use the
     * {@link #bind(TypeVariableBinder)} method in that case to create a version of this signature which has type
     * variables which are properly bound to their parameters.
     *
     * @return A possibly erased version of this signature as a {@link TypeDescriptor}.
     */
    @NotNull TypeDescriptor asDescriptor();

    /**
     * Parse the given internal JVM type signature text into a new {@link TypeSignature}.
     *
     * <p>This method throws {@link JvmTypeParseFailureException} if the given text is not a valid type signature. Use
     * {@link JvmTypeParser#parseTypeSignature(String, int)} if you prefer to have {@code null} be returned instead.
     *
     * @param text The text to parse.
     * @return The {@link TypeSignature}.
     * @throws JvmTypeParseFailureException If the given text does not represent a valid JVM type signature.
     */
    static @NotNull TypeSignature parse(final @NotNull String text) throws JvmTypeParseFailureException {
        return parse(text, 0);
    }

    /**
     * Parse the given internal JVM type signature text into a new {@link TypeSignature}.
     *
     * <p>This method throws {@link JvmTypeParseFailureException} if the given text is not a valid type signature. Use
     * {@link JvmTypeParser#parseTypeSignature(String, int)} if you prefer to have {@code null} be returned instead.
     *
     * @param text The text to parse.
     * @param from The index to start parsing from.
     * @return The {@link TypeSignature}.
     * @throws JvmTypeParseFailureException If the given text does not represent a valid JVM type signature.
     */
    static @NotNull TypeSignature parse(final @NotNull String text, final int from) throws JvmTypeParseFailureException {
        if (text.length() > 1 && from == 0) {
            final TypeSignature r = Intern.tryFind(ClassTypeSignature.class, text);
            if (r != null) {
                return r;
            }
        }
        final TypeSignature result = JvmTypeParser.parseTypeSignature(text, from);
        if (result == null) {
            throw new JvmTypeParseFailureException("text is not a valid type signature: " + text.substring(from));
        }
        return result;
    }

    @Override
    @NotNull TypeSignature bind(final @NotNull TypeVariableBinder binder);
}

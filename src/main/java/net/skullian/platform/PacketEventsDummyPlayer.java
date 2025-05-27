/*
 * This file is part of InteractiveChat.
 *
 * Copyright (C) 2020 - 2025. LoohpJames <jamesloohp@gmail.com>
 * Copyright (C) 2020 - 2025. Contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package net.skullian.platform;

import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.skullian.util.ByteBuddyFactory;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

// https://github.com/LOOHP/InteractiveChat/commit/9fac2fd7e65c7813de3398f74960cb5a850dc95e#diff-a9f505dce18a31e912568912571b93d85e4ef017128ab4543b673a51bd1e7cbaR41
public abstract class PacketEventsDummyPlayer implements Player {

    private static final Constructor<? extends PacketEventsDummyPlayer> CONSTRUCTOR = setupProxyPlayerConstructor(true);

    private static Constructor<? extends PacketEventsDummyPlayer> setupProxyPlayerConstructor(boolean init) {
        if (init) {
            try {
                return setupProxyPlayerConstructor(false);
            } catch (Throwable ignore) {
            }
        }

        MethodDelegation implementation = MethodDelegation.to(new Object() {
            @RuntimeType
            public Object delegate(@This Object obj, @Origin Method method, @AllArguments Object... args) {
                throw new UnsupportedOperationException("This operation is unsupported for DummyPlayer");
            }
        });
        ElementMatcher.Junction<MethodDescription> callbackFilter = ElementMatchers.not(ElementMatchers.isAbstract());

        try {
            return ByteBuddyFactory.getInstance()
                    .createSubclass(PacketEventsDummyPlayer.class, ConstructorStrategy.Default.IMITATE_SUPER_CLASS)
                    .name(PacketEventsDummyPlayer.class.getPackage().getName() + ".DummyPlayerInvocationHandler")
                    .implement(Player.class)
                    .method(callbackFilter)
                    .intercept(implementation)
                    .make()
                    .load(PacketEventsDummyPlayer.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded()
                    .getDeclaredConstructor(String.class, UUID.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find PacketEventsDummyPlayer constructor!", e);
        }
    }

    public static PacketEventsDummyPlayer newInstance(String name, UUID uuid) {
        try {
            return CONSTRUCTOR.newInstance(name, uuid);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final String name;
    private final UUID uuid;

    public PacketEventsDummyPlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return uuid;
    }

}

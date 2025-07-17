package net.skullian.player;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy.Default;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

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
            return new ByteBuddy().subclass(PacketEventsDummyPlayer.class, Default.IMITATE_SUPER_CLASS)
                    .name(PacketEventsDummyPlayer.class.getPackage().getName() + ".DummyPlayerInvocationHandler")
                    .implement(Player.class)
                    .method(callbackFilter)
                    .intercept(implementation)
                    .make()
                    .load(PacketEventsDummyPlayer.class.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
                    .getLoaded()
                    .getDeclaredConstructor(String.class, UUID.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Failed to find DummyPlayer constructor!", e);
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

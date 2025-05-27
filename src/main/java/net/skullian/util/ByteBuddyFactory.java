package net.skullian.util;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;

// https://github.com/LOOHP/InteractiveChat/commit/9fac2fd7e65c7813de3398f74960cb5a850dc95e#diff-a9f505dce18a31e912568912571b93d85e4ef017128ab4543b673a51bd1e7cbaR41
public final class ByteBuddyFactory {
    private static final ByteBuddyFactory INSTANCE = new ByteBuddyFactory();

    private ByteBuddyFactory() {
    }

    public static ByteBuddyFactory getInstance() {
        return INSTANCE;
    }

    public <T> DynamicType.Builder.MethodDefinition.ImplementationDefinition.Optional<T> createSubclass(Class<T> clz, ConstructorStrategy.Default constructorStrategy) {
        return new ByteBuddy()
                .subclass(clz, constructorStrategy)
                .implement(GeneratedByteBuddy.class);
    }

    public static final class GeneratedByteBuddy {
    }
}

package at.petrak.hexcasting.fabric.xplat;

import at.petrak.hexcasting.xplat.IXplatRegister;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class FabricRegister<B> implements IXplatRegister<B> {
    private final Registry<B> register;

    @SuppressWarnings("unchecked")
    public FabricRegister(ResourceKey<Registry<B>> registryKey) {
        this.register = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY).lookupOrThrow(registryKey);
    }


    @Override
    public <T extends B> Supplier<T> register(String id, Supplier<T> provider) {
        T value = provider.get();
        Registry.register(register, modLoc(id), value);
        return () -> value;
    }

    @Override
    public <T extends B> Holder<B> registerHolder(String id, Supplier<T> provider) {
        T value = provider.get();
        Registry.register(register, modLoc(id), value);
        return register.wrapAsHolder(value);
    }

    @Override
    @Deprecated(since = "This is fabric. We register eagerly.")
    public void registerAll() {
        // This is fabric. We register eagerly.
    }
}

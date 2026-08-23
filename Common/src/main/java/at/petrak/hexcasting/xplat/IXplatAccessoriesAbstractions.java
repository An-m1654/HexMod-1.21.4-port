package at.petrak.hexcasting.xplat;

import at.petrak.hexcasting.api.HexAPI;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.function.TriFunction;

import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface IXplatAccessoriesAbstractions {
    boolean accessoryModInstalled();
    InteractionResult customUseCode(Level level, Player player, InteractionHand interactionHand, TriFunction<Level, Player, InteractionHand, InteractionResult> originalFunc);

    ///

    IXplatAccessoriesAbstractions INSTANCE = find();

    private static IXplatAccessoriesAbstractions find() {
        var providers = ServiceLoader.load(IXplatAccessoriesAbstractions.class).stream().toList();
        if (providers.size() != 1) {
            var names = providers.stream().map(p -> p.type().getName()).collect(Collectors.joining(",", "[", "]"));
            throw new IllegalStateException(
                "There should be exactly one IXplatAccessoriesAbstractions implementation on the classpath. Found: " + names);
        } else {
            var provider = providers.get(0);
            HexAPI.LOGGER.debug("Instantiating xplat impl: " + provider.type().getName());
            return provider.get();
        }
    }
}

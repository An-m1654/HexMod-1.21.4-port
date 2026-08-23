package at.petrak.hexcasting.mixin.accessor.client;

import net.minecraft.client.ScrollWheelHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ScrollWheelHandler.class)
public interface AccessorMouseHandler {
    @Accessor("accumulatedScrollY")
    double hex$getAccumulatedScrollY();

    @Accessor("accumulatedScrollY")
    void hex$setAccumulatedScrollY(double scroll);
}

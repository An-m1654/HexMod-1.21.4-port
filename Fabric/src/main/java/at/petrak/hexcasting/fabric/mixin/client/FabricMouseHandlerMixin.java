package at.petrak.hexcasting.fabric.mixin.client;

import at.petrak.hexcasting.fabric.event.MouseScrollCallback;
import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(MouseHandler.class)
public class FabricMouseHandlerMixin {
    @Inject(method = "onScroll", cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/ScrollWheelHandler;onMouseScroll(DD)Lorg/joml/Vector2i;"))
    private void onScroll(long winptr, double xOff, double yOff, CallbackInfo ci, boolean discreteScroll, double sensitivity, double scaledXOff, double scaledYOff) {
        var cancel = MouseScrollCallback.EVENT.invoker().interact(scaledYOff);
        if (cancel) {
            ci.cancel();
        }
    }
}
package at.petrak.hexcasting.fabric.mixin.client;

import at.petrak.hexcasting.fabric.client.ExtendedTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractTexture.class)
public abstract class FabricAbstractTextureMixin implements ExtendedTexture {
    @Unique
    protected boolean blur;

    @Unique
    protected boolean mipmap;

    @Shadow
    public abstract void setFilter(boolean bilinear, boolean mipmap);

    @Inject(method = "setFilter(ZZ)V", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;assertOnRenderThreadOrInit()V", shift = At.Shift.AFTER))
    public void setFilter(boolean bilinear, boolean mipmap, CallbackInfo ci) {
        this.blur = bilinear;
        this.mipmap = mipmap;
    }


    @Unique
    private boolean lastBilinear;

    @Unique
    private boolean lastMipmap;

    @Override
    public void setFilterSave(boolean bilinear, boolean mipmap) {
        this.lastBilinear = this.blur;
        this.lastMipmap = this.mipmap;
        setFilter(bilinear, mipmap);
    }

    @Override
    public void restoreLastFilter() {
        setFilter(this.lastBilinear, this.lastMipmap);
    }
}

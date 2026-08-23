package at.petrak.hexcasting.client.particles;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import at.petrak.hexcasting.xplat.IClientXplatAbstractions;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.CoreShaders;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
import net.minecraft.util.TriState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.function.Function;

public class ConjureParticle extends TextureSheetParticle {
    private static final Random RANDOM = new Random();

    private final SpriteSet sprites;

    ConjureParticle(ClientLevel pLevel, double x, double y, double z, double dx, double dy, double dz,
        SpriteSet pSprites, int color) {
        super(pLevel, x, y, z, dx, dy, dz);
        this.quadSize *= 0.9f;
        this.setParticleSpeed(dx, dy, dz);

        var r = ARGB.red(color);
        var g = ARGB.green(color);
        var b = ARGB.blue(color);
        this.setColor(r / 255f, g / 255f, b / 255f);
        this.setAlpha(0.3f);

        this.friction = 0.96F;
        this.gravity = dy != 0 && dx != 0 && dz != 0 ? -0.01F : 0F;
        this.speedUpWhenYMotionIsBlocked = true;
        this.sprites = pSprites;

        this.roll = RANDOM.nextFloat(360);
        this.oRoll = this.roll;

        this.lifetime = (int) (64.0 / ((Math.random() + 3f) * 0.25f));
        this.hasPhysics = false;
        this.setSpriteFromAge(pSprites);
    }

    public @NotNull ParticleRenderType getRenderType() {
        return CONJURE_RENDER_TYPE;
    }

    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.alpha = 1.0f - ((float) this.age / (float) this.lifetime);
        this.alpha *= 0.3f;
        this.quadSize *= 0.96f;
    }

    public void setSpriteFromAge(@NotNull SpriteSet pSprite) {
        if (!this.removed) {
            int age = this.age * 4;
            if (age > this.lifetime) {
                age /= 4;
            }
            this.setSprite(pSprite.get(age, this.lifetime));
        }
    }

    public static class Provider implements ParticleProvider<ConjureParticleOptions> {
        private final SpriteSet sprite;

        public Provider(SpriteSet pSprites) {
            this.sprite = pSprites;
        }

        @Nullable
        @Override
        public Particle createParticle(ConjureParticleOptions type, ClientLevel level,
            double pX, double pY, double pZ,
            double pXSpeed, double pYSpeed, double pZSpeed) {
            return new ConjureParticle(level, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite, type.color());
        }
    }

    // https://github.com/VazkiiMods/Botania/blob/db85d778ab23f44c11181209319066d1f04a9e3d/Xplat/src/main/java/vazkii/botania/client/fx/FXWisp.java
//    private record ConjureRenderType() implements ParticleRenderType {
//-        @Override
//        public BufferBuilder begin(Tesselator tess, TextureManager texMan) {
//            Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
//            RenderSystem.depthMask(false);
//            RenderSystem.enableBlend();
//            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
//
//            RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_PARTICLES);
//            var tex = texMan.getTexture(TextureAtlas.LOCATION_PARTICLES);
//            IClientXplatAbstractions.INSTANCE.setFilterSave(tex, false, false);
//            RenderSystem.enableDepthTest();
//            return tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
//        }
//
//-        @Override
//        public String toString() {
//            return HexAPI.MOD_ID + ":conjure";
//        }
//    }
    private static final RenderType ConjureRenderType = RenderType.create(
            HexAPI.MOD_ID + ":conjure",
            DefaultVertexFormat.PARTICLE,
            VertexFormat.Mode.QUADS,
            1536,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(RenderStateShard.PARTICLE_SHADER)
                    .setLightmapState(RenderStateShard.LIGHTMAP) // Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer()
                    .setWriteMaskState(RenderStateShard.COLOR_WRITE) // RenderSystem.depthMask(false)
                    .setTransparencyState(RenderStateShard.LIGHTNING_TRANSPARENCY) // enableBlend() & blendFunc(...)
                    .setTextureState(
                        new RenderStateShard.TextureStateShard(
                            TextureAtlas.LOCATION_PARTICLES,
                            TriState.FALSE,
                            false
                        )
                    )
//                  ^ setShaderTexture(...) & IClientXplatAbstractions.INSTANCE.setFilterSave(...)
//                  .setDepthTestState(RenderStateShard.LEQUAL_DEPTH_TEST) // enableDepthTest. This is default behavior
                    .createCompositeState(false)
    );
    public static final ParticleRenderType CONJURE_RENDER_TYPE = new ParticleRenderType("CONJURE_RENDER_TYPE", ConjureRenderType);
}

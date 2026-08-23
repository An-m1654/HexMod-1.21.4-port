package at.petrak.hexcasting.client.entity;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.client.render.WorldlyPatternRenderHelpers;
import at.petrak.hexcasting.common.entities.EntityWallScroll;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.DisplayEntityRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.EmptyBlockAndTintGetter;
import org.joml.Matrix4f;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class WallScrollRenderer extends EntityRenderer<EntityWallScroll, WallScrollRenderState> {
    private static final ResourceLocation PRISTINE_BG_LARGE = modLoc("textures/entity/scroll_large.png");
    private static final ResourceLocation PRISTINE_BG_MEDIUM = modLoc("textures/entity/scroll_medium.png");
    private static final ResourceLocation PRISTINE_BG_SMOL = modLoc("textures/block/scroll_paper.png");
    private static final ResourceLocation ANCIENT_BG_LARGE = modLoc("textures/entity/scroll_ancient_large.png");
    private static final ResourceLocation ANCIENT_BG_MEDIUM = modLoc("textures/entity/scroll_ancient_medium.png");
    private static final ResourceLocation ANCIENT_BG_SMOL = modLoc("textures/block/ancient_scroll_paper.png");

    public WallScrollRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    // I do as the PaintingRenderer guides
    @Override
    public WallScrollRenderState createRenderState() {
        return new WallScrollRenderState();
    }

    @Override
    public void render(WallScrollRenderState wallScrollState, PoseStack ps, MultiBufferSource bufSource, int packedLight) {
        RenderSystem.setShader(CoreShaders.POSITION_TEX);

        ps.pushPose();

        ps.mulPose(Axis.YP.rotationDegrees(180f - wallScrollState.entityYRot));
        ps.mulPose(Axis.ZP.rotationDegrees(180f));

        int light = LevelRenderer.getLightColor(wallScrollState.level, new BlockPos(Mth.floor(wallScrollState.x), Mth.floor(wallScrollState.y), Mth.floor(wallScrollState.z)));
        {
            ps.pushPose();
            // X is right, Y is down, Z is *in*
            // Our origin will be the lower-left corner of the scroll touching the wall
            // (so it has "negative" thickness)
            ps.translate(-wallScrollState.blockSize / 2f, -wallScrollState.blockSize / 2f, 1f / 32f);

            float dx = wallScrollState.blockSize, dy = wallScrollState.blockSize, dz = -1f / 16f;
            float margin = 1f / 48f;
            var last = ps.last();
            var mat = last.pose();

            RenderType layer = RenderType.entityCutout(this.getTextureLocation(wallScrollState));

            var verts = bufSource.getBuffer(layer);
            // Remember: CCW
            // Front face
            vertex(mat, last, light, verts, 0, 0, dz, 0, 0, 0, 0, -1);
            vertex(mat, last, light, verts, 0, dy, dz, 0, 1, 0, 0, -1);
            vertex(mat, last, light, verts, dx, dy, dz, 1, 1, 0, 0, -1);
            vertex(mat, last, light, verts, dx, 0, dz, 1, 0, 0, 0, -1);
            // Back face
            vertex(mat, last, light, verts, 0, 0, 0, 0, 0, 0, 0, 1);
            vertex(mat, last, light, verts, dx, 0, 0, 1, 0, 0, 0, 1);
            vertex(mat, last, light, verts, dx, dy, 0, 1, 1, 0, 0, 1);
            vertex(mat, last, light, verts, 0, dy, 0, 0, 1, 0, 0, 1);
            // Top face
            vertex(mat, last, light, verts, 0, 0, 0, 0, 0, 0, -1, 0);
            vertex(mat, last, light, verts, 0, 0, dz, 0, margin, 0, -1, 0);
            vertex(mat, last, light, verts, dx, 0, dz, 1, margin, 0, -1, 0);
            vertex(mat, last, light, verts, dx, 0, 0, 1, 0, 0, -1, 0);
            // Left face
            vertex(mat, last, light, verts, 0, 0, 0, 0, 0, -1, 0, 0);
            vertex(mat, last, light, verts, 0, dy, 0, 0, 1, -1, 0, 0);
            vertex(mat, last, light, verts, 0, dy, dz, margin, 1, -1, 0, 0);
            vertex(mat, last, light, verts, 0, 0, dz, margin, 0, -1, 0, 0);
            // Right face
            vertex(mat, last, light, verts, dx, 0, dz, 1 - margin, 0, 1, 0, 0);
            vertex(mat, last, light, verts, dx, dy, dz, 1 - margin, 1, 1, 0, 0);
            vertex(mat, last, light, verts, dx, dy, 0, 1, 1, 1, 0, 0);
            vertex(mat, last, light, verts, dx, 0, 0, 1, 0, 1, 0, 0);
            // Bottom face
            vertex(mat, last, light, verts, 0, dy, dz, 0, 1 - margin, 0, 1, 0);
            vertex(mat, last, light, verts, 0, dy, 0, 0, 1, 0, 1, 0);
            vertex(mat, last, light, verts, dx, dy, 0, 1, 1, 0, 1, 0);
            vertex(mat, last, light, verts, dx, dy, dz, 1, 1 - margin, 0, 1, 0);

            ps.popPose();

            if(wallScrollState.pattern != null)
                WorldlyPatternRenderHelpers.renderPatternForScroll(wallScrollState.pattern, wallScrollState, ps, bufSource, light, wallScrollState.blockSize, wallScrollState.getShowsStrokeOrder());
        }

        ps.popPose();
        super.render(wallScrollState, ps, bufSource, packedLight);
    }

    // I do as the PaintingRenderer guides
    public ResourceLocation getTextureLocation(WallScrollRenderState wallScrollState) {
        if (wallScrollState.isAncient) {
            if (wallScrollState.blockSize <= 1) {
                return ANCIENT_BG_SMOL;
            } else if (wallScrollState.blockSize == 2) {
                return ANCIENT_BG_MEDIUM;
            } else {
                return ANCIENT_BG_LARGE;
            }
        } else {
            if (wallScrollState.blockSize <= 1) {
                return PRISTINE_BG_SMOL;
            } else if (wallScrollState.blockSize == 2) {
                return PRISTINE_BG_MEDIUM;
            } else {
                return PRISTINE_BG_LARGE;
            }
        }
    }

    private static void vertex(Matrix4f mat, PoseStack.Pose last, int light, VertexConsumer verts, float x, float y,
                               float z, float u,
                               float v, float nx, float ny, float nz) {
        verts.addVertex(mat, x, y, z)
                .setColor(0xffffffff)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(last, nx, ny, nz);
    }

    @Override
    public void extractRenderState(EntityWallScroll entity, WallScrollRenderState entityRenderState, float f) {
        super.extractRenderState(entity, entityRenderState, f);
        entityRenderState.pattern = entity.pattern;
        entityRenderState.showStrokeOrder = entity.getShowsStrokeOrder();
        entityRenderState.isAncient = entity.isAncient;
        entityRenderState.entityYRot = entity.getYRot();
        entityRenderState.x = entity.getX();
        entityRenderState.y = entity.getY();
        entityRenderState.z = entity.getZ();
        entityRenderState.blockSize = entity.blockSize;
        entityRenderState.level = entity.level();
    }
}

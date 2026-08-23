package at.petrak.hexcasting.client.model;

import at.petrak.hexcasting.xplat.IXplatAbstractions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ElytraModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import static at.petrak.hexcasting.api.HexAPI.modLoc;

public class AltioraLayer<S extends PlayerRenderState, M extends EntityModel<PlayerRenderState>> extends RenderLayer<S, M> {
    private static final ResourceLocation TEX_LOC = modLoc("textures/misc/altiora.png");

    private final ElytraModel elytraModel;

    public AltioraLayer(RenderLayerParent<S, M> renderer, EntityModelSet ems) {
        super(renderer);
        this.elytraModel = new ElytraModel(ems.bakeLayer(HexModelLayers.ALTIORA));
        System.out.println("AltioraLayer still needs a fix. Currently, CCAltiora is changed to sync to client also. Not sure if that'll work.");
    }

//-    @Override
//    public void render(PoseStack ps, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player,
//        float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw,
//        float headPitch) {
//        var altiora = IXplatAbstractions.INSTANCE.getAltiora(player);
//-        // do a best effort to not render over other elytra, although we can never patch up everything
//        var chestSlot = player.getItemBySlot(EquipmentSlot.CHEST);
//        if (altiora != null && !chestSlot.is(Items.ELYTRA)) {
//            ps.pushPose();
//            ps.translate(0.0, 0.0, 0.125);
//
//            this.getParentModel().copyPropertiesTo(this.elytraModel);
//            this.elytraModel.setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
//            VertexConsumer verts = ItemRenderer.getArmorFoilBuffer(
//                    buffer, RenderType.armorCutoutNoCull(TEX_LOC), true);
//-            // TODO port: check color
//            this.elytraModel.renderToBuffer(ps, verts, packedLight, OverlayTexture.NO_OVERLAY, -1);
//            ps.popPose();
//        }
//    }

    @Override
    public void render(PoseStack ps, MultiBufferSource buffer, int packedLight, S player, float limbSwing, float limbSwingAmount) {
        var altiora = IXplatAbstractions.INSTANCE.getAltiora(Minecraft.getInstance().player); // THIS USES CLIENT MINECRAFT PLAYER!!!!!!!!!!! IT MIGHT NOT CONNECT WITH THE OTHER MINECRAFT PLAYER USE
//         do a best effort to not render over other elytra, although we can never patch up everything
        var chestSlot = player.chestEquipment;
        if (altiora != null && !chestSlot.is(Items.ELYTRA)) {
            ps.pushPose();
            ps.translate(0.0, 0.0, 0.125);

            this.elytraModel.setupAnim(player);
            VertexConsumer verts = ItemRenderer.getArmorFoilBuffer(
                    buffer, RenderType.armorCutoutNoCull(TEX_LOC), true);
            // TODO port: check color
            this.elytraModel.renderToBuffer(ps, verts, packedLight, OverlayTexture.NO_OVERLAY, -1);

            ps.popPose();
        }
    }
}

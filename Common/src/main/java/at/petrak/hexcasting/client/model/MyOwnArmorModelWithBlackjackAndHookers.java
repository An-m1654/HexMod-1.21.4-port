package at.petrak.hexcasting.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.ArmorStandRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;

// https://github.com/VazkiiMods/Botania/blob/1.19.x/Xplat/src/main/java/vazkii/botania/client/model/armor/ArmorModel.java
public class MyOwnArmorModelWithBlackjackAndHookers extends HumanoidModel<ArmorStandRenderState> {
    protected final EquipmentSlot slot;

    public MyOwnArmorModelWithBlackjackAndHookers(ModelPart root, EquipmentSlot slot) {
        super(root);
        this.slot = slot;
    }

    // [VanillaCopy] ArmorStandArmorModel.setupAnim because armor stands are dumb
    // This fixes the armor "breathing" and helmets always facing south on armor stands
    @Override
    public void setupAnim(ArmorStandRenderState state) {
        this.head.xRot = ((float) Math.PI / 180F) * state.headPose.getX();
        this.head.yRot = ((float) Math.PI / 180F) * state.headPose.getY();
        this.head.zRot = ((float) Math.PI / 180F) * state.headPose.getZ();
        this.head.setPos(0.0F, 1.0F, 0.0F);
        this.body.xRot = ((float) Math.PI / 180F) * state.bodyPose.getX();
        this.body.yRot = ((float) Math.PI / 180F) * state.bodyPose.getY();
        this.body.zRot = ((float) Math.PI / 180F) * state.bodyPose.getZ();
        this.leftArm.xRot = ((float) Math.PI / 180F) * state.leftArmPose.getX();
        this.leftArm.yRot = ((float) Math.PI / 180F) * state.leftArmPose.getY();
        this.leftArm.zRot = ((float) Math.PI / 180F) * state.leftArmPose.getZ();
        this.rightArm.xRot = ((float) Math.PI / 180F) * state.rightArmPose.getX();
        this.rightArm.yRot = ((float) Math.PI / 180F) * state.rightArmPose.getY();
        this.rightArm.zRot = ((float) Math.PI / 180F) * state.rightArmPose.getZ();
        this.leftLeg.xRot = ((float) Math.PI / 180F) * state.leftLegPose.getX();
        this.leftLeg.yRot = ((float) Math.PI / 180F) * state.leftLegPose.getY();
        this.leftLeg.zRot = ((float) Math.PI / 180F) * state.leftLegPose.getZ();
        this.leftLeg.setPos(1.9F, 11.0F, 0.0F);
        this.rightLeg.xRot = ((float) Math.PI / 180F) * state.rightLegPose.getX();
        this.rightLeg.yRot = ((float) Math.PI / 180F) * state.rightLegPose.getY();
        this.rightLeg.zRot = ((float) Math.PI / 180F) * state.rightLegPose.getZ();
        this.rightLeg.setPos(-1.9F, 11.0F, 0.0F);
        this.hat.copyFrom(this.head);
    }

    @Override
    public void renderToBuffer(PoseStack ms, VertexConsumer buffer, int light, int overlay, int color) {
        setPartVisibility(slot);
        super.renderToBuffer(ms, buffer, light, overlay, color);
    }

    // [VanillaCopy] HumanoidArmorLayer
    private void setPartVisibility(EquipmentSlot slot) {
        setAllVisible(false);
        switch (slot) {
            case HEAD -> {
                head.visible = true;
                hat.visible = true;
            }
            case CHEST -> {
                body.visible = true;
                rightArm.visible = true;
                leftArm.visible = true;
            }
            case LEGS -> {
                body.visible = true;
                rightLeg.visible = true;
                leftLeg.visible = true;
            }
            case FEET -> {
                rightLeg.visible = true;
                leftLeg.visible = true;
            }
        }
    }
}

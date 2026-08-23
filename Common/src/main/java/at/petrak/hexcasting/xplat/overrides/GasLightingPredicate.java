package at.petrak.hexcasting.xplat.overrides;

import at.petrak.hexcasting.client.render.GaslightingTracker;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record GasLightingPredicate() implements RangeSelectItemModelProperty {
    public static final MapCodec<GasLightingPredicate> MAP_CODEC = MapCodec.unit(GasLightingPredicate::new);
    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity holder, int holderID) {
        return Math.abs(GaslightingTracker.getGaslightingAmount() % 4);
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}

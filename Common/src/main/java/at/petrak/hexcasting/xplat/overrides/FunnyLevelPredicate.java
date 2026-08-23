package at.petrak.hexcasting.xplat.overrides;

import at.petrak.hexcasting.common.lib.HexDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public record FunnyLevelPredicate() implements RangeSelectItemModelProperty {
    public static final MapCodec<FunnyLevelPredicate> MAP_CODEC = MapCodec.unit(FunnyLevelPredicate::new);
    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity holder, int holderID) {
        if (!stack.has(DataComponents.CUSTOM_NAME)) {
            return 0;
        }
        var name = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
        if (name.contains("old")) {
            return 1f;
        } else if (name.contains("cherry")) {
            return 2f;
        } else {
            return 0f;
        }
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}

package at.petrak.hexcasting.xplat.overrides;

import at.petrak.hexcasting.common.lib.HexDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ThoughtKnotWrittenPredicate() implements RangeSelectItemModelProperty {
    public static final MapCodec<ThoughtKnotWrittenPredicate> MAP_CODEC = MapCodec.unit(ThoughtKnotWrittenPredicate::new);
    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity holder, int holderID) {
        if (stack.has(HexDataComponents.IOTA_HOLDER_IOTA)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}

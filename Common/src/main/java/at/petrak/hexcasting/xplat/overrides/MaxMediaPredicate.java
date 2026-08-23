package at.petrak.hexcasting.xplat.overrides;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.magic.ItemMediaBattery;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

// suck it fabric trying to be "safe"
public record MaxMediaPredicate() implements RangeSelectItemModelProperty {
    public static final MapCodec<MaxMediaPredicate> MAP_CODEC = MapCodec.unit(MaxMediaPredicate::new);
    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity,
                     int seed) {
        if (stack.getItem() instanceof ItemMediaBattery item) {
            var max = item.getMaxMedia(stack);
            return 1.049658f * (float) Math.log((float) max / MediaConstants.CRYSTAL_UNIT + 9.06152f) - 2.1436f;
        }
        return 0;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
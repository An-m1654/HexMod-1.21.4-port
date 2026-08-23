package at.petrak.hexcasting.xplat.overrides;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.item.VariantItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

// suck it fabric trying to be "safe"
public record MediaPredicate() implements RangeSelectItemModelProperty {
    public static final MapCodec<MediaPredicate> MAP_CODEC = MapCodec.unit(MediaPredicate::new);
    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity,
                     int seed) {
        if (stack.getItem() instanceof MediaHolderItem item) {
            return item.getMediaFullness(stack);
        }
        return 0;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
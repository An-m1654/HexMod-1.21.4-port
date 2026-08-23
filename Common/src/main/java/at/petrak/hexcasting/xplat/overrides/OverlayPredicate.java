package at.petrak.hexcasting.xplat.overrides;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import at.petrak.hexcasting.api.item.OverlayItem;
import at.petrak.hexcasting.api.item.VariantItem;
import at.petrak.hexcasting.common.lib.HexDataComponents;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

// suck it fabric trying to be "safe"
public record OverlayPredicate() implements RangeSelectItemModelProperty {
    public static final MapCodec<OverlayPredicate> MAP_CODEC = MapCodec.unit(OverlayPredicate::new);
    @Override
    public float get(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity,
                     int seed) {
        if (stack.getItem() instanceof OverlayItem overlayItem) {
            Predicate<ItemStack> hasIota = overlayItem.hasIota();
            if (!hasIota.test(stack) && !stack.has(HexDataComponents.VISUAL_OVERRIDE)) {
                return 0;
            }
            Predicate<ItemStack> isSealed = overlayItem.isSealed();
            if (!isSealed.test(stack)) {
                return 1;
            }
            return 2;
        }
        return 0;
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}
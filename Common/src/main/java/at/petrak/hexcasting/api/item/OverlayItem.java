package at.petrak.hexcasting.api.item;

import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public interface OverlayItem {
    Predicate<ItemStack> isSealed();
    Predicate<ItemStack> hasIota();
}

package at.petrak.hexcasting.client;

import at.petrak.hexcasting.api.item.IotaHolderItem;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record IotaStorageColorizerTintSource(ItemStack item) implements ItemTintSource {
    public static final MapCodec<IotaStorageColorizerTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ItemStack.CODEC.fieldOf("item").forGetter(IotaStorageColorizerTintSource::item)
    ).apply(instance, IotaStorageColorizerTintSource::new));

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        if (item.getItem() instanceof IotaHolderItem typedItem) {
            return typedItem.getColor(itemStack);
        }
        LogUtils.getLogger().warn("ItemStack gotten is NOT instanceof IotaHolderItem. " +
                "This message is related to the IotaStorageColorizerTintSource thingy. " +
                "If this shows up, tell me and I'll be like: BRUHHHHHH and try to fix it.");
        return 0xff_ffffff;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}

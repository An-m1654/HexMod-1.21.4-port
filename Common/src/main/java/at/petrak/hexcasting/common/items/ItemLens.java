package at.petrak.hexcasting.common.items;

import at.petrak.hexcasting.annotations.SoftImplement;
import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.common.items.magic.ItemTrinket;
import at.petrak.hexcasting.common.lib.HexAttributes;
import at.petrak.hexcasting.xplat.IXplatAccessoriesAbstractions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class ItemLens extends Item implements HexBaubleItem { // Wearable,

    // The 0.1 is *additive*

    public static final AttributeModifier GRID_ZOOM = new AttributeModifier(
            HexAPI.modLoc("scrying_lens_zoom"), 0.33, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);

    public static final AttributeModifier SCRY_SIGHT = new AttributeModifier(
            HexAPI.modLoc("scrying_lens_sight"), 1.0, AttributeModifier.Operation.ADD_VALUE);

    public static final ItemAttributeModifiers MODIFIERS = ItemAttributeModifiers.builder()
        .add(HexAttributes.GRID_ZOOM, GRID_ZOOM, EquipmentSlotGroup.HAND)
        .add(HexAttributes.GRID_ZOOM, GRID_ZOOM, EquipmentSlotGroup.HEAD)
        .add(HexAttributes.SCRY_SIGHT, SCRY_SIGHT, EquipmentSlotGroup.HAND)
        .add(HexAttributes.SCRY_SIGHT, SCRY_SIGHT, EquipmentSlotGroup.HEAD)
        .build();

    public static class MobCanWearArmorEntitySelector implements Predicate<Entity> {
        private final ItemStack itemStack;

        public MobCanWearArmorEntitySelector(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        public boolean test(@Nullable Entity entity) {
            if (!entity.isAlive()) {
                return false;
            } else {
                if (!(entity instanceof LivingEntity livingEntity)) {
                    return false;
                } else {
                    return livingEntity.getItemBySlot(livingEntity.getEquipmentSlotForItem(itemStack)).isEmpty();
                }
            }
        }
    }

    public static boolean dispenseArmor(BlockSource blockSource, ItemStack itemStack) {
        BlockPos blockPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
        List<LivingEntity> list = blockSource.level()
                .getEntitiesOfClass(LivingEntity.class, new AABB(blockPos), EntitySelector.NO_SPECTATORS.and(new MobCanWearArmorEntitySelector(itemStack)));
        if (list.isEmpty()) {
            return false;
        } else {
            LivingEntity livingEntity = list.get(0);
            EquipmentSlot equipmentSlot = livingEntity.getEquipmentSlotForItem(itemStack);
            ItemStack itemStack2 = itemStack.split(1);
            livingEntity.setItemSlot(equipmentSlot, itemStack2);
            if (livingEntity instanceof Mob) {
                ((Mob)livingEntity).setDropChance(equipmentSlot, 2.0F);
                ((Mob)livingEntity).setPersistenceRequired();
            }

            return true;
        }
    }

    public ItemLens(Properties pProperties) {
        super(pProperties);
        DispenserBlock.registerBehavior(this, new OptionalDispenseItemBehavior() {
            protected @NotNull ItemStack execute(@NotNull BlockSource world, @NotNull ItemStack stack) {
                this.setSuccess(dispenseArmor(world, stack));
                return stack;
            }
        });
    }

    @Override
    public Multimap<Holder<Attribute>, AttributeModifier> getHexBaubleAttrs(ItemStack stack) {
        HashMultimap<Holder<Attribute>, AttributeModifier> out = HashMultimap.create();
        out.put(HexAttributes.GRID_ZOOM, GRID_ZOOM);
        out.put(HexAttributes.SCRY_SIGHT, SCRY_SIGHT);
        return out;
    }

    // In fabric impled with extension property?
    @Nullable
    @SoftImplement("forge")
    public EquipmentSlot getEquipmentSlot(ItemStack stack) {
        return EquipmentSlot.HEAD;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand interactionHand) {
        if (IXplatAccessoriesAbstractions.INSTANCE.accessoryModInstalled()) {
            return IXplatAccessoriesAbstractions.INSTANCE.customUseCode(level, player, interactionHand, super::use);
        }
        return super.use(level, player, interactionHand);
    }

    //    @Nullable
//    @Override
//    public SoundEvent getEquipSound() {
//        return SoundEvents.AMETHYST_BLOCK_CHIME;
//    }

}

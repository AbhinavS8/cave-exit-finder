//package net.dominosq.dqpetrespawn.item.custom;
//
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.network.chat.Component;
//import net.minecraft.world.InteractionHand;
//import net.minecraft.world.InteractionResult;
//import net.minecraft.world.entity.EntityType;
//import net.minecraft.world.entity.LivingEntity;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.TooltipFlag;
//
//import java.util.List;
//import java.util.UUID;
//
//public class PetCharmItem extends Item {
//    public PetCharmItem(Properties properties) {
//        super(properties);
//    }
//
//    @Override
//    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand usedHand) {
//        if (!player.level().isClientSide) {
//            if (isTamedPet(target)) {
//                //storePetData(stack, target);
//                player.displayClientMessage(Component.literal("Pet linked!"), true);
//                return InteractionResult.SUCCESS;
//            } else {
//                player.displayClientMessage(Component.literal("This entity cannot be linked."), true);
//                return InteractionResult.FAIL;
//            }
//        }
//        return InteractionResult.PASS;
//    }
//
//    private boolean isTamedPet(LivingEntity entity) {
//        // Example: Allow cats, dogs, and parrots to be linked
//        return entity.getType() == EntityType.WOLF ||
//                entity.getType() == EntityType.CAT ||
//                entity.getType() == EntityType.PARROT;
//    }
//
//
//
//
//}

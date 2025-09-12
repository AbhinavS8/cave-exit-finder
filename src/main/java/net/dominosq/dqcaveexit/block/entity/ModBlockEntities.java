//package net.dominosq.dqpetrespawn.block.entity;
//
//import net.dominosq.dqpetrespawn.DQPetRespawn;
//import net.dominosq.dqpetrespawn.block.ModBlocks;
//import net.minecraft.core.registries.BuiltInRegistries;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.neoforged.bus.api.IEventBus;
//import net.neoforged.neoforge.registries.DeferredRegister;
//
//import java.util.function.Supplier;
//
//public class ModBlockEntities {
//    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
//            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DQPetRespawn.MODID);
//
//    public static final Supplier<BlockEntityType<PetRespawnAnchorBlockEntity>> PET_RESPAWN_ANCHOR_BE =
//            BLOCK_ENTITIES.register("pet_respawn_anchor_be",() -> BlockEntityType.Builder.of(
//                    PetRespawnAnchorBlockEntity::new, ModBlocks.PET_RESPAWN_ANCHOR_BLOCK.get()).build(null));
//
//    public static void register(IEventBus eventBus) {
//        BLOCK_ENTITIES.register(eventBus);
//    }
//}

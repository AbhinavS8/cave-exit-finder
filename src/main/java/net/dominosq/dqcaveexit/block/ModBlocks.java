package net.dominosq.dqcaveexit.block;

import net.dominosq.dqcaveexit.DQCaveExit;
import net.dominosq.dqcaveexit.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DQCaveExit.MODID);

//    public static final DeferredBlock<Block> PET_RESPAWN_ANCHOR_BLOCK = registerBlock("pet_respawn_anchor_block",
//            () -> new PetRespawnAnchorBlock(BlockBehaviour.Properties.of().noOcclusion()));

//    public static final DeferredBlock<Block> BISMUTH_ORE = registerBlock("bismuth_ore",
//            () -> new DropExperienceBlock(UniformInt.of(2, 4),
//                    BlockBehaviour.Properties.of().strength(3f).requiresCorrectToolForDrops()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }
    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}

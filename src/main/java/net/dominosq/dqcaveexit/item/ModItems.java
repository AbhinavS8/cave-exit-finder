package net.dominosq.dqcaveexit.item;

import net.dominosq.dqcaveexit.DQCaveExit;
import net.dominosq.dqcaveexit.item.custom.AStarItem;
import net.dominosq.dqcaveexit.item.custom.BFSItem;
import net.dominosq.dqcaveexit.item.custom.BeamSearchItem;
import net.dominosq.dqcaveexit.item.custom.BestFirstItem;
import net.dominosq.dqcaveexit.item.custom.BranchAndBoundItem;
import net.dominosq.dqcaveexit.item.custom.DFSItem;
import net.dominosq.dqcaveexit.item.custom.HillClimbingItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DQCaveExit.MODID);

    public static final DeferredItem<Item> BFS_SEARCH = ITEMS.register("bfs_search",
            () -> new BFSItem(new Item.Properties().durability(1)));

    public static final DeferredItem<Item> A_STAR_SEARCH = ITEMS.register("a_star_search",
            () -> new AStarItem(new Item.Properties().durability(1)));

    public static final DeferredItem<Item> BEAM_SEARCH = ITEMS.register("beam_search",
            () -> new BeamSearchItem(new Item.Properties().durability(1)));

    public static final DeferredItem<Item> DFS_SEARCH = ITEMS.register("dfs_search",
            () -> new DFSItem(new Item.Properties().durability(1)));

    public static final DeferredItem<Item> HILL_CLIMBING_SEARCH = ITEMS.register("hill_climbing_search",
            () -> new HillClimbingItem(new Item.Properties().durability(1)));

    public static final DeferredItem<Item> BEST_FIRST_SEARCH = ITEMS.register("best_first_search",
            () -> new BestFirstItem(new Item.Properties().durability(1)));

    public static final DeferredItem<Item> BRANCH_AND_BOUND_SEARCH = ITEMS.register("branch_and_bound_search",
            () -> new BranchAndBoundItem(new Item.Properties().durability(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
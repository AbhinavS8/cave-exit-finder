package net.dominosq.dqcaveexit.item;

import net.dominosq.dqcaveexit.DQCaveExit;
//import net.dominosq.dqpetrespawn.item.custom.PetCharmItem;
import net.dominosq.dqcaveexit.item.custom.CagedCanaryItem;
import net.dominosq.dqcaveexit.item.custom.Canary2Item;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DQCaveExit.MODID);

    public static final DeferredItem<Item> CAGED_CANARY = ITEMS.register("caged_canary",
            () -> new CagedCanaryItem(new Item.Properties().durability(1)));

    public static final DeferredItem<Item> A_CANARY = ITEMS.register("a_canary",
            () -> new Canary2Item(new Item.Properties().durability(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
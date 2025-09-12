package net.dominosq.dqcaveexit.item;

import net.dominosq.dqcaveexit.DQCaveExit;
//import net.dominosq.dqpetrespawn.item.custom.PetCharmItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(DQCaveExit.MODID);

//    public static final DeferredItem<Item> PET_CHARM_ITEM = ITEMS.register("pet_charm_item",
//            () -> new PetCharmItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
package net.mednours.letsgogambling.item;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.mednours.letsgogambling.LetsGoGambling;
import net.mednours.letsgogambling.item.custom.CoinItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static final Item DIRT_COIN = registerItem("dirt_coin", new CoinItem(new Item.Settings()));
    public static final Item STONE_COIN = registerItem("stone_coin", new CoinItem(new Item.Settings()));
    public static final Item WOOD_COIN = registerItem("wood_coin", new CoinItem(new Item.Settings()));
    public static final Item IRON_COIN = registerItem("iron_coin", new CoinItem(new Item.Settings()));
    public static final Item GOLD_COIN = registerItem("gold_coin", new CoinItem(new Item.Settings()));
    public static final Item EMERALD_COIN = registerItem("emerald_coin", new CoinItem(new Item.Settings()));
    public static final Item DIAMOND_COIN = registerItem("diamond_coin", new CoinItem(new Item.Settings()));
    public static final Item NETHERITE_COIN = registerItem("netherite_coin", new CoinItem(new Item.Settings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(LetsGoGambling.MOD_ID, name), item);
    }
    public static void registerModItems() {
        LetsGoGambling.LOGGER.info("Registering Mod Items for " + LetsGoGambling.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.add(DIRT_COIN);
            entries.add(WOOD_COIN);
            entries.add(STONE_COIN);
            entries.add(IRON_COIN);
            entries.add(GOLD_COIN);
            entries.add(EMERALD_COIN);
            entries.add(DIAMOND_COIN);
            entries.add(NETHERITE_COIN);
        });
    }


}

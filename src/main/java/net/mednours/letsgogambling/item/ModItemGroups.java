package net.mednours.letsgogambling.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.mednours.letsgogambling.LetsGoGambling;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup LETS_GO_GAMBLING_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(LetsGoGambling.MOD_ID, "lets_go_gambling_group"),
            FabricItemGroup.builder()
                    .icon(() -> new ItemStack(ModItems.GOLD_COIN))
                    .displayName(Text.translatable("itemGroup.lets-go-gambling.lets_go_gambling_group"))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.DIRT_COIN);
                        entries.add(ModItems.WOOD_COIN);
                        entries.add(ModItems.STONE_COIN);
                        entries.add(ModItems.IRON_COIN);
                        entries.add(ModItems.GOLD_COIN);
                        entries.add(ModItems.EMERALD_COIN);
                        entries.add(ModItems.DIAMOND_COIN);
                        entries.add(ModItems.NETHERITE_COIN);
                    })
                    .build());

    public static void registerItemGroups() {
        LetsGoGambling.LOGGER.info("Registering Mod Item Groups for " + LetsGoGambling.MOD_ID);
    }
}

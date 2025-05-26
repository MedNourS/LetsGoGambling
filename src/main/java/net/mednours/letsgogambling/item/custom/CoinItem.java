package net.mednours.letsgogambling.item.custom;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class CoinItem extends Item {
    public CoinItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack stack = user.getStackInHand(hand);
        Item item = stack.getItem();
        String id = Registries.ITEM.getId(item).getPath();

        if (!world.isClient) {

            switch (id) {
                case "dirt_coin":
                    // Logic for Dirt Coin
                    break;
                case "wood_coin":
                    // Logic for Stone Coin
                    break;
                case "stone_coin":
                    // Logic for Wood Coin
                    break;
                case "iron_coin":
                    // Logic for Iron Coin
                    break;
                case "gold_coin":
                    // Logic for Gold Coin
                    break;
                case "emerald_coin":
                    // Logic for Emerald Coin
                    break;
                case "diamond_coin":
                    // Logic for Diamond Coin
                    break;
                case "netherite_coin":
                    // Logic for Netherite Coin
                    break;
                default:
                    // Handle unknown coin type
                    System.out.println("Unknown coin type: " + id);
                    return TypedActionResult.fail(stack);
            }

            System.out.println("Coin used by " + user.getName().getString());
        }

        return TypedActionResult.success(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.lets-go-gambling.coin"));
        super.appendTooltip(stack, context, tooltip, type);
    }
}

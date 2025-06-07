package net.mednours.letsgogambling.item.custom;

import net.mednours.letsgogambling.sounds.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import net.minecraft.entity.effect.StatusEffect;

public class CoinItem extends Item {
    public CoinItem(Settings settings) {
        super(settings);
    }

    Integer streak = 0;
    double successChance = 0;
    boolean wasHoldingCoin = false;

    private static final Set<RegistryEntry<StatusEffect>> REMOVABLE_EFFECTS = Set.of(
        StatusEffects.SPEED,
        StatusEffects.REGENERATION,
        StatusEffects.STRENGTH,
        StatusEffects.RESISTANCE,
        StatusEffects.LUCK,
        StatusEffects.HASTE,
        StatusEffects.HEALTH_BOOST,
        StatusEffects.ABSORPTION
    );

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            boolean isHoldingCoin = player.getMainHandStack() == stack || player.getOffHandStack() == stack;

            if (isHoldingCoin && !wasHoldingCoin) {
                world.playSound(
                        null,
                        player.getBlockPos(),
                        ModSounds.COIN_HOLD_EVENT,
                        player.getSoundCategory(),
                        0.25f,
                        1.0f
                );
            }

            wasHoldingCoin = isHoldingCoin;
        }

        super.inventoryTick(stack, world, entity, slot, selected);
    }

    /**
     * When the coin is used, it will roll a chance based on the coin type and streak.
     * If successful, it will apply a positive effect and give items.
     * If failed, it will remove positive effects, take valuable items, apply negative effects,
     * and possibly spawn a hostile mob.
     */
    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {

        ItemStack stack = user.getStackInHand(hand);
        Item item = stack.getItem();
        String id = Registries.ITEM.getId(item).getPath();

        // Get the type of coin from the item ID
        int coinRank = switch (id) {
            // case "dirt_coin" -> 0;
            case "wood_coin" -> 1;
            case "stone_coin" -> 2;
            case "iron_coin" -> 3;
            case "gold_coin" -> 4;
            case "emerald_coin" -> 5;
            case "diamond_coin" -> 6;
            case "netherite_coin" -> 7;
            default -> 0;
        };

        // Set the base success chance based on coin rank and define a streak impact
        double baseSuccess = 0.25 + (coinRank * 0.08);
        double successStreakScale = -0.04 * (streak); // -4% per streak
        double failureStreakScale = 0.02 * (streak); // 2% per streak

        // Cap the success chance at 80%
        successChance = Math.min(0.80, baseSuccess + successStreakScale); // never above 80%


        if (!world.isClient) {
            boolean success = Math.random() < successChance;
            if (success) {

                streak++; // Add one to the streak

                /*
                    Positive outcome 1: Apply a random positive effect
                 */

                int amp = Math.max(1, Math.min(1 + coinRank / 2 + streak / 4, 5)); // Amplifier based on coin rank and streak

                // List of positive effects to choose from
                StatusEffectInstance[] positiveEffects = new StatusEffectInstance[] {
                    new StatusEffectInstance(StatusEffects.SPEED, 600 * coinRank, amp),
                    new StatusEffectInstance(StatusEffects.REGENERATION, 600 * coinRank, amp),
                    new StatusEffectInstance(StatusEffects.STRENGTH, 600 * coinRank, amp),
                    new StatusEffectInstance(StatusEffects.RESISTANCE, 600 * coinRank, amp),
                    new StatusEffectInstance(StatusEffects.HASTE, 600 * coinRank, amp),
                    new StatusEffectInstance(StatusEffects.HEALTH_BOOST, 600 * coinRank, amp),
                    new StatusEffectInstance(StatusEffects.ABSORPTION, 600 * coinRank, amp)
                };

                // Randomly select a positive effect ...
                StatusEffectInstance effect = positiveEffects[(int)(Math.random()*positiveEffects.length)];
                user.addStatusEffect(effect); // ... and apply it to the user

                // If the coin rank is 5 (emerald) or higher and has a streak higher than 2 ...
                if (coinRank >= 5 && streak > 2 && Math.random() < 0.5) {
                    while (Math.random() < 0.5) {
                        user.addStatusEffect(positiveEffects[(int) (Math.random() * positiveEffects.length)]);
                        // ... apply additional effects with a 50% chance until it fails
                    }
                }

                /*
                    Positive outcome 2: Give items based on coin rank and streak
                 */

                // Determine how many items to be obtained with coin rank
                int itemsToGive = 0;
                for (int i = 0; i < coinRank; i++) {
                    if (Math.random() < 0.125) { // 12.5% chance per possible item
                        itemsToGive++;
                    }
                }

                if (streak > 3) itemsToGive++; // Add one more item for streaks above 3

                for (int i = 0; i < itemsToGive; i++) {
                    int rewardTier = Math.max(0, coinRank - (int)(Math.random() * 3)); // current, -1, or -2
                    int rewardCount = (int)(Math.random() * (coinRank + 1) + (double) streak / 2);

                    ItemStack reward = switch (rewardTier) {
                        case 0 -> new ItemStack(Items.DIRT, rewardCount);
                        case 1 -> new ItemStack(Items.OAK_LOG, rewardCount);
                        case 2 -> new ItemStack(Items.COBBLESTONE, rewardCount);
                        case 3 -> new ItemStack(Items.IRON_INGOT, rewardCount);
                        case 4 -> new ItemStack(Items.GOLD_INGOT, rewardCount);
                        case 5 -> new ItemStack(Items.EMERALD, rewardCount);
                        case 6 -> new ItemStack(Items.DIAMOND, rewardCount);
                        case 7 -> new ItemStack(Items.NETHERITE_SCRAP, rewardCount);
                        default -> new ItemStack(Items.AIR, rewardCount);
                    };

                    if (!user.getInventory().insertStack(reward)) {
                        user.dropItem(reward, false);
                    }
                }

                // Play the success sound effect
                if (Math.random() < 0.5) {
                    world.playSound(
                            null,
                            user.getBlockPos(),
                            ModSounds.COIN_SUCCESS_1_EVENT,
                            user.getSoundCategory(),
                            0.25f,
                            1.0f
                    );
                } else {
                    world.playSound(
                            null,
                            user.getBlockPos(),
                            ModSounds.COIN_SUCCESS_2_EVENT,
                            user.getSoundCategory(),
                            0.25f,
                            1.0f
                    );
                }

                user.sendMessage(Text.literal("Success! Streak: " + streak), true);

            } else {

                /*
                    Negative outcome 1: Remove positive effects
                */

                // Check if the user has any positive effects and remove them
                for (StatusEffectInstance eff : new ArrayList<>(user.getStatusEffects())) {
                    var type = eff.getEffectType();
                    if (type != null && REMOVABLE_EFFECTS.stream().anyMatch(type::equals)) {
                        user.removeStatusEffect(type);
                    }
                }

                /*
                    Negative outcome 2: Take valuable items
                */

                // Define what valuable items to take based on coin rank
                Item[] valuable = switch (coinRank) {
                    case 0 -> new Item[] {Items.DIRT};
                    case 1 -> new Item[] {Items.OAK_LOG, Items.SPRUCE_LOG, Items.BIRCH_LOG, Items.JUNGLE_LOG, Items.ACACIA_LOG, Items.DARK_OAK_LOG, Items.MANGROVE_LOG, Items.CHERRY_LOG, Items.BAMBOO_BLOCK};
                    case 2 -> new Item[] {Items.COBBLESTONE, Items.STONE};
                    case 3 -> new Item[] {Items.IRON_INGOT, Items.IRON_BLOCK};
                    case 4 -> new Item[] {Items.GOLD_INGOT, Items.GOLD_BLOCK};
                    case 5 -> new Item[] {Items.EMERALD, Items.EMERALD_BLOCK};
                    case 6 -> new Item[] {Items.DIAMOND, Items.DIAMOND_BLOCK};
                    case 7 -> new Item[] {Items.NETHERITE_INGOT, Items.NETHERITE_BLOCK, Items.NETHERITE_SCRAP};
                    default -> new Item[] {Items.AIR};
                };

                // Find all valuables in the user's inventory
                List<ItemStack> matching = new ArrayList<>();
                for (int i = 0; i < user.getInventory().size(); i++) {
                    ItemStack s = user.getInventory().getStack(i);
                    for (Item val : valuable) {
                        if (!s.isEmpty() && s.getItem() == val) {
                            matching.add(s);
                        }
                    }
                }

                /*
                    Negative outcome 3: A low chance to take EVERYTHING
                 */

                if (Math.random() < failureStreakScale) {
                    // Remove all items from inventory
                    for (int i = 0; i < user.getInventory().size(); i++) {
                        ItemStack s = user.getInventory().getStack(i);
                        if (!s.isEmpty()) s.decrement(s.getCount());
                    }

                    // Remove all armor
                    for (ItemStack s : user.getArmorItems()) {
                        if (!s.isEmpty()) s.decrement(s.getCount());
                    }

                    // Remove offhand
                    ItemStack off = user.getOffHandStack();
                    if (!off.isEmpty()) off.decrement(off.getCount());
                    user.sendMessage(Text.literal("Failure! The coin has taken EVERYTHING!"), true);
                    // Coin always breaks in this case
                    if (stack.getCount() > 0) stack.decrement(1);

                } else if (!matching.isEmpty()) { // If there are any valuable items found ...

                    ItemStack toRemove = matching.get((int)(Math.random()*matching.size())); // ... randomly select one of them ...
                    toRemove.decrement(1); // ... and remove one from the inventory

                    // After taking an item, also give a small chance to break the coin
                    if (stack.getCount() > 0 && Math.random() <  failureStreakScale) {
                        stack.decrement(1);
                        user.sendMessage(Text.literal("Failure! Streak lost. A valuable item was taken and the coin broke!"), true);
                    } else {
                        user.sendMessage(Text.literal("Failure! Streak lost. A valuable item was taken."), true);
                    }
                } else {
                    // No valuable item found, coin tries to break
                    if (Math.random() < failureStreakScale*3) {
                        if (stack.getCount() > 0) {
                            stack.decrement(1);
                            user.sendMessage(Text.literal("The coin broke! Streak lost."), true);
                        }
                    } else {
                        user.sendMessage(Text.literal("Failure! Streak lost. No valuable item was found."), true);
                    }

                }

                /*
                    Negative outcome 4: Apply negative effects
                 */

                // Apply negative effect(s) regardless
                int amp = Math.max(2, Math.min(2 + coinRank / 2 + streak / 3, 6));
                int duration = (15 + (int)(Math.random()*15) + coinRank * 2 + streak * 2) * 20; // 15-29s base, +coinRank, +streak, in ticks
                StatusEffectInstance[] negativeEffects = new StatusEffectInstance[] {
                    new StatusEffectInstance(StatusEffects.POISON, duration, amp),
                    new StatusEffectInstance(StatusEffects.WEAKNESS, duration, amp),
                    new StatusEffectInstance(StatusEffects.SLOWNESS, duration, amp),
                    new StatusEffectInstance(StatusEffects.NAUSEA, duration, amp),
                    new StatusEffectInstance(StatusEffects.BLINDNESS, duration, amp),
                    new StatusEffectInstance(StatusEffects.MINING_FATIGUE, duration, amp)
                };

                // Randomly select a negative effect and apply it
                user.addStatusEffect(negativeEffects[(int)(Math.random()*negativeEffects.length)]);

                // If the user had a streak higher than 2, roll additional negative effects
                if (streak >= 2 && Math.random() < 0.5) {
                    while (Math.random() < 0.5) {
                        user.addStatusEffect(negativeEffects[(int) (Math.random() * negativeEffects.length)]);
                        // ... apply additional effects with a 50% chance until it fails
                    }
                }

                /*
                    Negative outcome 5: Spawn a hostile mob
                 */

                if (Math.random() < 0.1 + failureStreakScale) { // 10% chance

                    EntityType<?>[] hostileMobs = new EntityType<?>[]{
                            EntityType.ZOMBIE,
                            EntityType.SKELETON,
                            EntityType.CREEPER,
                            EntityType.SPIDER,
                            EntityType.ENDERMAN,
                            EntityType.WITCH,
                            EntityType.PHANTOM,
                            EntityType.SLIME,
                            EntityType.WITHER_SKELETON
                    };

                    EntityType<?> randomMob = hostileMobs[(int) (Math.random() * hostileMobs.length)];

                    // Spawn a random hostile mob near the player
                    randomMob.spawn((ServerWorld) world, user.getBlockPos(), SpawnReason.EVENT);
                }

                // Play the failure sound effect
                world.playSound(
                        null,
                        user.getBlockPos(),
                        ModSounds.COIN_FAIL_EVENT,
                        user.getSoundCategory(),
                        0.25f,
                        1.0f
                );

                // And finally reset the streak
                streak = 0;
            }

            // Print the streak to the console for debugging
            System.out.println("Coin used by " + user.getName().getString() + " with streak: " + streak);
        }

        return TypedActionResult.success(stack);
    }

    /**
    When the coin is used on a block, it will reset the streak, reducing the impact of the negative effects.
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient() && context.getPlayer() != null) {
            this.streak = 0;
            context.getPlayer().sendMessage(Text.literal("Streak reset! Chances reset."), true);
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.lets-go-gambling.coin"));
        tooltip.add(Text.literal("Success chance: " + successChance));
        tooltip.add(Text.literal("Streak: " + streak));
        super.appendTooltip(stack, context, tooltip, type);
    }
}

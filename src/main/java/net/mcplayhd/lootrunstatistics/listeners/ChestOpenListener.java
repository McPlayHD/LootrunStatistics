package net.mcplayhd.lootrunstatistics.listeners;

import net.mcplayhd.lootrunstatistics.chests.utils.MinMax;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.PotionType;
import net.mcplayhd.lootrunstatistics.enums.PowderType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.DrawStringHelper;
import net.mcplayhd.lootrunstatistics.helpers.FormatterHelper;
import net.mcplayhd.lootrunstatistics.utils.Loc;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.*;
import static net.mcplayhd.lootrunstatistics.helpers.FormatterHelper.formatString;
import static net.mcplayhd.lootrunstatistics.helpers.FormatterHelper.getFormatted;

public class ChestOpenListener {
    private BlockPos chestLocation = null;
    private boolean chestConsidered = false;
    private int foundItemsUntilSlot = -1;
    private long lastItemArrived = -1;
    private int dryThisChest = 0;

    private Loc getLastChestLocation() {
        return chestLocation == null ?
                new Loc(0, -1, 0) :
                new Loc(chestLocation.getX(), chestLocation.getY(), chestLocation.getZ());
    }

    private EntityPlayerSP getPlayer() {
        return Minecraft.getMinecraft().player;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void openChest(PlayerInteractEvent.RightClickBlock e) {
        if (e.isCanceled()) return;
        BlockPos pos = e.getPos();
        IBlockState state = e.getEntityPlayer().world.getBlockState(pos);
        if (!(state.getBlock() instanceof BlockContainer)) return;
        // reset state
        chestConsidered = false;
        foundItemsUntilSlot = -1;
        lastItemArrived = -1;
        // storing the chest location
        chestLocation = pos.toImmutable();
        getLogger().info("Clicked chest at " + chestLocation.getX() + "," + chestLocation.getY() + "," + chestLocation.getZ() + ".");
    }

    /*
    Credits to https://github.com/albarv340/chestcountmod for using the InitGuiEvent to change the title of the chest
        and counting the chest.
     */
    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.InitGuiEvent event) {
        try { // don't want to crash
            if (event.getGui() == null) return;
            EntityPlayerSP player = getPlayer();
            if (player == null) return;
            Container openContainer = player.openContainer;
            if (!(openContainer instanceof ContainerChest)) return;
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(lowerInventory.getName()));
            if (containerName.startsWith("Loot Chest") && !containerName.contains("\u00a77\u00a7r") && !chestConsidered) {
                // this is a loot chest, and we did not yet change its name.
                getChestCountData().addChest();
                int totalChests = getChestCountData().getTotalChests();
                getDryData().addChestDry();
                dryThisChest = getDryData().getChestsDry();
                // "\u00a77\u00a7r" is our identifier.
                // It won't show because it just sets the color and resets it immediately.
                if (getConfiguration().displayTotalChestCountInChest()) {
                    lowerInventory.setCustomName(lowerInventory.getName() + "\u00a77\u00a7r" + " #" + getFormatted(totalChests));
                } else {
                    lowerInventory.setCustomName(lowerInventory.getName() + "\u00a77\u00a7r");
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    @SubscribeEvent
    public void guiDraw(GuiScreenEvent.DrawScreenEvent.Pre event) {
        try { // don't want to crash
            if (event.getGui() == null) return;
            EntityPlayerSP player = getPlayer();
            if (player == null) return;
            Container openContainer = player.openContainer;
            if (!(openContainer instanceof ContainerChest)) return;
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(lowerInventory.getName()));
            if (!containerName.startsWith("Loot Chest")) return;
            if (getConfiguration().displayDryCountInChest()) {
                // Credits to https://github.com/albarv340/chestcountmod
                GlStateManager.pushMatrix();
                GlStateManager.translate(1f, 1f, 1f);
                int screenWidth = event.getGui().width;
                int screenHeight = event.getGui().height;
                DrawStringHelper.drawStringLeft(getFormatted(dryThisChest) + " dry", screenWidth / 2 - 20, screenHeight / 2 - 11, new Color(64, 64, 64));
                GlStateManager.popMatrix();
            }
            if (chestConsidered) {
                // we only want to look at each chest once.
                return;
            }
            int newFoundItemsUntilSlot = foundItemsUntilSlot;
            for (int slot = foundItemsUntilSlot + 1; slot < lowerInventory.getSizeInventory(); slot++) {
                ItemStack itemStack = lowerInventory.getStackInSlot(slot);
                if (itemStack.getDisplayName().equals("Air")) {
                    continue;
                }
                newFoundItemsUntilSlot = slot;
            }
            if (newFoundItemsUntilSlot == foundItemsUntilSlot) {
                // no (new) items found...
                if (lastItemArrived != -1) {
                    // we already found items before
                    if (System.currentTimeMillis() > lastItemArrived + 250) {
                        // we didn't get any new items for 250ms so let's consider this chest finished
                        // because otherwise if the player manually adds an item to the chest it would count it
                        chestConsidered = true;
                    }
                }
                return;
            }
            // new items were added so let's actually add them to the chest
            Loc loc = getLastChestLocation();
            lastItemArrived = System.currentTimeMillis();
            if (foundItemsUntilSlot == -1) {
                // this is the first time we saw an item in this chest
                getChests().registerOpened(loc);
                containerName = containerName.substring("Loot Chest ".length());
                String[] sp = containerName.split(" ");
                String roman = sp[0];
                int tier = FormatterHelper.convertRomanToArabic(roman);
                getChests().setTier(loc, tier);
            }
            boolean dryDataUpdated = false;
            // we can now check every slot up until the slot we found the last item in
            getLogger().info("Checking from slot " + (foundItemsUntilSlot + 1) + " up to and including slot " + (newFoundItemsUntilSlot));
            for (int slot = foundItemsUntilSlot + 1; slot <= newFoundItemsUntilSlot; slot++) {
                try { // I intentionally cause exceptions because it's more convenient to develop
                    ItemStack itemStack = lowerInventory.getStackInSlot(slot);
                    if (itemStack.getDisplayName().equals("Air")) {
                        continue;
                    }
                    List<String> lore = itemStack.getTooltip(player, ITooltipFlag.TooltipFlags.ADVANCED);
                    Optional<String> itemType = lore.stream()
                            .filter(line -> Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(line)).contains("Type: ")).findFirst();
                    Optional<String> itemTier = lore.stream()
                            .filter(line -> Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(line)).contains("Tier: ")).findFirst();
                    Optional<String> itemLevel = lore.stream()
                            .filter(line -> Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(line)).contains("Lv. ")).findFirst();
                    Optional<String> combatLvMin = lore.stream()
                            .filter(line -> Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(line)).contains("Combat Lv. Min: ")).findFirst();
                    if (itemType.isPresent() && itemLevel.isPresent() && itemTier.isPresent()) {
                        String[] levelSp = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemLevel.get()))
                                .replace("Lv. Range: ", "\n")
                                .split("\n");
                        String[] fromTo = levelSp[1].split("-");
                        int minLvl = Integer.parseInt(fromTo[0]);
                        int maxLvl = Integer.parseInt(fromTo[1]);
                        MinMax minMax = new MinMax(minLvl, maxLvl);
                        ItemType type = ItemType.valueOf(Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemType.get()))
                                .replace("Type: ", "\n")
                                .split("\n")[1]
                                .toUpperCase());
                        Tier tier = Tier.valueOf(Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemTier.get()))
                                .replace("Tier: ", "\n")
                                .split("\n")[1]
                                .toUpperCase());
                        getDryData().addItemDry(tier);
                        dryDataUpdated = true;
                        if (tier == Tier.MYTHIC) {
                            String mythicString = itemStack.getDisplayName() + " " + itemLevel.get();
                            getMythicFindsData().addMythic(mythicString, loc);
                        }
                        getChests().addBox(loc, type, tier, minMax);
                    } else if (combatLvMin.isPresent()) {
                        int lvl = Integer.parseInt(
                                Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(combatLvMin.get()))
                                        .replace("Combat Lv. Min: ", "\n")
                                        .split("\n")[1]
                        );
                        String displayName = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName()));
                        if (displayName.startsWith("Potion of ")) {
                            PotionType potionType;
                            if (displayName.startsWith("Potion of Wisdom")) {
                                potionType = PotionType.WISDOM;
                            } else {
                                // all other potions have [x/X] behind, so we can parse it by reading the second last string
                                String[] displayNameSp = displayName.substring("Potion of ".length()).split(" ");
                                String potionTypeSt = displayNameSp[displayNameSp.length - 2];
                                potionType = PotionType.valueOf(potionTypeSt.toUpperCase());
                            }
                            getChests().addPotion(loc, potionType, lvl);
                        } else {
                            displayName = displayName.replace("Cap", "Helmet");
                            displayName = displayName.replace("Tunic", "Chestplate");
                            displayName = displayName.replace("Chain Mail", "Chestplate");
                            displayName = displayName.replace("Stick", "Wand");
                            String[] displayNameSp = displayName.split(" ");
                            ItemType type = ItemType.valueOf(displayNameSp[displayNameSp.length - 1].toUpperCase());
                            getDryData().addItemDry(Tier.NORMAL);
                            dryDataUpdated = true;
                            getChests().addNormalItem(loc, type, lvl);
                        }
                    } else {
                        String displayName = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName()));
                        if (displayName.equals("Emerald")) {
                            int emeralds = itemStack.getCount();
                            getChests().addEmeralds(loc, emeralds);
                            getDryData().addEmeralds(emeralds);
                            dryDataUpdated = true;
                        } else if (displayName.contains("Earth Powder")
                                || displayName.contains("Thunder Powder")
                                || displayName.contains("Fire Powder")
                                || displayName.contains("Water Powder")
                                || displayName.contains("Air Powder")) {
                            String[] displayNameSp = displayName.split(" ");
                            PowderType powderType = PowderType.valueOf(displayNameSp[1].toUpperCase());
                            String roman = displayNameSp[displayNameSp.length - 1];
                            int tier = FormatterHelper.convertRomanToArabic(roman);
                            getChests().addPowder(loc, powderType, tier);
                        } else if (displayName.contains("Emerald Pouch")) {
                            String roman = displayName.replace("Emerald Pouch [Tier ", "").replace("]", "");
                            int tier = FormatterHelper.convertRomanToArabic(roman);
                            getChests().addPouch(loc, tier);
                        } else if (displayName.contains("✫✫✫")) {
                            String name = displayName.replace(" [", "\n").split("\n")[0];
                            // 'Toxic Lumps [§8✫✫✫§7]'
                            // 'Sylphid Tears [§e✫§8✫✫§6]'
                            // 'Soulbound Cinders [§d✫✫§8✫§5]'
                            // 'Glacial Anomaly [§b✫✫✫§3]'
                            String displayNameRaw = itemStack.getDisplayName();
                            int tier = displayNameRaw.contains("[§8✫✫✫§7]") ? 0
                                    : displayNameRaw.contains("[§e✫§8✫✫§6]") ? 1
                                    : displayNameRaw.contains("[§d✫✫§8✫§5]") ? 2
                                    : displayNameRaw.contains("[§b✫✫✫§3]") ? 3
                                    : -1; // should never happen but whatever
                            Optional<String> craftingLvMin = lore.stream()
                                    .filter(line -> Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(line)).contains("Crafting Lv. Min: ")).findFirst();
                            int level = -1;
                            if (craftingLvMin.isPresent()) {
                                level = Integer.parseInt(
                                        Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(craftingLvMin.get()))
                                                .replace("Crafting Lv. Min: ", "\n")
                                                .split("\n")[1]
                                );
                            }
                            getChests().addIngredient(loc, name, tier, level);
                        } else {
                            player.sendMessage(formatString("§7[§3LootrunStatistics§7] §6Warning§7: §eSaved nothing for §7'" + itemStack.getDisplayName() + "§7'§8(§e" + itemStack.getCount() + "§8) §ein slot " + slot));
                            getLogger().warn("Saved nothing for '" + itemStack.getDisplayName() + "'(" + itemStack.getCount() + ") in slot " + slot);
                        }
                    }
                } catch (Throwable throwable) {
                    player.sendMessage(formatString("§7[§3LootrunStatistics§7] §6Warning§7: §eCaught exception §7'§e" + throwable.getMessage() + "§7' §efor slot " + slot));
                    getLogger().warn("Caught exception '" + throwable.getMessage() + "' for slot " + slot);
                }
            }
            // updating the biggest slot where an item was found
            foundItemsUntilSlot = newFoundItemsUntilSlot;
            // saving dry data if something changed
            if (dryDataUpdated) {
                getDryData().save();
            }
            // chest data is updated no matter what
            getChests().save();
            getChests().updateChestInfo(loc);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}

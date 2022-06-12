package net.mcplayhd.lootrunstatistics.listeners;

import net.mcplayhd.lootrunstatistics.chests.utils.MinMax;
import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.helpers.DrawStringHelper;
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
import static net.mcplayhd.lootrunstatistics.helpers.FormatterHelper.getFormatted;

public class ChestOpenListener {

    private BlockPos chestLocation = null;
    private boolean chestConsidered = false;
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
        chestLocation = pos.toImmutable();
        chestConsidered = false;
        getLogger().info("Clicked chest at " + chestLocation.getX() + "," + chestLocation.getY() + "," + chestLocation.getZ() + ".");
    }

    @SubscribeEvent
    public void onGuiOpen(GuiScreenEvent.InitGuiEvent event) {
        if (event.getGui() == null) return;
        EntityPlayerSP player = getPlayer();
        if (player == null) return;
        Container openContainer = player.openContainer;
        if (!(openContainer instanceof ContainerChest)) return;
        InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
        String containerName = lowerInventory.getName();
        if (containerName.contains("Loot Chest") && !containerName.contains("\u00a77\u00a7r")) {
            // this is a loot chest, and we did not yet change its name.
            getChestCountData().addChest();
            int totalChests = getChestCountData().getTotalChests();
            getDryData().addChestDry();
            dryThisChest = getDryData().getChestsDry();
            // "\u00a77\u00a7r" is our identifier.
            // It won't show because it just sets the color and resets it immediately.
            if (getConfiguration().displayTotalChestCountInChest()) {
                lowerInventory.setCustomName(containerName + "\u00a77\u00a7r" + " #" + getFormatted(totalChests));
            } else {
                lowerInventory.setCustomName(containerName + "\u00a77\u00a7r");
            }
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
            String containerName = lowerInventory.getName();
            if (!containerName.contains("Loot Chest")) return;
            if (getConfiguration().displayDryCountInChest()) {
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
            boolean itemFound = false;
            for (int slot = 0; slot < lowerInventory.getSizeInventory(); slot++) {
                ItemStack itemStack = lowerInventory.getStackInSlot(slot);
                if (itemStack.getDisplayName().equals("Air"))
                    continue;
                itemFound = true;
                break;
            }
            if (!itemFound) {
                // No items found so far...
                return;
            }
            // we know that all items are loaded into the chest at the exact same time so that's why we can proceed here.
            Loc loc = getLastChestLocation();
            chestConsidered = true;
            boolean dryDataUpdated = false;
            boolean chestsDatabaseUpdated = false;
            for (int slot = 0; slot < lowerInventory.getSizeInventory(); slot++) {
                try { // I intentionally cause exceptions because it's more convenient to develop
                    ItemStack itemStack = lowerInventory.getStackInSlot(slot);
                    if (itemStack.getDisplayName().equals("Air"))
                        continue;
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
                        chestsDatabaseUpdated = true;
                    } else if (combatLvMin.isPresent()) {
                        int lvl = Integer.parseInt(
                                Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(combatLvMin.get()))
                                        .replace("Combat Lv. Min: ", "\n")
                                        .split("\n")[1]
                        );
                        String displayName = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName()));
                        displayName = displayName.replace("Chain Mail", "Chestplate");
                        String[] displayNameSp = displayName.split(" ");
                        ItemType type = ItemType.valueOf(displayNameSp[displayNameSp.length - 1].toUpperCase());
                        getDryData().addItemDry(Tier.NORMAL);
                        dryDataUpdated = true;
                        getChests().addNormalItem(loc, type, lvl);
                        chestsDatabaseUpdated = true;
                    } else {
                        String displayName = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName()));
                        if (displayName.equals("Emerald")) {
                            getDryData().addEmeralds(itemStack.getCount());
                            dryDataUpdated = true;
                        } else {
                            getLogger().info("Saved nothing for '" + itemStack.getDisplayName() + "'(" + itemStack.getCount() + ") in slot " + slot);
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            if (dryDataUpdated) {
                getDryData().save();
            }
            containerName = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(containerName));
            containerName = containerName.substring("Loot Chest ".length());
            String[] sp = containerName.split(" ");
            String rome = sp[0];
            int tier = 0;
            switch (rome) {
                case "I":
                    tier = 1;
                    break;
                case "II":
                    tier = 2;
                    break;
                case "III":
                    tier = 3;
                    break;
                case "IV":
                    tier = 4;
                    break;
            }
            chestsDatabaseUpdated = chestsDatabaseUpdated || getChests().setTier(loc, tier);
            if (chestsDatabaseUpdated) {
                getChests().save();
                getChests().updateChestInfo(loc);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}

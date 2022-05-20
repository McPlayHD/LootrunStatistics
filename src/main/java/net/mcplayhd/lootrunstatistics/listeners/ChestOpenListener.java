package net.mcplayhd.lootrunstatistics.listeners;

import net.mcplayhd.lootrunstatistics.enums.ItemType;
import net.mcplayhd.lootrunstatistics.enums.Tier;
import net.mcplayhd.lootrunstatistics.utils.Loc;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static net.mcplayhd.lootrunstatistics.LootrunStatistics.*;

public class ChestOpenListener {

    private BlockPos chestLocation = null;
    private boolean chestConsidered = false;

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
    public void guiDraw(GuiScreenEvent.DrawScreenEvent.Pre event) {
        if (chestConsidered) {
            // we only want to look at each chest once.
            return;
        }
        try { // don't want to crash
            if (event.getGui() == null) return;
            EntityPlayerSP player = getPlayer();
            if (player == null) return;
            Container openContainer = player.openContainer;
            if (!(openContainer instanceof ContainerChest)) return;
            InventoryBasic lowerInventory = (InventoryBasic) ((ContainerChest) openContainer).getLowerChestInventory();
            String containerName = lowerInventory.getName();
            if (!containerName.contains("Loot Chest")) return;
            boolean itemFound = false;
            for (int slot = 0; slot < lowerInventory.getSizeInventory(); slot++) {
                ItemStack itemStack = lowerInventory.getStackInSlot(slot);
                if (itemStack.getDisplayName().equals("Air")) continue;
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
            getChestCountData().addChest();
            getDryData().addChestDry();
            for (int slot = 0; slot < lowerInventory.getSizeInventory(); slot++) {
                try { // I intentionally cause exceptions because it's more convenient to develop
                    ItemStack itemStack = lowerInventory.getStackInSlot(slot);
                    if (itemStack.getDisplayName().equals("Air")) continue;
                    getLogger().info("Found " + itemStack.getDisplayName() + "(" + itemStack.getCount() + ") in slot " + slot);
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
                        // I need this for now because it will throw exceptions if the type is nothing I want to track
                        ItemType type = ItemType.valueOf(Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemType.get()))
                                .replace("Type: ", "\n")
                                .split("\n")[1]
                                .toUpperCase());
                        Tier tier = Tier.valueOf(Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemTier.get()))
                                .replace("Tier: ", "\n")
                                .split("\n")[1]
                                .toUpperCase());
                        getDryData().addItemDry(tier);
                        if (tier == Tier.MYTHIC) {
                            String mythicString = itemStack.getDisplayName() + " " + itemLevel.get();
                            getMythicFindsData().addMythic(mythicString, loc);
                        }
                    } else if (combatLvMin.isPresent()) {
                        String displayName = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName()));
                        displayName = displayName.replace("Chain Mail", "Chestplate");
                        String[] displayNameSp = displayName.split(" ");
                        // I need this for now because it will throw exceptions if the type is nothing I want to track
                        ItemType type = ItemType.valueOf(displayNameSp[displayNameSp.length - 1].toUpperCase());
                        getDryData().addItemDry(Tier.NORMAL);
                    } else {
                        String displayName = Objects.requireNonNull(TextFormatting.getTextWithoutFormattingCodes(itemStack.getDisplayName()));
                        if (displayName.equals("Emerald")) {
                            getDryData().addEmeralds(itemStack.getCount());
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            getDryData().save();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}

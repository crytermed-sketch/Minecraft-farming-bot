package io.crytermed.farmbot;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class InventoryUtils {

    public static int findSeedSlot(ClientPlayerEntity player) {
        for (int i = 0; i < 9; i++) {
            if (isSeedItem(player.getInventory().getStack(i).getItem())) return i;
        }
        for (int i = 9; i < player.getInventory().size(); i++) {
            if (isSeedItem(player.getInventory().getStack(i).getItem())) return i;
        }
        return -1;
    }

    private static boolean isSeedItem(Item item) {
        return item == Items.WHEAT_SEEDS
            || item == Items.CARROT
            || item == Items.POTATO
            || item == Items.BEETROOT;
    }
}
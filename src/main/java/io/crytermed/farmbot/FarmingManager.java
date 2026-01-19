package io.crytermed.farmbot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.Direction;
import net.minecraft.text.Text;

import java.util.Optional;

public class FarmingManager {
    private final int radius;
    private volatile boolean active = false;
    private volatile String ownerPlayer = null;

    public FarmingManager(int radius) {
        this.radius = radius;
    }

    public void startForPlayer(String playerName) {
        this.ownerPlayer = playerName;
        this.active = true;
    }

    public void stop() {
        this.active = false;
        this.ownerPlayer = null;
    }

    public void tick(MinecraftClient client) {
        if (!active) return;
        ClientPlayerEntity player = client.player;
        if (player == null || client.world == null) return;

        BlockPos center = player.getBlockPos();

        Optional<BlockPos> found = CropUtils.findNearestMatureCrop(client, center, radius);
        if (found.isEmpty()) {
            return;
        }

        BlockPos cropPos = found.get();

        try {
            if (client.interactionManager != null) {
                client.interactionManager.attackBlock(cropPos, Direction.UP);
            } else {
                player.swingHand(Hand.MAIN_HAND);
            }

            boolean replanted = CropUtils.tryReplantAt(client, player, cropPos);
            if (!replanted) {
                if (ownerPlayer != null && !ownerPlayer.isEmpty()) {
                    String msg = "/msg " + ownerPlayer + " Throw away unnecessary items from your inventory.";
                    client.player.sendChatMessage(msg);
                    this.active = false;
                }
            }
        } catch (Throwable t) {
            t.printStackTrace();
            this.active = false;
        }
    }
}
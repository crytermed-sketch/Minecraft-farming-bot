package io.crytermed.farmbot;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.BlockState;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;

public class CropUtils {

    public static java.util.Optional<BlockPos> findNearestMatureCrop(MinecraftClient client, BlockPos center, int radius) {
        if (client.world == null) return java.util.Optional.empty();
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;

        for (int dx = -radius; dx <= radius; dx++) {
            for (int dz = -radius; dz <= radius; dz++) {
                for (int dy = -2; dy <= 2; dy++) {
                    BlockPos p = center.add(dx, dy, dz);
                    BlockState st = client.world.getBlockState(p);
                    if (isMatureCrop(st)) {
                        double dist = center.getSquaredDistance(p);
                        if (dist < bestDist) {
                            bestDist = dist;
                            best = p;
                        }
                    }
                }
            }
        }

        return java.util.Optional.ofNullable(best);
    }

    public static boolean isMatureCrop(BlockState st) {
        if (st.isOf(Blocks.WHEAT) || st.isOf(Blocks.CARROTS) || st.isOf(Blocks.POTATOES) || st.isOf(Blocks.BEETROOTS)) {
            if (st.contains(Properties.AGE)) {
                Integer age = st.get(Properties.AGE);
                int maxAge = st.isOf(Blocks.BEETROOTS) ? 3 : 7;
                return age != null && age >= maxAge;
            }
        }
        if (st.isOf(Blocks.MELON) || st.isOf(Blocks.MELON_BLOCK)) {
            return true;
        }
        return false;
    }

    public static boolean tryReplantAt(MinecraftClient client, ClientPlayerEntity player, BlockPos cropPos) {
        BlockPos below = cropPos.down();
        BlockState belowState = client.world.getBlockState(below);
        if (!belowState.isOf(Blocks.FARMLAND)) {
            return false;
        }

        int slotWithSeed = InventoryUtils.findSeedSlot(player);
        if (slotWithSeed < 0) return false;

        player.getInventory().selectedSlot = slotWithSeed;
        BlockHitResult hit = new BlockHitResult(player.getPos(), Direction.UP, cropPos, false);
        player.interactBlock(player.world, player.getStackInHand(Hand.MAIN_HAND), Hand.MAIN_HAND, hit);
        return true;
    }
}
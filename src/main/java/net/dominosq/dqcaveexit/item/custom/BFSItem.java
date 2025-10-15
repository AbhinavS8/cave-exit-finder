package net.dominosq.dqcaveexit.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class BFSItem extends Item {

    public BFSItem(Properties properties) {
        super(properties);
    }

    private int getMoveCost(Level level, BlockPos to) {
        int cost = 1;

        for (BlockPos offset : BlockPos.betweenClosed(-1, -1, -1, 1, 1, 1)) {
            if (level.getBlockState(to.offset(offset)).is(Blocks.LAVA)) {
                cost += 16;
                break;
            }
            if (level.getBlockState(to.offset(offset)).is(Blocks.GRAVEL)) {
                cost+= 1;
            }
        }

        return cost;
    }
    
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            Queue<BlockPos> queue = new ArrayDeque<>();
            Map<BlockPos, BlockPos> parentMap = new HashMap<>(); // child -> parent
            Set<BlockPos> visited = new HashSet<>();

            BlockPos startPos = player.blockPosition();
            queue.add(startPos);
            visited.add(startPos);
            parentMap.put(startPos, null); // start has no parent

            BlockPos goalPos = null;

            long startTime = System.nanoTime();  // Start timer

            while (!queue.isEmpty()) {
                BlockPos pos = queue.poll();

                if (level.canSeeSkyFromBelowWater(pos)) {
                    goalPos = pos;

                    // mark exit
                    level.setBlock(pos, Blocks.RED_WOOL.defaultBlockState(), 3);

                    String msg = "Found exit at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
                    player.sendSystemMessage(Component.literal(msg));
                    break;
                }

                // neighbors
                for (BlockPos next : Arrays.asList(
                        pos.above(), pos.below(), pos.north(),
                        pos.south(), pos.east(), pos.west()
                )) {
                    BlockState state = level.getBlockState(next);
                    // block radius bound
                    if (state.isAir() && !visited.contains(next) && startPos.distToCenterSqr(next.getCenter()) < 20000) {
                        queue.add(next);
                        visited.add(next);
                        parentMap.put(next, pos); // store parent for path reconstruction
                    }
                }
            }

            // Reconstruct path and place white wool
            if (goalPos != null) {
                List<BlockPos> path = new ArrayList<>();
                BlockPos current = goalPos;

                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }

                // path is from goal → start, reverse it if needed
                Collections.reverse(path);

                // Calculate total path cost
                int totalPathCost = 0;
                for (int i = 1; i < path.size(); i++) { // Start from 1 to skip start position
                    totalPathCost += getMoveCost(level, path.get(i));
                }

                for (BlockPos block : path) {
                    if (!block.equals(startPos) && !block.equals(goalPos)) {
                        level.setBlock(block, Blocks.WHITE_WOOL.defaultBlockState(), 3);
                    }
                }

                // Report total path cost
                player.sendSystemMessage(Component.literal("Total path cost: " + totalPathCost));
            }
            long endTime = System.nanoTime();    // End timer
            long duration = (endTime - startTime) / 1_000_000; // ms

            player.sendSystemMessage(Component.literal("BFS: " + duration + " ms"));

        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}

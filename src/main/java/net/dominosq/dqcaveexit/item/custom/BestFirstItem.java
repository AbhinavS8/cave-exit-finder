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

public class BestFirstItem extends Item {

    public BestFirstItem(Properties properties) {
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

            PriorityQueue<BlockPos> openList = new PriorityQueue<>(
                    Comparator.comparingInt((BlockPos pos) -> -pos.getY()) // higher Y = higher priority
            );

            Map<BlockPos, BlockPos> parentMap = new HashMap<>();
            Set<BlockPos> visited = new HashSet<>();

            BlockPos startPos = player.blockPosition();
            openList.add(startPos);
            visited.add(startPos);
            parentMap.put(startPos, null);

            BlockPos goalPos = null;

            long startTime = System.nanoTime();

            while (!openList.isEmpty()) {
                BlockPos current = openList.poll();

                // Check for goal
                if (level.canSeeSkyFromBelowWater(current)) {
                    goalPos = current;
                    level.setBlock(current, Blocks.RED_WOOL.defaultBlockState(), 3);
                    player.sendSystemMessage(Component.literal(
                            "Best-First Search exit found at: " + current.getX() + ", " + current.getY() + ", " + current.getZ()
                    ));
                    break;
                }

                // Explore neighbors
                for (BlockPos next : Arrays.asList(
                        current.above(), current.below(),
                        current.north(), current.south(),
                        current.east(), current.west()
                )) {
                    BlockState state = level.getBlockState(next);
                    if (state.isAir() && !visited.contains(next) //&& startPos.distToCenterSqr(next.getCenter()) < 20000
                            ) {

                        openList.add(next);
                        visited.add(next);
                        parentMap.put(next, current);
                    }
                }
            }

            // Reconstruct path
            if (goalPos != null) {
                List<BlockPos> path = new ArrayList<>();
                BlockPos current = goalPos;
                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path);

                // Calculate total path cost
                int totalPathCost = 0;
                for (int i = 1; i < path.size(); i++) { // Start from 1 to skip start position
                    totalPathCost += getMoveCost(level, path.get(i));
                }

                for (BlockPos block : path) {
                    if (!block.equals(startPos) && !block.equals(goalPos)) {
                        level.setBlock(block, Blocks.LIME_CONCRETE.defaultBlockState(), 3);
                    }
                }

                // Report total path cost
                player.sendSystemMessage(Component.literal("Total path cost: " + totalPathCost));
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            player.sendSystemMessage(Component.literal("Best-First Search took " + duration + " ms"));
        }

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}

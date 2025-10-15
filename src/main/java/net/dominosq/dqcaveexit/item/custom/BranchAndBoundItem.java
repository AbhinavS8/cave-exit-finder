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

import java.util.*;

public class BranchAndBoundItem extends Item {
    public BranchAndBoundItem(Properties properties) {
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
                cost += 1;
            }
        }
        return cost;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            BlockPos startPos = player.blockPosition();
            Set<BlockPos> extendedList = new HashSet<>();
            Map<BlockPos, BlockPos> parentMap = new HashMap<>();
            Map<BlockPos, Integer> costMap = new HashMap<>();
            PriorityQueue<BlockPos> open = new PriorityQueue<>(Comparator.comparingInt(costMap::get));

            open.add(startPos);
            costMap.put(startPos, 0);
            parentMap.put(startPos, null);

            BlockPos goalPos = null;
            int bestCost = Integer.MAX_VALUE;

            long startTime = System.nanoTime();

            while (!open.isEmpty()) {
                BlockPos current = open.poll();
                int currentCost = costMap.get(current);
                extendedList.add(current);

                if (level.canSeeSkyFromBelowWater(current)) {
                    if (currentCost < bestCost) {
                        bestCost = currentCost;
                        goalPos = current;
                    }
                    continue;
                }

                for (BlockPos next : Arrays.asList(
                        current.above(), current.below(),
                        current.north(), current.south(),
                        current.east(), current.west()
                )) {
                    if (!level.getBlockState(next).isAir()) continue;
                    if (extendedList.contains(next)) continue;
                    int moveCost = getMoveCost(level, next);
                    int newCost = currentCost + moveCost;
                    if (newCost >= bestCost) continue; // prune paths worse than best found
                    if (!costMap.containsKey(next) || newCost < costMap.get(next)) {
                        costMap.put(next, newCost);
                        parentMap.put(next, current);
                        open.add(next);
                    }
                }
            }

            if (goalPos != null) {
                List<BlockPos> path = new ArrayList<>();
                BlockPos cur = goalPos;

                level.setBlock(goalPos, Blocks.RED_WOOL.defaultBlockState(), 3);
                while (cur != null) {
                    path.add(cur);
                    cur = parentMap.get(cur);
                }
                Collections.reverse(path);
                int totalPathCost = 0;
                for (int i = 1; i < path.size(); i++) {
                    totalPathCost += getMoveCost(level, path.get(i));
                }
                for (BlockPos block : path) {
                    if (!block.equals(startPos) && !block.equals(goalPos)) {
                        level.setBlock(block, Blocks.YELLOW_CONCRETE.defaultBlockState(), 3);
                    }
                }
                player.sendSystemMessage(Component.literal("Branch and Bound exit at: " + goalPos.getX() + ", " + goalPos.getY() + ", " + goalPos.getZ()));
                player.sendSystemMessage(Component.literal("Total path cost: " + totalPathCost));
            } else {
                player.sendSystemMessage(Component.literal("No exit found with branch and bound."));
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            player.sendSystemMessage(Component.literal("Branch and Bound took " + duration + " ms"));
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}

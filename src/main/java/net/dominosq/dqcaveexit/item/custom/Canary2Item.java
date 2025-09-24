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

public class Canary2Item extends Item {

    public Canary2Item(Properties properties) {
        super(properties);
    }

    //A*???????
    //AO*??????
    //heuristic - height
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {

            long startTime = System.nanoTime();


            // --- A* search ---
            Map<BlockPos, BlockPos> aStarParentMap = new HashMap<>();
            Map<BlockPos, Integer> gScore = new HashMap<>();
            Set<BlockPos> closedSet = new HashSet<>();

            BlockPos aStartPos = player.blockPosition();
            gScore.put(aStartPos, 0);
            aStarParentMap.put(aStartPos, null);

            // PriorityQueue: Node with lowest f = g + h first (inline heuristic)
            PriorityQueue<BlockPos> openSet = new PriorityQueue<>(Comparator.comparingInt(pos ->
                    gScore.getOrDefault(pos, Integer.MAX_VALUE) + (level.getMaxBuildHeight() - pos.getY())
            ));

            openSet.add(aStartPos);
            BlockPos aGoalPos = null;

            while (!openSet.isEmpty()) {
                BlockPos current = openSet.poll();
                if (closedSet.contains(current)) continue;
                closedSet.add(current);

                if (level.canSeeSkyFromBelowWater(current)) {
                    aGoalPos = current;
                    if (!level.isClientSide) {
                        level.setBlock(current, Blocks.RED_WOOL.defaultBlockState(), 3);
                    }
                    player.sendSystemMessage(Component.literal(
                            "A* exit found at: " + current.getX() + ", " + current.getY() + ", " + current.getZ()
                    ));
                    break;
                }

                for (BlockPos next : Arrays.asList(
                        current.above(), current.below(), current.north(),
                        current.south(), current.east(), current.west()
                )) {
                    BlockState state = level.getBlockState(next);
                    if (!state.isAir()) continue;
                    if (aStartPos.distToCenterSqr(next.getCenter()) > 20000) continue;

                    int tentativeG = gScore.get(current) + 1; // step cost = 1
                    if (tentativeG < gScore.getOrDefault(next, Integer.MAX_VALUE)) {
                        aStarParentMap.put(next, current);
                        gScore.put(next, tentativeG);
                        openSet.add(next);
                    }
                }
            }

            // Reconstruct A* path and place white wool
            if (aGoalPos != null && !level.isClientSide) {
                List<BlockPos> path = new ArrayList<>();
                BlockPos current = aGoalPos;

                while (current != null) {
                    path.add(current);
                    current = aStarParentMap.get(current);
                }

                Collections.reverse(path);
                for (BlockPos block : path) {
                    if (!block.equals(aStartPos) && !block.equals(aGoalPos)) {
                        level.setBlock(block, Blocks.WHITE_WOOL.defaultBlockState(), 3);
                    }
                }
            }


            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            player.sendSystemMessage(Component.literal("Search took " + duration + " ms"));

            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}

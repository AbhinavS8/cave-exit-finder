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

public class AStarItem extends Item {

    public AStarItem(Properties properties) {
        super(properties);
    }

    //A*???????
    //AO*??????
    //heuristic - height
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {

            long startTime = System.nanoTime();

            class Node {
                final BlockPos pos;
                final int g;
                final double f; // g + h

                Node(BlockPos pos, int g, double f) {
                    this.pos = pos; this.g = g; this.f = f;
                }
            }

// local copies (avoid repeated calls)
            final int maxY = level.getMaxBuildHeight();
            final double maxDistSq = 40000;
            final BlockPos aStartPos = player.blockPosition();

            Map<BlockPos, BlockPos> aStarParentMap = new HashMap<>();
            Map<BlockPos, Integer> gScore = new HashMap<>(); // best g seen so far
            Set<BlockPos> closedSet = new HashSet<>();

// PQ of Node objects ordered by f only (fast comparisons)
            PriorityQueue<Node> openPQ = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));

// init
            gScore.put(aStartPos, 0);
            aStarParentMap.put(aStartPos, null);
            double startH = (maxY - aStartPos.getY())*1.5;
            openPQ.add(new Node(aStartPos, 0, 0 + startH));

            int nodesExpanded = 0;
            BlockPos aGoalPos = null;

            while (!openPQ.isEmpty()) {
                Node node = openPQ.poll();
                BlockPos current = node.pos;

                // if we've already processed this pos with a better g, skip
                int recordedG = gScore.getOrDefault(current, Integer.MAX_VALUE);
                if (node.g != recordedG) {
                    // outdated Node (we found a better g earlier), skip
                    continue;
                }

                // mark as expanded
                nodesExpanded++;
                closedSet.add(current);

                if (level.canSeeSkyFromBelowWater(current)) {
                    aGoalPos = current;
                    if (!level.isClientSide) level.setBlock(current, Blocks.RED_WOOL.defaultBlockState(), 3);
                    player.sendSystemMessage(Component.literal(
                            "A* exit found at: " + current.getX() + ", " + current.getY() + ", " + current.getZ()
                    ));
                    break;
                }

                // neighbors
                for (BlockPos next : Arrays.asList(current.above(), current.below(), current.north(),
                        current.south(), current.east(), current.west())) {

                    if (!level.getBlockState(next).isAir()) continue;
                    if (aStartPos.distToCenterSqr(next.getCenter()) > maxDistSq) continue;
                    if (closedSet.contains(next)) continue;

                    int tentativeG = node.g + 1; // step cost

                    int prevG = gScore.getOrDefault(next, Integer.MAX_VALUE);
                    if (tentativeG < prevG) {
                        // store best parent & g
                        aStarParentMap.put(next, current);
                        gScore.put(next, tentativeG);

                        // compute heuristic inline (height-based)
                        int h = maxY - next.getY();
                        int f = tentativeG + h;

                        openPQ.add(new Node(next, tentativeG, f));
                    }
                }
            }

// Reconstruct path as before
            if (aGoalPos != null && !level.isClientSide) {
                List<BlockPos> path = new ArrayList<>();
                BlockPos cur = aGoalPos;
                while (cur != null) {
                    path.add(cur);
                    cur = aStarParentMap.get(cur);
                }
                Collections.reverse(path);
                for (BlockPos block : path) {
                    if (!block.equals(aStartPos) && !block.equals(aGoalPos)) {
                        level.setBlock(block, Blocks.WHITE_WOOL.defaultBlockState(), 3);
                    }
                }
            }

// Diagnostics: nodes expanded and PQ size
            player.sendSystemMessage(Component.literal("A* nodes expanded: " + nodesExpanded
                    + " openPQ size: " + openPQ.size()));

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            player.sendSystemMessage(Component.literal("Search took " + duration + " ms"));

            return InteractionResultHolder.success(player.getItemInHand(usedHand));
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
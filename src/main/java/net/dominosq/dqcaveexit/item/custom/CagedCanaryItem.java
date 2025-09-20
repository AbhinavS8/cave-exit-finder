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

public class CagedCanaryItem extends Item {

    public CagedCanaryItem(Properties properties) {
        super(properties);
    }

    //A*???????
    //AO*??????
    //heuristic - height
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {

        Queue<BlockPos> queue = new ArrayDeque<>();
        Map<BlockPos, BlockPos> parentMap = new HashMap<>(); // child -> parent
        Set<BlockPos> visited = new HashSet<>();

        BlockPos startPos = player.blockPosition();
        queue.add(startPos);
        visited.add(startPos);
        parentMap.put(startPos, null); // start has no parent

        BlockPos goalPos = null;

        long startTime = System.nanoTime();  // Start timer

        // BFS loop
        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();

            if (level.canSeeSkyFromBelowWater(pos)) {
                goalPos = pos;

                // mark exit block with red wool
                if (!level.isClientSide) {
                    level.setBlock(pos, Blocks.RED_WOOL.defaultBlockState(), 3);
                }

                String msg = "Found exit at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
                player.sendSystemMessage(Component.literal(msg));
                break; // exit BFS
            }

            // neighbors
            for (BlockPos next : Arrays.asList(
                    pos.above(), pos.below(), pos.north(),
                    pos.south(), pos.east(), pos.west()
            )) {
                BlockState state = level.getBlockState(next);
                // BFS bounds: air, not visited, within radius
                if (state.isAir() && !visited.contains(next) && startPos.distToCenterSqr(next.getCenter()) < 20000) {
                    queue.add(next);
                    visited.add(next);
                    parentMap.put(next, pos); // store parent for path reconstruction
                }
            }
        }

        // Reconstruct path and place white wool
        if (goalPos != null && !level.isClientSide) {
            List<BlockPos> path = new ArrayList<>();
            BlockPos current = goalPos;

            while (current != null) {
                path.add(current);
                current = parentMap.get(current);
            }

            // path is from goal → start, reverse it if needed
            Collections.reverse(path);

            for (BlockPos block : path) {
                if (!block.equals(startPos) && !block.equals(goalPos)) {
                    level.setBlock(block, Blocks.WHITE_WOOL.defaultBlockState(), 3);
                }
            }
        }
        long endTime = System.nanoTime();    // End timer
        long duration = (endTime - startTime) / 1_000_000; // ms

        player.sendSystemMessage(Component.literal("Search took " + duration + " ms"));

        //now A* runtime
        startTime = System.nanoTime();







        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1_000_000;
        player.sendSystemMessage(Component.literal("Search took " + duration + " ms"));

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}

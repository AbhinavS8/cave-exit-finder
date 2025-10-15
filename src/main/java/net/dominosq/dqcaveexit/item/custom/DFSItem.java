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

public class DFSItem extends Item {

    public DFSItem(Properties properties) {
        super(properties);
    }



    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {
            Stack<BlockPos> stack = new Stack<>();
            Set<BlockPos> visited = new HashSet<>();

            BlockPos startPos = player.blockPosition();
            stack.push(startPos);
            visited.add(startPos);


            long startTime = System.nanoTime();  // Start timer

            while (!stack.isEmpty()) {
                BlockPos pos = stack.pop();

                if (level.canSeeSkyFromBelowWater(pos)) {

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
                        stack.push(next);
                        visited.add(next);
                    }
                }
            }


            long endTime = System.nanoTime();    // End timer
            long duration = (endTime - startTime) / 1_000_000; // ms

            player.sendSystemMessage(Component.literal("DFS: " + duration + " ms"));
            // Note: DFS doesn't track the path taken, so no path cost calculation is possible

        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}

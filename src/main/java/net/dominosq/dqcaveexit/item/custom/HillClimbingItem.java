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

public class HillClimbingItem extends Item {

    private static final int MAX_STEPS = 500;

    public HillClimbingItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {

            BlockPos startPos = player.blockPosition();
            BlockPos current = startPos;
            Set<BlockPos> visited = new HashSet<>();
            visited.add(startPos);

            long startTime = System.nanoTime();
            int steps = 0;

            while (steps < MAX_STEPS) {
                steps++;

                if (level.canSeeSkyFromBelowWater(current)) {
                    level.setBlock(current, Blocks.RED_WOOL.defaultBlockState(), 3);
                    player.sendSystemMessage(Component.literal(
                            "Hill Climbing exit found at: " + current.getX() + ", " + current.getY() + ", " + current.getZ()
                    ));
                    break;
                }

                List<BlockPos> neighbors = Arrays.asList(
                        current.above(), current.below(),
                        current.north(), current.south(),
                        current.east(), current.west()
                );

                BlockPos bestNeighbor = null;
                int bestHeight = current.getY();

                for (BlockPos next : neighbors) {
                    BlockState state = level.getBlockState(next);
                    if (state.isAir() && !visited.contains(next)) {
                        int h = next.getY();
                        if (h > bestHeight) {
                            bestHeight = h;
                            bestNeighbor = next;
                        } else if (h == bestHeight && bestNeighbor == null) {
                            // allow sideways move if equal height and no better found
                            bestNeighbor = next;
                        }
                    }
                }

                if (bestNeighbor == null) {
                    player.sendSystemMessage(Component.literal(
                            "Hill Climbing stopped at local minima"
                    ));
                    break;
                }

                // move to next
                visited.add(bestNeighbor);
                current = bestNeighbor;

                // mark visited
                if (!current.equals(startPos)) {
                    level.setBlock(current, Blocks.WHITE_WOOL.defaultBlockState(), 3);
                }
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            player.sendSystemMessage(Component.literal(
                    "Hill Climbing finished in " + duration + " ms"
            ));
        }

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}

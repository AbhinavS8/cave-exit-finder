package net.dominosq.dqcaveexit.item.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        //get player location
        //begin search in all directions
        //heuristic -> ?? do bfs first
        //general BFS algo
        //visit set of nearby nodes
        //add valid ones to queue
        //exit if goal node (canSeeSky)

        Queue<BlockPos> queue = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        BlockPos current_pos = player.blockPosition();
        queue.add(current_pos);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            visited.add(pos);

            if (level.canSeeSky(pos)) {
                if (!level.isClientSide) { // Only do this on server
                    level.setBlock(pos, Blocks.RED_WOOL.defaultBlockState(), 3);
                }
                String msg = "Found exit at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ();
                player.sendSystemMessage(Component.literal(msg));
                return InteractionResultHolder.success(player.getItemInHand(usedHand));
            }

            BlockPos up    = pos.above();
            BlockPos down  = pos.below();
            BlockPos north = pos.north();
            BlockPos south = pos.south();
            BlockPos east  = pos.east();
            BlockPos west  = pos.west();

            List<BlockPos> neighbors = Arrays.asList(up, down, north, south, east, west);

            for (BlockPos next : neighbors) {
                BlockState state = level.getBlockState(next);

                if (state.isAir() && !visited.contains(next) && current_pos.distToCenterSqr(next.getCenter())<250) {
                    queue.add(next);
                    visited.add(next);
                }
            }
        }
        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}

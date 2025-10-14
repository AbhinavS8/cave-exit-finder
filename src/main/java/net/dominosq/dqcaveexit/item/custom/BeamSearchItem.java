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

public class BeamSearchItem extends Item {
    
    private final int beamWidth;

    public BeamSearchItem(Properties properties) {
        this(properties, 30);
    }

    public BeamSearchItem(Properties properties, int beamWidth) {
        super(properties);
        this.beamWidth = beamWidth;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide) {

            List<BlockPos> currentBeam = new ArrayList<>();
            Map<BlockPos, BlockPos> parentMap = new HashMap<>();
            Set<BlockPos> visited = new HashSet<>();

            BlockPos startPos = player.blockPosition();
            currentBeam.add(startPos);
            visited.add(startPos);
            parentMap.put(startPos, null);

            BlockPos goalPos = null;

            long startTime = System.nanoTime();  // Start timer

            while (!currentBeam.isEmpty()) {
                List<BlockPos> nextBeamCandidates = new ArrayList<>();

                for (BlockPos pos : currentBeam) {
                    if (level.canSeeSkyFromBelowWater(pos)) {
                        goalPos = pos;

                        // mark exit block with red wool
                        level.setBlock(pos, Blocks.RED_WOOL.defaultBlockState(), 3);

                        player.sendSystemMessage(Component.literal(
                                "Beam Search exit found at: " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()
                        ));
                        nextBeamCandidates.clear();
                        break;
                    }

                    // neighbors
                    for (BlockPos next : Arrays.asList(
                            pos.above(), pos.below(), pos.north(),
                            pos.south(), pos.east(), pos.west()
                    )) {
                        BlockState state = level.getBlockState(next);
                        if (state.isAir() && !visited.contains(next)
                                && startPos.distToCenterSqr(next.getCenter()) < 20000) {
                            nextBeamCandidates.add(next);
                            parentMap.put(next, pos);
                            visited.add(next);
                        }
                    }
                }

                if (goalPos != null) break;

                // sort candidates by heuristic (highest Y first)
                nextBeamCandidates.sort(Comparator.comparingInt((BlockPos pos) -> pos.getY()).reversed());
                // keep only top `beamWidth` nodes
                currentBeam = nextBeamCandidates.size() > this.beamWidth ?
                        nextBeamCandidates.subList(0, this.beamWidth) :
                        nextBeamCandidates;
            }

            // Reconstruct path and place white wool
            if (goalPos != null) {
                List<BlockPos> path = new ArrayList<>();
                BlockPos current = goalPos;
                while (current != null) {
                    path.add(current);
                    current = parentMap.get(current);
                }
                Collections.reverse(path);

                for (BlockPos block : path) {
                    if (!block.equals(startPos) && !block.equals(goalPos)) {
                        level.setBlock(block, Blocks.WHITE_WOOL.defaultBlockState(), 3);
                    }
                }
            }

            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000;
            player.sendSystemMessage(Component.literal("Beam Search took " + duration + " ms"));
        }

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}

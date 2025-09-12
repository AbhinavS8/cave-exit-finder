//package net.dominosq.dqpetrespawn.block.custom;
//
//import com.mojang.serialization.MapCodec;
//import net.minecraft.core.BlockPos;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.util.RandomSource;
//import net.minecraft.world.level.block.BaseEntityBlock;
//import net.minecraft.world.level.block.RenderShape;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.state.BlockState;
//import org.jetbrains.annotations.Nullable;
//
//public class PetRespawnAnchorBlock extends BaseEntityBlock {
//
//    public static final MapCodec<PetRespawnAnchorBlock> CODEC = simpleCodec(PetRespawnAnchorBlock::new);
//
//    public PetRespawnAnchorBlock(Properties properties) {
//        super(properties);
//    }
//
//    @Override
//    protected MapCodec<? extends BaseEntityBlock> codec() {
//        return CODEC;
//    }
//
//    @Override
//    protected RenderShape getRenderShape(BlockState state) {
//        return RenderShape.MODEL;
//    }
//
//    @Override
//    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
//
//    }
//
////    @Override
////    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
////
////        BlockEntity blockEntity = level.getBlockEntity(pos);
////        if (blockEntity instanceof PetRespawnAnchorBlockEntity be) {
////            be.respawnPets(); // Respawn all stored pets
////        }
////    }
//}

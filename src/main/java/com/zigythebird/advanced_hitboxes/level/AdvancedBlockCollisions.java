package com.zigythebird.advanced_hitboxes.level;

import com.google.common.collect.AbstractIterator;
import com.zigythebird.advanced_hitboxes.phys.OBB;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.CollisionGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

public class AdvancedBlockCollisions extends AbstractIterator<VoxelShape> {
    private final CollisionContext context;
    private final OBB box;
    private final Cursor3D cursor;
    private final BlockPos.MutableBlockPos pos;
    private final CollisionGetter collisionGetter;
    private final boolean onlySuffocatingBlocks;
    @Nullable
    private BlockGetter cachedBlockGetter;
    private long cachedBlockGetterPos;
    private final BiFunction<BlockPos.MutableBlockPos, VoxelShape, VoxelShape> resultProvider;

    public AdvancedBlockCollisions(CollisionGetter collisionGetter, @Nullable Entity entity, OBB box, boolean onlySuffocatingBlocks, BiFunction<BlockPos.MutableBlockPos, VoxelShape, VoxelShape> resultProvider) {
        this.context = entity == null ? CollisionContext.empty() : CollisionContext.of(entity);
        this.pos = new BlockPos.MutableBlockPos();
        this.collisionGetter = collisionGetter;
        this.box = box;
        this.onlySuffocatingBlocks = onlySuffocatingBlocks;
        this.resultProvider = resultProvider;
        box.updateScaledAxis();
        Vec3 minVertex = box.getMinVertex();
        Vec3 maxVertex = box.getMaxVertex();
        int i = Mth.floor(minVertex.x - 1.0E-7) - 1;
        int j = Mth.floor(maxVertex.x + 1.0E-7) + 1;
        int k = Mth.floor(minVertex.y - 1.0E-7) - 1;
        int l = Mth.floor(maxVertex.y + 1.0E-7) + 1;
        int i1 = Mth.floor(minVertex.z - 1.0E-7) - 1;
        int j1 = Mth.floor(maxVertex.z + 1.0E-7) + 1;
        this.cursor = new Cursor3D(i, k, i1, j, l, j1);
    }

    @Nullable
    private BlockGetter getChunk(int x, int z) {
        int i = SectionPos.blockToSectionCoord(x);
        int j = SectionPos.blockToSectionCoord(z);
        long k = ChunkPos.asLong(i, j);
        if (this.cachedBlockGetter != null && this.cachedBlockGetterPos == k) {
            return this.cachedBlockGetter;
        } else {
            BlockGetter blockgetter = this.collisionGetter.getChunkForCollisions(i, j);
            this.cachedBlockGetter = blockgetter;
            this.cachedBlockGetterPos = k;
            return blockgetter;
        }
    }

    @Override
    protected VoxelShape computeNext() {
        while (this.cursor.advance()) {
            int i = this.cursor.nextX();
            int j = this.cursor.nextY();
            int k = this.cursor.nextZ();
            int l = this.cursor.getNextType();
            if (l != 3) {
                BlockGetter blockgetter = this.getChunk(i, k);
                if (blockgetter != null) {
                    this.pos.set(i, j, k);
                    BlockState blockstate = blockgetter.getBlockState(this.pos);
                    if ((!this.onlySuffocatingBlocks || blockstate.isSuffocating(blockgetter, this.pos))
                            && (l != 1 || blockstate.hasLargeCollisionShape())
                            && (l != 2 || blockstate.is(Blocks.MOVING_PISTON))) {
                        VoxelShape voxelshape = blockstate.getCollisionShape(this.collisionGetter, this.pos, this.context);
                        if (voxelshape == Shapes.block()) {
                            if (this.box.intersects(new AABB(i, j, k, (double)i + 1.0, (double)j + 1.0, (double)k + 1.0))) {
                                return this.resultProvider.apply(this.pos, voxelshape.move(i, j, k));
                            }
                        } else {
                            VoxelShape voxelshape1 = voxelshape.move(i, j, k);
                            //Todo: Might be able to optimize this code later.
                            boolean intersects = false;
                            for (AABB aabb : voxelshape1.toAabbs()) {
                                if (box.intersects(aabb)) {
                                    intersects = true;
                                    break;
                                }
                            }
                            if (!voxelshape1.isEmpty() && intersects) {
                                return this.resultProvider.apply(this.pos, voxelshape1);
                            }
                        }
                    }
                }
            }
        }

        return this.endOfData();
    }
}

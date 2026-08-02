package net.shoreline.client.impl.block;

import com.google.common.collect.AbstractIterator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.shoreline.client.impl.level.entity.state.EntityState;
import net.shoreline.client.impl.level.entity.state.LivingEntityState;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AsyncCollisionScanner extends AsyncBlockGetter implements AsyncCollisionView
{
    private final WorldBorder border = new WorldBorder();

    @Override
    public Iterable<VoxelShape> getBlockCollisions(LivingEntityState living, AABB boundingBox)
    {
        return () -> new AsyncCollisionIterator(new AsyncCollisionContext(living), boundingBox);
    }

    @Override
    public WorldBorder getWorldBorder()
    {
        return border;
    }

    @Override
    public BlockGetter getChunkForCollisions(int chunkX, int chunkZ)
    {
        return this;
    }

    @Override
    public List<VoxelShape> getEntityCollisions(Entity source, AABB testArea)
    {
        return new ArrayList<>();
    }

    public VoxelShape getCollisionShape(CollisionContext context,
                                        BlockState state,
                                        BlockPos pos)
    {
        return state.getCollisionShape(this, pos, context);
    }

    public class AsyncCollisionIterator extends AbstractIterator<VoxelShape>
    {
        private final AABB box;
        private final CollisionContext context;
        private final Cursor3D cursor;
        private final BlockPos.MutableBlockPos pos;
        private final VoxelShape boxShape;

        public AsyncCollisionIterator(CollisionContext context, AABB box)
        {
            this.context = context;
            this.pos = new BlockPos.MutableBlockPos();
            this.boxShape = Shapes.create(box);
            this.box = box;
            int i = Mth.floor(box.minX - 1.0e-7) - 1;
            int j = Mth.floor(box.maxX + 1.0e-7) + 1;
            int k = Mth.floor(box.minY - 1.0e-7) - 1;
            int l = Mth.floor(box.maxY + 1.0e-7) + 1;
            int m = Mth.floor(box.minZ - 1.0e-7) - 1;
            int n = Mth.floor(box.maxZ + 1.0e-7) + 1;
            cursor = new Cursor3D(i, k, m, j, l, n);
        }

        @Override
        protected @Nullable VoxelShape computeNext()
        {
            while (cursor.advance())
            {
                int i = cursor.nextX();
                int j = cursor.nextY();
                int k = cursor.nextZ();
                int l = cursor.getNextType();
                if (l == 3)
                {
                    continue;
                }

                pos.set(i, j, k);
                BlockState blockState = getBlockState(pos);
                VoxelShape voxelShape = getCollisionShape(context, blockState, pos);
                if (voxelShape == Shapes.block())
                {
                    if (!box.intersects(i, j, k, (double) i + 1.0, (double) j + 1.0, (double) k + 1.0))
                    {
                        continue;
                    }

                    return voxelShape.move(i, j, k);
                }

                VoxelShape voxelShape2 = voxelShape.move(i, j, k);
                if (voxelShape2.isEmpty() || !Shapes.joinIsNotEmpty(voxelShape2, boxShape, BooleanOp.AND))
                {
                    continue;
                }

                return voxelShape2;
            }

            return endOfData();
        }
    }
}

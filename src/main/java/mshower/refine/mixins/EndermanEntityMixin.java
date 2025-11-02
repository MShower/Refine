package mshower.refine.mixins;


import mshower.refine.config.RefineConfig;
import mshower.refine.utils.EndermanGriefingBlacklistControl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.mob.EndermanEntity;
//#if MC>=11700
import net.minecraft.registry.Registries;
//#else
//$$ import net.minecraft.util.registry.Registry;
//#endif
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
//#if MC>=11700
import net.minecraft.util.math.random.Random;
import net.minecraft.world.event.GameEvent;
//#endif
import org.spongepowered.asm.mixin.*;

import static mshower.refine.utils.EndermanGriefingBlacklistControl.isBlacklisted;

@Mixin(targets = "net.minecraft.entity.mob.EndermanEntity$PickUpBlockGoal")
public abstract class EndermanEntityMixin {
    @Shadow
    @Final
    private EndermanEntity enderman;

    /**
     * @author Mark Lin
     * @reason Let enderman not pick sth up.
     */
    @Overwrite
    public void tick() {
        //#if MC>=11700
        Random random = this.enderman.getRandom();
        World world = this.enderman.getEntityWorld();
        double ex = this.enderman.getX();
        double ey = this.enderman.getY();
        double ez = this.enderman.getZ();
        //#else
        //$$ java.util.Random random = this.enderman.getRandom();
        //$$ World world = this.enderman.world;
        //$$ Vec3d pos = this.enderman.getPos();
        //$$ double ex = pos.x;
        //$$ double ey = pos.y;
        //$$ double ez = pos.z;
        //#endif

        int i = MathHelper.floor(ex - 2.0 + random.nextDouble() * 4.0);
        int j = MathHelper.floor(ey + random.nextDouble() * 3.0);
        int k = MathHelper.floor(ez - 2.0 + random.nextDouble() * 4.0);

        BlockPos blockPos = new BlockPos(i, j, k);
        BlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();

        Vec3d from = new Vec3d(MathHelper.floor(ex) + 0.5, j + 0.5, MathHelper.floor(ez) + 0.5);
        Vec3d to = new Vec3d(i + 0.5, j + 0.5, k + 0.5);

        BlockHitResult hit = world.raycast(new RaycastContext(from, to,
                RaycastContext.ShapeType.OUTLINE,
                RaycastContext.FluidHandling.NONE,
                this.enderman));

        boolean canPick = hit.getType() != HitResult.Type.MISS && hit.getBlockPos().equals(blockPos);
        boolean beLimited;
        if (RefineConfig.INSTANCE.featureConfig.EnableEndermanGriefingBlacklist) {
            beLimited = isBlacklisted(block);
        }
        else {
            beLimited = false;
        }
        if (canPick
                //#if MC>=11700
                && blockState.isIn(BlockTags.ENDERMAN_HOLDABLE)
                //#else
                //$$ && block.matches(BlockTags.ENDERMAN_HOLDABLE)
                //#endif
                && !beLimited) {
            //#if MC>=11700
            world.removeBlock(blockPos, false);
            world.emitGameEvent(GameEvent.BLOCK_DESTROY, blockPos,
                    GameEvent.Emitter.of(this.enderman, blockState));
            //#else
            //$$ world.removeBlock(blockPos, false);
            //#endif

            this.enderman.setCarriedBlock(blockState.getBlock().getDefaultState());
        }
    }
}

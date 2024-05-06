package org.zeith.squarry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;

@SimplyRegister
public class BlockPoweredQuarry
		extends BlockBaseQuarry
{
	@RegistryName("powered_quarry")
	public static final BlockPoweredQuarry POWERED_QUARRY = new BlockPoweredQuarry(Properties.of()
			.sound(SoundType.METAL)
			.strength(4.5F)
			.requiresCorrectToolForDrops()
	);
	
	protected BlockPoweredQuarry(Properties props)
	{
		super(props);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return TilePoweredQuarry.POWERED_QUARRY.create(pos, state);
	}
}
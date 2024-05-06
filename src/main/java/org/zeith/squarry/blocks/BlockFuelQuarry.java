package org.zeith.squarry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;

@SimplyRegister
public class BlockFuelQuarry
		extends BlockBaseQuarry
{
	@RegistryName("fuel_quarry")
	public static final BlockFuelQuarry FUEL_QUARRY = new BlockFuelQuarry(Properties.of()
			.sound(SoundType.METAL)
			.strength(4F)
			.requiresCorrectToolForDrops()
	);
	
	protected BlockFuelQuarry(Properties props)
	{
		super(props);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return TileFuelQuarry.FUEL_QUARRY.create(pos, state);
	}
}
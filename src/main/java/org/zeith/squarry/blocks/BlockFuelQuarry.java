package org.zeith.squarry.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.logging.log4j.util.Cast;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.registrars.Registrar;
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
	
	@RegistryName("fuel_quarry")
	public static final Registrar<MapCodec<? extends Block>> FUEL_QUARRY_CODEC = Registrar.blockType(simpleCodec(BlockFuelQuarry::new));
	
	protected BlockFuelQuarry(Properties props)
	{
		super(props);
	}
	
	@Override
	protected MapCodec<? extends BaseEntityBlock> codec()
	{
		return Cast.cast(FUEL_QUARRY_CODEC.get());
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return TileFuelQuarry.FUEL_QUARRY.create(pos, state);
	}
}
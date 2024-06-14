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
	
	@RegistryName("powered_quarry")
	public static final Registrar<MapCodec<? extends Block>> FUEL_QUARRY_CODEC = Registrar.blockType(simpleCodec(BlockPoweredQuarry::new));
	
	protected BlockPoweredQuarry(Properties props)
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
		return TilePoweredQuarry.POWERED_QUARRY.create(pos, state);
	}
}
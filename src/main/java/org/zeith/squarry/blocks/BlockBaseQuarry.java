package org.zeith.squarry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.squarry.SimpleQuarry;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;

import java.util.List;

public abstract class BlockBaseQuarry
		extends BaseEntityBlock
		implements ICreativeTabBlock
{
	protected BlockBaseQuarry(Properties props)
	{
		super(props);
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, Tiers.IRON, this);
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		if(level.getBlockEntity(pos) instanceof TileFuelQuarry tfq)
		{
			int y = pos.getY();
			int minedLevels = y - tfq._y;
			int maxMineLevels = y - level.getMinBuildHeight();
			int v = Math.round(minedLevels * 15F / maxMineLevels);
			return 15 - Mth.clamp(v, 0, 15);
		}
		return 0;
	}
	
	@Override
	public void onRemove(BlockState prevState, Level world, BlockPos pos, BlockState newState, boolean flag64)
	{
		if(!prevState.is(newState.getBlock()))
		{
			BlockEntity b = world.getBlockEntity(pos);
			
			if(b instanceof TileFuelQuarry tfq)
			{
				tfq.dropEverything(world, pos);
				world.updateNeighbourForOutputSignal(pos, this);
			}
			
			super.onRemove(prevState, world, pos, newState, flag64);
		}
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> def)
	{
		def.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.ENABLED);
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return defaultBlockState()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, ctx.getHorizontalDirection().getOpposite())
				.setValue(BlockStateProperties.ENABLED, true);
	}
	
	@Override
	public RenderShape getRenderShape(BlockState p_49232_)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
	{
		ContainerAPI.openContainerTile(player, Cast.cast(level.getBlockEntity(pos), TileFuelQuarry.class));
		return InteractionResult.SUCCESS;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return BlockAPI.ticker(level);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootParams.Builder b)
	{
		return List.of(new ItemStack(this));
	}
	
	@Override
	public CreativeTab getCreativeTab()
	{
		return SimpleQuarry.ITEM_GROUP;
	}
}

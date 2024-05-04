package org.zeith.squarry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.*;
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
import org.zeith.hammerlib.annotations.*;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.SimpleQuarry;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;
import org.zeith.squarry.blocks.entity.TilePoweredQuarry;

import java.util.List;

@SimplyRegister
public class BlockPoweredQuarry
		extends BaseEntityBlock
		implements ICreativeTabBlock
{
	@RegistryName("powered_quarry")
	public static final BlockPoweredQuarry POWERED_QUARRY = new BlockPoweredQuarry();
	
	protected BlockPoweredQuarry()
	{
		super(Properties
				.of()
				.sound(SoundType.METAL)
				.strength(4.5F)
				.requiresCorrectToolForDrops()
		);
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, Tiers.IRON, this);
	}
	
	@Override
	public void onRemove(BlockState prevState, Level world, BlockPos pos, BlockState newState, boolean flag64)
	{
		if(!prevState.is(newState.getBlock()))
		{
			BlockEntity b = world.getBlockEntity(pos);
			
			if(b instanceof TilePoweredQuarry tfq)
			{
				Containers.dropContents(world, pos, tfq.queueItems);
				Containers.dropContents(world, pos, tfq.inventory.items);
				Containers.dropContents(world, pos, tfq.invUpgrades);
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
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
	{
		if(SQConfig.isPoweredQuarry())
			ContainerAPI.openContainerTile(player, Cast.cast(level.getBlockEntity(pos), TileFuelQuarry.class));
		return InteractionResult.SUCCESS;
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_)
	{
		return BlockAPI.ticker();
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return TilePoweredQuarry.POWERED_QUARRY.create(pos, state);
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
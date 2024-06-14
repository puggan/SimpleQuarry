package org.zeith.squarry.blocks;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.blocks.ICreativeTabBlock;
import org.zeith.hammerlib.api.items.CreativeTab;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.core.adapter.TagAdapter;
import org.zeith.squarry.SimpleQuarry;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;
import org.zeith.squarry.init.TagsSQ;

import java.util.List;

@SimplyRegister
public class BlockQuarryPipe
		extends Block
		implements ICreativeTabBlock
{
	protected static final VoxelShape SHAPE = box(5, 0, 5, 11, 16, 11);
	
	@RegistryName("quarry_pipe")
	public static final BlockQuarryPipe QUARRY_PIPE = new BlockQuarryPipe(Properties.ofFullCopy(Blocks.IRON_BLOCK)
			.strength(2.0F, 8.0F)
	);
	
	public BlockQuarryPipe(Properties props)
	{
		super(props);
		TagAdapter.bind(TagsSQ.Blocks.QUARRY_PIPE, this);
		BlockHarvestAdapter.bindTool(BlockHarvestAdapter.MineableType.PICKAXE, Tiers.IRON, this);
	}
	
	@Override
	public void appendHoverText(ItemStack stack, @Nullable Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flags)
	{
		tooltip.add(Component.translatable("info.squarry.quarry_pipe")
				.withStyle(ChatFormatting.GRAY)
		);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
	{
		builder.add(BlockStateProperties.UP, BlockStateProperties.DOWN);
	}
	
	public BlockState tfState(BlockState base, BlockGetter level, BlockPos pos)
	{
		var above = level.getBlockState(pos.above());
		var below = level.getBlockState(pos.below());
		return base
				.setValue(BlockStateProperties.UP, above.is(this) || level.getBlockEntity(pos.above()) instanceof TileFuelQuarry)
				.setValue(BlockStateProperties.DOWN, below.is(this));
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx)
	{
		return tfState(super.getStateForPlacement(ctx), ctx.getLevel(), ctx.getClickedPos());
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction p_60542_, BlockState p_60543_, LevelAccessor level, BlockPos pos, BlockPos p_60546_)
	{
		return tfState(state, level, pos);
	}
	
	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootParams.Builder b)
	{
		return List.of(new ItemStack(this));
	}
	
	@Override
	public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_)
	{
		return SHAPE;
	}
	
	@Override
	public CreativeTab getCreativeTab()
	{
		return SimpleQuarry.ITEM_GROUP;
	}
}
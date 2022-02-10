package org.zeith.squarry.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import org.zeith.hammerlib.annotations.OnlyIf;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.blocks.IItemGroupBlock;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.api.forge.ContainerAPI;
import org.zeith.hammerlib.core.adapter.BlockHarvestAdapter;
import org.zeith.hammerlib.util.java.Cast;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.SimpleQuarry;
import org.zeith.squarry.blocks.entity.TileFuelQuarry;

import java.util.List;

@SimplyRegister
public class BlockFuelQuarry
		extends BaseEntityBlock
		implements IItemGroupBlock
{
	@RegistryName("fuel_quarry")
	@OnlyIf(owner = SQConfig.class, member = "enableFuelQuarry")
	public static final BlockFuelQuarry FUEL_QUARRY = new BlockFuelQuarry();

	protected BlockFuelQuarry()
	{
		super(Block.Properties
				.of(Material.METAL)
				.sound(SoundType.METAL)
				.strength(4F)
				.requiresCorrectToolForDrops()
		);
		BlockHarvestAdapter.bindToolType(BlockHarvestAdapter.MineableType.PICKAXE, this);
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
		return TileFuelQuarry.FUEL_QUARRY.create(pos, state);
	}

	@Override
	public List<ItemStack> getDrops(BlockState p_60537_, LootContext.Builder b)
	{
		NonNullList<ItemStack> drops = NonNullList.create();
		drops.add(new ItemStack(this));
		if(b.getOptionalParameter(LootContextParams.BLOCK_ENTITY) instanceof TileFuelQuarry tfq)
		{
			drops.addAll(tfq.queueItems);
			drops.addAll(tfq.inventory.items);
		}
		return drops;
	}

	@Override
	public CreativeModeTab getItemGroup()
	{
		return SimpleQuarry.ITEM_GROUP;
	}
}
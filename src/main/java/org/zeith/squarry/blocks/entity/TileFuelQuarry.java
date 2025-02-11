package org.zeith.squarry.blocks.entity;

import net.minecraft.core.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.zeith.api.wrench.IWrenchable;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.tiles.tooltip.ITooltipConsumer;
import org.zeith.hammerlib.tiles.tooltip.ITooltipTile;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.squarry.*;
import org.zeith.squarry.api.ItemInjector;
import org.zeith.squarry.api.ItemStackList;
import org.zeith.squarry.api.energy.QFStorage;
import org.zeith.squarry.api.particle.ParticleVortex;
import org.zeith.squarry.blocks.BlockBaseQuarry;
import org.zeith.squarry.blocks.BlockFuelQuarry;
import org.zeith.squarry.init.TagsSQ;
import org.zeith.squarry.inventory.ContainerFuelQuarry;

import java.util.*;
import java.util.function.Function;

import static org.zeith.squarry.SQConstants.*;

@SimplyRegister
public class TileFuelQuarry
		extends TileSyncableTickable
		implements IContainerTile, IWrenchable, ITooltipTile
{
	@RegistryName("fuel_quarry")
	public static final BlockEntityType<TileFuelQuarry> FUEL_QUARRY = BlockAPI.createBlockEntityType(TileFuelQuarry::new, BlockFuelQuarry.FUEL_QUARRY);
	
	public static final Map<ResourceKey<Level>, Map<ChunkPos, BlockPos>> QUARRY_MAP = new HashMap<>();
	private static final Function<ResourceKey<Level>, Map<ChunkPos, BlockPos>> QUARRY_MAP_COMPUTE = world -> new HashMap<>();
	
	@NBTSerializable
	public final SimpleInventory inventory = new SimpleInventory(1);
	
	public int tickRate = SQConfig.getFuelQuarryTickRate();
	
	@NBTSerializable
	public int _burnTicks;
	
	@NBTSerializable
	public int _totalBurnTicks;
	
	@NBTSerializable("y")
	public int _y = Integer.MIN_VALUE;
	
	@NBTSerializable
	public final QFStorage storage = new QFStorage(getQFCapacity());
	
	@NBTSerializable
	public final ItemStackList queueItems = ItemStackList.createList();
	
	public AABB boundingBox;
	//	private QuarryVortex vortex;
	
	@NBTSerializable
	private ChunkPos chunkPos;
	
	public ParticleVortex vortex;
	
	protected double getQFCapacity()
	{
		return 8000;
	}
	
	public final PropertyInt burnTicks = new PropertyInt(DirectStorage.create($ -> _burnTicks = $, () -> _burnTicks));
	public final PropertyInt totalBurnTicks = new PropertyInt(DirectStorage.create($ -> _totalBurnTicks = $, () -> _totalBurnTicks));
	public final PropertyInt yLevel = new PropertyInt(DirectStorage.create($ -> _y = $, () -> _y));
	
	protected TileFuelQuarry(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		this.dispatcher.registerProperty("burn_ticks", burnTicks);
		this.dispatcher.registerProperty("total_burn_ticks", totalBurnTicks);
		this.dispatcher.registerProperty("y", yLevel);
	}
	
	public void validateQuarry()
	{
		Map<ChunkPos, BlockPos> map = QUARRY_MAP.computeIfAbsent(level.dimension(), QUARRY_MAP_COMPUTE);
		BlockPos cpos = map.get(chunkPos);
		if(cpos != null && cpos.asLong() != worldPosition.asLong() && level.getBlockEntity(cpos) instanceof TileFuelQuarry)
		{
			level.destroyBlock(worldPosition, true);
			level.explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 3, Level.ExplosionInteraction.NONE);
			return;
		}
		map.put(chunkPos, worldPosition);
	}
	
	protected Block getQuarryBlock()
	{
		return BlockFuelQuarry.FUEL_QUARRY;
	}
	
	@Override
	public void update()
	{
		double qf;
		ItemStack stack;
		LevelChunk c = level.getChunkAt(worldPosition);
		
		var pcp = chunkPos;
		chunkPos = c.getPos();
		int chunkX = chunkPos.x;
		int chunkZ = chunkPos.z;
		if(pcp != null && (pcp.x != chunkX || pcp.z != chunkZ))
		{
			// Quarry has moved
			SimpleQuarry.LOG.info("Quarry moved from chunk {}, {} -> {}, {} (currently at {}). Reset Y level.", pcp.x, pcp.z, chunkX, chunkZ, worldPosition);
			yLevel.setInt(Integer.MIN_VALUE);
		}
		
		if(level.isClientSide)
		{
			int yMax = computeTopmostY();
			if(boundingBox == null || boundingBox.minY != (double) _y || boundingBox.maxY != yMax + 0.5)
				boundingBox = new AABB(chunkX * 16, _y, chunkZ * 16, chunkX * 16 + 16, yMax + 0.5, chunkZ * 16 + 16);
			
			if(SQConfig.isParticleVortex())
			{
				if(vortex == null)
					vortex = SimpleQuarry.PROXY.createQuarryVortex(this);
				vortex.update();
			}
			return;
		}
		
		if(queueItems.size() >= 2)
		{
			tryEject();
			tickRate = SQConfig.getFuelQuarryTickRate();
			return;
		}
		
		if(storage.storedQF > 0.0 && _y == Integer.MIN_VALUE)
		{
			yLevel.setInt(computeTopmostY());
			boundingBox = new AABB(chunkX * 16, _y, chunkZ * 16, chunkX * 16 + 16, _y + 1, chunkZ * 16 + 16);
			setEnabledState(true);
		}
		
		if(storage.storedQF > 0.0)
		{
			validateQuarry();
		}
		
		BlockState state0 = level.getBlockState(worldPosition);
		
		if(state0.getBlock() != getQuarryBlock())
		{
			tickRate = SQConfig.getFuelQuarryTickRate();
			return;
		}
		
		if(_y > DimensionType.MIN_Y && _y <= level.getMinBuildHeight())
			setEnabledState(false);
		
		double QFPerBlock = FT.convertTo(SQCommonProxy.COAL.getBurnTime(null), QF) / SQConfig.getBlocksPerCoal();
		QFPerBlock *= getUsageMult();
		
		int bt;
		if(state0.getValue(BlockStateProperties.ENABLED)
		   && !level.isClientSide
		   && atTickRate(20)
		   && _burnTicks < 1
		   && !(stack = inventory.getStackInSlot(0)).isEmpty()
		   && (bt = stack.getBurnTime(null)) > 0
		   && storage.consumeQF(null, FT.convertTo(1, QF), true) == FT.convertTo(1, QF))
		{
			burnTicks.setInt(burnTicks.getInt() + bt);
			
			totalBurnTicks.setInt(burnTicks.getInt());
			stack.shrink(1);
			sync();
		}
		
		if(_burnTicks > 0)
		{
			burnTicks.setInt(_burnTicks - 1);
			
			double ftqf = FT.convertTo(1, QF);
			double qf2 = storage.consumeQF(null, ftqf, true);
			
			if(qf2 == ftqf)
				storage.consumeQF(null, qf2, false);
			
			sync();
		}
		
		if(Double.isNaN(qf = storage.getStoredQF(null)) || Double.isInfinite(qf))
			storage.storedQF = 0.0;
		
		tryBreak:
		if(_y > level.getMinBuildHeight() && atTickRate(tickRate) && storage.getStoredQF(null) >= QFPerBlock)
		{
			if(level.getBlockState(new BlockPos(worldPosition.getX(), _y, worldPosition.getZ())).is(TagsSQ.Blocks.QUARRY_PIPE))
			{
				yLevel.setInt(_y - 1);
				break tryBreak;
			}
			
			boolean hasBrokenBlock = false;
			
			for(BlockPos pos : BlockPos.betweenClosed(chunkX * 16, _y, chunkZ * 16, chunkX * 16 + 15, _y, chunkZ * 16 + 15))
			{
				BlockState state = level.getBlockState(pos);
				
				if(level.isEmptyBlock(pos) || !canBreak(state, pos))
					continue;
				
				captureItems(makeDrops(pos, state));
				hasBrokenBlock = true;
				breakBlock(pos, state);
				storage.produceQF(null, QFPerBlock, false);
				sync();
				
				break;
			}
			
			if(!hasBrokenBlock)
				yLevel.setInt(_y - 1);
		}
		
		if(isMining(state0))
			captureEntityItems(level.getEntitiesOfClass(ItemEntity.class, new AABB(chunkX * 16, _y, chunkZ * 16, chunkX * 16 + 16, worldPosition.getY(), chunkZ * 16 + 16)));
		
		tryEject();
		tickRate = SQConfig.getFuelQuarryTickRate();
	}
	
	public int computeTopmostY()
	{
		BlockPos pos = worldPosition.immutable().below();
		
		if(level == null) return pos.getY();
		
		while(level.getBlockState(pos).is(TagsSQ.Blocks.QUARRY_PIPE)) pos = pos.below();
		
		return pos.getY();
	}
	
	public boolean isDone()
	{
		BlockState state0 = level.getBlockState(worldPosition);
		if(!level.isClientSide && state0.getBlock() == getQuarryBlock())
			return !state0.getValue(BlockStateProperties.ENABLED);
		return false;
	}
	
	public double getUsageMult()
	{
		return 1;
	}
	
	public void addToolEnchantments(ItemEnchantments.Mutable enchantmentMap)
	{
	}
	
	public NonNullList<ItemStack> makeDrops(BlockPos pos, BlockState state)
	{
		NonNullList<ItemStack> drops = NonNullList.create();
		if(level instanceof ServerLevel sl)
		{
			ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
			var enchMap = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
			addToolEnchantments(enchMap);
			EnchantmentHelper.setEnchantments(tool, enchMap.toImmutable());
			
			LootParams.Builder bl = new LootParams.Builder(sl)
					.withParameter(LootContextParams.BLOCK_STATE, state)
					.withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(worldPosition))
					.withParameter(LootContextParams.TOOL, tool);
			BlockEntity be = level.getBlockEntity(pos);
			if(be != null)
				bl = bl.withParameter(LootContextParams.BLOCK_ENTITY, be);
			drops.addAll(state.getDrops(bl));
		}
		return drops;
	}
	
	public boolean isMining(BlockState state)
	{
		return state.getBlock() == getQuarryBlock()
			   && state.getValue(BlockStateProperties.ENABLED)
			   && _y > level.getMinBuildHeight()
			   && storage.storedQF > 0;
	}
	
	public void breakBlock(BlockPos pos, BlockState state)
	{
		level.removeBlock(pos, false);
		
		FluidState fluidstate = level.getFluidState(pos);
		level.levelEvent(2001, pos, Block.getId(state));
		boolean flag = level.setBlock(pos, fluidstate.createLegacyBlock(), 3, 512);
		if(flag)
			level.gameEvent(null, GameEvent.BLOCK_DESTROY, pos);

//		if(state != null) Network.sendToArea(HLTargetPoint.atCenterOf(worldPosition), new PacketBlock(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, state));
	}
	
	public boolean canBreak(BlockState state, BlockPos pos)
	{
		if(state.getBlock() instanceof LiquidBlock) return false;
		if(state.getDestroySpeed(level, pos) < 0F) return false;
		return !state.is(TagsSQ.Blocks.QUARRY_BLACKLIST);
	}
	
	public void dropStack(ItemStack stack)
	{
		var rand = level.getRandom();
		if(!stack.isEmpty() && !level.isClientSide)
		{
			var ei = new ItemEntity(level, (double) worldPosition.getX() + 0.5, worldPosition.getY() + 1, (double) worldPosition.getZ() + 0.5, stack.copy());
			ei.setDeltaMovement((rand.nextDouble() - rand.nextDouble()) * 0.045, 1.0 + rand.nextDouble() * 0.5, (rand.nextDouble() - rand.nextDouble()) * 0.045);
			level.addFreshEntity(ei);
		}
	}
	
	public void tryEject()
	{
		while(!queueItems.isEmpty())
		{
			if(queueItems.get(0).isEmpty())
			{
				queueItems.remove(0);
				continue;
			}
			
			var stack = queueItems.remove(0);
			
			for(var face : Direction.values())
			{
				stack = ItemInjector.inject(stack, level, worldPosition.relative(face), face.getOpposite());
				if(stack.isEmpty())
					break;
			}
			
			if(!stack.isEmpty())
				dropStack(stack);
		}
	}
	
	public void addQueueItem(ItemStack e)
	{
		queueItems.add(e);
	}
	
	public void captureItems(List<ItemStack> items)
	{
		while(!items.isEmpty())
			addQueueItem(items.remove(0));
	}
	
	public void captureEntityItems(List<ItemEntity> items)
	{
		for(int j = 0; j < Math.min(items.size(), 1); ++j)
		{
			ItemEntity item = items.get(j);
			if(item.getItem().getCount() <= 0)
				continue;
			addQueueItem(item.getItem().copy());
			item.setItem(ItemStack.EMPTY);
			item.kill();
		}
	}
	
	public void setEnabledState(boolean enabled)
	{
		BlockState s = level.getBlockState(worldPosition);
		if(s.getBlock() instanceof BlockBaseQuarry && !Objects.equals(s.getValue(BlockStateProperties.ENABLED), enabled))
		{
			level.setBlockAndUpdate(worldPosition, s.setValue(BlockStateProperties.ENABLED, enabled));
			level.setBlockEntity(this);
			sync();
		}
	}
	
	@Override
	public boolean onWrenchUsed(UseOnContext context)
	{
		Direction d = context.getClickedFace();
		if(context.getPlayer().isShiftKeyDown()) d = d.getOpposite();
		
		BlockState state = level.getBlockState(worldPosition);
		if(state.getBlock() == BlockFuelQuarry.FUEL_QUARRY)
		{
			Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
			final Direction origin = facing;
			
			if(d == Direction.UP)
				facing = facing.getClockWise();
			else if(d == Direction.DOWN)
				facing = facing.getCounterClockWise();
			else
				facing = d;
			
			if(origin != facing)
			{
				level.setBlockAndUpdate(worldPosition, state.setValue(BlockStateProperties.HORIZONTAL_FACING, facing));
			}
		}
		
		return true;
	}
	
	@Override
	public AbstractContainerMenu openContainer(Player player, int windowId)
	{
		return new ContainerFuelQuarry(player, windowId, this);
	}
	
	@Nullable
	@Override
	public Component getDisplayName()
	{
		return getBlockState().getBlock().getName();
	}
	
	@Override
	public void addTooltip(ITooltipConsumer consumer, Player player)
	{
		if(Integer.MIN_VALUE != _y)
			consumer.addLine(Component.literal("Y: ").append(Integer.toString(_y)));
	}
	
	public void dropEverything(Level world, BlockPos pos)
	{
		Containers.dropContents(world, pos, queueItems);
		Containers.dropContents(world, pos, inventory.items);
	}
	
	public RegistryAccess registryAccess()
	{
		return level.registryAccess();
	}
}
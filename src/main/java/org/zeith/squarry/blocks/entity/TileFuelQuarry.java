package org.zeith.squarry.blocks.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.Nullable;
import org.zeith.api.wrench.IWrenchable;
import org.zeith.hammerlib.annotations.OnlyIf;
import org.zeith.hammerlib.annotations.RegistryName;
import org.zeith.hammerlib.annotations.SimplyRegister;
import org.zeith.hammerlib.api.forge.BlockAPI;
import org.zeith.hammerlib.api.inv.SimpleInventory;
import org.zeith.hammerlib.api.io.NBTSerializable;
import org.zeith.hammerlib.api.tiles.IContainerTile;
import org.zeith.hammerlib.net.properties.PropertyInt;
import org.zeith.hammerlib.tiles.TileSyncableTickable;
import org.zeith.hammerlib.util.java.DirectStorage;
import org.zeith.squarry.SQCommonProxy;
import org.zeith.squarry.SQConfig;
import org.zeith.squarry.SimpleQuarry;
import org.zeith.squarry.api.ItemInjector;
import org.zeith.squarry.api.ItemStackList;
import org.zeith.squarry.api.energy.QFStorage;
import org.zeith.squarry.api.particle.ParticleVortex;
import org.zeith.squarry.blocks.BlockFuelQuarry;
import org.zeith.squarry.init.TagsSQ;
import org.zeith.squarry.inventory.ContainerFuelQuarry;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.zeith.squarry.SQConstants.FT;
import static org.zeith.squarry.SQConstants.QF;

@SimplyRegister
public class TileFuelQuarry
		extends TileSyncableTickable
		implements IContainerTile, IWrenchable
{
	@RegistryName("fuel_quarry")
	@OnlyIf(owner = SQConfig.class, member = "enableFuelQuarry")
	public static final BlockEntityType<TileFuelQuarry> FUEL_QUARRY = BlockAPI.createBlockEntityType(TileFuelQuarry::new, BlockFuelQuarry.FUEL_QUARRY);
	public static final Map<ResourceKey<Level>, Map<ChunkPos, BlockPos>> QUARRY_MAP = new HashMap<>();
	private static final Function<ResourceKey<Level>, Map<ChunkPos, BlockPos>> QUARRY_MAP_COMPUTE = world -> new HashMap<>();
	@NBTSerializable
	public final SimpleInventory inventory = new SimpleInventory(1);
	public int tickRate = SQConfig.fuelQuarryTickRate();
	@NBTSerializable
	public int _burnTicks, _totalBurnTicks, y = -65;
	@NBTSerializable
	public final QFStorage storage = new QFStorage(getQFCapacity());
	@NBTSerializable
	public final ItemStackList queueItems = ItemStackList.createList();
	public AABB boundingBox;
	//	private QuarryVortex vortex;
	private ChunkPos chunkPos;
	public ParticleVortex vortex;
	
	protected double getQFCapacity()
	{
		return 8000;
	}
	
	public final PropertyInt burnTicks = new PropertyInt(DirectStorage.create($ -> _burnTicks = $, () -> _burnTicks));
	public final PropertyInt totalBurnTicks = new PropertyInt(DirectStorage.create($ -> _totalBurnTicks = $, () -> _totalBurnTicks));
	
	protected TileFuelQuarry(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		this.dispatcher.registerProperty("burn_ticks", burnTicks);
		this.dispatcher.registerProperty("total_burn_ticks", totalBurnTicks);
	}
	
	private TileFuelQuarry(BlockPos pos, BlockState state)
	{
		super(FUEL_QUARRY, pos, state);
	}
	
	public void validateQuarry()
	{
		Map<ChunkPos, BlockPos> map = QUARRY_MAP.computeIfAbsent(level.dimension(), QUARRY_MAP_COMPUTE);
		BlockPos cpos = map.get(chunkPos);
		if(cpos != null && cpos.asLong() != worldPosition.asLong() && level.getBlockEntity(cpos) instanceof TileFuelQuarry)
		{
			level.destroyBlock(worldPosition, true);
			level.explode(null, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), 3, Explosion.BlockInteraction.NONE);
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
		
		chunkPos = c.getPos();
		int chunkX = chunkPos.x;
		int chunkZ = chunkPos.z;
		
		if(level.isClientSide)
		{
			if(boundingBox == null || boundingBox.minY != (double) y)
				boundingBox = new AABB(chunkX * 16, y, chunkZ * 16, chunkX * 16 + 16, worldPosition.getY(), chunkZ * 16 + 16);
			if(SQConfig.enableParticleVortex())
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
			tickRate = SQConfig.fuelQuarryTickRate();
			return;
		}
		
		if(y == -65)
		{
			y = worldPosition.getY() - 1;
			boundingBox = new AABB(chunkX * 16, y, chunkZ * 16, chunkX * 16 + 16, y + 1, chunkZ * 16 + 16);
			sync();
		}
		
		if(storage.storedQF > 0.0)
		{
			validateQuarry();
		}
		
		
		BlockState state0 = level.getBlockState(worldPosition);
		
		if(state0.getBlock() != getQuarryBlock())
		{
			tickRate = SQConfig.fuelQuarryTickRate();
			return;
		}
		
		if(state0.getValue(BlockStateProperties.ENABLED) && y < -63)
		{
			level.setBlock(this.worldPosition, state0.setValue(BlockStateProperties.ENABLED, false), 3);
//			validate();
			level.setBlockEntity(this);
			
			sync();
		}
		
		double QFPerBlock = FT.convertTo(ForgeHooks.getBurnTime(SQCommonProxy.COAL, null), QF) / SQConfig.getBlockPerCoal();
		QFPerBlock *= getUsageMult();
		
		int bt;
		if(state0.getValue(BlockStateProperties.ENABLED)
				&& !level.isClientSide
				&& atTickRate(20)
				&& _burnTicks < 1
				&& !(stack = inventory.getStackInSlot(0)).isEmpty()
				&& (bt = ForgeHooks.getBurnTime(stack, null)) > 0
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
		
		if(y > -64 && atTickRate(tickRate) && storage.getStoredQF(null) >= QFPerBlock)
		{
			boolean hasBrokenBlock = false;
			
			for(BlockPos pos : BlockPos.betweenClosed(chunkX * 16, y, chunkZ * 16, chunkX * 16 + 15, y, chunkZ * 16 + 15))
			{
				BlockState state = level.getBlockState(pos);
				Block b = state.getBlock();
				
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
				--y;
		}
		
		if(isMining(state0))
			captureEntityItems(level.getEntitiesOfClass(ItemEntity.class, new AABB(chunkX * 16, y, chunkZ * 16, chunkX * 16 + 16, worldPosition.getY(), chunkZ * 16 + 16)));
		
		tryEject();
		tickRate = SQConfig.fuelQuarryTickRate();
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
	
	public void addToolEnchantments(Map<Enchantment, Integer> enchantmentMap)
	{
	}
	
	public NonNullList<ItemStack> makeDrops(BlockPos pos, BlockState state)
	{
		NonNullList<ItemStack> drops = NonNullList.create();
		if(level instanceof ServerLevel sl)
		{
			ItemStack tool = new ItemStack(Items.DIAMOND_PICKAXE);
			Map<Enchantment, Integer> enchMap = new HashMap<>();
			addToolEnchantments(enchMap);
			EnchantmentHelper.setEnchantments(enchMap, tool);
			LootContext.Builder bl = new LootContext.Builder(sl)
					.withRandom(RandomSource.create())
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
		return state.getBlock() == getQuarryBlock() && state.getValue(BlockStateProperties.ENABLED) && y > -64 && storage.storedQF > 0;
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
				BlockEntity tile = level.getBlockEntity(worldPosition.relative(face));
				stack = ItemInjector.inject(stack, tile, face.getOpposite());
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
		if(s.getBlock() == BlockFuelQuarry.FUEL_QUARRY)
			level.setBlockAndUpdate(worldPosition, s.setValue(BlockStateProperties.ENABLED, enabled));
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
}
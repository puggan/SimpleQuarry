package org.zeith.squarry.api.energy;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import org.zeith.hammerlib.api.io.IAutoNBTSerializable;
import org.zeith.hammerlib.api.io.NBTSerializable;

public class QFStorage
		implements IQFConsumer, IQFProducer, IAutoNBTSerializable
{
	@NBTSerializable("QFStored")
	public double storedQF = 0.0;
	
	@NBTSerializable("QFCapacity")
	public double capacity;

	public QFStorage(double capacity)
	{
		this.capacity = capacity;
	}

	/**
	 * Stabilizes QF energy to prevent infinities, NaNs and too little numbers,
	 * also forces in-bound values for fuel.
	 */
	public void fixPower()
	{
		if(Double.isInfinite(this.storedQF) || Double.isNaN(this.storedQF) || this.storedQF <= 1.0E-2)
			this.storedQF = 0F;
		this.storedQF = Math.max(0, Math.min(capacity, this.storedQF));
	}

	public QFStorage(double capacity, double QF)
	{
		this(capacity);
		fixPower();
	}

	@Override
	public boolean canConnectQF(Direction to)
	{
		return true;
	}

	@Override
	public double getStoredQF(Direction to)
	{
		fixPower();
		return this.storedQF;
	}

	@Override
	public double getQFCapacity(Direction to)
	{
		return this.capacity;
	}

	@Override
	public double produceQF(Direction to, double howMuch, boolean simulate)
	{
		fixPower();
		double extracted = Math.min(howMuch, this.storedQF);
		if(!simulate)
			this.storedQF -= extracted;
		return extracted;
	}

	@Override
	public double consumeQF(Direction from, double howMuch, boolean simulate)
	{
		fixPower();
		double accepted = Math.min(this.capacity - this.storedQF, howMuch);
		if(!simulate)
			this.storedQF += accepted;
		return accepted;
	}
	
	@Override
	public CompoundTag serializeNBT(HolderLookup.Provider provider)
	{
		fixPower();
		return IAutoNBTSerializable.super.serializeNBT(provider);
	}
	
	@Override
	public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
	{
		IAutoNBTSerializable.super.deserializeNBT(provider, nbt);
		fixPower();
	}
	
	public static QFStorage readQFStorage(CompoundTag nbt)
	{
		return new QFStorage(nbt.getDouble("QFCapacity"), nbt.getDouble("QFStored"));
	}
}
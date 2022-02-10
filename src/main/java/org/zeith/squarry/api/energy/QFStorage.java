package org.zeith.squarry.api.energy;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class QFStorage
		implements IQFConsumer, IQFProducer, INBTSerializable<CompoundTag>
{
	public double storedQF = 0.0;
	public double capacity = 1.0;

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
	public CompoundTag serializeNBT()
	{
		CompoundTag nbt = new CompoundTag();
		fixPower();
		nbt.putDouble("QFStored", this.storedQF);
		nbt.putDouble("QFCapacity", this.capacity);
		return nbt;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt)
	{
		this.storedQF = nbt.getDouble("QFStored");
		this.capacity = nbt.getDouble("QFCapacity");
		fixPower();
	}

	public static QFStorage readQFStorage(CompoundTag nbt)
	{
		return new QFStorage(nbt.getDouble("QFCapacity"), nbt.getDouble("QFStored"));
	}
}
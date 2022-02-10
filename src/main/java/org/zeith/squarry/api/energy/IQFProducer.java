package org.zeith.squarry.api.energy;

import net.minecraft.core.Direction;

public interface IQFProducer
		extends IQFConnection
{
	double produceQF(Direction to, double quant, boolean simulate);
}
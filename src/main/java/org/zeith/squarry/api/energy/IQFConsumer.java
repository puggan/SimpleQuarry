package org.zeith.squarry.api.energy;

import net.minecraft.core.Direction;

public interface IQFConsumer
		extends IQFConnection
{
	double consumeQF(Direction to, double quant, boolean simulate);
}
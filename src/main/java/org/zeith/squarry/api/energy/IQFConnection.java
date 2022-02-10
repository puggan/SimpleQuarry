package org.zeith.squarry.api.energy;

import net.minecraft.core.Direction;

public interface IQFConnection
{
	boolean canConnectQF(Direction to);

	double getStoredQF(Direction to);

	double getQFCapacity(Direction to);
}
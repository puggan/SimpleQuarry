package org.zeith.squarry;

import org.apache.logging.log4j.Logger;
import org.zeith.hammerlib.api.energy.EnergyUnit;

public class SQConstants
{
	public static final EnergyUnit FT = EnergyUnit.getUnit("FT", 20);
	public static final EnergyUnit QF = EnergyUnit.getUnit("QF", 8);
	public static final SQCommonProxy PROXY = SimpleQuarry.PROXY;
	public static final Logger LOG = SimpleQuarry.LOG;
	public static final String MOD_ID = "squarry";
}
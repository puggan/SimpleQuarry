package org.zeith.squarry.api.energy;

public class UniversalConverter
{
	public static double EU_QF(double EU)
	{
		return EU / 2.0;
	}
	
	public static double QF_EU(double QF)
	{
		return QF * 2.0;
	}
	
	public static double RF_QF(double RF)
	{
		return RF / 8.0;
	}
	
	public static double QF_RF(double QF)
	{
		return QF * 8.0;
	}
	
	public static double TU_QF(double TU)
	{
		return TU / 8.0;
	}
	
	public static double QF_TU(double QF)
	{
		return QF / 8.0;
	}
	
	public static double FT_QF(double fuel)
	{
		return fuel * 2.5;
	}
	
	public static double QF_FT(double fuel)
	{
		return fuel / 2.5;
	}
}

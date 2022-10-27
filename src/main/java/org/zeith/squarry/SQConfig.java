package org.zeith.squarry;

import net.minecraftforge.fml.loading.FMLPaths;
import org.zeith.hammerlib.util.configured.ConfiguredLib;
import org.zeith.hammerlib.util.configured.data.DecimalValueRange;
import org.zeith.hammerlib.util.configured.data.IntValueRange;

import java.io.File;

public class SQConfig
{
	private static double blocksPerCoal;
	private static int fQuarryTickRate, pQuarryTickRate;
	private static boolean poweredQuarry, easyPowerQuarryRecipe;
	private static float feConversion, heConversion;
	private static boolean particleVortex;
	
	public static void reload()
	{
		File file = new File(FMLPaths.CONFIGDIR.get().toFile(), SQConstants.MOD_ID + ".cfg");
		
		try
		{
			var cfg = ConfiguredLib.create(file, true);
			cfg.withComment("Config file for Simple Quarry");
			
			var gameplay = cfg.setupCategory("Gameplay").withComment("Core features of the mod");
			{
				var fquarry = gameplay.setupSubCategory("Fuel Quarry").withComment("All tweaks regarding Fuel Quarry");
				{
					blocksPerCoal = fquarry.getElement(ConfiguredLib.DECIMAL, "Blocks Per Coal")
							.withRange(DecimalValueRange.rangeClosed(0, 65536))
							.withDefault(96)
							.withComment("How much blocks can 1 coal mine? This value is taken for all other fuel types as a standard.")
							.getValue()
							.doubleValue();
					
					fQuarryTickRate = fquarry.getElement(ConfiguredLib.INT, "Fuel Quarry Mine Tick Rate")
							.withRange(IntValueRange.rangeClosed(1, 65536))
							.withDefault(10)
							.withComment("How frequently the Fuel Quarry will mine blocks?")
							.getValue()
							.intValue();
				}
				
				var pquarry = gameplay.setupSubCategory("Powered Quarry").withComment("All tweaks regarding Powered Quarry");
				{
					poweredQuarry = pquarry.getElement(ConfiguredLib.BOOLEAN, "Enabled")
							.withDefault(true)
							.withComment("Should powered quarry be added into the game?")
							.getValue();
					
					pQuarryTickRate = pquarry.getElement(ConfiguredLib.INT, "Powered Quarry Mine Tick Rate")
							.withRange(IntValueRange.rangeClosed(1, 65536))
							.withDefault(5)
							.withComment("How frequently the Powered Quarry will mine blocks?")
							.getValue()
							.intValue();
					
					easyPowerQuarryRecipe = pquarry.getElement(ConfiguredLib.BOOLEAN, "Easy Powered Quarry Recipe")
							.withDefault(false)
							.withComment("Enable easier power quarry recipe?")
							.getValue();
					
					feConversion = pquarry.getElement(ConfiguredLib.DECIMAL, "Full Efficiency Conversion")
							.withRange(DecimalValueRange.rangeClosed(1F, 65536F))
							.withDefault(200F)
							.withComment("If the powered quarry gets this or higher amount of FE/transaction, it will be considered a full efficiency conversion to internal storage and divided by this value.")
							.getValue()
							.floatValue();
					
					heConversion = pquarry.getElement(ConfiguredLib.DECIMAL, "Half Efficiency Conversion")
							.withRange(DecimalValueRange.rangeClosed(1F, 65536F))
							.withDefault(300F)
							.withComment("If the powered quarry doesn't get " + ((int) feConversion) + "+ FE/transaction, it will be considered a half efficiency conversion to internal storage and divided by this value.")
							.getValue()
							.floatValue();
				}
			}
			
			var clientside = cfg.setupCategory("Clientside").withComment("Client-side features of the mod");
			{
				particleVortex = clientside.getElement(ConfiguredLib.BOOLEAN, "Particle Vortex")
						.withDefault(true)
						.withComment("Should quarry suck particles in?")
						.getValue();
			}
			
			if(cfg.hasChanged())
				cfg.save();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean enableParticleVortex()
	{
		return particleVortex;
	}
	
	public static float getFeConversion()
	{
		return feConversion;
	}
	
	public static float getHeConversion()
	{
		return heConversion;
	}
	
	public static boolean enableFuelQuarry()
	{
		return true;
	}
	
	public static boolean enablePoweredQuarry()
	{
		return poweredQuarry;
	}
	
	public static boolean enableUpgrades()
	{
		return poweredQuarry;
	}
	
	public static boolean easyPoweredQuarryRecipe()
	{
		return easyPowerQuarryRecipe;
	}
	
	public static double getBlockPerCoal()
	{
		return blocksPerCoal;
	}
	
	public static int fuelQuarryTickRate()
	{
		return fQuarryTickRate;
	}
	
	public static int poweredQuarryTickRate()
	{
		return pQuarryTickRate;
	}
}
package org.zeith.squarry;

import net.minecraftforge.fml.loading.FMLPaths;
import org.zeith.hammerlib.util.cfg.ConfigFile;
import org.zeith.hammerlib.util.cfg.entries.ConfigEntryCategory;

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
			ConfigFile cfg = new ConfigFile(file);
			cfg.setComment("Config file for Simple Quarry");

			ConfigEntryCategory gameplay = cfg.getCategory("Gameplay").setDescription("Core features of the mod");
			{
				blocksPerCoal = gameplay.getDoubleEntry("Blocks Per Coal", 96, 0, 65536).setDescription("How much blocks can 1 coal mine? This value is taken for all other fuel types as a standard.").getValue();
				fQuarryTickRate = gameplay.getIntEntry("Fuel Quarry Mine Tick Rate", 10, 1, 65536).setDescription("How frequently the Fuel Quarry will mine blocks?").getValue();

				ConfigEntryCategory pquarry = gameplay.getCategory("Powered Quarry").setDescription("All tweaks regarding Powered Quarry");
				{
					poweredQuarry = pquarry.getBooleanEntry("Enabled", true).setDescription("Should powered quarry be added into the game?").getValue();
					pQuarryTickRate = pquarry.getIntEntry("Powered Quarry Mine Tick Rate", 5, 1, 65536).setDescription("How frequently the Powered Quarry will mine blocks?").getValue();
					easyPowerQuarryRecipe = pquarry.getBooleanEntry("Easy Powered Quarry Recipe", false).setDescription("Enable easier power quarry recipe?").getValue();
					feConversion = pquarry.getFloatEntry("Full Efficiency Conversion", 200F, 1F, 65536F).setDescription("If the powered quarry gets this or higher amount of FE/transaction, it will be considered a full efficiency conversion to internal storage and divided by this value.").getValue();
					heConversion = pquarry.getFloatEntry("Half Efficiency Conversion", 300F, 1F, 65536F).setDescription("If the powered quarry doesn't get " + ((int) feConversion) + "+ FE/transaction, it will be considered a half efficiency conversion to internal storage and divided by this value.").getValue();
				}
			}

			ConfigEntryCategory clientside = cfg.getCategory("Clientside").setDescription("Client-side features of the mod");
			{
				particleVortex = clientside.getBooleanEntry("Particle Vortex", true).setDescription("Should quarry suck particles in?").getValue();
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
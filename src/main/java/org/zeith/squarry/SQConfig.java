package org.zeith.squarry;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.zeith.hammerlib.util.cfg.ConfigFile;
import org.zeith.hammerlib.util.cfg.entries.ConfigEntryCategory;
import org.zeith.hammerlib.util.mcf.RunnableReloader;

import java.io.File;

@Mod.EventBusSubscriber
public class SQConfig
{
	@SubscribeEvent
	public static void addReloadListener(AddReloadListenerEvent e)
	{
		e.addListener(RunnableReloader.of(SQConfig::reload));
	}

	private static double blocksPerCoal;
	private static int fQuarryTickRate, pQuarryTickRate;
	private static boolean easyPowerQuarryRecipe;

	public static void reload()
	{
		File file = new File(FMLPaths.CONFIGDIR.get().toFile(), SQConstants.MOD_ID + ".cfg");

		try
		{
			ConfigFile cfg = new ConfigFile(file);
			cfg.setComment("Config file for Simple Quarry");

			ConfigEntryCategory gameplay = cfg.getCategory("Gameplay").setDescription("Core features of the mod");
			blocksPerCoal = gameplay.getDoubleEntry("Blocks Per Coal", 96, 0, 65536).setDescription("How much blocks can 1 coal mine? This value is taken for all other fuel types as a standard.").getValue();
			fQuarryTickRate = gameplay.getIntEntry("Fuel Quarry Mine Tick Rate", 10, 1, 65536).setDescription("How frequently the Fuel Quarry will mine blocks?").getValue();
			pQuarryTickRate = gameplay.getIntEntry("Powered Quarry Mine Tick Rate", 5, 1, 65536).setDescription("How frequently the Powered Quarry will mine blocks?").getValue();
			easyPowerQuarryRecipe = gameplay.getBooleanEntry("Easy Powered Quarry Recipe", false).setDescription("Enable easier power quarry recipe?").getValue();

			if(cfg.hasChanged())
				cfg.save();
		} catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public static boolean enableFuelQuarry()
	{
		return true;
	}

	public static boolean enablePoweredQuarry()
	{
		return true;
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
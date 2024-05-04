package org.zeith.squarry;

import lombok.Getter;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.loading.FMLPaths;
import org.zeith.hammerlib.util.configured.ConfiguredLib;
import org.zeith.hammerlib.util.configured.data.DecimalValueRange;
import org.zeith.hammerlib.util.configured.data.IntValueRange;
import org.zeith.hammerlib.util.configured.types.*;

import java.io.File;
import java.util.*;

public class SQConfig
{
	public static final String COMMON_NAMESPACE = Tags.Items.RAW_MATERIALS.location().getNamespace();
	private static final String[] BASE_MATERIALS = {
			COMMON_NAMESPACE + ":raw_materials/",
			COMMON_NAMESPACE + ":nuggets/",
			COMMON_NAMESPACE + ":dusts/",
			COMMON_NAMESPACE + ":gems/"
	};
	
	private static @Getter double blocksPerCoal;
	private static @Getter int fuelQuarryTickRate, poweredQuarryTickRate;
	private static @Getter boolean poweredQuarry, easyPowerQuarryRecipe;
	private static @Getter float feConversion, heConversion;
	private static @Getter boolean particleVortex;
	private static @Getter String[] allowedUnificationMaterials = BASE_MATERIALS.clone();
	private static @Getter String[] excludedUnificationEntries = { };
	private static @Getter boolean enableUnification;
	
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
					
					fuelQuarryTickRate = fquarry.getElement(ConfiguredLib.INT, "Fuel Quarry Mine Tick Rate")
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
					
					poweredQuarryTickRate = pquarry.getElement(ConfiguredLib.INT, "Powered Quarry Mine Tick Rate")
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
				
				ConfigCategory unification = gameplay.setupSubCategory("Unification Upgrade").withComment("All tweaks to unification upgrade.");
				{
					enableUnification = unification.getElement(ConfiguredLib.BOOLEAN, "Enabled")
							.withDefault(true)
							.withComment("Should the unification upgrade perform unification? If set to 'false', the upgrade will do nothing.")
							.getValue();
					
					ConfigArray<ConfigString> allowedPrefixes = unification.getElement(ConfiguredLib.STRING.arrayOf(), "Allowed Prefixes")
							.withComment("This is a list of supported conversible tag groups.\nEvery entry ending with '/' would require item to have tags starting with the input entry.\nOtherwise it matches the tag precisely.");
					
					List<ConfigString> elems = allowedPrefixes.getElements();
					if(elems.isEmpty())
						for(String mat : BASE_MATERIALS)
							elems.add(allowedPrefixes.createElement().withDefault(mat));
					
					Set<String> keys = new HashSet<>();
					for(ConfigString string : new ArrayList<>(elems))
					{
						if(keys.add(string.getValue())) continue;
						elems.remove(string);
					}
					
					allowedUnificationMaterials = keys.toArray(String[]::new);
					
					ConfigArray<ConfigString> excludedItems = unification.getElement(ConfiguredLib.STRING.arrayOf(), "Excluded Items")
							.withComment("This is a list items and/or tags (use '#' as first character to reference a tag) that are not going to be conversible.");
					
					excludedUnificationEntries = excludedItems.getElements()
							.stream()
							.map(ConfigString::getValue)
							.distinct()
							.toArray(String[]::new);
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
	
	public static boolean enableUpgrades()
	{
		return poweredQuarry;
	}
}
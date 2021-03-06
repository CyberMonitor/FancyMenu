package de.keksuccino.fancymenu.menu.fancy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.keksuccino.core.input.StringUtils;
import de.keksuccino.core.math.MathUtils;
import de.keksuccino.core.properties.PropertiesSection;
import de.keksuccino.core.properties.PropertiesSerializer;
import de.keksuccino.core.properties.PropertiesSet;
import de.keksuccino.fancymenu.FancyMenu;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.common.Loader;

public class MenuCustomizationProperties {
	
	private static List<PropertiesSet> properties = new ArrayList<PropertiesSet>();
	
	public static void loadProperties() {
		properties.clear();
		
		File f = FancyMenu.getCustomizationPath();
		if (!f.exists()) {
			f.mkdirs();
		}
		
		for (File f2 : f.listFiles()) {
			PropertiesSet s = PropertiesSerializer.getProperties(f2.getAbsolutePath());
			if ((s != null) && s.getPropertiesType().equalsIgnoreCase("menu")) {
				List<PropertiesSection> l = s.getPropertiesOfType("customization-meta");
				//TODO remove deprecated "type-meta" section name
				if (l.isEmpty()) {
					l = s.getPropertiesOfType("type-meta");
				}
				if (!l.isEmpty()) {
					String s2 = l.get(0).getEntryValue("identifier");
					String s3 = l.get(0).getEntryValue("requiredmods");
					String s4 = l.get(0).getEntryValue("minimumfmversion");
					String s5 = l.get(0).getEntryValue("maximumfmversion");
					String s6 = l.get(0).getEntryValue("minimummcversion");
					String s7 = l.get(0).getEntryValue("maximummcversion");
					
					if (s2 == null) {
						continue;
					}
					if (!isVersionCompatible(s4, s5, FancyMenu.VERSION)) {
						continue;
					}
					if (!isVersionCompatible(s6, s7, ForgeVersion.mcVersion)) {
						continue;
					}
					if (!allRequiredModsLoaded(s3)) {
						continue;
					}
					
					l.get(0).addEntry("path", f2.getPath());
					properties.add(s);
				}
			}
		}
	}
	
	private static String fillUpToLength(String s, String fillWith, int length) {
		String out = s;
		int add = length - s.length();
		for (int i = 1; i <= add; i++) {
			out += fillWith;
		}
		return out;
	}
	
	private static boolean isVersionCompatible(String minimum, String maximum, String version) {
		if (version == null) {
			return true;
		}
		String versionRaw = fillUpToLength(StringUtils.replaceAllExceptOf(version, "", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"), "0", 9);

		if (MathUtils.isInteger(versionRaw)) {
			int ver = Integer.parseInt(versionRaw);
			
			if (minimum != null) {
				String minShort = StringUtils.replaceAllExceptOf(minimum, "", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
				if ((minShort.length() > 0) && MathUtils.isInteger(minShort)) {
					String minRaw = fillUpToLength(minShort, "0", 9);
					int min = Integer.parseInt(minRaw);
					if (ver < min) {
						return false;
					}
				}
			}
			
			if (maximum != null) {
				String maxShort = StringUtils.replaceAllExceptOf(maximum, "", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
				if ((maxShort.length() > 0) && MathUtils.isInteger(maxShort)) {
					String maxRaw = fillUpToLength(maxShort, "0", 9);
					int max = Integer.parseInt(maxRaw);
					if (ver > max) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private static boolean allRequiredModsLoaded(String requiredMods) {
		if ((requiredMods == null) || (requiredMods.replace(" ", "").length() == 0)) {
			return true;
		}
		List<String> mods = new ArrayList<String>();
		if (requiredMods.contains(",")) {
			for (String s : requiredMods.replace(" ", "").split("[,]")) {
				if (s.length() > 0) {
					mods.add(s);
				}
			}
		} else {
			mods.add(requiredMods.replace(" ", ""));
		}
		for (String s : mods) {
			Loader.instance();
			if (!Loader.isModLoaded(s)) {
				return false;
			}
		}
		return true;
	}
	
	public static List<PropertiesSet> getProperties() {
		return properties;
	}
	
	public static List<PropertiesSet> getPropertiesWithIdentifier(String identifier) {
		List<PropertiesSet> l = new ArrayList<PropertiesSet>();
		for (PropertiesSet s : getProperties()) {
			List<PropertiesSection> l2 = s.getPropertiesOfType("customization-meta");
			if (l2.isEmpty()) {
				l2 = s.getPropertiesOfType("type-meta");
			}
			if (l2.isEmpty()) {
				continue;
			}
			String s2 = l2.get(0).getEntryValue("identifier");
			if (s2.equalsIgnoreCase(identifier)) {
				l.add(s);
			}
		}
		return l;
	}

}

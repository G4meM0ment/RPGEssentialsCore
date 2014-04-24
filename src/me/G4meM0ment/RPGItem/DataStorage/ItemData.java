package me.G4meM0ment.RPGItem.DataStorage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.G4meM0ment.RPGEssentials.RPGEssentials;
import me.G4meM0ment.RPGEssentials.DataStorage.FileHandler;
import me.G4meM0ment.RPGItem.RPGItem;
import me.G4meM0ment.RPGItem.CustomItem.CustomItem;
import me.G4meM0ment.RPGItem.Handler.ListHandler;

public class ItemData {
	
	private RPGEssentials plugin;
	private RPGItem subplugin;
	private FileHandler fileHandler;
	
	private static String defConfig = "itemDataFileExample.yml";
	private static String logTit;
	
	private static String dir;
	private static File folder;
	private static HashMap<String, File> dataFiles = new HashMap<String, File>();
	
	public ItemData(RPGEssentials plugin) {
		this.plugin = plugin;
		subplugin = new RPGItem();
		fileHandler = new FileHandler();
		
		dir = plugin.getDir()+"/RPGItem/data";
		folder = new File(dir);
		logTit = subplugin.getLogTit();
		
		startAutoSaver();
	}
	public ItemData() {
		plugin = (RPGEssentials) Bukkit.getPluginManager().getPlugin("RPGEssentials");
		subplugin = new RPGItem();
		fileHandler = new FileHandler();
	}
	
	public static HashMap<String, File> getDataFiles() {
		return dataFiles;
	}
	
	public String getDir() {
		return dir;
	}
	
	public void addDataFile(File file) {
		getDataFiles().put(file.getName().replace(".yml", ""), file);
	}
	
	public void initializeDataFiles() {
		List<File> files = fileHandler.getFiles(folder);
		for(File file : files) {
			getDataFiles().put(file.getName().replace(".yml", ""), file);
		}
	}
	
	public File getFile(String name) {
		File file = getDataFiles().get(name);
		if(file == null) {
			addDataFile(new File(getDir()+"/"+name+".yml"));
			return getDataFiles().get(name);
		} else
		return file;
	}
	
	public void reloadDataFile(File configFile) {
		if (configFile == null) {
	    	configFile = new File(dir+"/RPGItem.yml");
			plugin.getLogger().info(logTit+"Created data file.");
	    }
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
	 
	    // Look for defaults in the jar
	    InputStream defConfigStream = plugin.getResource(defConfig);
	    if(defConfigStream != null && configFile.getName().equalsIgnoreCase("rpgitem.yml")) {
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	        config.setDefaults(defConfig);
	        config.options().copyDefaults(true);
	        try {
				config.save(configFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
//		plugin.getLogger().info(logTit+configFile.getName()+" data file loaded.");
	}
	
	public FileConfiguration getDataFile(File configFile) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
	    if(config == null) {
	        reloadDataFile(configFile);
	        config = YamlConfiguration.loadConfiguration(configFile);
	    }
	    return config;
	}
	public void saveDataFile(File configFile) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
	    if (config == null || configFile == null) {
	    	return;
	    }
	    try {
	        config.save(configFile);
	        plugin.getLogger().info(logTit+configFile.getName()+" data file saved");
	    } catch (IOException ex) {
	        plugin.getLogger().log(Level.SEVERE, logTit+"Could not save data file to " + configFile, ex);
	    }
	}
	
	public void reloadDataFiles() {
		for(File file : dataFiles.values()) {
			reloadDataFile(file);
		}
	}
	
	private void startAutoSaver() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				saveDataToFiles();
			}
		}, 0, 30000);
	}
	public void saveDataToFiles() {
		for(String s : ListHandler.getCustomItemTypes().keySet()) {
			for(CustomItem item : ListHandler.getCustomItemTypeList(s)) 
			{
				File data = getFile(s);
				FileConfiguration dataFile = getDataFile(data);
				dataFile.set(item.getId()+".durability", item.getDurability());
				try {
					dataFile.save(data);
				} catch (IOException e) {
					subplugin.getLogger().info(subplugin.getLogTit()+"Could not save "+data.getName());
				}
			}
		}
	}
}

package me.G4meM0ment.DeathAndRebirth.Handler;

import me.G4meM0ment.DeathAndRebirth.DeathAndRebirth;
import me.G4meM0ment.DeathAndRebirth.DataStorage.DropData;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryHandler {
	
	private DeathAndRebirth subplugin;
	private DropData dropData;
	
	public InventoryHandler(DeathAndRebirth subplugin)
	{
		this.subplugin = subplugin;
		dropData = new DropData();
	}
	
	/**
	 * save a players inventory at this moment to the data file
	 * @param p
	 */
	public void saveInventory(Player p)
	{
		if(p == null) return;
		FileConfiguration config = dropData.getConfig();
		String path = p.getName()+"."+p.getWorld().getName();
		int iter = 0;

		if(config == null)
		{
			subplugin.getLogger().warning(subplugin.getLogTit()+"Could not get file to backup "+p.getName()+"'s inventory");
			return;
		}
		
		//item contents
		for(ItemStack i : p.getInventory().getContents())
		{
			if(i == null) continue;
			config.set(path+".items.item"+iter++, i.serialize());
		}
		
		//armor contents
		iter = 0;
		for(ItemStack i : p.getInventory().getArmorContents())
		{	
			if(i == null) continue;
			config.set(path+".armor.item"+iter++, i.serialize());
		}
			
		dropData.saveConfig();
	}

	public void loadInventory(Player p)
	{
		FileConfiguration config = dropData.getConfig();
		String path = p.getName()+"."+p.getWorld().getName();
		
		//remove the compass
		p.getInventory().clear();
		
		//get armor and items from config and set it into their inventory
		p.getInventory().setContents(dropData.getItemsFromConfig(config, path+".items"));
		p.getInventory().setArmorContents(dropData.getItemsFromConfig(config, path+".armor"));
		
		//remove saved data
		dropData.removePlayersSection(p.getName(), p.getWorld().getName());
	}
}

package me.G4meM0ment.RPGEssentials;

import me.G4meM0ment.Chaintrain.Chaintrain;
import me.G4meM0ment.DeathAndRebirth.DeathAndRebirth;
import me.G4meM0ment.InventoryBackup.InventoryBackup;
import me.G4meM0ment.RPGEssentials.Commands.CommandHandler;
import me.G4meM0ment.RPGItem.RPGItem;
import me.G4meM0ment.UnamedPortalPlugin.UnnamedPortalPlugin;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.herocraftonline.heroes.Heroes;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

public class RPGEssentials extends JavaPlugin {

	private RPGItem rpgItem;
	private UnnamedPortalPlugin upp;
	private InventoryBackup ib;
	private DeathAndRebirth dar;
	private Chaintrain chaintrain;
	
	private CommandHandler cH;
	
	private Economy econ;
	private WorldGuardPlugin wg;
	private WorldEditPlugin we;
	private Heroes heroes;
	
	private static String dir = "plugins/RPGEssentials";
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdf = getDescription();
		
		//Enabling config
		try {
			getConfig().options().copyDefaults(true);
			saveConfig();
			getLogger().info("Config loaded.");
		} catch(Exception e) {
			getLogger().info("Could not load config.");
		}

//################ Init APIs ###################
	    if (!setupEconomy() ) {
	        getLogger().info("Disabled due to no Vault dependency found!");
	        getServer().getPluginManager().disablePlugin(this);
	        return;
	    }
		we = initWorldEdit();
		wg = initWorldGuard();
		heroes = initHeroes();
		
		//Initialize messages
		getLogger().info("Initializing sub-plugins:");
		
//################ Initializing RPGItem and debugging ################
		if(getConfig().getBoolean("RPGItemEnabled")) {
			rpgItem = new RPGItem(this);
			boolean rpgItemEnabled;
			rpgItemEnabled = rpgItem.onEnable();

			if(rpgItemEnabled)
				getLogger().info("RPGItem enabled!");
			else
				getLogger().info("RPGItem couldn't be enabled!");
		}
		else
			getLogger().info("RPGItem found, but disabled in config!");
		
//################ Initializing UnnamedPortalPlugin and debugging ################
		if(getConfig().getBoolean("UnnamedPortalPluginEnabled")) {
			upp = new UnnamedPortalPlugin(this);
			boolean uppEnabled = upp.onEnable();
			
			if(uppEnabled)
				getLogger().info("UnnamedPortalPlugin enabled!");
			else
				getLogger().info("UnnamedPortalPlugin couldn't be enabled!");
		} else
			getLogger().info("UnnamedPortalPlugin found, but disabled in config!");

//################ Initializing InventoryBackup and debugging ################
		if(getConfig().getBoolean("InventoryBackupEnabled")) 
		{
			ib = new InventoryBackup(this);
			boolean ibEnabled = ib.onEnable();
					
			if(ibEnabled)
				getLogger().info("InventoryBackup enabled!");
			else
				getLogger().info("InventoryBackup couldn't be enabled!");
		} 
		else
			getLogger().info("InventoryBackup found, but disabled in config!");
		
//################ Initializing DeathAndRebirth and debugging ################
		if(getConfig().getBoolean("DeathAndRebirthEnabled")) 
		{
			dar = new DeathAndRebirth(this);
			boolean darEnabled = dar.onEnable();
							
			if(darEnabled)
				getLogger().info("DeathAndRebirth enabled!");
			else
				getLogger().info("DeathAndRebirth couldn't be enabled!");
		} 
		else
			getLogger().info("DeathAndRebirth found, but disabled in config!");

//################ Initializing Chaintrain and debugging ################
		if(getConfig().getBoolean("ChaintrainEnabled")) {
			chaintrain = new Chaintrain(this);
			boolean chaintrainEnabled = chaintrain.onEnable();
							
			if(chaintrainEnabled)
				getLogger().info("Chaintrain enabled!");
			else
				getLogger().info("Chaintrain couldn't be enabled!");
		} else
			getLogger().info("Chaintrain found, but disabled in config!");
		
		/*
		 * 
		 * Commands
		 * 
		 */
		cH = new CommandHandler(this);
		getCommand("dar").setExecutor(cH);
		getCommand("grave").setExecutor(cH);
		getCommand("upp").setExecutor(cH);
		getCommand("ri").setExecutor(cH);
		getCommand("ib").setExecutor(cH);
		getCommand("chaintrain").setExecutor(cH);
		
		//Finished initializing plugin enabled
		getLogger().info("Initialization done!");
		
		//Plguin enabled
		getLogger().info("Enabled version "+pdf.getVersion());
	}

	@Override
	public void onDisable() 
	{
		//Disable messages
		getServer().getScheduler().cancelTasks(this);
		
		//Disable sub-plugins
		getLogger().info("Disabling sub-plugins:");
		
		if(rpgItem != null)
			if(!rpgItem.onDisable() && rpgItem.isEnabled())
				getLogger().info("RPGItem disabled!");
		
		if(upp != null)
			if(upp.onDisable() && !upp.isEnabled())
				getLogger().info("UnnamedPortalPlugin disabled!");
		
		if(ib != null)
			if(ib.onDisable() && !ib.isEnabled())
				getLogger().info("InventoryBackup disabled!");
		
		if(dar != null)
			if(dar.onDisable() && !dar.isEnabled())
				getLogger().info("DeathAndRebirth disabled!");
		
		if(chaintrain != null)
			if(chaintrain.onDisable() && !chaintrain.isEnabled())
				getLogger().info("Chaintrain disabled!");
	}
	
	/*
	 * 
	 * Getter of variables from main plugin
	 * 
	 */
	public String getDir() {
		return dir;
	}
	
	
	/*
	 * 
	 * 
	 * Initialization of other plugins
	 * 
	 * 
	 * 
	 */
	/**
	 * Checks for the plugin enabled and sets it to null if not found
	 * @return
	 */
    private boolean setupEconomy() {
    	if(getServer().getPluginManager().getPlugin("Vault") == null) {
    		this.getLogger().info("Vault not found");
    		return false;
    	}
    	RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
    	if (rsp == null) {
    		getLogger().info("No economy plugin found!");
    		return true;
    	}
    	econ = rsp.getProvider();
    	return econ != null;
    }
    public Economy getEconomy() {
    	return econ;
    }
	
	/**
	 * Checks for the plugin enabled and sets it to null if not found
	 * @return
	 */
	private WorldEditPlugin initWorldEdit() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldEdit");
	 
	    // WorldEdit may not be loaded
	    if (plugin == null || !(plugin instanceof WorldEditPlugin))
	        return null; // Maybe you want throw an exception instead
		getLogger().info("WorldEdit found enabled features");
	    return (WorldEditPlugin) plugin;
	}
	public WorldEditPlugin getWorldEdit() {
		return we;
	}
	
	/**
	 * Checks for the plugin enabled and sets it to null if not found
	 * @return
	 */
	private WorldGuardPlugin initWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    // WorldGuard may not be loaded
	    if (plugin == null || !(plugin instanceof WorldGuardPlugin)) 
	        return null; // Maybe you want throw an exception instead
		getLogger().info("WorldGuard found enabled features");
	    return (WorldGuardPlugin) plugin;
	}
	public WorldGuardPlugin getWorldGuard() {
		return wg;
	}
	
	/**
	 * Checks for the plugin enabled and sets it to null if not found
	 * @return
	 */
	private Heroes initHeroes() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("Heroes");
	 
	    if (plugin == null || !(plugin instanceof Heroes))
	        return null; // Maybe you want throw an exception instead
		getLogger().info("Heroes found enabled features");
	    return (Heroes) plugin;
	}
	public Heroes getHeroes() {
		return heroes;
	}
	
	/*
	 * 
	 * File and cache data utils
	 * 
	 */
	/**
	 * Reloads all files data
	 */
	public void reloadRPGEssentials() {
		reloadConfig();
		if(getConfig().getBoolean("RPGItemEnabled") && rpgItem != null && rpgItem.isEnabled())
			rpgItem.reloadConfigs();
		if(getConfig().getBoolean("UnnamedPortalPluginEnabled") && upp != null && upp.isEnabled())
			upp.reloadConfigs();
	}
	
	/*
	 * 
	 * Getter for subplugins
	 * 
	 */
	public RPGItem getRPGItem() {
		return rpgItem;
	}
	public UnnamedPortalPlugin getUnnamedPortalPlugin() {
		return upp;
	}
	public DeathAndRebirth getDeathAndRebirth() {
		return dar;
	}
	public Chaintrain getChaintrain() {
		return chaintrain;
	}
	public InventoryBackup getInventoryBackup() {
		return ib;
	}

	public boolean isSpoutcraftPluginEnabled() {
		return false;
	}
 }

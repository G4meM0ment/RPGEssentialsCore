package me.G4meM0ment.RPGItem.Listener;

import me.G4meM0ment.RPGEssentials.RPGEssentials;
import me.G4meM0ment.RPGItem.RPGItem;
import me.G4meM0ment.RPGItem.CustomItem.CustomItem;
import me.G4meM0ment.RPGItem.DataStorage.ItemData;
import me.G4meM0ment.RPGItem.Handler.CustomItemHandler;
import me.G4meM0ment.RPGItem.Handler.ItemHandler;
import me.G4meM0ment.RPGItem.Handler.PermHandler;
import me.G4meM0ment.RPGItem.Handler.EventHandler.InventoryHandler;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PListener implements Listener {
	
	@SuppressWarnings("unused")
	private RPGEssentials plugin;
	private InventoryHandler invHandler;
	private ItemData itemData;
	private PermHandler permHandler;
	private ItemHandler itemHandler;
	private CustomItemHandler customItemHandler;
	private RPGItem subplugin;
	
	public PListener(RPGEssentials plugin) {
		this.plugin = plugin;
		subplugin = new RPGItem();
		invHandler = new InventoryHandler();
		itemData = new ItemData();
		permHandler = new PermHandler(plugin);
		itemHandler = new ItemHandler();
		customItemHandler = new CustomItemHandler();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event){
		Player p = event.getPlayer();
		PlayerInventory i = p.getInventory();
		if(p == null || i == null) return;
		
		invHandler.processInventory(i, p);
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		PlayerInventory i = p.getInventory();
		if(p == null || i == null) return;
		
		itemData.saveDataToFiles();
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		if(!(event.getClickedBlock() instanceof Block)) return;
		Block b = event.getClickedBlock();
		if(b.getType() == Material.ANVIL && p.getGameMode() != GameMode.CREATIVE) event.setCancelled(true);
		if(p == null || !itemHandler.isCustomItem(p.getItemInHand()) || !subplugin.getConfig().getBoolean("useIDs")) return;
		
		CustomItem cItem = customItemHandler.getCustomItem(p.getItemInHand());
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
				Material m = cItem.getRepairMaterial();
				if(m != null)
				{
					if(permHandler.hasRPGItemRepairPerms(p) && hasItemInInv(p, cItem.getRepairMaterial(), 1))
					{
						customItemHandler.repairCustomItem(cItem, subplugin.getConfig().getInt("RepairAmountPerRepair"));
						p.playSound(b.getLocation(), Sound.ANVIL_USE, 50, 1);
						p.getInventory().removeItem(new ItemStack(cItem.getRepairMaterial(), 1));
						event.setCancelled(true);
					}	
				}
		} else if(event.getAction() == Action.LEFT_CLICK_BLOCK)
		{
			if(cItem.getDurability() == 0)
				event.setCancelled(true);
		}
	}
	
	/**
	 * Hopefully possible in the future to get entity player is looking at to add range to weapons
	 * @param event
	 */
	/*@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerInteractLeftClick(PlayerInteractEvent event) {
		if(event.getAction().equals(Action.LEFT_CLICK_AIR)) {
			event.getPlayer().sendMessage("Registered left click on air");
			if(event.getPlayer().getItemInHand().getType().equals(Material.WRITTEN_BOOK)) {
				event.getPlayer().sendMessage("Gebetsbuch + "+event.getPlayer().get.getX()+" "+event.getPlayer().getEyeLocation().getY()+" "+event.getPlayer().getEyeLocation().getZ());
				for(Entity  e : event.getPlayer().getNearbyEntities(10.0, 10.0, 10.0)) {
					if(!(e instanceof LivingEntity)) continue;
					event.getPlayer().sendMessage(e.getLocation().getX()+" "+e.getLocation().getY()+" "+e.getLocation().getZ());
					if(event.getPlayer().getEyeLocation().distance(e.getLocation()) < 0.75) {
						event.getPlayer().sendMessage("Registered left click on entity");
						break;
					}
				}
			}
			
		}
	} */
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onEntityShootBow(EntityShootBowEvent event) {
		if(event.getEntity() instanceof Player)
			customItemHandler.repairItem(event.getBow());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		Player p = event.getPlayer();
		ItemStack i = event.getItem().getItemStack();
		
		if(itemHandler.isCustomItem(i))
			customItemHandler.updateItem(i, p, true);
	}
	
	public boolean hasItemInInv(Player p, Material m, int amount) {
		int counted = 0;
		for(ItemStack i : p.getInventory()) 
		{
			if(i == null) continue;
			if(i.getType() == m) 
			{
				if(i.getAmount() >= amount)
					return true;
				else
					counted += i.getAmount();
			}
		}
		if(counted >= amount)
			return true;
		
		return false;
	}
	
}

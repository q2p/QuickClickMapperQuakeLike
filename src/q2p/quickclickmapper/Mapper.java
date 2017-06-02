package q2p.quickclickmapper;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import q2p.quickclickmapper.base.levels.LevelBase;

public class Mapper extends JavaPlugin implements Listener {
	private static final String validLetters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
	private static final short MAX_SIZE = 128;
	/*
	TODO:
	инструкция по использованию
	убрать лишние функции
	рефактор кода
	*/
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			if(command.getName().equals("createbox")) {
				createBox(player, args);
				return true;
			}
			else if(command.getName().equals("removebox")) {
				removeBox(player, args);
				return true;
			}
			else if(command.getName().equals("parse")) {
				parse(player, args);
				return true;
			}
			else if(command.getName().equals("load")) {
				load(player, args);
				return true;
			}
			else if(command.getName().equals("buildkit")) {
				giveBuildKit(player, args);
				return true;
			}
		}
		if(command.getName().equals("day")) {
			List<World> worlds = sender.getServer().getWorlds();
			for(World world : worlds) world.setFullTime(1000);
			return true;
		}
		return false;
	}
	
	private void giveBuildKit(Player player, String[] args) {
		Player target;
		if(args.length == 1 && ((target = Bukkit.getPlayer(args[0])) == null || !target.isOnline())) {
			player.sendMessage("Player " + args[0] + " not found.");
			return;
		} else if(args.length == 0) {
			target = player;
		} else {
			player.sendMessage("Usage: /buildkit [player]");
			return;
		}
		target.setGameMode(GameMode.CREATIVE);
		Inventory inventory = target.getInventory();
		inventory.clear();
		for(int i = 0; i < LevelBase.BASES.length; i++) {
			ItemStack stack = LevelBase.BASES[i].getStack();
			if(stack != null) inventory.addItem(stack);
		}
		player.updateInventory();
	}

	private void load(Player player, String[] args) {
		if(args.length != 1) {
			player.sendMessage("Usage: /load <file name>");
			return;
		}
		File file = new File("maps/"+args[0]+".map");
		if(!file.exists()) {
			player.sendMessage("Map "+args[0]+" was not found.");
			return;
		}
		if(!isOnMarkedBlock(player)) {
			player.sendMessage("You must stand on marked bedrock block");
			return;
		}
		Location location = player.getLocation();
		location.setY(location.getY() - 0.8);
		location.setX(location.getX() + 1);
		location.setZ(location.getZ() + 1);
		LevelBase.beginLoading(location.getBlock(), file);
		player.sendMessage("Map loaded.");
	}

	private void parse(Player player, String[] args) {
		if(args.length != 0) {
			player.sendMessage("Usage: /parse");
			return;
		}
		if(!isOnMarkedBlock(player)) {
			player.sendMessage("You must stand on marked bedrock block");
			return;
		}
		
		Location location = player.getLocation();
		location.setY(location.getY() - 0.8);
		location.setX(location.getX() + 1);
		location.setZ(location.getZ() + 1);
		LevelBase.beginParsing(location.getBlock());
	}

	private static void removeBox(Player player, String[] args) {
		if(args.length != 0) {
			player.sendMessage("Usage: /removebox");
			return;
		}
		if(!isOnMarkedBlock(player)) {
			player.sendMessage("You must stand on marked bedrock block");
			return;
		}
		Location location = player.getLocation();
		location.setX(location.getX() + 1);
		location.setY(location.getY() - 0.5);
		location.setZ(location.getZ() + 1);
		Block beg = location.getBlock();
		int bx = beg.getX();
		int by = beg.getY();
		int bz = beg.getZ();
		World w = player.getWorld();
		short[] size = getBoxSize(beg);
		for(short x = 0; x < size[0]+2; x++) {
			for(short y = (short)(size[1]+2); y >= 0; y--) {
				for(short z = 0; z < size[2]+2; z++) {
					w.getBlockAt(bx+x, by+y, bz+z).setType(Material.AIR);
				}
			}
		}
		w.getBlockAt(bx-1, by+1, bz-1).setType(Material.AIR);
		w.getBlockAt(bx-1, by, bz-1).setType(Material.AIR);
		return;
	}

	public static short[] getBoxSize(Block beg) {
		World w = beg.getWorld();
		int bx = beg.getX();
		int by = beg.getY();
		int bz = beg.getZ();
		short[] size = new short[]{0,0,0};
		while(size[0]<=MAX_SIZE+2) {
			Block b = w.getBlockAt(bx+size[0], by, bz);
			if(b.getType() != Material.BEDROCK) break;
			size[0]++;
		}
		while(size[1]<=MAX_SIZE+2) {
			Block b = w.getBlockAt(bx, by+size[1], bz);
			if(b.getType() != Material.BEDROCK) break;
			size[1]++;
		}
		while(size[2]<=MAX_SIZE+2) {
			Block b = w.getBlockAt(bx, by, bz+size[2]);
			if(b.getType() != Material.BEDROCK) break;
			size[2]++;
		}
		for(byte i = 0; i < 3; i++) size[i]-=2;
		return size;
	}

	private static void createBox(Player player, String sizeStr[]) {
		if(sizeStr.length != 3) {
			player.sendMessage("Usage: /createbox <x size> <y size> <z size>");
			return;
		}
		int[] size = new int[3];
		for(byte i = 0; i < 3; i++) {
			try {
				size[i] = Integer.parseInt(sizeStr[i]);
			} catch (NumberFormatException e) {
				player.sendMessage("Usage: /createbox <x size> <y size> <z size>");
				return;
			}
			if(size[i] > MAX_SIZE) {
				player.sendMessage("Max size "+MAX_SIZE+" blocks");
				return;
			} else if(size[i] < 1) {
				player.sendMessage("Minimal size 1 blocks");
				return;
			}
		}
		Location location = player.getLocation();
		location.setY(location.getY() - 1);
		location.setX(location.getX() + 1);
		location.setZ(location.getZ() + 1);
		Block beg = location.getBlock();
		int bx = beg.getX();
		int by = beg.getY();
		int bz = beg.getZ();
		World w = player.getWorld();
		for(int y = 0; y < size[1]+2; y++) {
			for(int x = 0; x < size[0]+2; x++) {
				w.getBlockAt(bx+x, by+y, bz).setType(Material.BEDROCK);
				w.getBlockAt(bx+x, by+y, bz+size[2]+1).setType(Material.BEDROCK);
			}
			for(int z = 0; z < size[2]+2; z++) {
				w.getBlockAt(bx, by+y, bz+z).setType(Material.BEDROCK);
				w.getBlockAt(bx+size[0]+1, by+y, bz+z).setType(Material.BEDROCK);
			}
		}
		for(int x = 0; x < size[0]; x++) {
			for(int z = 0; z < size[2]; z++) {
				w.getBlockAt(bx+x+1, by, bz+z+1).setType(Material.BEDROCK);
				w.getBlockAt(bx+x+1, by+size[1]+1, bz+z+1).setType(Material.GLASS);
			}
		}
		for(int x = 0; x < size[0]; x++) {
			for(int y = 0; y < size[1]; y++) {
				for(int z = 0; z < size[2]; z++) {
					w.getBlockAt(bx+x+1, by+y+1, bz+z+1).setType(Material.AIR);
				}
			}
		}
		w.getBlockAt(bx-1, by, bz-1).setType(Material.BEDROCK);
		Block block = w.getBlockAt(bx-1, by+1, bz-1);
		block.setType(Material.SIGN_POST);
		Sign sign = (Sign) block.getState();
		sign.setLine(0, "<file name>");
		sign.setLine(1, "<map display name> (if not spawn)");
		player.teleport(new Location(w, bx-0.5, by+1, bz-0.5), TeleportCause.PLUGIN);
	}
	
	private static boolean isOnMarkedBlock(Player player) {
		if(!((Entity)player).isOnGround()) return false;
		Location location = player.getLocation();
		location.setY(location.getY()-0.8);
		if(location.getBlock().getType() != Material.BEDROCK) return false;
		return true;
	}
	
	public static int[] threeIntegerArgumentsFromLine(String line) {
		int args[] = new int[3];
		String[] s = line.trim().split(" ");
		if(s.length == 3) {
			for(byte i = 0; i < 3; i++) {
				try { args[i] = Integer.parseInt(s[i]); }
				catch(Exception e) { args[i] = 0; }
			}
		} else args = new int[]{0,0,0};
		return args;
	}
	
	public static byte[] twoByteArgumentsFromLine(String line) {
		byte[] args = new byte[2];
		String[] s = line.trim().split(" ");
		if(s.length == 2) {
			for(byte i = 0; i < 2; i++) {
				try { args[i] = Byte.parseByte(s[i]); }
				catch(Exception e) { args[i] = 0; }
			}
		} else args = new byte[]{0,0};
		return args;
	}
	
	public static float[] threeFloatArgumentsFromLine(String line) {
		float args[] = new float[3];
		String[] s = line.trim().split(" ");
		if(s.length == 3) {
			for(byte i = 0; i < 3; i++) {
				try { args[i] = Float.parseFloat(s[i]); }
				catch(Exception e) { args[i] = 0; }
			}
		} else args = new float[]{0,0,0};
		return args;
	}

	public static String byteArrayToIntegerString(byte[] array) {
		return array[0] + " " + array[1] + " " + array[2];
	}
	public static byte[] threeByteArgumentsFromLine(String line) {
		byte args[] = new byte[3];
		String[] s = line.trim().split(" ");
		if(s.length == 3) {
			for(byte i = 0; i < 3; i++) {
				try { args[i] = Byte.parseByte(s[i]); }
				catch(Exception e) { args[i] = 0; }
			}
		} else args = new byte[]{0,0,0};
		return args;
	}
	
	public static String byteArrayToFloatString(byte[] array) {
		String ret = "";
		String s;
		for(byte i = 0; i < 3; i++) {
			if(array[i]%2 != 0) {
				s = ""+((float)array[i]/2f);
				ret += " " + s.substring(0,s.indexOf(".")+2);
			}
			else ret += " " + (array[i]/2);
		}
		return ret.trim();
	}
	
	public static boolean isValid(String string) {
		for(int i = 0; i < string.length(); i++) if(!validLetters.contains(""+string.charAt(i))) return false;
		return true;
	}

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event) {
		if(event.toWeatherState()){
			event.getWorld().setStorm(false);
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onThunerChange(ThunderChangeEvent event) {
		if(event.toThunderState()){
			event.getWorld().setStorm(false);
			event.setCancelled(true);
		}
	}

}

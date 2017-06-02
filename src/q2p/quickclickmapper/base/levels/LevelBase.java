package q2p.quickclickmapper.base.levels;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import q2p.quickclickmapper.Mapper;
import q2p.quickclickmapper.base.blocks.AirBase;
import q2p.quickclickmapper.base.blocks.AndesiteBase;
import q2p.quickclickmapper.base.blocks.BlockBase;
import q2p.quickclickmapper.base.blocks.DirtBase;
import q2p.quickclickmapper.base.blocks.GlowstoneBase;
import q2p.quickclickmapper.base.blocks.GoldBlockBase;
import q2p.quickclickmapper.base.blocks.GrassBase;
import q2p.quickclickmapper.base.blocks.LavaBase;
import q2p.quickclickmapper.base.blocks.StoneBase;
import q2p.quickclickmapper.base.blocks.StoneBrickBase;
import q2p.quickclickmapper.base.blocks.StoneBrickStairsBase;
import q2p.quickclickmapper.base.blocks.StoneBrickStepBase;
import q2p.quickclickmapper.base.info.SignData;

public class LevelBase {
	public static final BlockBase[] BASES = new BlockBase[] {
			new AirBase(),
			new StoneBase(),
			new DirtBase(),
			new GrassBase(),
			new AndesiteBase(),
			new GoldBlockBase(),
			new GlowstoneBase(),
			new LavaBase(),
			new StoneBrickBase(),
			new StoneBrickStepBase(false),
			new StoneBrickStepBase(true),
			new StoneBrickStairsBase(false, BlockFace.NORTH),
			new StoneBrickStairsBase(false, BlockFace.SOUTH),
			new StoneBrickStairsBase(false, BlockFace.WEST),
			new StoneBrickStairsBase(false, BlockFace.EAST),
			new StoneBrickStairsBase(true, BlockFace.NORTH),
			new StoneBrickStairsBase(true, BlockFace.SOUTH),
			new StoneBrickStairsBase(true, BlockFace.WEST),
			new StoneBrickStairsBase(true, BlockFace.EAST)
	};
	private static final LevelSpecification[] SPECS = new LevelSpecification[] {
		new SpawnSpecification(),
		new QuakeSpecification()
	};
		
	public static void beginParsing(Block beg) {
		World w = beg.getWorld();
		int[] begin = new int[] {beg.getX(), beg.getY(), beg.getZ()};
		Block block = w.getBlockAt(begin[0]-1, begin[1]+1, begin[2]-1);
		byte spec = -1;
		if(block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) {
			SignData data = new SignData(block, (short)0, (short)0, (short)0); 
			for(byte i = 0; i < SPECS.length; i++) {
				if(SPECS[i].compare(data.lines[0])) {
					spec = i;
					break;
				}
			}
			if(spec == -1) {
				Bukkit.broadcastMessage("Error: Level type " + data.lines[0] + " not suported.");
				return;
			}
			if(!SPECS[spec].parseMeta(data.lines[1], data.lines[2], data.lines[3])) return;
		} else {
			Bukkit.broadcastMessage("Error: Meta sign on marked bedrock block not found.");
			return;
		}
		ArrayList<SignData> signs = new ArrayList<SignData>();
		short[] size = Mapper.getBoxSize(beg);
		byte[][][] content = new byte[size[2]][size[1]][size[0]];
		for(byte i = 0; i < 3; i++) begin[i]++;

		for(short z = 0; z < size[2]; z++) {
			for(short y = 0; y < size[1]; y++) {
				for(short x = 0; x < size[0]; x++) {
					block = w.getBlockAt(begin[0]+x, begin[1]+y, begin[2]+z);
					if(block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN) signs.add(new SignData(block, x, y, z));
					else {
						boolean found = false;
						for(byte i = 0; i < BASES.length; i++) {
							if(BASES[i].compare(block)) {
								content[z][y][x] = i;
								found = true;
							}
						}
						if(!found) content[z][y][x] = 0;
					}
				}
			}
		}
		int offsetX = 0;
		int offsetY = 0;
		int offsetZ = 0;
		boolean found = false;
		for(;offsetX < size[0]; offsetX++) {
			for(int z = 0; z < size[2] && !found; z++) {
				for(int y = 0; y < size[1] && !found; y++) {
					if(content[z][y][offsetX] != 0) found = true;
				}
			}
			if(found) break;
		}
		found = false;
		for(;offsetY < size[1]; offsetY++) {
			for(int z = 0; z < size[2] && !found; z++) {
				for(int x = 0; x < size[0] && !found; x++) {
					if(content[z][offsetY][x] != 0) found = true;
				}
			}
			if(found) break;
		}
		found = false;
		for(;offsetZ < size[2]; offsetZ++) {
			for(int y = 0; y < size[1] && !found; y++) {
				for(int x = 0; x < size[0] && !found; x++) {
					if(content[offsetZ][y][x] != 0) found = true;
				}
			}
			if(found) break;
		}
		int borderX = size[0];
		int borderY = size[1];
		int borderZ = size[2];
		found = false;
		for(;borderX > offsetX; borderX--) {
			for(int z = 0; z < size[2] && !found; z++) {
				for(int y = 0; y < size[1] && !found; y++) {
					if(content[z][y][borderX-1] != 0) found = true;
				}
			}
			if(found) break;
		}
		found = false;
		for(;borderY > offsetY; borderY--) {
			for(int z = 0; z < size[2] && !found; z++) {
				for(int x = 0; x < size[0] && !found; x++) {
					if(content[z][borderY-1][x] != 0) found = true;
				}
			}
			if(found) break;
		}
		found = false;
		for(;borderZ > offsetZ; borderZ--) {
			for(int y = 0; y < size[1] && !found; y++) {
				for(int x = 0; x < size[0] && !found; x++) {
					if(content[borderZ-1][y][x] != 0) found = true;
				}
			}
			if(found) break;
		}
		size = new short[]{(short)(borderX-offsetX),(short)(borderY-offsetY),(short)(borderZ-offsetZ)};
		byte[][][] newContent = new byte[size[2]][size[1]][size[0]];
		for(int z = 0; z < size[2]; z++) {
			for(int y = 0; y < size[1]; y++) {
				for(int x = 0; x < size[0]; x++) {
					newContent[z][y][x] = content[offsetZ+z][offsetY+y][offsetX+x];
				}
			}
		}
		content = newContent;
		for(SignData sign : signs) {
			sign.position[0] -= offsetX;
			sign.position[1] -= offsetY;
			sign.position[2] -= offsetZ;
		}
		
		while(!signs.isEmpty()) SPECS[spec].parseSign(signs.remove(0));
		
		SPECS[spec].postSign();
		
		File file = new File("maps/");
		if(!file.isDirectory()) file.delete();
		if(!file.exists()) file.mkdir();
		file = new File("maps/"+SPECS[spec].getFileName()+".map");
		if(file.exists()) file.delete();
		try { file.createNewFile();
		} catch (IOException e) { 
			Bukkit.broadcastMessage("Can't create file " + file.getAbsolutePath());
			return;
		}
		DataOutputStream dos = null;
		try { dos = new DataOutputStream(new FileOutputStream(file));
		} catch (FileNotFoundException e) {
			Bukkit.broadcastMessage("Can't write to file " + file.getAbsolutePath());
			return;
		}
		try {
			/*
			spec byte
			info many
			size[3] short
			blocks[xyz] byte
			*/
			dos.writeByte(spec);
			SPECS[spec].writeInfo(dos);
			for(byte i = 0; i < 3; i++) dos.writeShort(size[i]);
			for(short z = 0; z < size[2]; z++) {
				for(short y = 0; y < size[1]; y++) {
					for(short x = 0; x < size[0]; x++) {
						dos.writeByte(content[z][y][x]);
					}
				}
			}
		} catch (IOException e) {
			try {dos.close();} catch (IOException e1) {}
			Bukkit.broadcastMessage("Can't write to file " + file.getAbsolutePath());
			return;
		}
		try {dos.close();} catch (IOException e) {}
		SPECS[spec].cleanUp();
		Bukkit.broadcastMessage("Map saved.");
	}

	public static void beginLoading(Block beg, File file) {
		DataInputStream dis = null;
		try { dis = new DataInputStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			Bukkit.broadcastMessage("Can't read from file " + file.getAbsolutePath());
			return;
		}
		short[] size = new short[3];
		byte[][][] map = null;
		byte spec = 0;
		try {
			spec = dis.readByte();
			SPECS[spec].readInfo(dis, file.getName().substring(0, file.getName().length()-".map".length()));
			for(byte i = 0; i < 3; i++) size[i] = dis.readShort();
			map = new byte[size[2]][size[1]][size[0]];
			for(short z = 0; z < size[2]; z++) {
				for(short y = 0; y < size[1]; y++) {
					for(short x = 0; x < size[0]; x++) {
						map[z][y][x] = dis.readByte();
					}
				}
			}
		} catch (IOException e) {
			try {dis.close();} catch (IOException e1) {}
			Bukkit.broadcastMessage("Can't read from file " + file.getAbsolutePath());
			return;
		}
		try {dis.close();} catch (IOException e1) {}
		World w = beg.getWorld();
		int[] begin = new int[] {beg.getX(), beg.getY(), beg.getZ()};
		for(byte i = 0; i < 3; i++) begin[i]++;
		
		for(short z = 0; z < size[2]; z++) {
			for(short y = 0; y < size[1]; y++) {
				for(short x = 0; x < size[0]; x++) {
					BASES[map[z][y][x]].place(w.getBlockAt(x+begin[0], y+begin[1], z+begin[2]));
				}
			}
		}
		
		ArrayList<SignData> datas = SPECS[spec].createSigns();
		while(!datas.isEmpty()) {
			SignData data = datas.remove(0);
			Block block = w.getBlockAt(begin[0]+data.position[0], begin[1]+data.position[1], begin[2]+data.position[2]);
			block.setType(Material.SIGN_POST);
			Sign sign = (Sign)block.getState();
			for(byte i = 0; i < 4; i++) sign.setLine(i, data.lines[i]);
			sign.update();
		}
		Block block = w.getBlockAt(begin[0]-2, begin[1], begin[2]-2);
		block.setType(Material.SIGN_POST);
		Sign sign = (Sign)block.getState();
		String[] lines = SPECS[spec].sendMeta();
		for(byte i = 0; i < 4; i++) sign.setLine(i, lines[i]);
		sign.update();
		SPECS[spec].cleanUp();
	}
}
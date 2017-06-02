package q2p.quickclickmapper.base.info;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SignData {
	public String[] lines;
	public short[] position;
	public SignData(Block block, short x, short y, short z) {
		Sign sign = (Sign) block.getState();
		lines = new String[4];
		for(byte i = 0; i < lines.length; i++) {
			String line = sign.getLine(i);
			if(line == null || line.trim() == "") lines[i] = "";
			else lines[i] = line;
		}
		position = new short[] {x, y, z};
	}
	public SignData(short[] position, String[] lines) {
		this.lines = lines;
		this.position = position;
	}
}

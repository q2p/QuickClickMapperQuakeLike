package q2p.quickclickmapper.base.blocks;

import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

public interface BlockBase {
	ItemStack getStack();
	boolean compare(Block block);
	void place(Block block);
}
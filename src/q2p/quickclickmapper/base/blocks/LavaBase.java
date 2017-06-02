package q2p.quickclickmapper.base.blocks;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;

public class LavaBase implements BlockBase {
	public ItemStack getStack() {
		return new Wool(DyeColor.ORANGE).toItemStack(1);
	}
	public boolean compare(Block block) {
		return (block.getType() == Material.WOOL && ((Wool)block.getState().getData()).getColor() == DyeColor.ORANGE);
	}
	public void place(Block block) {
		block.setType(Material.WOOL);
		((Wool)block.getState().getData()).setColor(DyeColor.ORANGE);
		block.getState().update();
	}
}
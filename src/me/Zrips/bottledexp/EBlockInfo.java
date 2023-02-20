package me.Zrips.bottledexp;

import java.util.HashMap;

import org.bukkit.Location;

public class EBlockInfo {

    private static HashMap<String, EBlock> AllBlocks = new HashMap<String, EBlock>();

    public EBlockInfo() {
    }

    public void addBlock(EBlock block) {
	AllBlocks.put(convert(block), block);
    }

    public HashMap<String, EBlock> getAllBlocks() {
	return AllBlocks;
    }

    public EBlock getBlock(Location loc) {
	return AllBlocks.get(loc.getWorld().getName() + ":" + loc.getBlockX() + ":" + loc.getBlockY() + ":" + loc.getBlockZ());
    }

    public void removeBlock(EBlock block) {
	AllBlocks.remove(convert(block));
    }

    private static String convert(EBlock block) {
	return block.getWorld() + ":" + block.getX() + ":" + block.getY() + ":" + block.getZ();
    }
}

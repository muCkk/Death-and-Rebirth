package muCkk.DeathAndRebirth;

import org.bukkit.block.Block;

public class DARShrine  { 
	
	private Block [] blocks;
	private int [] originalIDs, tb;
	private String world;
	
	// tb, rock1, rock2, rock3
	// NW, N, NE
	// SW, S, SE
	// W, E
	public DARShrine(Block[] blocks) {
		this.blocks = blocks;
		world = blocks[0].getWorld().getName();
		originalIDs = new int[12];
		
		tb = new int[3];
		tb[0] =  blocks[0].getX();
		tb[1] = blocks[0].getY(); 
		tb[2] = blocks[0].getZ();
		
		setup(blocks);		
	}
	public DARShrine(Block[] blocks, int[] ids) {
		this.blocks = blocks;
		world = blocks[0].getWorld().getName();
		this.originalIDs = ids;
	}
	public Block[] getBlocks() {
		return blocks;
	}
	private void setup(Block[] blocks) {
		for(int i=0; i < blocks.length; i++) {
			originalIDs[i] = blocks[i].getTypeId();
		}
	}
	public int[] getIDs() {
		return originalIDs;
	}	
	public int[] getTB() {
		return tb;
	}
	public String getWorld() {
		return world;
	}
}

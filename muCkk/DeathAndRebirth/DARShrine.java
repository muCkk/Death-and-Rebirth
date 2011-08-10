package muCkk.DeathAndRebirth;

import org.bukkit.block.Block;

public class DARShrine  { 
	
	private Block [] blocks;
	private int [] originalIDs;
	private int [] max, min;
	private int [] tb;
	
	// tb, rock1, rock2, rock3
	// NW, N, NE
	// SW, S, SE
	// W, E
	public DARShrine(Block[] blocks) {
		this.blocks = blocks;
		originalIDs = new int[12];
		
		tb = new int[3];
		tb[0] =  blocks[0].getX();
		tb[1] = blocks[0].getY(); 
		tb[2] = blocks[0].getZ();
		
		setup(blocks);		
	}
	public DARShrine(Block[] blocks, int[] ids) {
		this.blocks = blocks;
		this.originalIDs = ids;
	}
	public Block[] getBlocks() {
		return blocks;
	}
	private void setup(Block[] blocks) {
		max = new int[3];
		max[0] = blocks[7].getX();
		max[1] = blocks[3].getY();
		max[2] = blocks[7].getZ();
		min = new int[3];
		min[0] = blocks[6].getX();
		min[1] = blocks[6].getY();
		min[2] = blocks[6].getZ();
		
		
		for(int i=0; i < blocks.length; i++) {
			originalIDs[i] = blocks[i].getTypeId();
		}
	}
	public int[] getIDs() {
		return originalIDs;
	}
	public int[] getMax() {
		return max;
	}
	public int[] getMin() {
		return min;
	}
	
	public int[] getTB() {
		return tb;
	}
}

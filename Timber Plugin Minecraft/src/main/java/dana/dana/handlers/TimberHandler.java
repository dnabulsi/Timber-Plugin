package dana.dana.handlers;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import dana.dana.Dana;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.HashSet;


public class TimberHandler implements Listener {
    public TimberHandler(Dana plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    Queue<Block> blocksToSearchAround = new LinkedList<Block>();
    Set<Block> blocksFound = new HashSet<Block>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        if (block.getType().toString().contains("LOG")) {
            // make a queue and add the block to it
            blocksToSearchAround.add(block);
            }

        // hasNext() returns true if the queue has more elements
        while(!blocksToSearchAround.isEmpty()) {
            // remove the head of the queue
            Block b = blocksToSearchAround.element();
            
            // if b is already in blocksFound, remove it from the queue and continue
            if (blocksFound.contains(b)) {
                blocksToSearchAround.poll();
                continue;
            }

            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Block relativeBlock = b.getRelative(x, y, z);
                        if (relativeBlock.getType().toString().contains("LOG") && !blocksFound.contains(relativeBlock)) {
                            blocksToSearchAround.add(relativeBlock);
                        }
                    }
                }
            }

            blocksToSearchAround.poll();
            blocksFound.add(b);
            b.breakNaturally();
        }
    }
}
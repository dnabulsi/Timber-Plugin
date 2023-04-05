package teammt.mtpolls.containers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import masecla.mlib.classes.builders.ItemBuilder;
import masecla.mlib.containers.instances.SquaredPagedContainer;
import masecla.mlib.main.MLib;
import teammt.mtpolls.polls.Poll;
import teammt.mtpolls.polls.PollManager;

public class ActivePollsContainer extends SquaredPagedContainer {

    private PollManager pollManager;
    private ViewablePollContainer viewablePollContainer;

    public ActivePollsContainer(MLib lib, PollManager pollManager, ViewablePollContainer viewablePollContainer) {
        super(lib);
        this.pollManager = pollManager;
        this.viewablePollContainer = viewablePollContainer;
    }

    @Override
    public void usableClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null)
            return;
        Player player = (Player) event.getWhoClicked();
        String tag = lib.getNmsAPI().read(event.getCurrentItem()).getString("poll").getValue();
        if (tag == null)
            return;
        Poll poll = pollManager.getPoll(tag);
        this.viewablePollContainer.setPoll(poll);
        lib.getContainerAPI().openFor(player, ViewablePollContainer.class);
    }

    @Override
    public List<ItemStack> getOrderableItems(Player player) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        for (Poll poll : pollManager.getPolls()) {
            result.add(buildItemForPoll(player, poll));
        }
        return result;
    }

    private ItemStack buildItemForPoll(Player player, Poll poll) {
        ItemBuilder result = new ItemBuilder().skull(Bukkit.getPlayer(poll.getCreatedBy())).name(poll.getName());
        if (poll.getPlayersVoted() != null) {
            if (poll.getPlayersVoted().contains(player)) {
                result.enchant(Enchantment.FIRE_ASPECT, 1)
                        .hideAll();
            }
        }
        return result.tagString("poll", poll.getPollId().toString()).build(lib);
    }

    @Override
    public int getSize(Player player) {
        return 45;
    }

    @Override
    public int getUpdatingInterval() {
        return 10;
    }

}

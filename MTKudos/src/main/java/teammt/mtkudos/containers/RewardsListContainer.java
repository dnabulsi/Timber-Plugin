package teammt.mtkudos.containers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import masecla.mlib.classes.Replaceable;
import masecla.mlib.classes.builders.ItemBuilder;
import masecla.mlib.containers.instances.SquaredPagedContainer;
import masecla.mlib.main.MLib;
import teammt.mtkudos.kudos.KudoManager;
import teammt.mtkudos.rewards.Reward;
import teammt.mtkudos.rewards.RewardManager;

public class RewardsListContainer extends SquaredPagedContainer {

    private RewardManager rewardManager;
    private KudoManager kudoManager;

    public RewardsListContainer(MLib lib, RewardManager rewardManager, KudoManager kudoManager) {
        super(lib);
        this.rewardManager = rewardManager;
        this.kudoManager = kudoManager;
    }

    @Override
    public void usableClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getCurrentItem() == null)
            return;

        Player player = (Player) event.getWhoClicked();

        String tag = lib.getNmsAPI().read(event.getCurrentItem()).getString("reward").getValue();
        if (tag == null)
            return;

        rewardManager.giveReward(player, tag);
    }

    @Override
    public List<ItemStack> getOrderableItems(Player player) {
        List<ItemStack> result = new ArrayList<ItemStack>();
        for (Reward reward : rewardManager.getRewards()) {
            result.add(buildItemForReward(player, reward));
        }
        return result;
    }

    private ItemStack buildItemForReward(Player player, Reward reward) {
        Material iconType = reward.getIconType();
        ItemBuilder result = new ItemBuilder(iconType).name(reward.getIconName()).lore(reward.getIconDescription());

        Replaceable needed = new Replaceable("%needed%", reward.getTarget());
        Replaceable has = new Replaceable("%has%", kudoManager.getKudoData(player.getUniqueId()).getKudosReceived());

        int neededInt = reward.getTarget() - kudoManager.getKudoData(player.getUniqueId()).getKudosReceived();
        boolean claimed = rewardManager.hasReceived(player, reward.getId());
        Replaceable func = new Replaceable("%func%", claimed ? "Claimed" : neededInt > 0 ? neededInt : "Claim");

        result.replaceable(needed, has, func);

        if (rewardManager.hasReceived(player, reward.getId())) {
            result.enchant(Enchantment.FIRE_ASPECT, 1)
                    .hideAll();
        }

        return result.tagString("reward", reward.getId()).build(lib);
    }

    @Override
    public int getSize(Player player) {
        return 45;
    }

    @Override
    public int getUpdatingInterval() {
        return 10;
    }

    @Override
    public String getTitle(Player player) {
        return lib.getMessagesAPI().getPluginMessage("reward-list-title", player);
    }

}

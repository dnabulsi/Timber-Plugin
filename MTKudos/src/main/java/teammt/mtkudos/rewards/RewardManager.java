package teammt.mtkudos.rewards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import masecla.mlib.classes.Registerable;
import masecla.mlib.classes.Replaceable;
import masecla.mlib.main.MLib;
import teammt.mtkudos.kudos.KudoData;
import teammt.mtkudos.kudos.KudoManager;

public class RewardManager extends Registerable {

    private KudoManager kudoManager;

    public RewardManager(MLib lib, KudoManager kudoManager) {
        super(lib);
        this.kudoManager = kudoManager;
    }

    public Set<String> getRewardIDs() {
        return lib.getConfigurationAPI().getConfig().getConfigurationSection("rewards").getKeys(false);
    }

    public Reward getReward(String reward) {
        ConfigurationSection section = lib.getConfigurationAPI().getConfig()
                .getConfigurationSection("rewards." + reward);
        Reward rewardInstance = new Reward(section, reward);
        return rewardInstance;
    }

    public boolean giveReward(Player p, String reward) {
        KudoData data = kudoManager.getKudoData(p.getUniqueId());
        if (data.getRewardsClaimed().contains(reward)) {
            lib.getMessagesAPI().sendMessage("reward-already-claimed", p);
            return false;
        }
        if (data.getKudosReceived() < this.getReward(reward).getTarget()) {
            lib.getMessagesAPI().sendMessage("not-enough-kudos", p,
                    new Replaceable("%kudos%", this.getReward(reward).getTarget() - data.getKudosReceived()));
            return false;
        }

        Reward rewardInstance = this.getReward(reward);
        rewardInstance.give(p);

        data.getRewardsClaimed().add(reward);
        if (rewardInstance.isBroadcast())
            Bukkit.broadcastMessage(lib.getMessagesAPI().getPluginMessage("announce-message", p,
                    new Replaceable("%player%", p.getName()),
                    new Replaceable("%reward%", rewardInstance.getIconName())));
        data.save(lib);
        return true;
    }

    public boolean hasReceived(Player p, String reward) {
        KudoData data = kudoManager.getKudoData(p.getUniqueId());
        return data.getRewardsClaimed().contains(reward);
    }

    public List<Reward> getRewards() {
        List<Reward> rewardsList = new ArrayList<>(
                this.getRewardIDs().stream().map(this::getReward).collect(Collectors.toSet()));
        Collections.sort(rewardsList, (r1, r2) -> r1.getTarget() - r2.getTarget());
        return rewardsList;
    }

}
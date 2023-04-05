package teammt.mtpolls.polls;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import masecla.mlib.classes.Registerable;
import masecla.mlib.classes.Replaceable;
import masecla.mlib.main.MLib;

public class PollManager extends Registerable {

    public PollManager(MLib lib) {
        super(lib);
    }

    public void save(Poll poll) {
        lib.getConfigurationAPI().getConfig("data").set("data." + poll.getPollId(), this);
    }

    public void vote(Player player, Poll poll, int index) {
        if (poll.getPlayersVoted().contains(player)) {
            lib.getMessagesAPI().sendMessage("already-voted", player);
            return;
        }
        poll.getPlayersVoted().add(player);
        poll.getOptions().get(index).incrementVotes();
        lib.getMessagesAPI().sendMessage("poll-successfully-voted", player, new Replaceable("%option%", poll.getOptions().get(index).getOption()), new Replaceable("%name%", poll.getName()));
    }

    public List<Poll> getPolls() {
        List<Poll> polls = new ArrayList<>();
        for (Object cr : lib.getConfigurationAPI().getConfig("data").getConfigurationSection("data").getKeys(false)) {
            Poll poll = (Poll) lib.getConfigurationAPI().getConfig("data").get("data." + cr);
            if (poll.isOpen())
                polls.add(poll);
        }
        return polls;
    }

    public Poll getPoll(String id) {
        return (Poll) lib.getConfigurationAPI().getConfig("data").get("data." + id);
    }

}

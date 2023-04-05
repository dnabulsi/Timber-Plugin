package teammt.mtpolls.polls;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import masecla.mlib.main.MLib;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Poll {
    private UUID pollId;
    private UUID createdBy;
    private String name;
    private String question;
    private int duration;
    private List<PollOption> options = new ArrayList<>();
    private long createdAt;
    private boolean open;
    private List<Player> playersVoted;

    public Poll(UUID createdBy) {
        this.pollId = UUID.randomUUID();
        this.createdBy = createdBy;
        this.open = true;
    }

    public void save(MLib lib, Poll poll) {
        lib.getConfigurationAPI().getConfig("data").set("data." + poll.getPollId(), this);
    }
}

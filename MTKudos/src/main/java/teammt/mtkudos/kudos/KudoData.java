package teammt.mtkudos.kudos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import masecla.mlib.main.MLib;

@Data
@NoArgsConstructor
public class KudoData {
    private UUID player;
    private long lastGiven = 0;
    private int kudosGiven = 0;
    private int kudosReceived = 0;

    private Set<String> rewardsClaimed = new HashSet<>();

    public KudoData(UUID player) {
        this.player = player;
    }

    public void incrementKudosGiven() {
        kudosGiven++;
    }

    public void incrementKudosReceived() {
        kudosReceived++;
    }

    public void save(MLib lib) {
        lib.getConfigurationAPI().getConfig("data").set("data." + player, this);
    }
}
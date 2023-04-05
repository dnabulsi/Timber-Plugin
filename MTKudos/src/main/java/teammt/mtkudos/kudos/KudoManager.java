package teammt.mtkudos.kudos;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import masecla.mlib.classes.Registerable;
import masecla.mlib.main.MLib;

public class KudoManager extends Registerable {

    public KudoManager(MLib lib) {
        super(lib);
    }

    public KudoData getKudoData(UUID player) {
        return (KudoData) lib.getConfigurationAPI().getConfig("data").get("data." + player, new KudoData(player));
    }

    public void addKudo(UUID giver, UUID receiver) {
        KudoData giverData = getKudoData(giver);
        giverData.setLastGiven(Instant.now().getEpochSecond());
        giverData.incrementKudosGiven();
        giverData.save(lib);

        KudoData receiverData = getKudoData(receiver);
        receiverData.incrementKudosReceived();
        receiverData.save(lib);
    }

    public Set<UUID> getPlayersFromConfig() {
        if (lib.getConfigurationAPI().getConfig("data").getConfigurationSection("data") == null)
            return null;
        Set<String> playerIdsSet = lib.getConfigurationAPI().getConfig("data").getConfigurationSection("data")
                .getKeys(false);

        Set<UUID> playerIdsList = new HashSet<>();
        for (String cr : playerIdsSet) {
            try {
                playerIdsList.add(UUID.fromString(cr));
            } catch (Exception e) {
            }
        }

        return playerIdsList;
    }
}

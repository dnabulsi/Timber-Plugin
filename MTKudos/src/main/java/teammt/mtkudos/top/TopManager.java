package teammt.mtkudos.top;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import masecla.mlib.classes.Registerable;
import masecla.mlib.main.MLib;
import teammt.mtkudos.kudos.KudoData;
import teammt.mtkudos.kudos.KudoManager;

public class TopManager extends Registerable {

    private KudoManager kudoManager;

    public TopManager(MLib lib, KudoManager kudoManager) {
        super(lib);
        this.kudoManager = kudoManager;
    }

    public List<KudoData> getTop() {
        if (kudoManager.getPlayersFromConfig() == null)
            return null;
        return kudoManager.getPlayersFromConfig().stream()
                .map(c -> kudoManager.getKudoData(c))
                .sorted(Comparator.comparing(c -> c.getKudosGiven()))
                .limit(15)
                .collect(Collectors.toList());
    }

}

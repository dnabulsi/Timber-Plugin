package teammt.mtpolls.polls;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import masecla.mlib.main.MLib;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PollOption {

    MLib mlib;
    private transient Poll poll;
    private String option;
    private int votes;

    public PollOption(String option, Poll poll) {
        this.poll = poll;
        this.option = option;
        this.votes = 0;
    }

    public void incrementVotes() {
        this.votes++;
        poll.save(mlib, poll);
    }
}

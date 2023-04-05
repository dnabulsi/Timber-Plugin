package teammt.mtpolls.main;

import org.bukkit.plugin.java.JavaPlugin;

import masecla.mlib.main.MLib;
import teammt.mtpolls.commands.PollsCommand;
import teammt.mtpolls.containers.ActivePollsContainer;
import teammt.mtpolls.containers.PollCreationContainer;
import teammt.mtpolls.containers.ViewablePollContainer;
import teammt.mtpolls.polls.PollManager;

public class MTPolls extends JavaPlugin {
    private MLib lib;
    private PollManager pollManager;
    private PollCreationContainer pollCreationContainer;
    private ActivePollsContainer activePollsContainer;
    private ViewablePollContainer viewablePollContainer;

    @Override
    public void onEnable() {
        this.lib = new MLib(this);
        lib.getConfigurationAPI().requireAll();

        // Managers
        pollManager = new PollManager(lib);
        pollManager.register();
        pollCreationContainer = new PollCreationContainer(lib);
        pollCreationContainer.register();
        viewablePollContainer = new ViewablePollContainer(lib, pollManager);
        viewablePollContainer.register();
        activePollsContainer = new ActivePollsContainer(lib, pollManager, viewablePollContainer);
        activePollsContainer.register();

        // Commands
        new PollsCommand(lib, pollCreationContainer).register();

    }

}
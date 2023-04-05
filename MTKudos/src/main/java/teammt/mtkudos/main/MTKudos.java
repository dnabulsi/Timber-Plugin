package teammt.mtkudos.main;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import masecla.mlib.main.MLib;
import net.milkbowl.vault.economy.Economy;
import teammt.mtkudos.commands.KudosCommand;
import teammt.mtkudos.containers.ConfirmationDialogueContainer;
import teammt.mtkudos.containers.RewardsListContainer;
import teammt.mtkudos.containers.TopPlayersContainer;
import teammt.mtkudos.kudos.KudoManager;
import teammt.mtkudos.rewards.RewardManager;
import teammt.mtkudos.top.TopManager;

public class MTKudos extends JavaPlugin {

	private KudoManager kudoManager;
	private RewardManager rewardManager;
	private TopManager topManager;
	private Economy economy;

	private MLib lib;

	@Override
	public void onEnable() {
		this.lib = new MLib(this);
		lib.getConfigurationAPI().requireAll();

		if (lib.getConfigurationAPI().getConfig().getInt("kudo-cost") > 0) {
			setupEconomy();
		}

		// Managers
		this.kudoManager = new KudoManager(lib);
		this.kudoManager.register();
		this.rewardManager = new RewardManager(lib, kudoManager);
		this.rewardManager.register();
		this.topManager = new TopManager(lib, kudoManager);
		this.topManager.register();
		new RewardsListContainer(lib, rewardManager, kudoManager).register();

		ConfirmationDialogueContainer dialogueContainer = new ConfirmationDialogueContainer(lib, kudoManager, economy);
		dialogueContainer.register();
		TopPlayersContainer playersContainer = new TopPlayersContainer(lib, topManager);
		playersContainer.register();

		// Commands
		new KudosCommand(lib, kudoManager, dialogueContainer).register();
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		economy = rsp.getProvider();
		return economy != null;
	}
}
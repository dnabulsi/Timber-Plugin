package teammt.mtkudos.commands;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import masecla.mlib.annotations.RegisterableInfo;
import masecla.mlib.annotations.SubcommandInfo;
import masecla.mlib.classes.Registerable;
import masecla.mlib.classes.Replaceable;
import masecla.mlib.main.MLib;
import teammt.mtkudos.containers.ConfirmationDialogueContainer;
import teammt.mtkudos.containers.RewardsListContainer;
import teammt.mtkudos.containers.TopPlayersContainer;
import teammt.mtkudos.kudos.KudoData;
import teammt.mtkudos.kudos.KudoManager;

@RegisterableInfo(command = "kudos")
public class KudosCommand extends Registerable {

	private KudoManager kudoManager;

	private ConfirmationDialogueContainer confirmationDialogue;

	public KudosCommand(MLib lib, KudoManager kudoManager, ConfirmationDialogueContainer confirmationDialogue) {
		super(lib);
		this.kudoManager = kudoManager;
		this.confirmationDialogue = confirmationDialogue;
	}

	/**
	 * Help menu.
	 */
	@SubcommandInfo(permission = "teammt.mtkudos.commands.help")
	public void handleHelpNoArgs(CommandSender sender) {
		lib.getMessagesAPI().sendMessage("help-menu", sender);
	}

	@SubcommandInfo(subcommand = "help", permission = "teammt.mtkudos.commands.help")
	public void handleHelpArgs(CommandSender sender) {
		lib.getMessagesAPI().sendMessage("help-menu", sender);
	}

	/**
	 * Check kudos count.
	 */
	@SubcommandInfo(subcommand = "check", permission = "teammt.mtkudos.commands.check")
	public void handleView(Player sender, String playerName) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) {
			lib.getMessagesAPI().sendMessage("player-not-online", sender);
			return;
		}

		KudoData playerKudoData = kudoManager.getKudoData(player.getUniqueId());
		lib.getMessagesAPI().sendMessage("player-kudo-count", sender, new Replaceable("%player%", player.getName()),
				new Replaceable("%received_kudos%", playerKudoData.getKudosReceived()),
				new Replaceable("%given_kudos%", playerKudoData.getKudosGiven()));
	}

	/**
	 * Reload configurations currently doing the config reload
	 */
	@SubcommandInfo(subcommand = "reload", permission = "teammt.mtkudos.commands.reload")
	public void onReload(Player sender) {
		try {
			lib.getConfigurationAPI().reloadAll();
		} catch (IOException e) {
		}
		lib.getMessagesAPI().reloadSharedConfig();
		lib.getMessagesAPI().sendMessage("plugin-reloaded", sender);
	}

	/**
	 * Display rewards GUI
	 */
	@SubcommandInfo(subcommand = "rewards", permission = "teammt.mtkudos.commands.rewards")
	public void handleRewards(Player sender) {
		lib.getContainerAPI().openFor(sender, RewardsListContainer.class);
	}

	/**
	 * Display top 10 players
	 */
	@SubcommandInfo(subcommand = "top", permission = "teammt.mtkudos.commands.top")
	public void handleTop(Player sender) {
		lib.getContainerAPI().openFor(sender, TopPlayersContainer.class);
	}

	/**
	 * Give a player kudos.
	 */
	@SubcommandInfo(subcommand = "give", permission = "teammt.mtkudos.commands.give")
	public void handleGive(Player sender, String playerName) {
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) {
			lib.getMessagesAPI().sendMessage("player-not-online", sender);
			return;
		}

		if (player.equals(sender)) {
			lib.getMessagesAPI().sendMessage("player-gave-themselves", sender);
			return;
		}
		this.confirmationDialogue.setGivingKudos(sender.getUniqueId(), player.getUniqueId());
		lib.getContainerAPI().openFor(sender, ConfirmationDialogueContainer.class);
	}

}
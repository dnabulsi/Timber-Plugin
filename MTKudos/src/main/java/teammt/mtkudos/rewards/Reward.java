package teammt.mtkudos.rewards;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import lombok.Getter;

@Getter
public class Reward {
    private String id;
    private int target;
    private String announceMessage;
    private boolean broadcast;

    private Material iconType;
    private String iconName;
    private List<String> iconDescription;

    private List<String> consoleCommands;

    public Reward(ConfigurationSection section, String id) {
        this.id = id;
        this.target = section.getInt("target", 0);
        this.announceMessage = section.getString("announce-message");
        this.broadcast = section.getBoolean("broadcast");

        this.iconType = Material.matchMaterial(section.getString("icon.type"));
        this.iconName = section.getString("icon.name");
        this.iconDescription = section.getStringList("icon.description");

        this.consoleCommands = section.getStringList("commands.by-console");
    }

    public void give(Player player) {

        CommandSender sender = Bukkit.getConsoleSender();
        List<String> consoleCommands = this.getConsoleCommands();
        for (String currentCommand : consoleCommands) {
            currentCommand = currentCommand.replace("%player%", player.getName());
            Bukkit.dispatchCommand(sender, currentCommand);
        }
    }

}
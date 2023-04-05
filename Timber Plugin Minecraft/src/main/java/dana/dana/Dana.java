package dana.dana;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import dana.dana.handlers.TimberHandler;

public final class Dana extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("Hello World! v1.3");

        new TimberHandler(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("Shutting Down");
    }
}

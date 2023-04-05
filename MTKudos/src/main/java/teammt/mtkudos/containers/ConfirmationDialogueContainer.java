package teammt.mtkudos.containers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import masecla.mlib.classes.Replaceable;
import masecla.mlib.classes.builders.InventoryBuilder;
import masecla.mlib.classes.builders.ItemBuilder;
import masecla.mlib.containers.generic.ImmutableContainer;
import masecla.mlib.main.MLib;
import net.milkbowl.vault.economy.Economy;
import teammt.mtkudos.kudos.KudoManager;

public class ConfirmationDialogueContainer extends ImmutableContainer {

    private Map<UUID, UUID> givingKudos = new HashMap<>();

    private KudoManager kudoManager;
    private Economy economy;

    public ConfirmationDialogueContainer(MLib lib, KudoManager kudoManager, Economy economy) {
        super(lib);
        this.kudoManager = kudoManager;
        this.economy = economy;
    }

    @Override
    public void onTopClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getSlot() == 24) {
            event.getWhoClicked().closeInventory();
        } else if (event.getSlot() == 20) {
            event.getWhoClicked().closeInventory();

            UUID giver = event.getWhoClicked().getUniqueId();
            UUID receiver = this.givingKudos.get(giver);

            if (receiver == null) {
                event.getWhoClicked().closeInventory();
                return;
            }

            Player givPlayer = Bukkit.getPlayer(giver);

            int cooldown = lib.getConfigurationAPI().getConfig().getInt("kudo-cooldown");
            long giverLastGiven = kudoManager.getKudoData(giver).getLastGiven();
            if (giverLastGiven > Instant.now().getEpochSecond() - cooldown) {
                lib.getMessagesAPI().sendMessage("cooldown", givPlayer,
                        new Replaceable("%time%", (giverLastGiven + cooldown - Instant.now().getEpochSecond()) / 60));
                return;
            }

            if (economy != null) {
                double giverFunds = economy.getBalance(givPlayer);
                double kudoCost = lib.getConfigurationAPI().getConfig().getDouble("kudo-cost");
                if (giverFunds < kudoCost) {
                    lib.getMessagesAPI().sendMessage("not-enough-currency", givPlayer,
                            new Replaceable("%needed-funds%", kudoCost - giverFunds));
                    return;
                }
                economy.withdrawPlayer(givPlayer, kudoCost);
            }

            kudoManager.addKudo(giver, receiver);

            OfflinePlayer receiverInstance = Bukkit.getOfflinePlayer(receiver);
            lib.getMessagesAPI().sendMessage("player-given-kudo", event.getWhoClicked(),
                    new Replaceable("%player%", receiverInstance.getName()));

            if (receiverInstance.isOnline())
                lib.getMessagesAPI().sendMessage("player-received-kudo", (Player) receiverInstance,
                        new Replaceable("%player%", event.getWhoClicked().getName()));
        }
    }

    @Override
    public int getSize(Player player) {
        return 45;
    }

    @Override
    public int getUpdatingInterval() {
        return 10;
    }

    @Override
    public boolean requiresUpdating() {
        return true;
    }

    @Override
    public Inventory getInventory(Player player) {
        UUID receiverUUID = givingKudos.get(player.getUniqueId());
        Player receiver = Bukkit.getPlayer(receiverUUID);

        return new InventoryBuilder()
                .size(getSize(player))
                .messages()
                .title("confirmation-dialogue-title")
                .border(getConfirmationDialogueBorder())
                .setItem(4, getConfirmationDialogueSkull(receiver))
                .setItem(20, getConfirmationDialogueConfirm(receiver))
                .setItem(24, getConfirmationDialogueDeny(receiver))
                .setItem(40, getConfirmationDialogueClose())
                .build(lib, player);

    }

    private ItemStack getConfirmationDialogueConfirm(Player receiver) {
        ItemBuilder sack = null;
        if (lib.getCompatibilityApi().getServerVersion().getMajor() <= 12)
            sack = new ItemBuilder(Material.matchMaterial("INK_SACK"))
                    .data((byte) 1);
        else
            sack = new ItemBuilder(Material.matchMaterial("LIME_DYE"));

        return sack.mnl("confirmation-dialogue-confirm")
                .replaceable("%player%", receiver.getName()).build(lib);
    }

    private ItemStack getConfirmationDialogueDeny(Player receiver) {
        ItemBuilder sack = null;
        if (lib.getCompatibilityApi().getServerVersion().getMajor() <= 12)
            sack = new ItemBuilder(Material.matchMaterial("INK_SACK"))
                    .data((byte) 1);
        else
            sack = new ItemBuilder(Material.matchMaterial("RED_DYE"));

        return sack.mnl("confirmation-dialogue-deny")
                .replaceable("%player%", receiver.getName()).build(lib);
    }

    private ItemStack getConfirmationDialogueBorder() {
        Material pane = null;
        if (lib.getCompatibilityApi().getServerVersion().getMajor() <= 12)
            pane = Material.matchMaterial("STAINED_GLASS_PANE");
        else
            pane = Material.matchMaterial("BLACK_STAINED_GLASS_PANE");
        ItemBuilder paneItem = new ItemBuilder(pane);
        if (lib.getCompatibilityApi().getServerVersion().getMajor() <= 12)
            paneItem = paneItem.data((byte) 15);
        return paneItem.name(" ").build(lib);
    }

    private ItemStack getConfirmationDialogueClose() {
        return new ItemBuilder(Material.BARRIER)
                .mnl("confirmation-dialogue-close")
                .build(lib);
    }

    private ItemStack getConfirmationDialogueSkull(Player receiver) {

        return new ItemBuilder().skull(receiver)
                .mnl("confirmation-dialogue-skull")
                .replaceable("%player%", receiver.getName())
                .build(lib);
    }

    public void setGivingKudos(UUID giver, UUID receiver) {
        this.givingKudos.put(giver, receiver);
    }

}

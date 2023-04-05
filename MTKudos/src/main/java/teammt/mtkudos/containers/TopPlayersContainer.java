package teammt.mtkudos.containers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import masecla.mlib.classes.builders.InventoryBuilder;
import masecla.mlib.classes.builders.ItemBuilder;
import masecla.mlib.containers.generic.ImmutableContainer;
import masecla.mlib.main.MLib;
import teammt.mtkudos.kudos.KudoData;
import teammt.mtkudos.top.TopManager;

public class TopPlayersContainer extends ImmutableContainer {

    private TopManager topManager;

    public TopPlayersContainer(MLib lib, TopManager topManager) {
        super(lib);
        this.topManager = topManager;
    }

    @Override
    public void onTopClick(InventoryClickEvent event) {
        event.setCancelled(true);

        if (event.getSlot() == 49) {
            event.getWhoClicked().closeInventory();
        }
    }

    @Override
    public int getSize(Player player) {
        return 54;
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
        List<KudoData> kudoDataList = topManager.getTop();
        int[] availableSlots = { 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33 };
        InventoryBuilder myInventory = new InventoryBuilder()
                .size(getSize(player))
                .messages()
                .title("top-dialogue-title");
        if (kudoDataList != null) {
            for (int i = 0; i < kudoDataList.size(); i++)
                myInventory.setItem(availableSlots[i], getConfirmationDialogueSkull(kudoDataList.get(i)));
        }

        return myInventory.border(getConfirmationDialogueBorder())
                .setItem(49, getConfirmationDialogueClose())
                .setItem(22, new ItemBuilder().skull(
                        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmViNTg4YjIxYTZmOThhZDFmZjRlMDg1YzU1MmRjYjA1MGVmYzljYWI0MjdmNDYwNDhmMThmYzgwMzQ3NWY3In19fQ==")
                        .mnl("top-dialogue-barrier").build(lib))
                .build(lib, player);
    }

    private ItemStack getConfirmationDialogueSkull(KudoData kudoData) {
        Player player = Bukkit.getPlayer(kudoData.getPlayer());
        return new ItemBuilder().skull(player)
                .mnl("top-dialogue-text")
                .replaceable("%player%", player.getName())
                .replaceable("%kudos%", kudoData.getKudosReceived()).build(lib);

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

}

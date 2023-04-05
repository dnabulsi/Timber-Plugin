package teammt.mtpolls.containers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import masecla.mlib.classes.builders.InventoryBuilder;
import masecla.mlib.classes.builders.ItemBuilder;
import masecla.mlib.containers.generic.ImmutableContainer;
import masecla.mlib.main.MLib;
import teammt.mtpolls.polls.Poll;
import teammt.mtpolls.polls.PollManager;
import teammt.mtpolls.polls.PollOption;

public class ViewablePollContainer extends ImmutableContainer {

    private Poll poll;
    private PollManager pollManager;

    public ViewablePollContainer(MLib lib, PollManager pollManager) {
        super(lib);
        this.pollManager = pollManager;
    }

    @Override
    public void onTopClick(InventoryClickEvent event) {
        if (event.getSlot() == 40) {
            event.getWhoClicked().closeInventory();
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR)
            return;
        if (lib.getNmsAPI().read(event.getCurrentItem()).getString("MTPolls_option").getValue().startsWith("option")) {
            event.setCancelled(true);
            String tag = lib.getNmsAPI().read(event.getCurrentItem()).getString("MTPolls_option").getValue();

            if (tag == null || tag.isEmpty())
                return;

            Player player = (Player) event.getWhoClicked();
            int index = Integer.parseInt(tag.substring(6));
            pollManager.vote(player, poll, index);
        }

    }

    @Override
    public Inventory getInventory(Player player) {
        Map<Integer, String> skullsMap = new HashMap<>();
        skullsMap.put(0,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjBlNDIwZjJhZWU0ZTBmOGZiNDA4ZDVlNjM0MjY0ZTQ0OWE5NjlhZjllMjQyOGUyZTRhYTE3ODNkZmNmNjg2YSJ9fX0=");
        skullsMap.put(1,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWU3MTQwNTY2NWRhZDYwZWMxMjk2YmQ5NmViNmZjZjIxYWY4MWRhMTAyNTM4MDk1OTMwN2RhZTk5ZmY5OGNkIn19fQ==");
        skullsMap.put(2,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmI0YjYyYTgwNmUyODNlZTBlMThlZGE4NmYyZTg2OWMwNDA1M2M3ODc3YmE1ZTA4OWFhNjNkMmNjZDllOGJmYSJ9fX0=");
        skullsMap.put(3,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBiNzQ2OWNlYmFmNzQ4ZGExNTcxZDYzOGE5YTRhMjgzNjY3OWRkMzFhMzNlMzUzZWQ1MDc3YTEyNTA4YzNlZSJ9fX0=");
        skullsMap.put(4,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzg5YmFhMDQyNTk5ZDA3OTI0MTcyZDg5N2FjNDE1MTg2ZDllNWYyNTQ2ODE1MjEwMjAxZGY3YmI2MzJkYTFjNCJ9fX0=");
        skullsMap.put(5,
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTlmY2FmOGMyYTE0ZTdkNGU4NDBiMDc5MGE5NTA5MWE2YzA2YTQ3NWQ2Yzk4ZTNlYjBmYmJmNzYzNDIzZWE2ZCJ9fX0=");

        Map<Integer, int[]> locationsMap = new HashMap<>();
        locationsMap.put(2, new int[] { 21, 23 });
        locationsMap.put(3, new int[] { 29, 31, 33 });
        locationsMap.put(4, new int[] { 28, 30, 32, 34 });
        locationsMap.put(5, new int[] { 20, 22, 24, 30, 32 });
        locationsMap.put(6, new int[] { 19, 21, 23, 25, 30, 32 });

        InventoryBuilder myInventory = new InventoryBuilder().title("'" + poll.getName() + "' Poll")
                .size(getSize(player))
                .border(getConfirmationDialogueBorder())
                .setItem(13, getConfirmationDialogueQuestion(poll.getQuestion()))
                .setItem(40, getConfirmationDialogueClose());
        int i = 0;
        for (PollOption option : poll.getOptions()) {
            myInventory.setItem(locationsMap.get(poll.getOptions().size())[i],
                    new ItemBuilder().skull(skullsMap.get(i)).mnl("viewable-poll-container-option")
                            .tagString("MTPolls_option", "option" + i)
                            .replaceable("%option%", option.getOption())
                            .replaceable("%letter%", String.valueOf((char) (i + 65)))
                            .replaceable("%votes%", option.getVotes()).build(lib));
            i++;
        }
        return myInventory.build(lib, player);
    }

    private ItemStack getConfirmationDialogueQuestion(String question) {
        return new ItemBuilder().skull(
                "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGEyZmRlMzRkMzRjODU4OGU1OGJmZDc5MGNlMTgwMjVmNzg0MzM5OWRlZTJhYjRjZWRjMmMwYjQ2M2ZkMWUifX19")
                .mnl("viewable-poll-container-question").replaceable("%question%", question).build(lib);
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
        return paneItem.build(lib);
    }

    private ItemStack getConfirmationDialogueClose() {
        return new ItemBuilder(Material.BARRIER)
                .mnl("viewable-poll-container-close")
                .build(lib);
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

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

}

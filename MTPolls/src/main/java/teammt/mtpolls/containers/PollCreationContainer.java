package teammt.mtpolls.containers;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import masecla.mlib.classes.Replaceable;
import masecla.mlib.classes.builders.InventoryBuilder;
import masecla.mlib.classes.builders.ItemBuilder;
import masecla.mlib.containers.generic.ImmutableContainer;
import masecla.mlib.main.MLib;
import teammt.mtpolls.polls.Poll;
import teammt.mtpolls.polls.PollOption;

public class PollCreationContainer extends ImmutableContainer {

    private Map<UUID, Poll> currentlyCreating = new HashMap<>();
    private Map<UUID, String> currentlyEditing = new HashMap<>();

    public PollCreationContainer(MLib lib) {
        super(lib);
    }

    @Override
    @EventHandler
    public void onTopClick(InventoryClickEvent event) {
        event.setCancelled(true);

        UUID uniqueId = event.getWhoClicked().getUniqueId();
        Player player = Bukkit.getPlayer(uniqueId);
        if (event.getSlot() == 40) {
            event.getWhoClicked().closeInventory();
            lib.getMessagesAPI().sendMessage("stopped-creating-poll", event.getWhoClicked());
            currentlyCreating.remove(uniqueId);
            return;
        }

        else if (event.getSlot() == 20) {
            Poll poll = currentlyCreating.get(uniqueId);
            if (poll.getName() != null && poll.getQuestion() != null && poll.getDuration() != 0
                    && poll.getOptions().size() > 1) {
                poll.setCreatedAt(Instant.now().getEpochSecond());
                poll.save(lib, poll);
                event.getWhoClicked().closeInventory();
                lib.getMessagesAPI().sendMessage("poll-successfully-created", player,
                        new Replaceable("%pollname%", poll.getName()));
            } else {
                String unfinished = "";
                if (poll.getName() == null)
                    unfinished += ("name ");
                if (poll.getQuestion() == null)
                    unfinished += ("question ");
                if (poll.getOptions().size() < 2)
                    unfinished += ("options ");
                if (poll.getDuration() == 0)
                    unfinished += ("duration");
                lib.getMessagesAPI().sendMessage("unfinished-poll", player,
                        new Replaceable("%unfinished%", unfinished));
            }
        }

        if (event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR))
            return;

        String tag = lib.getNmsAPI().read(event.getCurrentItem()).getString("MTPolls_option").getValue();

        if (tag == null || tag.isEmpty())
            return;
        event.getWhoClicked().closeInventory();
        currentlyEditing.put(uniqueId, tag);
        if (tag.startsWith("option")) {
            if (event.getClick().isRightClick()) {
                int optionNumber = Integer.parseInt(tag.substring(6));
                currentlyCreating.get(uniqueId).getOptions().remove(optionNumber);
                lib.getContainerAPI().openFor(player,
                        PollCreationContainer.class);
            }
            lib.getMessagesAPI().sendMessage("enter-option", event.getWhoClicked());
        } else
            lib.getMessagesAPI().sendMessage("enter-" + tag, event.getWhoClicked());

    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        UUID uniqueId = event.getPlayer().getUniqueId();
        Player player = Bukkit.getPlayer(uniqueId);
        if (!this.currentlyCreating.containsKey(uniqueId)
                || !this.currentlyEditing.containsKey(uniqueId))
            return;
        event.setCancelled(true);
        Poll poll = this.currentlyCreating.get(uniqueId);
        String editing = this.currentlyEditing.get(uniqueId);
        if (editing.equals("question")) {
            if (event.getMessage().length() > 50) {
                lib.getMessagesAPI().sendMessage("long-question", player,
                        new Replaceable("%question%", event.getMessage()));
            } else {
                poll.setQuestion(event.getMessage());
                lib.getMessagesAPI().sendMessage("question-added", player,
                        new Replaceable("%question%", event.getMessage()));
            }
        } else if (editing.equals("name")) {
            if (event.getMessage().length() > 20) {
                lib.getMessagesAPI().sendMessage("long-name", player,
                        new Replaceable("%name%", event.getMessage()));
            } else {
                poll.setName(event.getMessage());
                lib.getMessagesAPI().sendMessage("name-added", player,
                        new Replaceable("%name%", event.getMessage()));
            }
        } else if (editing.equals("duration")) {
            poll.setDuration(lib.getTimesAPI().timeToSeconds(event.getMessage()));
            lib.getMessagesAPI().sendMessage("duration-added", player,
                    new Replaceable("%duration%", event.getMessage()));
        } else if (editing.startsWith("option")) {
            int optionNumber = Integer.parseInt(editing.substring(6));
            if (optionNumber >= poll.getOptions().size())
                poll.getOptions().add(new PollOption());
            if (event.getMessage().length() > 50) {
                lib.getMessagesAPI().sendMessage("long-option", player,
                        new Replaceable("%option%", event.getMessage()));
            } else {
                poll.getOptions().get(optionNumber).setOption(event.getMessage());
            }
            lib.getMessagesAPI().sendMessage("option-added", player,
                    new Replaceable("%option%", event.getMessage()));
        }
        currentlyEditing.remove(uniqueId);
        lib.getContainerAPI().openFor(event.getPlayer(), PollCreationContainer.class);
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
        Poll poll = this.currentlyCreating.get(player.getUniqueId());
        int[] availableSlots = { 22, 23, 24, 25, 32, 33 };
        InventoryBuilder myInventory = new InventoryBuilder().messages()
                .title("poll-creator-gui-title")
                .size(getSize(player))
                .border(getConfirmationDialogueBorder())
                .setItem(20, getConfirmationDialogueConfirm(player))
                .setItem(40, getConfirmationDialogueClose())
                .setItem(13, getConfirmationDialogueName(poll.getName()))
                .setItem(14, getConfirmationDialogueQuestion(poll.getQuestion()))
                .setItem(15, getConfirmationDialogueDuration(lib.getTimesAPI().generateTime(poll.getDuration())));
        int i;
        for (i = 0; i < 6; i++) {
            if (poll.getOptions().size() == i)
                break;
            myInventory.setItem(availableSlots[i],
                    getConfirmationDialogueOption(poll.getOptions().get(i).getOption(), i));
        }
        if (i < 6) {
            myInventory.setItem(availableSlots[i],
                    new ItemBuilder(Material.PAPER).tagString("MTPolls_option", "option" + i)
                            .mnl("confirmation-dialogue-no-option").build(lib));
        }

        return myInventory.build(lib, player);
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

    private ItemStack getConfirmationDialogueConfirm(Player player) {
        ItemBuilder sack = null;
        if (lib.getCompatibilityApi().getServerVersion().getMajor() <= 12)
            sack = new ItemBuilder(Material.matchMaterial("INK_SACK"))
                    .data((byte) 1);
        else
            sack = new ItemBuilder(Material.matchMaterial("LIME_DYE"));

        return sack.mnl("confirmation-dialogue-confirm").build(lib);
    }

    private ItemStack getConfirmationDialogueClose() {
        return new ItemBuilder(Material.BARRIER)
                .mnl("confirmation-dialogue-close")
                .build(lib);
    }

    private ItemStack getConfirmationDialogueQuestion(String question) {
        ItemBuilder result = null;
        if (lib.getCompatibilityApi().getServerVersion().getMajor() <= 12)
            result = new ItemBuilder(Material.matchMaterial("BOOK_AND_QUILL"))
                    .data((byte) 1);
        else
            result = new ItemBuilder(Material.matchMaterial("WRITABLE_BOOK"));
        result = result.tagString("MTPolls_option", "question");
        if (question == null)
            return result.mnl("confirmation-dialogue-no-question").build(lib);
        return result.mnl("confirmation-dialogue-question").replaceable("%question%", question).build(lib);
    }

    private ItemStack getConfirmationDialogueName(String name) {
        ItemBuilder result = new ItemBuilder(Material.NAME_TAG);
        result = result.tagString("MTPolls_option", "name");
        if (name == null)
            return result.mnl("confirmation-dialogue-no-name").build(lib);
        return result.mnl("confirmation-dialogue-name").replaceable("%name%", name).build(lib);
    }

    private ItemStack getConfirmationDialogueDuration(String duration) {
        ItemBuilder result = new ItemBuilder(Material.CLOCK);
        result = result.tagString("MTPolls_option", "duration");
        if (duration == null)
            return result.mnl("confirmation-dialogue-no-duration").build(lib);
        return result.mnl("confirmation-dialogue-duration").replaceable("%duration%", duration).build(lib);
    }

    private ItemStack getConfirmationDialogueOption(String option, int index) {
        return new ItemBuilder(Material.FILLED_MAP).tagString("MTPolls_option", "option" + index)
                .mnl("confirmation-dialogue-option")
                .replaceable("%option%", option).build(lib);
    }

    public void setUUID(Player player) {
        currentlyCreating.put(player.getUniqueId(), new Poll(player.getUniqueId()));
    }

    public Map<UUID, Poll> getCurrentlyCreating() {
        return currentlyCreating;
    }
}

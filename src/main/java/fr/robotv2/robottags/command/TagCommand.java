package fr.robotv2.robottags.command;

import fr.robotv2.robottags.RobotTags;
import fr.robotv2.robottags.player.TagPlayer;
import fr.robotv2.robottags.tag.Tag;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import revxrsal.commands.annotation.AutoComplete;
import revxrsal.commands.annotation.Command;
import revxrsal.commands.annotation.Default;
import revxrsal.commands.annotation.Optional;
import revxrsal.commands.annotation.Subcommand;
import revxrsal.commands.annotation.Usage;
import revxrsal.commands.bukkit.BukkitCommandActor;
import revxrsal.commands.bukkit.annotation.CommandPermission;

@Command({"tag", "tags", "robottags"})
public final class TagCommand {

    private final RobotTags plugin;
    public TagCommand(RobotTags plugin) {
        this.plugin = plugin;
    }

    @Default
    public void onDefault(BukkitCommandActor actor) {
        plugin.getTagInventoryManager().openForPlayer(actor.requirePlayer(), 1);
    }

    @Subcommand("reload")
    @CommandPermission("robottags.reload")
    public void onReload(BukkitCommandActor actor) {
        plugin.onReload();
        actor.reply(ChatColor.GREEN + "The plugin has been reloaded successfully.");
    }

    @Subcommand("set")
    @CommandPermission("robottags.set")
    @Usage("<target> <tag>")
    @AutoComplete("@players @tags")
    public void onSet(BukkitCommandActor actor, OfflinePlayer target, Tag tag) {

        if(tag == null) {
            actor.reply(ChatColor.RED + "This tag doesn't exist.");
            return;
        }

        final TagPlayer tagPlayer = target.isOnline() ? TagPlayer.getTagPlayer(target.getUniqueId()) : plugin.getDataManager().getTagPlayer(target.getUniqueId());

        if(tagPlayer == null) {
            actor.reply(ChatColor.RED + "Couldn't find this player.");
            return;
        }

        tagPlayer.setTagId(tag.getId());

        if(!target.isOnline()) {
            plugin.getDataManager().saveTagPlayer(tagPlayer);
        }

        actor.reply(ChatColor.GREEN + "The tag of " + target.getName() + " has been changed.");
    }

    @Subcommand("clear")
    @CommandPermission("robottags.clear")
    @Usage("[<target>]")
    @AutoComplete("@players")
    public void onClear(BukkitCommandActor actor, @Optional OfflinePlayer target) {

        TagPlayer tagPlayer;

        if(target == null) {
            tagPlayer = TagPlayer.getTagPlayer(actor.requirePlayer());
        } else {
            tagPlayer = target.isOnline() ? TagPlayer.getTagPlayer(target.getUniqueId()) : plugin.getDataManager().getTagPlayer(target.getUniqueId());
        }

        if(tagPlayer == null) {
            actor.reply(ChatColor.RED + "Couldn't find this player.");
            return;
        }

        tagPlayer.setTagId(null);

        if(target != null && !target.isOnline()) {
            plugin.getDataManager().saveTagPlayer(tagPlayer);
        }

        actor.reply(ChatColor.GREEN + "The tag of " + target.getName() + " has been cleared.");
    }
}

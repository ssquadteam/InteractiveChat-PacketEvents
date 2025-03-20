package net.skullian.updater;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.skullian.InteractiveChatPacketEvents;
import net.skullian.util.ChatUtils;
import net.skullian.util.GithubBuildInfo;
import net.skullian.util.GithubUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.Locale;

public class Updater implements Listener {

    public static UpdateStatus checkUpdate(CommandSender... senders) {
        GithubBuildInfo currentBuild = GithubBuildInfo.CURRENT;
        GithubBuildInfo latestBuild;
        GithubUtils.GitHubStatusLookup lookupStatus;

        UpdateStatus updateStatus = new UpdateStatus(false, false);

        try {
            if (currentBuild.isStable()) {
                latestBuild = GithubUtils.lookupLatestRelease();
                lookupStatus = GithubUtils.compare(latestBuild.getId(), currentBuild.getId());
            } else {
                latestBuild = null;
                lookupStatus = GithubUtils.compare(GithubUtils.MAIN_BRANCH, currentBuild.getId());
            }
        } catch (IOException error) {
            ChatUtils.sendMessage("<red>Failed to fetch latest version: " + error, senders);
            updateStatus.setFailed(true);
            return updateStatus;
        }

        if (lookupStatus.isBehind()) {
            if (currentBuild.isStable()) {
                ChatUtils.sendMessage("<green>A new version of InteractiveChat-PacketEvents is available: " + latestBuild.getId() + "!", senders);
                ChatUtils.sendMessage("<grey>Download at: <click:open_url:'https://github.com/TerraByteDev/InteractiveChat-PacketEvents/releases/tag/" + latestBuild.getId() + "'>GitHub Releases</click>", senders);
            } else {
                ChatUtils.sendMessage("<yellow>You are running a development build of InteractiveChat-PacketEvents!\nThe latest available development build is " + String.format(Locale.ROOT, "%,d", lookupStatus.getDistance()) + " commits ahead.", senders);
            }
        }

        updateStatus.setUpToDate(lookupStatus.isBehind());
        return updateStatus;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static final class UpdateStatus {
        private boolean isUpToDate;
        private boolean failed;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(InteractiveChatPacketEvents.instance, () -> {
            Player player = event.getPlayer();
            if (player.hasPermission("interactivechatpacketevents.checkupdate")) {
                checkUpdate(player);
            }
        }, 100);
    }
}

package net.skullian.command.subcmd;

import net.skullian.InteractiveChatPacketEvents;
import net.skullian.updater.Updater;
import net.skullian.util.ChatUtils;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;

@Command("interactivechatpacketevents|icpe")
public class CheckUpdateCommand {

    @Command("checkupdate")
    @CommandDescription("Check for updates")
    @Permission(value = {"interactivechatpacketevents.checkupdate"})
    public void execute(
            CommandSender sender
    ) {
        ChatUtils.sendMessage("<grey>Checking for updates, please wait...", sender);

        Updater.UpdateStatus updateStatus = Updater.checkUpdate(sender);
        if (updateStatus.isUpToDate() && !updateStatus.isFailed()) {
            ChatUtils.sendMessage("<green>You are running the latest version of InteractiveChat-PacketEvents! <grey>[" + InteractiveChatPacketEvents.instance.getDescription().getVersion() + "]", sender);
        }
    }
}

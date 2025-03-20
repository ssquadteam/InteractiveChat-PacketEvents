package net.skullian.command;

import net.skullian.InteractiveChatPacketEvents;
import net.skullian.util.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.BukkitCommandManager;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.meta.SimpleCommandMeta;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

public class CommandHandler {

    private AnnotationParser<CommandSender> parser;
    public static LegacyPaperCommandManager<CommandSender> manager;

    public CommandHandler() {
        manager = LegacyPaperCommandManager.createNative(
                InteractiveChatPacketEvents.instance,
                ExecutionCoordinator.asyncCoordinator()
        );

        if (manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            manager.registerBrigadier();
        } else if (manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            manager.registerAsynchronousCompletions();
        }

        parser = new AnnotationParser<>(
                manager,
                CommandSender.class,
                params -> SimpleCommandMeta.empty()
        );

        registerCommands();

        ChatUtils.sendMessage("<green>Registered commands.", Bukkit.getConsoleSender());
    }

    private void registerCommands() {

    }
}

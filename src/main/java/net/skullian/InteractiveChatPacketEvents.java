package net.skullian;

import com.loohp.interactivechat.InteractiveChat;
import net.skullian.command.CommandHandler;
import net.skullian.platform.PacketEventsPlatform;
import net.skullian.updater.UpdateListener;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class InteractiveChatPacketEvents extends JavaPlugin {

    public static InteractiveChatPacketEvents instance;

    @Override
    public void onLoad() {
        instance = this;

        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[ICPE] Overriding InteractiveChat ProtocolProvider.");
        InteractiveChat.protocolPlatform = new PacketEventsPlatform();
    }

    @Override
    public void onEnable() {
        getServer().getConsoleSender().sendMessage(ChatColor.YELLOW + "[ICPE] Initialising ProtocolProvider.");
        InteractiveChat.protocolPlatform.initialize();

        getServer().getPluginManager().registerEvents(new UpdateListener(), this);

        new CommandHandler();
    }

    @Override
    public void onDisable() {
    }
}

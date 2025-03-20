package net.skullian;

import com.loohp.interactivechat.InteractiveChat;
import net.skullian.platform.PacketEventsPlatform;
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
    }

    @Override
    public void onDisable() {
    }
}

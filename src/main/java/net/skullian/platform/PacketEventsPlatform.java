package net.skullian.platform;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.chat.ChatTypes;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessageLegacy;
import com.github.retrooper.packetevents.protocol.chat.message.ChatMessage_v1_16;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSystemChatMessage;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTabComplete;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.libs.net.kyori.adventure.text.Component;
import com.loohp.interactivechat.objectholders.CustomTabCompletionAction;
import com.loohp.interactivechat.platform.ProtocolPlatform;
import com.loohp.interactivechat.utils.InteractiveChatComponentSerializer;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.NativeAdventureConverter;
import net.md_5.bungee.chat.ComponentSerializer;
import net.skullian.InteractiveChatPacketEvents;
import net.skullian.listeners.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.loohp.interactivechat.InteractiveChat.version;

public class PacketEventsPlatform implements ProtocolPlatform {

    @Override
    public void initialize() {
        PacketEvents.getAPI().getEventManager().registerListener(new PEOutMessagePacket(), PacketListenerPriority.MONITOR);
        PacketEvents.getAPI().getEventManager().registerListener(new PEClientSettingsPacket(), PacketListenerPriority.NORMAL);

        if (version.isNewerOrEqualTo(MCVersion.V1_19)) {
            PacketEvents.getAPI().getEventManager().registerListener(new PERedispatchSignedPacket(), PacketListenerPriority.HIGHEST);
        }

        if (!version.isLegacy()) {
            PacketEvents.getAPI().getEventManager().registerListener(new PEOutTabCompletePacket(), PacketListenerPriority.HIGH);
        }
    }

    @Override
    public void onBungeecordModeEnabled() {
        PacketEvents.getAPI().getEventManager().registerListener(new PEServerPingListener(), PacketListenerPriority.NORMAL);
    }

    @Override
    public void sendTabCompletionPacket(Player player, CustomTabCompletionAction action, List<String> list) {
        try {
            List<WrapperPlayServerTabComplete.CommandMatch> suggestions = new ArrayList<>();
            for (String cmd : list) {
                WrapperPlayServerTabComplete.CommandMatch match = new WrapperPlayServerTabComplete.CommandMatch(
                        cmd,
                        null
                );
                suggestions.add(match);
            }

            WrapperPlayServerTabComplete packet = new WrapperPlayServerTabComplete(
                    null,
                    new WrapperPlayServerTabComplete.CommandRange(0, list.size()),
                    suggestions
            );
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendUnprocessedChatMessage(CommandSender sender, UUID uuid, Component component) {
        try {
            if (sender instanceof Player) {
                net.kyori.adventure.text.Component nativeComponent = (net.kyori.adventure.text.Component) NativeAdventureConverter.componentToNative(component, false);

                PacketWrapper<?> packet;
                if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19)) {
                    packet = new WrapperPlayServerSystemChatMessage(false, nativeComponent);
                } else {
                    ChatMessage message;
                    if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_16)) {
                        message = new ChatMessage_v1_16(nativeComponent, ChatTypes.SYSTEM, uuid);
                    } else {
                        message = new ChatMessageLegacy(nativeComponent, ChatTypes.SYSTEM);
                    }

                    packet = new WrapperPlayServerChatMessage(message);
                }

                PacketEvents.getAPI().getPlayerManager().sendPacketSilently(sender, packet);
            } else {
                String json = InteractiveChatComponentSerializer.gson().serialize(component);
                sender.spigot().sendMessage(ComponentSerializer.parse(json));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasChatSigning() {
        return InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_19);
    }

    @Override
    public int getProtocolVersion(Player player) {
        return PacketEvents.getAPI().getProtocolManager().getClientVersion(player).getProtocolVersion();
    }

    @Override
    public Plugin getRegisteredPlugin() {
        return InteractiveChatPacketEvents.instance;
    }
}


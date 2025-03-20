package net.skullian.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.configuration.client.WrapperConfigClientSettings;
import com.loohp.interactivechat.InteractiveChat;
import com.loohp.interactivechat.listeners.packet.ClientSettingsHandler;
import com.loohp.interactivechat.utils.MCVersion;
import com.loohp.interactivechat.utils.PlayerUtils;
import net.skullian.InteractiveChatPacketEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PEClientSettingsPacket implements PacketListener, Listener {

    private final Map<UUID, Boolean> colorSettingsMap = new HashMap<>();

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (event.getPacketType() != PacketType.Configuration.Client.CLIENT_SETTINGS) return;

        if (InteractiveChat.version.isNewerOrEqualTo(MCVersion.V1_20_2)) {
            WrapperConfigClientSettings packet = new WrapperConfigClientSettings(event);

            UUID uuid = event.getPlayer();
            boolean colorSettings = packet.isChatColors();

            if (Bukkit.getPlayer(uuid) != null) {
                Player player = Bukkit.getPlayer(uuid);

                boolean originalColorSettings = PlayerUtils.canChatColor(player);
                ClientSettingsHandler.handlePacketReceiving(colorSettings, originalColorSettings, player);
            } else {
                colorSettingsMap.put(uuid, colorSettings);
            }
        }
    }

    public PEClientSettingsPacket() {
        Bukkit.getServer().getPluginManager().registerEvents(this, InteractiveChatPacketEvents.instance);
    }

    // Why do we have to do this?
    // Since PacketEvents injects directly into Netty, the above method is called BEFORE the server itself actually has time
    // to create the CraftPlayer instance. This means that we can't call PlayerUtils.canChatColor as that directly fetches
    // the CraftPlayer handle instance of the player, which results in an exception.
    // Therefore, we wait for the PlayerJoinEvent which will ensure the CraftPlayer instance has been created.
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        boolean originalColorSettings = PlayerUtils.canChatColor(player);
        ClientSettingsHandler.handlePacketReceiving(colorSettingsMap.getOrDefault(player.getUniqueId(), true), originalColorSettings, player);

        colorSettingsMap.remove(player.getUniqueId());
    }
}

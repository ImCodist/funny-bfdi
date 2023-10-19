package xyz.imcodist.funnybfdi;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.imcodist.funnybfdi.other.Config;
import xyz.imcodist.funnybfdi.other.MouthManager;

public class FunnyBFDI implements ModInitializer {
	public static final String MOD_ID = "funnybfdi";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("You swear you heard \"New Friendly\" begin to play in your head.");

		MidnightConfig.init(MOD_ID, Config.class);

		ClientReceiveMessageEvents.CHAT.register(((message, signedMessage, sender, params, receptionTimestamp) -> {
			if (sender == null) return;

			Text messageText = message;
			if (signedMessage != null) messageText = signedMessage.getContent();

			MouthManager.onPlayerChatted(Text.of(messageText.getString() + " "), sender.getId());
		}));

		ClientTickEvents.END_CLIENT_TICK.register((client -> MouthManager.tick()));
	}
}
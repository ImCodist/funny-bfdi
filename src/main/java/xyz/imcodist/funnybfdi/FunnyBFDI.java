package xyz.imcodist.funnybfdi;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.imcodist.funnybfdi.other.MouthManager;

public class FunnyBFDI implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("funnybfdi");

	@Override
	public void onInitialize() {
		LOGGER.info("You swear you heard \"New Friendly\" begin to play in your head.");

		ClientReceiveMessageEvents.CHAT.register(((message, signedMessage, sender, params, receptionTimestamp) -> {
			if (sender == null) return;

			Text messageText = message;
			if (signedMessage != null) messageText = signedMessage.getContent();

			MouthManager.onPlayerChatted(messageText, sender.getId());
		}));

		ClientTickEvents.END_CLIENT_TICK.register((client -> MouthManager.tick()));
	}
}
package xyz.imcodist.funnybfdi.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.text.Text;
import xyz.imcodist.funnybfdi.FunnyBFDI;
import xyz.imcodist.funnybfdi.other.MouthManager;

public class FunnyBFDIClient implements ClientModInitializer {
	public void onInitializeClient() {
		FunnyBFDI.LOGGER.info("You swear you heard \"New Friendly\" begin to play in your head.");

		ClientReceiveMessageEvents.CHAT.register(((message, signedMessage, sender, params, receptionTimestamp) -> {
			if (sender == null) return;

			Text messageText = message;
			if (signedMessage != null) messageText = signedMessage.getContent();

			MouthManager.onPlayerChatted(Text.of(messageText.getString() + " "), sender.getId());
		}));

		ClientTickEvents.END_CLIENT_TICK.register((client -> MouthManager.tick()));
	}
}
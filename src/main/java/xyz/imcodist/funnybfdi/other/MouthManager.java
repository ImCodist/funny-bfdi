package xyz.imcodist.funnybfdi.other;

import com.mojang.authlib.GameProfile;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.UUID;

public class MouthManager {
    private static final ArrayList<MouthState> playerMouths = new ArrayList<>();

    public static void tick() {
        playerMouths.forEach(MouthState::tick);
    }

    public static void onPlayerChatted(Text message, GameProfile sender) {
        MouthState mouthState = getOrCreatePlayerMouthState(sender.getId());

        mouthState.talkCharacter = 0;
        mouthState.talkText = message.getString();

        System.out.println(mouthState.talkText);

        mouthState.talking = true;
    }

    public static MouthState getOrCreatePlayerMouthState(UUID playerUUID) {
        MouthState getState = getPlayerMouthState(playerUUID);
        if (getState != null) return getState;

        MouthState newPlayerState = new MouthState();
        newPlayerState.playerUUID = playerUUID;
        playerMouths.add(newPlayerState);

        return newPlayerState;
    }

    public static MouthState getPlayerMouthState(UUID playerUUID) {
        for (MouthState mouthState : playerMouths) {
            if (mouthState.playerUUID.equals(playerUUID)) {
                return mouthState;
            }
        }

        return null;
    }

    public static class MouthState {
        public UUID playerUUID;

        public boolean talking = false;

        public String talkText = "";
        public int talkCharacter = 0;

        private double talkTimer = 0.0;

        public void tick() {
            if (talking) {
                talkTimer += 0.65;

                if (talkTimer >= 1.0) {
                    if (talkCharacter >= talkText.length() - 1) {
                        talking = false;
                    }

                    if (talkCharacter < talkText.length()) {
                        talkCharacter += 1;
                        if (talkCharacter >= talkText.length()) talkCharacter = talkText.length() - 1;
                    }

                    talkTimer -= 1.0;
                }
            }
        }
    }
}

package xyz.imcodist.funnybfdi.other;

import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.UUID;

public class MouthManager {
    private static final ArrayList<MouthState> playerMouths = new ArrayList<>();

    public static void tick() {
        if (!Config.enabled) {
            if (playerMouths.size() > 0) {
                playerMouths.clear();
            }

            return;
        }

        playerMouths.forEach(MouthState::tick);
        playerMouths.removeIf(mouthState -> mouthState.queueForDeletion);
    }

    public static void onPlayerChatted(Text message, UUID senderUUID) {
        if (!Config.enabled) return;

        MouthState mouthState = getOrCreatePlayerMouthState(senderUUID);

        mouthState.talkCharacter = 0;
        mouthState.talkText = message.getString();
        mouthState.updateMouthShape();

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

        public boolean queueForDeletion = false;

        public boolean talking = false;

        public String talkText = "";
        public int talkCharacter = 0;

        public String currentMouthShape = "0";
        public String transitionMouthShape = currentMouthShape;

        private double talkTimer = 0.0;

        public void tick() {
            if (talking) {
                talkTimer += 0.75 * Config.mouthSpeed;

                if (talkTimer >= 1.0) {
                    if (talkCharacter >= talkText.length() - 1) {
                        talking = false;
                        queueForDeletion = true;
                    }

                    if (talkCharacter < talkText.length()) {
                        talkCharacter += 1;
                        if (talkCharacter >= talkText.length()) talkCharacter = talkText.length() - 1;

                        updateMouthShape();
                    }

                    talkTimer -= 1.0;
                }

                if (Config.mouthTransitions) {
                    if (currentMouthShape.equals("8")) {
                        transitionMouthShape = switch (transitionMouthShape) {
                            case "9" -> "7";
                            case "7", "8" -> "8";
                            default -> "9";
                        };
                    } else {
                        if (transitionMouthShape.equals("8") || transitionMouthShape.equals("7")) {
                            transitionMouthShape = "9";
                        } else if (transitionMouthShape.equals("3") && !currentMouthShape.equals("3")) {
                            transitionMouthShape = "2";
                        } else {
                            transitionMouthShape = currentMouthShape;
                        }
                    }
                } else {
                    transitionMouthShape = currentMouthShape;
                }
            }
        }

        public void updateMouthShape() {
            String character = String.valueOf(talkText.charAt(talkCharacter));

            transitionMouthShape = currentMouthShape;
            currentMouthShape = switch (character.toLowerCase()) {
                case "a", "e", "u" -> "2";
                case "i" -> "3";
                case "o", "r" -> "8";
                case "m", "p", "b" -> "6";
                case "f", "v" -> "5";
                case "l" -> "4";
                case "t", "d", "k", "g", "n", "s", " " -> "0";
                default -> "1";
            };
        }
    }
}

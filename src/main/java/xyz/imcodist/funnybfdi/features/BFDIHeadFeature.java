package xyz.imcodist.funnybfdi.features;

import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import xyz.imcodist.funnybfdi.other.MouthManager;

public class BFDIHeadFeature<T extends LivingEntity, M extends EntityModel<T> & ModelWithHead> extends FeatureRenderer<T, M> {
    private final ModelPart base;

    public BFDIHeadFeature(FeatureRendererContext<T, M> context) {
        super(context);

        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("mouth", ModelPartBuilder.create().uv(0, 0).cuboid(-4.0F, -8.0F, 4.51F, 8.0F, 16.0F, 0.0F), ModelTransform.pivot(0.0F, 0.0F, 0.0F));
        TexturedModelData texturedModelData = TexturedModelData.of(modelData, 16, 16);

        this.base = texturedModelData.createModel().getChild("mouth");
    }

    @Override
    public void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        MouthManager.MouthState mouthState = MouthManager.getPlayerMouthState(entity.getUuid());

        String mouthExpression = "normal";
        if (entity.getHealth() <= 10) {
            // when less then half health
            mouthExpression = "sad";
        }

        String mouth = "idle";

        // has more then max hearts
        if (mouthExpression.equals("normal")) {
            for (StatusEffectInstance effectInstance : entity.getStatusEffects()) {
                if (effectInstance.getEffectType().equals(StatusEffects.ABSORPTION)) {
                    mouthExpression = "normal";
                    mouth = "absorption";
                    break;
                }
            }
        }

        // is at critical hearts (shaky health bar oooo)
        if (entity.getHealth() <= 4) {
            mouthExpression = "sad";
            mouth = "critical";
        }

        // is talking
        if (mouthState != null) {
            if (mouthState.talking) {
                String character = String.valueOf(mouthState.talkText.charAt(mouthState.talkCharacter));

                String num = switch (character.toLowerCase()) {
                    case "a", "e", "u" -> "2";
                    case "i" -> "3";
                    case "o", "r" -> "7";
                    case "m", "p", "b" -> "6";
                    case "f", "v" -> "5";
                    case "l" -> "4";
                    case "t", "d", "k", "g", "n", "s", " " -> "0";
                    default -> "1";
                };

                if (!mouth.equals("absorption") && !mouth.equals("critical")) {
                    if (!num.equals("0")) mouth = "talk" + num;
                } else {
                    if (!num.equals("0")) mouth = mouth + "talk";
                }
            }
        }

        // is hurt
        if (entity.hurtTime > 0) {
            mouthExpression = "special";
            mouth = "hurt";
        }

        RenderLayer renderLayer = RenderLayer.getEntityTranslucent(new Identifier("funnybfdi", "textures/mouths/" + mouthExpression + "/" + mouth + ".png"));
        VertexConsumer vertices = vertexConsumers.getBuffer(renderLayer);

        ModelPart head = getContextModel().getHead();

        // make changes to the matrix
        matrices.translate(head.pivotX / 8.0, head.pivotY / 16.0, head.pivotZ / 8.0);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(head.yaw));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotation(head.pitch));
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotation(head.roll));

        // render
        this.base.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);

        // revert all the changes i made to the matrix
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotation(-head.roll));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotation(-head.pitch));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-head.yaw));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-180.0F));

        matrices.translate(-head.pivotX / 8.0, -head.pivotY / 16.0, -head.pivotZ / 8.0);
    }
}

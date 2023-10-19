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
import xyz.imcodist.funnybfdi.FunnyBFDI;
import xyz.imcodist.funnybfdi.other.Config;
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
        if (!Config.enabled) return;

        MouthManager.MouthState mouthState = MouthManager.getPlayerMouthState(entity.getUuid());

        String mouthExpression = "normal";
        if (entity.getHealth() <= 10) {
            // when less then half health
            mouthExpression = "sad";
        }

        String mouth = "idle";

        // status effects
        boolean hasAbsorb = false;
        boolean hasPoison = false;
        for (StatusEffectInstance effectInstance : entity.getStatusEffects()) {
            if (effectInstance.getEffectType().equals(StatusEffects.ABSORPTION)) {
                hasAbsorb = true;
                continue;
            }

            if (effectInstance.getEffectType().equals(StatusEffects.POISON)) {
                hasPoison = true;
            }
        }

        // absorption
        if (mouthExpression.equals("normal") && hasAbsorb) {
            mouthExpression = "normal";
            mouth = "absorption";
        }

        // is at critical hearts (shaky health bar oooo)
        if (entity.getHealth() <= 4) {
            mouthExpression = "sad";
            mouth = "critical";
        }

        if (entity.isSubmergedInWater()) {
            mouth = "water";
        }

        // is talking
        if (mouthState != null && mouthState.talking) {
            String shape = mouthState.transitionMouthShape;

            if (!shape.equals("0")) {
                if (!mouth.equals("absorption") && !mouth.equals("critical") && !mouth.equals("water")) {
                    mouth = "talk" + shape;
                } else {
                    mouth = mouth + "talk";
                }
            } else {
                if (mouth.equals("water")) mouth = mouth + "talkclosed";
            }
        } else {
            if (hasPoison) {
                mouthExpression = "special";
                mouth = "poison";
            }
        }

        // is hurt
        if (entity.hurtTime > 0) {
            mouthExpression = "special";
            mouth = "hurt";
        }

        RenderLayer renderLayer = RenderLayer.getEntityTranslucent(new Identifier(FunnyBFDI.MOD_ID, "textures/mouths/" + mouthExpression + "/" + mouth + ".png"));
        VertexConsumer vertices = vertexConsumers.getBuffer(renderLayer);

        ModelPart head = getContextModel().getHead();

        float offsetScale = 250.0f;
        float mouthSize = Config.mouthSize;

        if (mouthSize <= 0.0f) {
            mouthSize = 0.5f;
            this.base.visible = false;
        } else this.base.visible = true;

        // make changes to the matrix
        matrices.translate(head.pivotX / 8.0, head.pivotY / 16.0, head.pivotZ / 8.0);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(head.yaw));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotation(head.pitch));
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotation(head.roll));

        matrices.translate(Config.mouthOffsetX / -offsetScale, Config.mouthOffsetY / offsetScale, Config.mouthOffsetZ / offsetScale);
        matrices.scale(mouthSize, mouthSize, 1.0f);

        // render
        this.base.render(matrices, vertices, light, OverlayTexture.DEFAULT_UV);

        // revert all the changes i made to the matrix
        matrices.scale(1.0f / mouthSize, 1.0f / mouthSize, 1.0f);
        matrices.translate((Config.mouthOffsetX / -offsetScale) * -1.0f, (Config.mouthOffsetY / offsetScale) * -1.0f, (Config.mouthOffsetZ / offsetScale) * -1.0f);

        matrices.multiply(RotationAxis.NEGATIVE_Z.rotation(-head.roll));
        matrices.multiply(RotationAxis.NEGATIVE_X.rotation(-head.pitch));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotation(-head.yaw));

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-180.0F));

        matrices.translate((head.pivotX / 8.0) * -1.0f, (head.pivotY / 16.0) * -1.0f, (head.pivotZ / 8.0) * -1.0f);
    }
}

package net.antik16384.paintingsneedglass.mixin.client;

import net.antik16384.paintingsneedglass.Config;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.PaintingEntityRenderer;
import net.minecraft.client.render.entity.state.PaintingEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.entity.decoration.painting.PaintingEntity;

@Mixin(PaintingEntityRenderer.class)
public abstract class PaintingEntityRendererMixin extends EntityRenderer<PaintingEntity, PaintingEntityRenderState> {

    @Final
    @Shadow
    private SpriteAtlasTexture paintingAtlases;

    protected PaintingEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/render/entity/state/PaintingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void paintingsneedglass$render(
            PaintingEntityRenderState state,
            MatrixStack matrixStack,
            OrderedRenderCommandQueue queue,
            CameraRenderState cameraRenderState,
            CallbackInfo ci
    ) {
        if (!Config.getInstance().modEnabled) {
            return;
        }

        PaintingVariant variant = state.variant;
        if (variant == null) return;

        matrixStack.push();
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180 - state.facing.getHorizontalQuarterTurns() * 90));

        Sprite front = this.paintingAtlases.getSprite(variant.assetId());
        Sprite back = this.paintingAtlases.getSprite(Identifier.ofVanilla("back"));

        RenderLayer layer = RenderLayers.entityCutout(front.getAtlasId());

        paintingsneedglass$renderPainting(
                matrixStack,
                queue,
                layer,
                state.lightmapCoordinates,
                variant.width(),
                variant.height(),
                front,
                back
        );

        matrixStack.pop();

        super.render(state, matrixStack, queue, cameraRenderState);
        ci.cancel();
    }

    @Unique
    private void paintingsneedglass$renderPainting(
            MatrixStack matrices,
            OrderedRenderCommandQueue queue,
            RenderLayer renderLayer,
            int[] lightmapCoordinates,
            int width,
            int height,
            Sprite front,
            Sprite back
    ) {
        boolean hideSides = Config.getInstance().hideSideTexture;

        queue.submitCustom(matrices, renderLayer, (matricesEntry, vertexConsumer) -> {
            float f = -width / 2.0F;
            float g = -height / 2.0F;

            float front_z = hideSides ? 0.0F : -0.03125F;
            float back_z = hideSides ? -0.0625F : 0.03125F;

            float k = back.getMinU();
            float l = back.getMaxU();
            float m = back.getMinV();
            float n = back.getMaxV();
            float o = back.getMinU();
            float p = back.getMaxU();
            float q = back.getMinV();
            float r = back.getFrameV(0.0625F);
            float s = back.getMinU();
            float t = back.getFrameU(0.0625F);
            float u = back.getMinV();
            float v = back.getMaxV();

            double d = 1.0 / width;
            double e = 1.0 / height;

            for (int w = 0; w < width; w++) {
                for (int x = 0; x < height; x++) {
                    float y = f + (w + 1);
                    float z = f + w;
                    float aa = g + (x + 1);
                    float ab = g + x;
                    int ac = lightmapCoordinates[w + x * width];

                    float ad = front.getFrameU((float)(d * (width - w)));
                    float ae = front.getFrameU((float)(d * (width - (w + 1))));
                    float af = front.getFrameV((float)(e * (height - x)));
                    float ag = front.getFrameV((float)(e * (height - (x + 1))));

                    // FRONT
                    paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, ab, ae, af, front_z, 0, 0, -1, ac);
                    paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, ab, ad, af, front_z, 0, 0, -1, ac);
                    paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, aa, ad, ag, front_z, 0, 0, -1, ac);
                    paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, aa, ae, ag, front_z, 0, 0, -1, ac);

                    // BACK
                    paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, aa, l, m, back_z, 0, 0, 1, ac);
                    paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, aa, k, m, back_z, 0, 0, 1, ac);
                    paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, ab, k, n, back_z, 0, 0, 1, ac);
                    paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, ab, l, n, back_z, 0, 0, 1, ac);

                    if (!hideSides) {
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, aa, o, q, front_z, 0, 1, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, aa, p, q, front_z, 0, 1, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, aa, p, r, back_z, 0, 1, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, aa, o, r, back_z, 0, 1, 0, ac);

                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, ab, o, q, back_z, 0, -1, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, ab, p, q, back_z, 0, -1, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, ab, p, r, front_z, 0, -1, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, ab, o, r, front_z, 0, -1, 0, ac);

                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, aa, t, u, front_z, -1, 0, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, ab, t, v, front_z, -1, 0, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, ab, s, v, back_z, -1, 0, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, y, aa, s, u, back_z, -1, 0, 0, ac);

                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, aa, t, u, back_z, 1, 0, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, ab, t, v, back_z, 1, 0, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, ab, s, v, front_z, 1, 0, 0, ac);
                        paintingsneedglass$vertex(matricesEntry, vertexConsumer, z, aa, s, u, front_z, 1, 0, 0, ac);
                    }
                }
            }
        });
    }

    @Unique
    private void paintingsneedglass$vertex(
            MatrixStack.Entry matrix,
            VertexConsumer vertexConsumer,
            float x, float y, float u, float v, float z,
            int normalX, int normalY, int normalZ, int light
    ) {
        vertexConsumer.vertex(matrix, x, y, z)
                .color(Colors.WHITE)
                .texture(u, v)
                .overlay(OverlayTexture.DEFAULT_UV)
                .light(light)
                .normal(matrix, normalX, normalY, normalZ);
    }
}
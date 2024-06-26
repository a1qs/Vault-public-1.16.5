package iskallia.vault.block.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.block.PlayerStatueBlock;
import iskallia.vault.block.VendingMachineBlock;
import iskallia.vault.block.entity.VendingMachineTileEntity;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.init.ModItems;
import iskallia.vault.vending.TraderCore;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

public class VendingMachineRenderer extends TileEntityRenderer<VendingMachineTileEntity> {

    public static final StatuePlayerModel<PlayerEntity> PLAYER_MODEL = new StatuePlayerModel<>(0.1f, true);

    public VendingMachineRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void
    render(VendingMachineTileEntity tileEntity, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int combinedLight, int combinedOverlay) {
        TraderCore renderCore = tileEntity.getRenderCore();

        if (renderCore == null)
            return; // Woopsies, no core no render.

        Minecraft minecraft = Minecraft.getInstance();
        boolean shouldOutline = false;
        if (minecraft.player != null && minecraft.player.getMainHandItem().getItem() == ModItems.TRADER_CORE) {
            ItemStack heldStack = minecraft.player.getMainHandItem();
            if (heldStack.hasTag()) {
                CompoundNBT nbt = heldStack.getTag();
                CompoundNBT coreNBT = nbt.getCompound("core");
                if (coreNBT.getString("NAME").equals(renderCore.getName())) {
                    shouldOutline = true;
                }
            }
        }

        BlockState blockState = tileEntity.getBlockState();

        ResourceLocation skinLocation = tileEntity.getSkin().getLocationSkin();

        if (shouldOutline) {
            IVertexBuilder outlineBuffer = buffer.getBuffer(RenderType.outline(skinLocation));

            renderTrader(matrixStack, blockState, renderCore,
                    outlineBuffer, combinedLight, combinedOverlay, 0.5f);
        }

        renderTrader(matrixStack, blockState, renderCore,
                buffer.getBuffer(PLAYER_MODEL.renderType(skinLocation)), combinedLight, combinedOverlay, 1f);

        BlockPos pos = tileEntity.getBlockPos();

        drawString(
                matrixStack,
                blockState.getValue(VendingMachineBlock.FACING).getOpposite(),
                tileEntity.getSkin().getLatestNickname(),
                6f / 16f, pos.getX(), pos.getY(), pos.getZ(), 0.01f
        );
    }

    public void renderTrader(MatrixStack matrixStack, BlockState blockState, TraderCore renderCore, IVertexBuilder vertexBuilder, int combinedLight, int combinedOverlay, float alpha) {
        Direction direction = blockState.getValue(PlayerStatueBlock.FACING);

        float scale = renderCore.isMegahead() ? 0.8f : 0.9f;
        float headScale = renderCore.isMegahead() ? 1.75f : 1f;

        matrixStack.pushPose();
        matrixStack.translate(0.5, renderCore.isMegahead() ? 1.1 : 1.3, 0.5);
        matrixStack.scale(scale, scale, scale);
        matrixStack.mulPose(Vector3f.YN.rotationDegrees(direction.toYRot() + 180));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(180));
        PLAYER_MODEL.body.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.leftLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.rightLeg.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.leftArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.rightArm.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);

        PLAYER_MODEL.jacket.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.leftPants.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.rightPants.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.leftSleeve.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);

        matrixStack.pushPose();
        matrixStack.translate(0, 0, -0.62f);
        PLAYER_MODEL.rightSleeve.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        matrixStack.popPose();

        matrixStack.scale(headScale, headScale, headScale);
        PLAYER_MODEL.hat.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);
        PLAYER_MODEL.head.render(matrixStack, vertexBuilder, combinedLight, combinedOverlay, 1, 1, 1, alpha);

        matrixStack.popPose();
    }

    public void drawString(MatrixStack matrixStack, Direction facing, String text, float yOffset, double x, double y, double z, float scale) {
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        float size = (float) fontRenderer.width(text) * scale;
        float textCenter = (1.0f + size) / 2.0f;

        matrixStack.pushPose();

        if (facing == Direction.NORTH) {
            matrixStack.translate(textCenter, yOffset, 6.0f / 16.0f - 0.4f);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
        } else if (facing == Direction.SOUTH) {
            matrixStack.translate(-textCenter + 1, yOffset, (16.0f - 6.0f) / 16.0f + 0.4f);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180));
        } else if (facing == Direction.EAST) {
            matrixStack.translate((16.0f - 6.0f) / 16.0f + 0.4f, yOffset, textCenter);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(90));
        } else if (facing == Direction.WEST) {
            matrixStack.translate(6.0f / 16.0f - 0.4f, yOffset, -textCenter + 1);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(270));
        }

        matrixStack.translate(0, 0, 0.5f / 16f);
        matrixStack.scale(scale, scale, scale);

//        this.setLightmapDisabled(true);
        fontRenderer.draw(matrixStack, text, 0, 0, 0xFF_FFFFFF);
//        this.setLightmapDisabled(false);

        matrixStack.popPose();
    }

    private int getLightAtPos(World world, BlockPos pos) {
        int blockLight = world.getBrightness(LightType.BLOCK, pos);
        int skyLight = world.getBrightness(LightType.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }

}

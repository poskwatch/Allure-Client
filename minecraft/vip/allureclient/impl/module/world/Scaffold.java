package vip.allureclient.impl.module.world;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;
import vip.allureclient.AllureClient;
import vip.allureclient.base.event.EventConsumer;
import vip.allureclient.base.event.EventListener;
import vip.allureclient.base.font.MinecraftFontRenderer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.module.annotations.ModuleData;
import vip.allureclient.base.util.client.TimerUtil;
import vip.allureclient.base.util.client.Wrapper;
import vip.allureclient.base.util.math.MathUtil;
import vip.allureclient.base.util.player.IRotations;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.visual.AnimationUtil;
import vip.allureclient.base.util.visual.GLUtil;
import vip.allureclient.base.util.world.BlockData;
import vip.allureclient.impl.event.player.UpdatePositionEvent;
import vip.allureclient.impl.event.visual.Render2DEvent;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.MultiSelectEnumProperty;
import vip.allureclient.impl.property.ValueProperty;

import java.awt.*;

@ModuleData(moduleName = "Scaffold", moduleBind = 0, moduleCategory = ModuleCategory.WORLD)
public class Scaffold extends Module implements IRotations {

    private final MultiSelectEnumProperty<Addons> addonsProperty = new MultiSelectEnumProperty<>("Addons", this,
            Addons.No_Sprint, Addons.Spoof_Swing);

    private final ValueProperty<Float> timerBoostProperty = new ValueProperty<>("Scaffold Timer Boost", 1.0F, 1.0F, 2.0F, this);

    private final EnumProperty<TowerMode> towerModeProperty = new EnumProperty<TowerMode>("Tower Mode", TowerMode.Watchdog, this) {
        @Override
        public boolean isPropertyHidden() {
            return !addonsProperty.isSelected(Addons.Tower);
        }
    };

    private final EnumProperty<DisplayType> displayTypeProperty = new EnumProperty<>("Display Type", DisplayType.Text, this);

    private static final BlockPos[] BLOCK_POSITIONS = new BlockPos[] { new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1) };
    private static final EnumFacing[] FACINGS = new EnumFacing[] { EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH };

    private final TimerUtil clickTimer = new TimerUtil();
    private int originalHotBarSlot;
    private int blockCount;
    private int bestBlockStack;
    private BlockData data;
    private float[] angles;

    private double blockBarAnimation;

    @EventListener
    EventConsumer<UpdatePositionEvent> onUpdatePositionEvent;

    @EventListener
    EventConsumer<Render2DEvent> onRender2DEvent;

    public Scaffold() {
        this.onUpdatePositionEvent = (event -> {
            if (event.isPre()) {
                updateBlockCount();
                if (addonsProperty.isSelected(Addons.No_Sprint)) {
                    Wrapper.getPlayer().setSprinting(false);
                    MovementUtil.setSpeed(0.06);
                }
                if (MovementUtil.isMoving())
                    Wrapper.getMinecraft().timer.timerSpeed = timerBoostProperty.getPropertyValue();
                this.data = null;
                this.bestBlockStack = findBestBlockStack();
                if (this.bestBlockStack != -1) {
                    if (this.bestBlockStack < 36 && this.clickTimer.hasReached(250L)) {
                        for (int i = 44; i >= 36; i--) {
                            ItemStack stack = Wrapper.getPlayer().inventoryContainer.getSlot(i).getStack();
                            if (stack != null && stack.stackSize > 1 && stack.getItem() instanceof ItemBlock) {
                                Wrapper.getMinecraft().playerController.windowClick(Wrapper.getPlayer().inventoryContainer.windowId, i - 36, i - 36, 2, Wrapper.getPlayer());
                                this.bestBlockStack = i;
                                break;
                            }
                        }
                    }
                    BlockPos blockUnder = MovementUtil.getBlockUnder();
                    BlockData data = getBlockData(blockUnder);
                    if (data == null) {
                        data = getBlockData(blockUnder.add(0, -1, 0));
                    }
                    if (data != null && this.bestBlockStack >= 36) {
                        if (validateReplaceable(data) && data.hitVec != null) {
                            this.angles = getRotations();
                        } else
                            data = null;
                    }
                    if (this.angles != null) {
                        setRotations(event, angles, true);
                    }
                    this.data = data;
                }
        } else if (this.data != null && this.bestBlockStack != -1 && this.bestBlockStack >= 36) {
                int hotBarSlot = this.bestBlockStack - 36;
                if (Wrapper.getPlayer().inventory.currentItem != hotBarSlot) {
                    if (addonsProperty.isSelected(Addons.Spoof_Switch))
                        Wrapper.sendPacketDirect(new C09PacketHeldItemChange(hotBarSlot));
                    else
                        Wrapper.getPlayer().inventory.currentItem = hotBarSlot;
                }
                if (Wrapper.getMinecraft().playerController.onPlayerRightClick(Wrapper.getPlayer(), Wrapper.getWorld(), Wrapper.getPlayer().inventory.getStackInSlot(hotBarSlot), this.data.pos, this.data.face, this.data.hitVec)) {
                    if (addonsProperty.isSelected(Addons.Spoof_Swing))
                        Wrapper.sendPacketDirect(new C0APacketAnimation());
                    else
                        Wrapper.getPlayer().swingItem();
                    if (addonsProperty.isSelected(Addons.Tower)) {
                        if (Wrapper.getMinecraft().gameSettings.keyBindJump.isKeyDown() &&
                            Wrapper.getWorld().checkBlockCollision(Wrapper.getPlayer().getEntityBoundingBox().addCoord(0, -0.0626D, 0)))
                        switch (towerModeProperty.getPropertyValue()) {
                            case NCP:
                                Wrapper.getPlayer().motionY = MovementUtil.getJumpHeight() - 4.54352838557992E-4D;
                                break;
                            case Watchdog:
                                if (Wrapper.getPlayer().ticksExisted % 2 != 0)
                                    Wrapper.getPlayer().jump();
                                break;
                        }
                    }
                }
            }
        });
        this.onRender2DEvent = (event -> {
            final float x = event.getScaledResolution().getScaledWidth()/2.0F;
            final float y = event.getScaledResolution().getScaledHeight()/2.0F + 50;
            switch (displayTypeProperty.getPropertyValue()) {
                case Text:
                    final MinecraftFontRenderer textRenderer = AllureClient.getInstance().getFontManager().mediumFontRenderer;
                    final String text = String.format("%d Blocks", blockCount);
                    GLUtil.glFilledQuad(x - textRenderer.getStringWidth(text)/2 - 3, y- 5, textRenderer.getStringWidth(text) + 6, 16, 0x90000000);
                    textRenderer.drawCenteredStringWithShadow(text, x, y, -1);
                    break;
                case Bar:
                    blockBarAnimation = AnimationUtil.linearAnimation(98 * (Math.min(blockCount / 128.0F, 1.0F)), blockBarAnimation, 1);
                    GLUtil.glFilledQuad(x - 50, y, 100, 8, 0x90000000);
                    GL11.glPushMatrix();
                    GL11.glEnable(GL11.GL_SCISSOR_TEST);
                    GLUtil.glScissor(x - 49, y + 1, blockBarAnimation, 6);
                    GLUtil.glHorizontalGradientQuad(x - 49, y + 1, 98, 6, Color.GREEN.getRGB(), Color.MAGENTA.getRGB());
                    GL11.glDisable(GL11.GL_SCISSOR_TEST);
                    GL11.glPopMatrix();
                    break;
            }

        });
    }

    @Override
    public void onEnable() {
        this.originalHotBarSlot = Wrapper.getPlayer().inventory.currentItem;
        super.onEnable();
    }

    @Override
    public void onDisable() {
        this.angles = null;
        Wrapper.getMinecraft().timer.timerSpeed = 1.0F;
        Wrapper.getPlayer().inventory.currentItem = originalHotBarSlot;
        Wrapper.sendPacketDirect(new C09PacketHeldItemChange(originalHotBarSlot));
        super.onDisable();
    }

    @Override
    public float[] getRotations() {
        final float originalYaw = MovementUtil.getMovementDirection() - 180.0F;
        return new float[]{(float) MathUtil.getRandomNumber(originalYaw - 45, originalYaw + 45), 75.0F};
    }

    @Override
    public void setRotations(UpdatePositionEvent event, float[] rotations, boolean visualize) {
        event.setYaw(rotations[0], visualize);
        event.setPitch(rotations[1], visualize);
    }

    private static int findBestBlockStack() {
        int bestSlot = -1;
        int blockCount = -1;
        for (int i = 44; i >= 9; i--) {
            ItemStack stack = Wrapper.getPlayer().inventoryContainer.getSlot(i).getStack();
            if (stack != null &&
                    stack.getItem() instanceof net.minecraft.item.ItemBlock &&
                    stack.stackSize > 1 && !(Block.getBlockFromItem(stack.getItem()) instanceof BlockContainer) &&
                    Block.getBlockFromItem(stack.getItem()).isFullBlock() && Block.getBlockFromItem(stack.getItem()).isFullCube() &&
                    !Block.getBlockFromItem(stack.getItem()).getMaterial().isLiquid() && !Block.getBlockFromItem(stack.getItem()).getMaterial().isReplaceable() &&
                    !(Block.getBlockFromItem(stack.getItem()) instanceof BlockFalling) &&
                    stack.stackSize > blockCount) {
                bestSlot = i;
                blockCount = stack.stackSize;
            }
        }
        return bestSlot;
    }

    private static boolean validateReplaceable(BlockData data) {
        return Wrapper.getWorld().getBlockState(data.pos.offset(data.face)).getBlock().isReplaceable(Wrapper.getWorld(), data.pos.offset(data.face));
    }

    private static boolean validateBlockRange(BlockData data) {
        if (data.hitVec == null)
            return false;
        double x = data.hitVec.xCoord - Wrapper.getPlayer().posX;
        double y = data.hitVec.yCoord - Wrapper.getPlayer().posY + Wrapper.getPlayer().getEyeHeight();
        double z = data.hitVec.zCoord - Wrapper.getPlayer().posZ;
        return (StrictMath.sqrt(x * x + y * y + z * z) <= 4.0D);
    }

    private BlockData getBlockData(BlockPos pos) {
        BlockPos[] blockPositions = BLOCK_POSITIONS;
        EnumFacing[] facings = FACINGS;
        WorldClient world = Wrapper.getWorld();
        for (int i = 0; i < blockPositions.length; i++) {
            BlockPos blockPos = pos.add(blockPositions[i]);
            if (isValidBlock(world.getBlockState(blockPos).getBlock())) {
                BlockData data = new BlockData(blockPos, facings[i]);
                if (validateBlockRange(data)) {
                    return data;
                }
            }
        }
        BlockPos posBelow = pos.add(0, -1, 0);
        if (isValidBlock(world.getBlockState(posBelow).getBlock())) {
            BlockData data = new BlockData(posBelow, EnumFacing.UP);
            if (validateBlockRange(data))
                return data;
        }  byte b;
        int j;
        BlockPos[] arrayOfBlockPos1;
        for (j = (arrayOfBlockPos1 = blockPositions).length, b = 0; b < j; ) { BlockPos blockPosition = arrayOfBlockPos1[b];
            BlockPos blockPos = pos.add(blockPosition);
            for (int k = 0; k < blockPositions.length; k++) {
                BlockPos blockPos1 = blockPos.add(blockPositions[k]);
                if (isValidBlock(world.getBlockState(blockPos1).getBlock())) {
                    BlockData data = new BlockData(blockPos1, facings[k]);
                    if (validateBlockRange(data)) {
                        return data;
                    }
                }
            }
            b++;
        }
        for (j = (arrayOfBlockPos1 = blockPositions).length, b = 0; b < j; ) { BlockPos blockPosition = arrayOfBlockPos1[b];
            BlockPos blockPos = pos.add(blockPosition); byte b1; int k; BlockPos[] arrayOfBlockPos;
            for (k = (arrayOfBlockPos = blockPositions).length, b1 = 0; b1 < k; ) { BlockPos position = arrayOfBlockPos[b1];
                BlockPos blockPos1 = blockPos.add(position);
                for (int m = 0; m < blockPositions.length; m++) {
                    BlockPos blockPos2 = blockPos1.add(blockPositions[m]);
                    if (isValidBlock(world.getBlockState(blockPos2).getBlock())) {
                        BlockData data = new BlockData(blockPos2, facings[m]);
                        if (validateBlockRange(data))
                            return data;
                    }
                }
                b1++;
            }
            b++;
        }
        return null;
    }

    private boolean isValidBlock(Block block) {
        if (block instanceof net.minecraft.block.BlockContainer)
            return false;
        return (!(block instanceof net.minecraft.block.BlockFalling) && block.isFullBlock() && block.isFullCube());
    }

    private void updateBlockCount() {
        this.blockCount = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = Wrapper.getPlayer().inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemBlock &&
                    isValidBlock(Block.getBlockFromItem(stack.getItem())))
                this.blockCount += stack.stackSize;
        }
    }

    private enum Addons {
        Spoof_Swing,
        Spoof_Switch,
        No_Sprint,
        Tower
    }

    private enum TowerMode {
        NCP,
        Watchdog
    }

    private enum DisplayType {
        None,
        Text,
        Bar
    }
}

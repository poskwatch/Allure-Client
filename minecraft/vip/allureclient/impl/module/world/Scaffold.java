package vip.allureclient.impl.module.world;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFalling;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import vip.allureclient.AllureClient;
import vip.allureclient.base.font.MinecraftFontRenderer;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Stopwatch;
import vip.allureclient.base.util.player.IRotations;
import vip.allureclient.base.util.player.MovementUtil;
import vip.allureclient.base.util.player.PacketUtil;
import vip.allureclient.base.util.visual.ColorUtil;
import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.base.util.world.BlockData;
import vip.allureclient.impl.event.events.player.BlockCollisionEvent;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.event.events.visual.Render2DEvent;
import vip.allureclient.impl.property.EnumProperty;
import vip.allureclient.impl.property.MultiSelectEnumProperty;
import vip.allureclient.impl.property.ValueProperty;
import vip.allureclient.visual.notification.NotificationType;

public class Scaffold extends Module implements IRotations {

    private final MultiSelectEnumProperty<ScaffoldAddons> addonsProperty = new MultiSelectEnumProperty<>("Addons", this,
            ScaffoldAddons.NO_SPRINT, ScaffoldAddons.SPOOF_SWING);

    private final ValueProperty<Float> timerBoostProperty = new ValueProperty<>("Scaffold Timer Boost", 1.0F, 1.0F, 2.0F, this);

    private final EnumProperty<TowerMode> towerModeProperty = new EnumProperty<TowerMode>("Tower Mode", TowerMode.WATCHDOG, this) {
        @Override
        public boolean isPropertyHidden() {
            return !addonsProperty.isSelected(ScaffoldAddons.TOWER);
        }
    };

    private static final BlockPos[] BLOCK_POSITIONS = new BlockPos[] { new BlockPos(-1, 0, 0), new BlockPos(1, 0, 0), new BlockPos(0, 0, -1), new BlockPos(0, 0, 1) };
    private static final EnumFacing[] FACINGS = new EnumFacing[] { EnumFacing.EAST, EnumFacing.WEST, EnumFacing.SOUTH, EnumFacing.NORTH };

    private final Stopwatch clickTimer = new Stopwatch();
    private int originalHotBarSlot;
    private int blockCount;
    private int bestBlockStack;
    private BlockData data;
    private float[] angles;

    private double blockBarAnimation;

    public Scaffold() {
        super("Scaffold", ModuleCategory.WORLD);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class, priority = Priority.HIGH)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = (event -> {
                if (event.isPre()) {
                    setModuleSuffix("Watchdog");
                    updateBlockCount();
                    if (addonsProperty.isSelected(ScaffoldAddons.NO_SPRINT)) {
                        mc.thePlayer.setSprinting(false);
                        MovementUtil.setSpeed(MovementUtil.getBaseMoveSpeed() *
                                (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 0.2 : 0.423));
                    }
                    if (MovementUtil.isMoving())
                        mc.timer.timerSpeed = timerBoostProperty.getPropertyValue();
                    data = null;
                    bestBlockStack = findBestBlockStack();
                    if (bestBlockStack != -1) {
                        if (bestBlockStack < 36 && clickTimer.hasReached(250L)) {
                            for (int i = 44; i >= 36; i--) {
                                ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                                if (stack != null && stack.stackSize > 1 && stack.getItem() instanceof ItemBlock && isValidBlock(((ItemBlock) stack.getItem()).getBlock())) {
                                    mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId, bestBlockStack, i - 36, 2, mc.thePlayer);
                                    bestBlockStack = i;
                                    break;
                                }
                            }
                        }
                        BlockPos blockUnder = MovementUtil.getBlockUnder();
                        BlockData data = getBlockData(blockUnder);
                        if (data == null) {
                            data = getBlockData(blockUnder.add(0, -1, 0));
                        }
                        if (data != null && bestBlockStack >= 36) {
                            if (validateReplaceable(data) && data.hitVec != null) {
                                getInstance().angles = getRotations();
                            } else
                                data = null;
                        }
                        if (getInstance().angles != null) {
                            setRotations(event, angles, true);
                        }
                        getInstance().data = data;
                    }
                } else if (data != null && bestBlockStack != -1 && bestBlockStack >= 36) {
                    int hotBarSlot = bestBlockStack - 36;
                    if (mc.thePlayer.inventory.currentItem != hotBarSlot) {
                        if (addonsProperty.isSelected(ScaffoldAddons.SPOOF_SWITCH))
                            PacketUtil.sendPacketDirect(new C09PacketHeldItemChange(hotBarSlot));
                        else
                            mc.thePlayer.inventory.currentItem = hotBarSlot;
                    }
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(hotBarSlot), data.pos, data.face, data.hitVec)) {
                        if (addonsProperty.isSelected(ScaffoldAddons.SPOOF_SWING))
                            PacketUtil.sendPacketDirect(new C0APacketAnimation());
                        else
                            mc.thePlayer.swingItem();
                        if (addonsProperty.isSelected(ScaffoldAddons.TOWER)) {
                            if (mc.gameSettings.keyBindJump.isKeyDown() &&
                                    mc.theWorld.checkBlockCollision(mc.thePlayer.getEntityBoundingBox().addCoord(0, -0.0626D, 0))
                                    && !MovementUtil.isMoving())
                                switch (towerModeProperty.getPropertyValue()) {
                                    case NCP:
                                        mc.thePlayer.motionY = MovementUtil.getJumpHeight() - 4.54352838557992E-4D;
                                        break;
                                    case WATCHDOG:
                                        mc.thePlayer.motionX = 0;
                                        mc.thePlayer.motionZ = 0;
                                        if (mc.thePlayer.ticksExisted % 2 != 0)
                                            mc.thePlayer.jump();
                                        break;
                                }
                        }
                    }
                }
            });
            @EventHandler(events = Render2DEvent.class, priority = Priority.VERY_LOW)
            final IEventCallable<Render2DEvent> onRender2D = (event -> {
                final float x = event.getScaledResolution().getScaledWidth()/2.0F;
                final float y = event.getScaledResolution().getScaledHeight()/2.0F + 50;
                final MinecraftFontRenderer textRenderer = AllureClient.getInstance().getFontManager().mediumFontRenderer;
                final String text = String.format("%d Blocks", blockCount);
                GLUtil.glOutlinedFilledQuad(x - textRenderer.getStringWidth(text)/2 - 3, y- 5, textRenderer.getStringWidth(text) + 6, 16,
                        0x90000000, ColorUtil.getClientColor(ColorUtil.ClientColor.PRIMARY).getRGB());
                textRenderer.drawCenteredString(text, x, y - 1, ColorUtil.getClientColor(ColorUtil.ClientColor.PRIMARY).getRGB());
            });
            @EventHandler(events = BlockCollisionEvent.class, priority = Priority.VERY_LOW)
            final IEventCallable<BlockCollisionEvent> onBlockCollision = (event -> {
                if (event.getBlock() instanceof BlockAir && !isOnEdge(2)) {
                    if (mc.thePlayer.isSneaking())
                        return;
                    double x = event.getBlockPos().getX();
                    double y = event.getBlockPos().getY();
                    double z = event.getBlockPos().getZ();
                    if (y < mc.thePlayer.posY) {
                     //   event.setBoundingBox(AxisAlignedBB.fromBounds(-5, -1, -5, 5, 1.0F, 5).offset(x, y, z));
                    }
                }
            });
        });
    }

    @Override
    public void onEnable() {
        this.originalHotBarSlot = mc.thePlayer.inventory.currentItem;
        super.onEnable();
        final Module speedModuleInstance = AllureClient.getInstance().getModuleManager().getModuleOrNull("Speed");
        if (speedModuleInstance.isToggled()) {
            speedModuleInstance.setToggled(false);
            AllureClient.getInstance().getNotificationManager().addNotification("Module Toggled",
                    "Speed was automatically toggled to prevent lag-back(s)", 1500, NotificationType.WARNING);
        }
    }

    @Override
    public void onDisable() {
        this.angles = null;
        mc.timer.timerSpeed = 1.0F;
        mc.thePlayer.inventory.currentItem = originalHotBarSlot;
        PacketUtil.sendPacketDirect(new C09PacketHeldItemChange(originalHotBarSlot));
        super.onDisable();
    }

    @Override
    public float[] getRotations() {
        final float originalYaw = MovementUtil.getMovementDirection() + 180.0F;
        return new float[]{originalYaw, 80.0F};
    }

    @Override
    public void setRotations(UpdatePositionEvent event, float[] rotations, boolean visualize) {
        event.setYaw(rotations[0], visualize);
        event.setPitch(rotations[1], visualize);
    }

    private int findBestBlockStack() {
        int bestSlot = -1;
        int blockCount = -1;
        for (int i = 44; i >= 9; i--) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
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

    private boolean validateReplaceable(BlockData data) {
        return mc.theWorld.getBlockState(data.pos.offset(data.face)).getBlock().isReplaceable(mc.theWorld, data.pos.offset(data.face));
    }

    private boolean validateBlockRange(BlockData data) {
        if (data.hitVec == null)
            return false;
        double x = data.hitVec.xCoord - mc.thePlayer.posX;
        double y = data.hitVec.yCoord - mc.thePlayer.posY + mc.thePlayer.getEyeHeight();
        double z = data.hitVec.zCoord - mc.thePlayer.posZ;
        return (StrictMath.sqrt(x * x + y * y + z * z) <= 4.0D);
    }

    private BlockData getBlockData(BlockPos pos) {
        BlockPos[] blockPositions = BLOCK_POSITIONS;
        EnumFacing[] facings = FACINGS;
        WorldClient world = mc.theWorld;
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

    private boolean isOnEdge(final double verbose) {
        double[] verboseArray = new double[]{0, verbose, -verbose};
        for (double x : verboseArray) {
            for (double z : verboseArray) {
                final BlockPos belowBlockPos = new BlockPos(mc.thePlayer.posX + x, Math.floor(mc.thePlayer.posY - 1), mc.thePlayer.posZ + z);
                if (!(mc.theWorld.getBlockState(belowBlockPos).getBlock() instanceof BlockAir))
                    return false;
            }
        }
        return true;
    }

    private void updateBlockCount() {
        this.blockCount = 0;
        for (int i = 9; i < 45; i++) {
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            if (stack != null && stack.getItem() instanceof net.minecraft.item.ItemBlock &&
                    isValidBlock(Block.getBlockFromItem(stack.getItem())))
                this.blockCount += stack.stackSize;
        }
    }

    private enum ScaffoldAddons {
        SPOOF_SWING("Spoof Swing"),
        SPOOF_SWITCH("Spoof Switch"),
        NO_SPRINT("No Sprint"),
        TOWER("Tower");

        private final String name;

        ScaffoldAddons(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private enum TowerMode {
        NCP("NCP"),
        WATCHDOG("Watchdog");

        private final String name;

        TowerMode(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    public static Scaffold getInstance() {
        return (Scaffold) AllureClient.getInstance().getModuleManager().getModuleOrNull("Scaffold");
    }
}

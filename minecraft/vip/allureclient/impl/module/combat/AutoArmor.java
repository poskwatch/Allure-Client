package vip.allureclient.impl.module.combat;

import io.github.poskwatch.eventbus.api.annotations.EventHandler;
import io.github.poskwatch.eventbus.api.enums.Priority;
import io.github.poskwatch.eventbus.api.interfaces.IEventCallable;
import io.github.poskwatch.eventbus.api.interfaces.IEventListener;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import vip.allureclient.base.module.Module;
import vip.allureclient.base.module.enums.ModuleCategory;
import vip.allureclient.base.util.client.Stopwatch;
import vip.allureclient.impl.event.events.player.UpdatePositionEvent;
import vip.allureclient.impl.property.BooleanProperty;
import vip.allureclient.impl.property.ValueProperty;

public class AutoArmor extends Module {

    private final ValueProperty<Long> equipDelay = new ValueProperty<>("Equip Delay", 150L, 10L, 1000L, this);

    private final BooleanProperty onlyInventory = new BooleanProperty("Only Inventory", true, this);

    private final Stopwatch equipStopwatch = new Stopwatch();

    public AutoArmor() {
        super("Auto Armor", ModuleCategory.COMBAT);
        this.setListener(new IEventListener() {
            @EventHandler(events = UpdatePositionEvent.class, priority = Priority.VERY_HIGH)
            final IEventCallable<UpdatePositionEvent> onUpdatePosition = event -> {
                if (onlyInventory.getPropertyValue() && !(mc.currentScreen instanceof GuiInventory))
                    return;
                for (int i = 9; i < 45; i++) {
                    final ItemStack currentStack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
                    if (currentStack != null) {
                        if (currentStack.getItem() instanceof ItemArmor) {
                            if (canEquip(getArmorPiece(currentStack)) && equipStopwatch.hasReached(equipDelay.getPropertyValue())) {
                                mc.playerController.windowClick(mc.thePlayer.inventoryContainer.windowId,
                                        i, 0, 1, mc.thePlayer);
                                equipStopwatch.reset();
                            }
                        }

                    }
                }
            };
        });
    }

    private ArmorPiece getArmorPiece(ItemStack stack) {
        if (stack.getItem().getUnlocalizedName().contains("helmet"))
            return ArmorPiece.HELMET;
        if (stack.getItem().getUnlocalizedName().contains("chestplate"))
            return ArmorPiece.CHEST_PLATE;
        if (stack.getItem().getUnlocalizedName().contains("leggings"))
            return ArmorPiece.LEGGINGS;
        if (stack.getItem().getUnlocalizedName().contains("boots"))
            return ArmorPiece.BOOTS;
        return null;
    }

    private boolean canEquip(ArmorPiece piece) {
        for (int i = 4; i > 0; i--) {
            if (mc.thePlayer.getEquipmentInSlot(i) == null && piece.equipmentIndex == i)
                return true;
        }
        return false;
    }

    private enum ArmorPiece {
        HELMET(4),
        CHEST_PLATE(3),
        LEGGINGS(2),
        BOOTS(1);

        final int equipmentIndex;

        ArmorPiece(final int equipmentIndex) {
            this.equipmentIndex = equipmentIndex;
        }
    }
}

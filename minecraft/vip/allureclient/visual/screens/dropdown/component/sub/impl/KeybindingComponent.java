package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.ModuleComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeybindingComponent extends Component {

    private final Supplier<Integer> keyBindSupplier;
    private final Consumer<Integer> keyBindConsumer;
    private final Runnable unbindRunnable;

    private boolean active;

    private final ModuleComponent parent;

    private int offset;

    public KeybindingComponent(Supplier<Integer> keyBindSupplier,
                               Consumer<Integer> keyBindConsumer,
                               Runnable unbindRunnable, ModuleComponent parent, int offset) {
        this.keyBindSupplier = keyBindSupplier;
        this.keyBindConsumer = keyBindConsumer;
        this.unbindRunnable = unbindRunnable;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, isHoveringComponent(mouseX, mouseY) ? 0xff050505 : 0xff151515);

        getFontRenderer().drawStringWithShadow("Key-bind", x + 3, y + 4, -1);

        final String state = (active ? "\2477[...]" : "\2477[" + Keyboard.getKeyName(keyBindSupplier.get()) + "]");

        getFontRenderer().drawStringWithShadow(state, x + 114 - getFontRenderer().getStringWidth(state), y + 4, -1);

        super.onDrawScreen(mouseX, mouseY);
    }

    @Override
    public void onKeyTyped(int typedKey) {
        if (active) {
            if (typedKey != Keyboard.KEY_DELETE) {
                keyBindConsumer.accept(typedKey);
            }
            else
                unbindRunnable.run();
            active = false;
        }
        super.onKeyTyped(typedKey);
    }

    @Override
    public void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        active = isHoveringComponent(mouseX, mouseY);
    }

    private boolean isHoveringComponent(int mouseX, int mouseY) {
        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;
        return mouseX >= x && mouseY >= y && mouseX <= x + 115 && mouseY <= y + 14;
    }

    @Override
    public void onGuiClosed() {
        if (active)
            keyBindConsumer.accept(0);
        active = false;
    }

    @Override
    public void setOffset(int offset) {
        this.offset = offset;
    }
}

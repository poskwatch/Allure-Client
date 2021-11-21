package vip.allureclient.visual.screens.dropdown.component.sub.impl;

import net.minecraft.client.gui.Gui;
import vip.allureclient.visual.screens.dropdown.component.Component;
import vip.allureclient.visual.screens.dropdown.component.sub.CheatButtonComponent;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeybindingComponent extends Component {

    private final Supplier<Integer> keyBindSupplier;

    private final Consumer<Integer> keyBindConsumer;

    private boolean active;

    private final CheatButtonComponent parent;

    private int offset;

    public KeybindingComponent(Supplier<Integer> keyBindSupplier, Consumer<Integer> keyBindConsumer, CheatButtonComponent parent, int offset) {
        this.keyBindSupplier = keyBindSupplier;
        this.keyBindConsumer = keyBindConsumer;
        this.parent = parent;
        this.offset = offset;
    }

    @Override
    public void onDrawScreen(int mouseX, int mouseY) {

        double x = parent.getParentFrame().getX();
        double y = parent.getParentFrame().getY() + offset;

        Gui.drawRectWithWidth(x, y, 115, 14, 0xff151515);

        getFontRenderer().drawStringWithShadow("Keybind", x + 3, y + 4, -1);

        super.onDrawScreen(mouseX, mouseY);
    }
}

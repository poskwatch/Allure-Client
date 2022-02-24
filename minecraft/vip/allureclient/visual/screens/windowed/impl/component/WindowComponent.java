package vip.allureclient.visual.screens.windowed.impl.component;

import vip.allureclient.base.util.visual.glsl.GLUtil;
import vip.allureclient.visual.screens.windowed.api.PeripheralEncapsulation;
import vip.allureclient.visual.screens.windowed.api.interfaces.IComponent;

import java.util.function.Consumer;

public class WindowComponent implements IComponent {

    private int x, y, distX, distY;
    private boolean dragging;

    @Override
    public Consumer<PeripheralEncapsulation> onDrawScreen() {
        return (peripheralEncapsulation -> {
            final int mouseX = peripheralEncapsulation.getMouseX();
            final int mouseY = peripheralEncapsulation.getMouseY();

            // Window
            GLUtil.drawFilledRectangle(this.x, this.y, 450, 300, 0xFF202020);
            // Dragging bar
            GLUtil.drawFilledRectangle(this.x, this.y, 450, 5, 0xFF101010);

            if (this.dragging) {
                this.x = mouseX - distX;
                this.y = mouseY - distY;
            }
        });
    }

    @Override
    public Consumer<PeripheralEncapsulation> onMouseClicked() {
        return (peripheralEncapsulation -> {
            final int mouseX = peripheralEncapsulation.getMouseX();
            final int mouseY = peripheralEncapsulation.getMouseY();

            if (isMouseHovering(mouseX, mouseY)) {
                this.dragging = true;
                this.distX = mouseX - x;
                this.distY = mouseY - y;
            }
        });
    }

    @Override
    public Consumer<PeripheralEncapsulation> onMouseReleased() {
        return (peripheralEncapsulation -> {
            this.dragging = false;
        });
    }

    @Override
    public Consumer<PeripheralEncapsulation> onKeyTyped() {
        return (peripheralEncapsulation -> {

        });
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return 450;
    }

    @Override
    public int getHeight() {
        return 300;
    }
}

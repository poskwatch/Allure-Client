package vip.allureclient.visual.screens.windowed.api.interfaces;

import vip.allureclient.visual.screens.windowed.api.PeripheralEncapsulation;

import java.util.function.Consumer;

public interface IComponent {

    // Draw screen method
    Consumer<PeripheralEncapsulation> onDrawScreen();

    // Mouse Clicked method
    Consumer<PeripheralEncapsulation> onMouseClicked();

    // Mouse Released method
    Consumer<PeripheralEncapsulation> onMouseReleased();

    // Key typed method
    Consumer<PeripheralEncapsulation> onKeyTyped();

    // Component's X coordinate
    int getX();

    // Component's Y coordinate

    int getY();

    // Component's width
    int getWidth();

    // Component's height
    int getHeight();

    // Method to determine if hovering, by default uses pos and dimensions
    default boolean isMouseHovering(int mouseX, int mouseY) {
        return mouseX >= this.getX()
                && mouseX <= this.getX() + this.getWidth()
                && mouseY >= this.getY()
                && mouseY <= this.getY() + this.getHeight();
    }

}

package vip.allureclient.visual.screens.windowed.api;

public class PeripheralEncapsulation {

    // Encapsulate GUI parameters like mouseX, mouseY, buttons, keys, etc.

    // Mouse coordinate variables
    private int mouseX, mouseY;

    // Mouse button variable
    private int mouseButton;

    // Key pressed variable
    private int keyPressed;

    // Multiple constructors

    // Designed for drawScreen

    public PeripheralEncapsulation(final int mouseX, final int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    // Designed for mouseClicked/mouseReleased

    public PeripheralEncapsulation(final int mouseX, final int mouseY, final int mouseButton) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.mouseButton = mouseButton;
    }

    // Designed for keyTyped
    public PeripheralEncapsulation(final int keyPressed) {
        this.keyPressed = keyPressed;
    }

    // Getters & Setters

    public int getMouseX() {
        return mouseX;
    }

    public void setMouseX(int mouseX) {
        this.mouseX = mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public void setMouseY(int mouseY) {
        this.mouseY = mouseY;
    }

    public int getMouseButton() {
        return mouseButton;
    }

    public void setMouseButton(int mouseButton) {
        this.mouseButton = mouseButton;
    }

    public int getKeyPressed() {
        return keyPressed;
    }

    public void setKeyPressed(int keyPressed) {
        this.keyPressed = keyPressed;
    }
}

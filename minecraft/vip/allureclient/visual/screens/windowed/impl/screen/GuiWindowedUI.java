package vip.allureclient.visual.screens.windowed.impl.screen;

import net.minecraft.client.gui.GuiScreen;
import vip.allureclient.visual.screens.windowed.api.PeripheralEncapsulation;
import vip.allureclient.visual.screens.windowed.api.interfaces.IComponent;
import vip.allureclient.visual.screens.windowed.impl.component.WindowComponent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class GuiWindowedUI extends GuiScreen {

    private final Collection<IComponent> components = new ArrayList<>();

    public GuiWindowedUI() {
        this.components.add(new WindowComponent());
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        final PeripheralEncapsulation drawScreenEncapsulation = new PeripheralEncapsulation(mouseX, mouseY);
        this.components.forEach(component -> component.onDrawScreen().accept(drawScreenEncapsulation));
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        final PeripheralEncapsulation mouseClickedEncapsulation = new PeripheralEncapsulation(mouseX, mouseY, mouseButton);
        this.components.forEach(component -> component.onMouseClicked().accept(mouseClickedEncapsulation));
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        final PeripheralEncapsulation mouseReleasedEncapsulation = new PeripheralEncapsulation(mouseX, mouseY, state);
        this.components.forEach(component -> component.onMouseReleased().accept(mouseReleasedEncapsulation));
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        final PeripheralEncapsulation keyTypedEncapsulation = new PeripheralEncapsulation(keyCode);
        this.components.forEach(component -> component.onKeyTyped().accept(keyTypedEncapsulation));
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}

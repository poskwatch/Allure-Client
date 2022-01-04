package vip.allureclient.visual.console;

import java.awt.*;
import java.util.ArrayList;

public class ConsoleManager {

    private final ArrayList<ConsoleLine> consoleLines;

    public ConsoleManager() {
        this.consoleLines = new ArrayList<>();
    }

    public void render() {
        ConsoleLine dead = null;
        for (int i = 0; i < consoleLines.size(); i++) {
            ConsoleLine currentLine = consoleLines.get(i);
            if (currentLine.isDead())
                dead = currentLine;
            currentLine.render(i);
        }
        if (dead != null)
            consoleLines.remove(dead);
    }

    public void addLine(String lineMessage, Color color) {
        consoleLines.add(new ConsoleLine(lineMessage, color));
    }
}

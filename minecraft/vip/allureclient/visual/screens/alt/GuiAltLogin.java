package vip.allureclient.visual.screens.alt;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.util.Session;
import vip.allureclient.AllureClient;
import vip.allureclient.visual.notification.NotificationType;

import java.io.IOException;
import java.net.Proxy;

public class GuiAltLogin extends GuiScreen {

    private GuiTextField emailField;
    private GuiTextField passwordField;
    private String status = "Idle...";

    public void keyTyped(char character, int key){
        emailField.textboxKeyTyped(character, key);
        passwordField.textboxKeyTyped(character, key);
    }

    public void initGui(){
        emailField = new GuiTextField(height / 4 + 24, Minecraft.getMinecraft().fontRendererObj, width / 2 - 100, 60, 200, 20);
        passwordField = new GuiTextField(height / 4 + 44, this.mc.fontRendererObj, width / 2 - 100, 100, 200, 20);
        ScaledResolution sr = new ScaledResolution(mc);
        int var3 = height / 4 + 24;
        buttonList.add(new GuiButton(1, sr.getScaledWidth()/2 - 100, var3 + 72 - 12, "Import email:pass"));
        buttonList.add(new GuiButton(2, sr.getScaledWidth()/2 - 100, var3 + 72 + 12, "Login"));
        buttonList.add(new GuiButton(3, sr.getScaledWidth()/2 - 100, var3 + 72 + 12 + 24, "Back to menu"));
    }


    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        AllureClient.getInstance().getNotificationManager().render();
        emailField.drawTextBox();
        passwordField.drawTextBox();
        Minecraft.getMinecraft().fontRendererObj.drawStringWithShadow(status, (float) (width/2.0 - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(status) / 2)), 25, -1);
        if (emailField.getText().length() == 0) {
            mc.fontRendererObj.drawString("\2477Username / Email", width / 2 - 95, 66, -1);
        }
        if (passwordField.getText().length() == 0) {
            mc.fontRendererObj.drawString("\2477Password", width / 2 - 95, 106, -1);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void mouseClicked(int mouseX, int mouseY, int button){
        emailField.mouseClicked(mouseX, mouseY, button);
        passwordField.mouseClicked(mouseX, mouseY, button);
        try {
            super.mouseClicked(mouseX, mouseY, button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void actionPerformed(GuiButton button){

        switch(button.id){
            case 1:
                String clipBoardText = getClipboardString();
                if (clipBoardText.contains("@") && clipBoardText.contains(":")) {
                    emailField.setText(clipBoardText.split(":")[0]);
                    passwordField.setText(clipBoardText.split(":")[1]);
                }
                break;
            case 2:
                try {
                    loginToAccount(emailField.getText(), passwordField.getText());
                    status = "Logged in as: \247e" + mc.getSession().getUsername();
                    AllureClient.getInstance().getNotificationManager().createNotification("Account Logged In", "Logged in as " + mc.getSession().getUsername(), 2, NotificationType.SUCCESS);
                } catch (AuthenticationException e) {
                    status = "\2474Invalid details!";
                    e.printStackTrace();
                }
                break;
            case 3:
                mc.displayGuiScreen(new GuiMainMenu());
                break;
        }
    }

    private void loginToAccount(String email, String pass) throws AuthenticationException {
        final YggdrasilAuthenticationService service = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication)service.createUserAuthentication(Agent.MINECRAFT);
        auth.setUsername(email);
        auth.setPassword(pass);
        auth.logIn();
        Minecraft.getMinecraft().session =  new Session(auth.getSelectedProfile().getName(), auth.getSelectedProfile().getId().toString(), auth.getAuthenticatedToken(), "mojang");
    }

}

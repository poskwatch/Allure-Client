package vip.allureclient.impl.command;

import vip.allureclient.base.command.Command;
import vip.allureclient.base.command.ArgumentException;
import vip.allureclient.base.util.client.Wrapper;

public class VClipCommand implements Command {
    @Override
    public String[] getAliases() {
        return new String[] {"vclip", "clip"};
    }

    @Override
    public void execute(String[] arguments) throws ArgumentException {
       if (arguments.length == 2) {
           double parsedDistance;
           try {
               parsedDistance = Double.parseDouble(arguments[1]);
               mc().thePlayer.setEntityBoundingBox(mc().thePlayer.getEntityBoundingBox().offset(0, parsedDistance, 0));
               return;
           }
           catch (NumberFormatException e) {
               e.printStackTrace();
               throw new ArgumentException(getUsage());
           }
       }
       throw new ArgumentException(getUsage());
    }

    @Override
    public String getUsage() {
        return ".vclip <distance>";
    }
}

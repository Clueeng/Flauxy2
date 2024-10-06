package uwu.noctura;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;
import net.arikia.dev.drpc.DiscordUser;
import net.arikia.dev.drpc.callbacks.ReadyCallback;

public class DiscordRP {

    private boolean isRunning = true;
    private long created = 0;
    public DiscordUser currentUser;

    public void init(){
        this.created = System.currentTimeMillis();
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler(new ReadyCallback() {
            @Override
            public void apply(DiscordUser discordUser) {
                currentUser = discordUser;
                System.out.println("[Flauxy] Discord connected " + discordUser.username + "#" +  discordUser.discriminator);
                update("Booting Client", " ");
            }
        }).build();
        DiscordRPC.discordInitialize("972902027300581406", handlers, true);

        new Thread("Discord RPC Callback"){
            @Override
            public void run() {
                while(isRunning){
                    DiscordRPC.discordRunCallbacks();
                }
            }
        }.start();
    }

    public void close(){
        isRunning = false;
        DiscordRPC.discordShutdown();
    }

    public void update(String firstL, String secLine){
        DiscordRichPresence.Builder b = new DiscordRichPresence.Builder(secLine);
        b.setBigImage("large", "");
        b.setDetails(firstL);
        b.setStartTimestamps(created);
        DiscordRPC.discordUpdatePresence(b.build());
    }

}

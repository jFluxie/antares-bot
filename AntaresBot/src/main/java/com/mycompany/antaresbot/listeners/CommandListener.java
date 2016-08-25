/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot.listeners;

import com.mycompany.antaresbot.main.Bot;
import com.mycompany.antaresbot.events.CommandExecutionEvent;
import com.github.axet.vget.VGet;
import com.github.axet.vget.info.VGetParser;
import com.github.axet.vget.info.VideoFileInfo;
import static com.mycompany.antaresbot.main.Bot.client;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;

/**
 *
 * @author jFluxie
 */
public class CommandListener {

    // This is the executor that we'll look for
    private final static String KEY = "!";

    private final static String BOT_MASTER = "BOT MASTER";

    public CommandListener(IDiscordClient client) {
        client.getDispatcher().registerListener(this);

    }

    @EventSubscriber
    public void watchForCommands(MessageReceivedEvent event) {
        try {

            IMessage message = event.getMessage();
            String content = message.getContent();

            if (!content.startsWith(KEY)) {
                return;
            }

            String command = content.toLowerCase().replace(KEY, "");
            String[] args = null;

            if (content.contains(" ")) {
                command = command.split(" ")[0];
                args = content.substring(content.indexOf(' ') + 1).split(" ");
            }

            CommandExecutionEvent _event = new CommandExecutionEvent(message, command, message.getAuthor(), args);
            Bot.client.getDispatcher().dispatch(_event);

        } catch (Exception ex) {
            // Handle how ever you please
        }
    }

    @EventSubscriber
    public void handle(CommandExecutionEvent event) {

        //Needs an alternative
        IGuild g = event.getMessage().getGuild();
        AudioPlayer player = AudioPlayer.getAudioPlayerForGuild(g);

        RequestBuffer.request(() -> {

            //COMMANDS FOR BOT MASTERS i.e. Admins
            if (containsBotRole(event.getBy().getRolesForGuild(g))) {

                if (event.isCommand("ping")) {
                    try {
                        event.getMessage().reply("Pong!");
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (event.isCommand("join")) {

                    try {
                        if (event.getMessage().getAuthor().getConnectedVoiceChannels().size() == 0) {
                            event.getMessage().getChannel().sendMessage(event.getBy() + ". Im sorry you are currently not in a channel. Try again");
                        } else {
                            event.getMessage().getAuthor().getConnectedVoiceChannels().get(0).join();

                        }
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (event.isCommand("leave")) {

                    try {
                        if (event.getMessage().getAuthor().getConnectedVoiceChannels().size() == 0) {
                            new MessageBuilder(Bot.client).withChannel(event.getMessage().getChannel()).withContent(event.getBy() + " .Im sorry, you are currently not in a channel. Try again.").build();
                        } else {
                            event.getMessage().getAuthor().getConnectedVoiceChannels().get(0).leave();

                        }
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (event.isCommand("play")) {

                    try {
                        /*
                        try {
                        String url = "http://www.youtube.com/watch?v=Nj6PFaDmp6c";
                        String path = "C:\\Users\\Jos\\Videos";
                        VideoFileInfo info=new VideoFileInfo(new URL("http://www.youtube.com/watch?v=Nj6PFaDmp6c"));
                        info.
                        VGet v = new VGet(new URL(url), new File(path));                      
                        v.download();
                        v.getv
                        } catch (Exception e) {
                        throw new RuntimeException(e);
                        }
                         */

                        AudioInputStream stream = AudioSystem.getAudioInputStream(new File("C:\\Users\\Jos\\Videos\\cosmos.mp3"));
                        player.queue(stream);

                    } catch (IOException | UnsupportedAudioFileException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (event.isCommand("pause")) {

                    player.setPaused(true);

                } else if (event.isCommand("resume")) {

                    player.setPaused(false);

                } else if (event.isCommand("volume")) {

                    String volume = event.getArgs()[0];

                    Integer value = Integer.parseInt(event.getArgs()[0]);

                    float finalVolume = (value / 100.0f);
                    if (Float.compare(finalVolume, 0)>0 && Float.compare(finalVolume, 1)<0) {
                        
                        player.setVolume(finalVolume);
                    }

                }
                
                else if (event.isCommand("logout")) {

                    try {
                        client.logout();
                    } catch (RateLimitException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }

            } else {

                try {
                    IMessage temp = event.getMessage();
                    event.getMessage().delete();
                    IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                    channel.sendMessage("Im sorry. Im afraid I can't do that Dave. You need BOT MASTER role to use !" + event.getCommand() + " command.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            if (event.isCommand("help")) {
                try {
                    IMessage temp = event.getMessage();
                    event.getMessage().delete();
                    IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                    String cm = "!ping: antares-bot responds 'Pong!'\n!join: antares-bot joins your voice channel.";
                    channel.sendMessage("Hello " + temp.getAuthor() + "\nHere's a list of commands you might find useful:\n" + cm);

                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        );

    }

    public boolean containsBotRole(List<IRole> roles) {
        for (int i = 0; i < roles.size(); i++) {
            IRole r = roles.get(i);
            if (r.getName().equals(BOT_MASTER)) {
                return true;
            }
        }
        return false;

    }

}

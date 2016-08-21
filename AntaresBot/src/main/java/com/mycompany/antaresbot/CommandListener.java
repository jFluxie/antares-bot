/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot;

import static com.mycompany.antaresbot.Bot.client;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

/**
 *
 * @author jFluxie
 */
public class CommandListener {

    // This is the executor that we'll look for
    final static String KEY = "!";

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
        
        RequestBuffer.request(() -> {  


        if (event.isCommand("ping")) {
            try {
                event.getMessage().reply("Pong!");
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(AnnotationListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (event.isCommand("help")) {
            try {
                IMessage temp=event.getMessage();
                event.getMessage().delete();
                IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                String cm="!ping: antares-bot responds 'Pong!'\n!join: antares-bot joins your voice channel.";
                channel.sendMessage("Hello "+temp.getAuthor()+"\nHere's a list of commands you might find useful:\n"+cm);
                
                
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(AnnotationListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //TODO
        else if (event.isCommand("join")) {
            
            System.out.println(event.getMessage().getAuthor().getConnectedVoiceChannels());
            try {
                if(event.getMessage().getAuthor().getVoiceChannel().equals(null))
                {
                
                }
                else
                {
                    client.getOurUser().moveToVoiceChannel(event.getMessage().getAuthor().getVoiceChannel().get());   
                }
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(AnnotationListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        });
        
    }

}

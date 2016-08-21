/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.GameChangeEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.events.StatusChangeEvent;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.impl.events.UserLeaveEvent;
import sx.blah.discord.handle.impl.obj.Message;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;

/**
 * @author jFluxie
 * @author JORGE VILLAREAL
 */
public class AnnotationListener {

    /*
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event) {
        RequestBuffer.request(() -> {

            try {

                if (event.getMessage().getContent().equals("help")) {
                    
                    IPrivateChannel channel = Bot.client.getOrCreatePMChannel(Bot.client.getUserByID(event.getMessage().getID()));
                    channel.sendMessage("Hello User");
                    
                }

            } catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
                Logger.getLogger(AnnotationListener.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        );

    }

     */
    @EventSubscriber
    public void onReady(ReadyEvent event) {
        RequestBuffer.request(() -> {

            try {
                new MessageBuilder(Bot.client).withChannel("182651110756974592").withContent("@here Hello everyone my name is Antares, if you need anything from me type !help").build();
            } catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
                Logger.getLogger(AnnotationListener.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
        );

    }

    @EventSubscriber
    public void onChangeGame(StatusChangeEvent event) {

        
        
        String oldGame = event.getOldStatus().getStatusMessage();
        String newGame = event.getNewStatus().getStatusMessage();

        String message;

        //User is not in a game at the moment and starts playing
        if (oldGame==null && newGame!=null) {
            message = event.getUser() + " has entered " + newGame;

        //User is already in a game and stops playing
        } else if (oldGame!=null && newGame==null ) {
            message = event.getUser() + " has left " + oldGame;

        //User has two games active at the moment
        } else if(oldGame!=null && newGame!=null){
            message = event.getUser() + " has changed from " + oldGame + " to " + newGame;
        }
        //We should never enter this place
        else
        {
            message="";
        }

        RequestBuffer.request(() -> {

            try {
                new MessageBuilder(Bot.client).withChannel("182651110756974592").withContent(message).build();
            } catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
                Logger.getLogger(AnnotationListener.class.getName()).log(Level.SEVERE, null, ex);

            }
        }
        );
         
    }

}

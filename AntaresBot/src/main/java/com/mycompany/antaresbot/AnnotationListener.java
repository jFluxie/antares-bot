/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot;

import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;

/**
 *
 * @author JORGE VILLAREAL
 */
public class AnnotationListener {
    
    @EventSubscriber
    public void onReady(MessageReceivedEvent event){ 
        try {    
            new MessageBuilder(Bot.client).withChannel(event.getMessage().getChannel()).withContent(event.getMessage().getContent()).build();
        } catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
            Logger.getLogger(AnnotationListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

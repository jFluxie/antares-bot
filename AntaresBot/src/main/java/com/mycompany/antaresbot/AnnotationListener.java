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
import sx.blah.discord.util.RequestBuffer;

/**
 * @author JORGE VILLAREAL
 * @author JOSE QUIROGA
 */
public class AnnotationListener {
    
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){ 
        RequestBuffer.request(()-> {
        try {  
            
            if(!event.getMessage().getAuthor().getName().equals("jFluxie"))
            {
                String lastMessage=event.getMessage().getContent();
                event.getMessage().delete();
                new MessageBuilder(Bot.client).withChannel(event.getMessage().getChannel()).withContent("The following message has been deleted: '"+lastMessage+"' \n").build();
                new MessageBuilder(Bot.client).withChannel(event.getMessage().getChannel()).withContent("http://i921.photobucket.com/albums/ad56/Trolling_is_a_art/U%20Mad/anime.jpg").build();

            }
            
        } catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
            Logger.getLogger(AnnotationListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        }
        );
        
    }
    /*
    @EventSubscriber
    public void onReady(ReadyEvent event){ 
        RequestBuffer.request(()-> {
        
            try {
                new MessageBuilder(Bot.client).withContent("Hi there, my name is antares-bot. Im here to mute you all :D")..build();
            } catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
                Logger.getLogger(AnnotationListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        );
    
    }
*/
}


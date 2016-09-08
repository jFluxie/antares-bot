/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot.main;

import com.mycompany.antaresbot.listeners.CommandListener;
import com.mycompany.antaresbot.listeners.CommandListener;
import com.mycompany.antaresbot.listeners.EventListener;
import com.mycompany.antaresbot.listeners.EventListener;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.audio.AudioPlayer;

/**
 *
 * @author jFluxie
 */
public class Bot {

    public static IDiscordClient client;
    
    public static String musicPath;

    public static void main(String[] args) throws DiscordException {

        client = getClient("MjE1ODc4MTk1NjUwNjI1NTM3.CrJstQ.aqfo7vkS1Itz59L89bGxco9Wazs");
        //Temporary file
        musicPath="C:\\AntaresMusic";
        EventListener eventLis = new EventListener(client);
        
        while (!eventLis.getReadyStatus()) {
            System.out.println("Loading...");
        }
        System.out.println("DONE!");
        new CommandListener(client);

    }

    public static IDiscordClient getClient(String token) throws DiscordException {
        return new ClientBuilder().withToken(token).login();
    }

}

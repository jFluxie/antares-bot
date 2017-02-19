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
import com.mycompany.antaresbot.listeners.MusicListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;

/**
 *
 * @author jFluxie
 */
public class Bot {

    public static IDiscordClient client;

    public static String token;

    public static String botRole;
    
    public static String owner;
    
    public static String guildId;
    
    public static String executionPath;
    
    public static ArrayList permissions;
    
    public static AudioPlayer audioPlayer;

    public static void main(String[] args) throws DiscordException, RateLimitException, MissingPermissionsException {

        init();
        client = getClient(token);

        EventListener eventLis = new EventListener(client);

        while (!eventLis.getReadyStatus()) {
            System.out.println("Loading...");
        }
        
        audioPlayer = AudioPlayer.getAudioPlayerForGuild(client.getGuildByID(guildId));
        audioPlayer.setVolume(0.15f);
        (new MusicListener(Bot.audioPlayer, Bot.client)).start();
        new CommandListener(client);
        System.out.println("DONE!");
        
        //Check if the owner is in a voice channel, if he is, then join his voice channel
        List<IVoiceChannel> vp=client.getUserByID(owner).getConnectedVoiceChannels();
        System.out.println("LIST: "+vp);
        if(!vp.isEmpty() ){
            vp.get(0).join();
        }
        //Where we are currently running the bot
        executionPath=System.getProperty("user.dir");
        System.out.println("LETS GO");

    }

    public static IDiscordClient getClient(String token) throws DiscordException {
        return new ClientBuilder().withToken(token).login();
    }

    public static void init() {
        
        permissions=new ArrayList();

        //Check properties file
        try (BufferedReader br = new BufferedReader(new FileReader("init.txt"))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.startsWith("token")) {
                    String value[] = line.split(":");
                    token = value[1];
                }
                if (line.startsWith("owner")) {
                    String value[] = line.split(":");
                    owner = value[1];
                }
                if (line.startsWith("guild")) {
                    String value[] = line.split(":");
                    guildId = value[1];
                }
                if (line.startsWith("permissions")) {
                    String value[] = line.split(":");
                    if(value.length>1)
                    {
                            
                            String val[]=value[1].split(",");
                            for(int i=0;i<val.length;i++)
                            {
                                permissions.add(val[i]);
                            }
                        

                    }
                }
                

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

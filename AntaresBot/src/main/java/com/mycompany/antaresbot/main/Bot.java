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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

    public static String token;

    public static String guildId;
    
    public static String botRole;

    public static void main(String[] args) throws DiscordException {

        init();
        client = getClient(token);

        musicPath = "C:\\AntaresMusic";
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

    public static void init() {

        //Check properties file
        try (BufferedReader br = new BufferedReader(new FileReader("init.txt"))) {

            String line;

            while ((line = br.readLine()) != null) {

                if (line.startsWith("token")) {
                    String value[] = line.split(":");
                    token = value[1];

                }
                if (line.startsWith("guild_id")) {
                    String value[] = line.split(":");
                    guildId = value[1];
                }
                if (line.startsWith("bot_role")) {
                    String value[] = line.split(":");
                    botRole = value[1];
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        //Check if AntaresMusic Folder exists, if not create it
        File f = new File("C:\\AntaresMusic");
        if (f.exists() && f.isDirectory()) {
            // no problems here
        }
        else
        {
            f.mkdir();
        }
      

    }

}

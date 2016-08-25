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

/**
 *
 * @author jFluxie
 */
public class Bot {

    public static IDiscordClient client;
        
    public static void main(String[] args) throws DiscordException {

        client = getClient("MjE1ODc4MTk1NjUwNjI1NTM3.Cp65dA.o4iFBVaZNu6lJoM1v_pxBS08QG4");
        new EventListener(client);
        new CommandListener(client);
    }

    public static IDiscordClient getClient(String token) throws DiscordException {
        return new ClientBuilder().withToken(token).login();
    }
    

}

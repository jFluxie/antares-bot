/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;

/**
 *
 * @author JOSE QUIROGA
 */
public class Bot {

    public static IDiscordClient client;

    public static void main(String[] args) throws DiscordException {

        client = getClient("MjE1ODc4MTk1NjUwNjI1NTM3.Cpd7xQ.Vm-ykhXZXig2IBSWISAuYSnipGk");
        client.getDispatcher().registerListener(new AnnotationListener());
        new CommandListener(client);
    }

    public static IDiscordClient getClient(String token) throws DiscordException {
        return new ClientBuilder().withToken(token).login();
    }

}

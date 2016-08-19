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
        
        client=getClient("MjE1ODc4MTk1NjUwNjI1NTM3.Cpd7xQ.Vm-ykhXZXig2IBSWISAuYSnipGk", false);
        //client = new ClientBuilder().withToken("MjE1ODc4MTk1NjUwNjI1NTM3.Cpd7xQ.Vm-ykhXZXig2IBSWISAuYSnipGk").login();
        client.login();
        client.getDispatcher().registerListener(new AnnotationListener());
    }

    /**
     * Returns an instance of the discord client
     */
    public static IDiscordClient getClient(String token, boolean login) throws DiscordException { 
        
        ClientBuilder clientBuilder = new ClientBuilder(); //Creates the ClientBuilder instance
        
        clientBuilder.withToken(token); //Adds the login info to the builder
        
        if (login) {
            return clientBuilder.login(); //Creates the client instance and logs the client in
        } else {
            return clientBuilder.build(); //Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
        }
    }

}

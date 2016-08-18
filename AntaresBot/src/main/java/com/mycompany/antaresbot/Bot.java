/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot;

import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.util.DiscordException;

/**
 *
 * @author Jose
 */
public class Bot {
    
    static IDiscordClient client;
    
    public static void main(String[] args) throws DiscordException {
        client = new ClientBuilder().withToken("MjE1ODc4MTk1NjUwNjI1NTM3.Cpd7xQ.Vm-ykhXZXig2IBSWISAuYSnipGk").login();

    }
    
}

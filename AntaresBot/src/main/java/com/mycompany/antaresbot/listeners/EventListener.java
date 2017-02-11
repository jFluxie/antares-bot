/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot.listeners;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.Status;

/**
 *
 * @author jFluxie
 */
public class EventListener {

    private boolean ready;

    private static Status status;

    public EventListener(IDiscordClient client) {
        client.getDispatcher().registerListener(this);
        status = null;
        ready = false;
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        ready = true;
    }

    public boolean getReadyStatus() {
        return ready;
    }
}

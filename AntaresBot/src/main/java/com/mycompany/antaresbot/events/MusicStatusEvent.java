/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot.events;

import sx.blah.discord.handle.obj.Status;

/**
 *
 * @author jFluxie
 */
public class MusicStatusEvent extends Status {
    
    public MusicStatusEvent(StatusType type, String message)
    {
        super(type, message);
    }
    
    public MusicStatusEvent(StatusType type)
    {
        super(type, "");
    
    }
    
}

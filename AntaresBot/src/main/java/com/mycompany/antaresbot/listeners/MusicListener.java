/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot.listeners;

import com.mycompany.antaresbot.events.MusicStatusEvent;
import java.util.logging.Level;
import java.util.logging.Logger;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.handle.obj.Status.StatusType;
import sx.blah.discord.util.audio.AudioPlayer;

/**
 *
 * @author jFluxie
 */
public class MusicListener extends Thread {

    private AudioPlayer audioPlayer;

    private IDiscordClient client;

    private String previousSong;

    public MusicListener(AudioPlayer player, IDiscordClient client) {
        this.audioPlayer = player;
        this.client = client;
        previousSong = "";

    }

    public void run() {
        while (true) {

            if (audioPlayer.getCurrentTrack() != null) {

                if (!previousSong.equalsIgnoreCase(audioPlayer.getCurrentTrack().getMetadata().get("file").toString())) {
                    previousSong = audioPlayer.getCurrentTrack().getMetadata().get("file").toString();
                    String song = previousSong.replace("music\\", "").replace(".mp3", "");
                    client.changeStatus(new MusicStatusEvent(StatusType.STREAM, song.replaceAll("_", " ")));
                }

            } else {
                client.changeStatus(new MusicStatusEvent(StatusType.NONE));
            }

            try {
                this.sleep(5000);
            } catch (InterruptedException ex) {
                Logger.getLogger(MusicListener.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot.listeners;

import com.mycompany.antaresbot.main.Bot;
import com.mycompany.antaresbot.events.CommandExecutionEvent;
import static com.mycompany.antaresbot.main.Bot.client;
import static com.mycompany.antaresbot.main.Bot.owner;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;

/**
 *
 * @author jFluxie
 */
public class CommandListener {

    //Any user can use these commands
    private ArrayList<String> commands;

    //Only owner can use these commands
    private ArrayList<String> botOwnerCommands;

    private final static String KEY = "!";

    private IGuild guild;

    private ProcessBuilder builder;

    public CommandListener(IDiscordClient client) {

        commands = new ArrayList<String>();
        botOwnerCommands = new ArrayList<String>();

        botOwnerCommands.add("logout");
        botOwnerCommands.add("qall");
        botOwnerCommands.add("join");
        botOwnerCommands.add("leave");

        commands.add("playlist");
        commands.add("q");
        commands.add("skip");
        commands.add("skipall");
        commands.add("pause");
        commands.add("resume");
        commands.add("volume");
        commands.add("shuffle");

        builder = new ProcessBuilder();
        guild = client.getGuildByID(Bot.guildId);
        client.getDispatcher().registerListener(this);
    }

    @EventSubscriber
    public void watchForCommands(MessageReceivedEvent event) {
        RequestBuffer.request(()
                -> {

            IMessage message = event.getMessage();
            String content = message.getContent();

            if (!content.startsWith(KEY)) {
                return;
            }

            String command = content.toLowerCase().replace(KEY, "");
            String[] args = null;

            if (content.contains(" ")) {
                command = command.split(" ")[0];
                args = content.substring(content.indexOf(' ') + 1).split(" ");
            }

            CommandExecutionEvent _event = new CommandExecutionEvent(message, command, message.getAuthor(), args);
            Bot.client.getDispatcher().dispatch(_event);

        });
    }

    @EventSubscriber
    public void handle(CommandExecutionEvent event) {

        RequestBuffer.request(()
                -> {
            if (commands.contains(event.getCommand()) || botOwnerCommands.contains(event.getCommand())) {

                if (String.valueOf(event.getBy().getID()).equals(owner)) {
                    executeCommand(event);
                } else if (!Bot.permissions.isEmpty() && containsBotRole(event.getBy().getID())) {
                    if (!botOwnerCommands.contains(event.getCommand())) {
                        executeCommand(event);
                    } else {
                        try {
                            IMessage temp = event.getMessage();
                            IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                            channel.sendMessage("Im sorry. You don't have permissions to use: '!" + event.getCommand() + "' command.");
                        } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else {
                    try {
                        IMessage temp = event.getMessage();
                        IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                        channel.sendMessage("Im sorry. You don't have permissions to use: '!" + event.getCommand() + "' command.");
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else {

                try {
                    IMessage temp = event.getMessage();
                    IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                    channel.sendMessage("Im sorry. The command '!" + event.getCommand() + "' does not exist.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    public void executeCommand(CommandExecutionEvent event) {

        RequestBuffer.request(()
                -> {

            if (event.isCommand("join")) {

                try {

                    IMessage message = event.getMessage();

                    if (message.getAuthor().getConnectedVoiceChannels().isEmpty()) {
                        event.getMessage().getChannel().sendMessage(event.getBy() + ". Im sorry you are currently not in a channel. Try again");
                    } else {
                        try {
                            event.getMessage().reply("Ok.");
                        } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        message.getAuthor().getConnectedVoiceChannels().get(0).join();
                    }

                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (event.isCommand("leave")) {
                IMessage message = event.getMessage();

                try {
                    event.getMessage().reply("Ok.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    if (message.getAuthor().getConnectedVoiceChannels().size() == 0) {
                        new MessageBuilder(Bot.client).withChannel(event.getMessage().getChannel()).withContent(event.getBy() + " .Im sorry, you are currently not in a channel. Try again.").build();
                    } else {
                        message.getAuthor().getConnectedVoiceChannels().get(0).leave();

                    }
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (event.isCommand("skipall")) {
                try {
                    event.getMessage().reply("Ok.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

                Bot.audioPlayer.clear();
            } else if (event.isCommand("playlist")) {
                try {
                    if (Bot.audioPlayer.getCurrentTrack() == null) {

                        event.getMessage().getChannel().sendMessage("There are no songs currently on queue.");
                    } else {
                        String qSongs = "Playlist: \n";
                        for (int i = 0; i < Bot.audioPlayer.getPlaylistSize(); i++) {
                            qSongs += (i + 1) + ". " + getFileName(Bot.audioPlayer.getPlaylist().get(i).getMetadata().get("file").toString()).replaceAll("_", " ").replaceAll("music", "").replace("\\", "").replace(".wav", "") + "\n";
                        }
                        event.getMessage().getChannel().sendMessage(qSongs);
                    }
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (event.isCommand("q")) {
                try {

                    String url = event.getArgs()[0];
                    String songName = "";

                    if (url.contains("http") || url.contains("https")) {

                        //GET SONG NAME
                        builder.command("cmd.exe", "/c", "cd " + Bot.executionPath + "\\lib && youtube-dl --get-filename --extract-audio "
                                + "--audio-format wav -o %(title)s.%(ext)s --restrict-filenames " + url);
                        builder.redirectErrorStream(true);
                        Process p2 = builder.start();
                        BufferedReader br2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                        songName = br2.readLine();
                        p2.waitFor();

                        songName = songName.replaceAll(".m4a", ".wav");
                        songName = songName.replaceAll(".webm", ".wav");
                        songName = songName.replaceAll(".mp4", ".wav");

                        System.out.println("SONGNAME IS " + songName);

                        //CHECK IF WE HAVE THE SONG
                        if (!containsFile(songName)) {
                            //FIRST DOWNLOAD THE SONG
                            System.out.println("FILE IS NOT ON FOLDER. START DOWNLOADING.");
                            builder.command("cmd.exe", "/c", "cd " + Bot.executionPath + "\\lib && youtube-dl --extract-audio "
                                    + "--audio-format wav -o %(title)s.%(ext)s --restrict-filenames " + url);
                            builder.redirectErrorStream(true);
                            Process p = builder.start();
                            InputStream is = p.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            String line;
                            while ((line = br.readLine()) != null) {
                                System.out.println(line);
                            }

                            p.waitFor();

                        } else {
                            System.out.println("WE ALREADY HAVE THE FILE");
                        }
                        //Move file to music directory
                        File song = new File("lib\\" + songName);
                        song.renameTo(new File("music\\" + songName));

                        Bot.audioPlayer.queue(new File("music\\" + songName));

                    } else {

                        for (int i = 1; i < event.getArgs().length; i++) {
                            url += "+" + event.getArgs()[i];
                        }

                        System.out.println("SONG NAME: " + url);

                        builder.command("cmd.exe", "/c", "cd " + Bot.executionPath + "\\lib && youtube-dl --get-filename --extract-audio --audio-format wav -o "
                                + "%(id)s:%(title)s.%(ext)s --restrict-filenames --default-search ytsearch: " + url);
                        builder.redirectErrorStream(true);
                        Process p2 = builder.start();
                        BufferedReader r = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                        songName = r.readLine();
                        p2.waitFor();
                        System.out.println("SONG NAME2: " + songName);

                        event.getMessage().getChannel().sendMessage(event.getMessage().getAuthor() + ". Queued: https://www.youtube.com/watch?v=" + extractVideoId(songName));

                        songName = songName.replaceAll(extractVideoId(songName) + "#", "");
                        String save2 = songName;
                        songName = songName.replaceAll(".m4a", ".wav");
                        songName = songName.replaceAll(".webm", ".wav");
                        songName = songName.replaceAll(".mp4", ".wav");

                        if (!containsFile(songName)) {
                            System.out.println("FILE NOT FOUND. WILL BEGIN DOWNLOADING.");
                            builder.command("cmd.exe", "/c", "cd " + Bot.executionPath + "\\lib && youtube-dl --extract-audio --audio-format wav -o %(title)s.%(ext)s "
                                    + "--restrict-filenames --default-search ytsearch: " + url);
                            builder.redirectErrorStream(true);
                            Process p = builder.start();

                            InputStream is = p.getInputStream();
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader br = new BufferedReader(isr);
                            String line;
                            while ((line = br.readLine()) != null) {
                                System.out.println(line);
                            }
                            p.waitFor();

                        } else {
                            System.out.println("WE ALREADY HAVE THE FILE");
                        }
                        //Move file to music directory
                        File song = new File("lib\\" + songName);
                        song.renameTo(new File("music\\" + songName));

                        System.out.println("NOW PLAYING...");
                        Bot.audioPlayer.queue(new File("music\\" + songName));

                    }

                } catch (IOException | UnsupportedAudioFileException | InterruptedException | MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (event.isCommand("qall")) {
                if (event.getArgs() == null) {
                    File folder = new File("music");
                    File[] listOfFiles = folder.listFiles();

                    for (int i = 0; i < listOfFiles.length; i++) {
                        if (listOfFiles[i].isFile() && getFileExt(listOfFiles[i].toString()).equals("wav")) {

                            try {
                                Bot.audioPlayer.queue(new File(listOfFiles[i].toString()));
                            } catch (UnsupportedAudioFileException | IOException ex) {
                                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }

                } else {
                    //
                }

            } else if (event.isCommand("pause")) {
                try {
                    event.getMessage().reply("Ok.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

                Bot.audioPlayer.setPaused(true);

            } else if (event.isCommand("resume")) {
                try {
                    event.getMessage().reply("Ok.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

                Bot.audioPlayer.setPaused(false);

            } else if (event.isCommand("skip")) {
                try {
                    event.getMessage().reply("Ok.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

                Bot.audioPlayer.skip();

            } else if (event.isCommand("loop")) {

                if (!Bot.audioPlayer.isLooping()) {
                    Bot.audioPlayer.setLoop(true);
                    try {
                        event.getMessage().getChannel().sendMessage("Playlist is on loop.");
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    Bot.audioPlayer.setLoop(false);
                    try {
                        event.getMessage().getChannel().sendMessage("Playlist is not on loop.");
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            } else if (event.isCommand("volume")) {

                IChannel channel = event.getMessage().getChannel();

                if (event.getArgs() != null) {
                    String volume = event.getArgs()[0];
                    if (!volume.isEmpty() && volume != null) {
                        Integer value = Integer.parseInt(event.getArgs()[0]);

                        float finalVolume = (value / 100.0f);
                        if (Float.compare(finalVolume, 0) >= 0 && Float.compare(finalVolume, 1) <= 0) {
                            try {
                                event.getMessage().reply("The volume has been set to " + Math.round(finalVolume * 100) + "%");
                            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            Bot.audioPlayer.setVolume(finalVolume);
                        } else {
                            try {
                                channel.sendMessage("Volume values must be between 0 and 100!");
                            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        try {
                            channel.sendMessage("You must specify a volume value!" + Bot.audioPlayer.getVolume());
                        } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }
                } else {
                    try {
                        channel.sendMessage("The volume is currently set to " + Math.round(Bot.audioPlayer.getVolume() * 100) + "%");
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else if (event.isCommand("logout")) {

                Bot.audioPlayer.clear();
                Bot.audioPlayer.clean();

                try {
                    event.getMessage().reply("Bye!");
                    client.logout();
                } catch (DiscordException | MissingPermissionsException | RateLimitException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.exit(0);

            } else if (event.isCommand("shuffle")) {
                try {
                    event.getMessage().reply("Ok.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }
                Bot.audioPlayer.shuffle();
            }

        });
    }

    public boolean containsFile(String fileName) {
        File folder = new File("music");
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].toString().equals("music" + "\\" + fileName)) {
                    return true;
                }

            }
        }
        return false;
    }

    public String getFileExt(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }

        return extension;

    }

    public String getFileName(String fileName) {
        return fileName.replace("C:\\AntaresMusic\\", "").replace(".mp3", "");

    }

    public String extractVideoId(String line) {
        char[] c = line.toCharArray();

        String id = "";

        for (int i = 0; i < c.length; i++) {
            if (c[i] != '#') {
                id += c[i];
            } else {
                break;
            }

        }
        return id;
    }

    private boolean containsBotRole(String id) {
        System.out.println(Bot.permissions);
        if (Bot.permissions.contains(id)) {
            return true;
        }
        return false;
    }

}

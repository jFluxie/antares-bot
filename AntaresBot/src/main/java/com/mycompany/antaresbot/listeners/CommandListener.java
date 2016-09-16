/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.antaresbot.listeners;

import com.mycompany.antaresbot.main.Bot;
import com.mycompany.antaresbot.events.CommandExecutionEvent;
import static com.mycompany.antaresbot.main.Bot.botRole;
import static com.mycompany.antaresbot.main.Bot.client;
import static com.mycompany.antaresbot.main.Bot.owner;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IPrivateChannel;
import sx.blah.discord.handle.obj.IRole;
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

    //Only user with bot Role can use this commands
    private ArrayList<String> botRoleCommands;

    //Only owner can use these commands
    private ArrayList<String> botOwnerCommands;

    private final static String KEY = "!";

    private AudioPlayer audioPlayer;

    private IGuild guild;

    private boolean playlistLoop;

    public CommandListener(IDiscordClient client) {

        commands = new ArrayList<String>();
        botRoleCommands = new ArrayList<String>();
        botOwnerCommands = new ArrayList<String>();

        commands.add("ping");
        commands.add("playlist");
        commands.add("help");

        botRoleCommands.add("ping");
        botRoleCommands.add("playlist");
        botRoleCommands.add("help");
        botRoleCommands.add("queue");
        botRoleCommands.add("skip");
        botRoleCommands.add("skipall");
        botRoleCommands.add("pause");
        botRoleCommands.add("resume");
        botRoleCommands.add("volume");
        botRoleCommands.add("loop");

        botOwnerCommands.add("ping");
        botOwnerCommands.add("playlist");
        botOwnerCommands.add("help");
        botOwnerCommands.add("join");
        botOwnerCommands.add("leave");
        botOwnerCommands.add("queue");
        botOwnerCommands.add("queuelocal");
        botOwnerCommands.add("skip");
        botOwnerCommands.add("skipall");
        botOwnerCommands.add("pause");
        botOwnerCommands.add("resume");
        botOwnerCommands.add("volume");
        botOwnerCommands.add("loop");
        botOwnerCommands.add("logout");

        audioPlayer = AudioPlayer.getAudioPlayerForGuild(client.getGuildByID(Bot.guildId));
        audioPlayer.setVolume(0.15f);
        guild = client.getGuildByID(Bot.guildId);
        playlistLoop = false;
        (new MusicListener(audioPlayer, Bot.client)).start();

        client.getDispatcher().registerListener(this);
    }

    @EventSubscriber
    public void watchForCommands(MessageReceivedEvent event) {
        try {

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

        } catch (Exception ex) {
            // Handle how ever you please
        }
    }

    @EventSubscriber
    public void handle(CommandExecutionEvent event) {

        RequestBuffer.request(()
                -> {
            if (botOwnerCommands.contains(event.getCommand())) {

                if (String.valueOf(event.getBy().getID()).equals(owner)) {
                    executeCommand(event);
                } else if (containsBotRole(event.getBy().getRolesForGuild(guild))) {
                    if (botRoleCommands.contains(event.getCommand())) {
                        executeCommand(event);
                    } else {

                        try {
                            IMessage temp = event.getMessage();
                            event.getMessage().delete();
                            IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                            channel.sendMessage("Im sorry. You don't have permissions to use: '!" + event.getCommand() + "' command.");
                        } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }

                } else if (commands.contains(event.getCommand())) {
                    executeCommand(event);
                } else {
                    try {
                        IMessage temp = event.getMessage();
                        event.getMessage().delete();
                        IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                        channel.sendMessage("Im sorry. You don't have permissions to use: '!" + event.getCommand() + "' command.");
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            } else {

                try {
                    IMessage temp = event.getMessage();
                    event.getMessage().delete();
                    IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                    channel.sendMessage("Im sorry. The command '!" + event.getCommand() + "' does not exist.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        });
    }

    public void executeCommand(CommandExecutionEvent event) {
        if (event.isCommand("help")) {
            try {
                IMessage temp = event.getMessage();
                event.getMessage().delete();
                IPrivateChannel channel = client.getOrCreatePMChannel(client.getUserByID(temp.getAuthor().getID()));
                String cm = "";
                for (int i = 0; i < commands.size(); i++) {
                    cm += "!" + commands.get(i) + "\n";
                }
                channel.sendMessage("Hello " + temp.getAuthor() + ". Here's a list of commands you might find useful:\n" + cm);

            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (event.isCommand("ping")) {
            try {
                event.getMessage().reply("Pong!");
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (event.isCommand("join")) {

            try {

                IMessage message = event.getMessage();
                event.getMessage().delete();

                if (message.getAuthor().getConnectedVoiceChannels().isEmpty()) {
                    event.getMessage().getChannel().sendMessage(event.getBy() + ". Im sorry you are currently not in a channel. Try again");
                } else {
                    message.getAuthor().getConnectedVoiceChannels().get(0).join();
                }

            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (event.isCommand("leave")) {
            IMessage message = event.getMessage();
            try {
                event.getMessage().delete();
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
                event.getMessage().delete();
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            audioPlayer.clear();
        } else if (event.isCommand("playlist")) {
            try {
                if (audioPlayer.getCurrentTrack() == null) {

                    event.getMessage().getChannel().sendMessage("There are no songs currently on queue.");
                } else {
                    String qSongs = "Playlist: \n";
                    for (int i = 0; i < audioPlayer.getPlaylistSize(); i++) {
                        qSongs += (i + 1) + ". " + getFileName(audioPlayer.getPlaylist().get(i).getMetadata().get("file").toString()).replaceAll("_", " ") + "\n";

                    }

                    event.getMessage().getChannel().sendMessage(qSongs);
                }

            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (event.isCommand("queue")) {

            try {

                String url = event.getArgs()[0];
                String songName = "";

                if (url.contains("http")) {

                    ProcessBuilder builder2 = new ProcessBuilder("cmd.exe", "/c", "youtube-dl --get-filename -o %(title)s.%(ext)s --restrict-filenames " + url);
                    builder2.redirectErrorStream(true);
                    Process p2 = builder2.start();
                    BufferedReader br2 = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                    songName = br2.readLine();
                    p2.waitFor();

                    songName = songName.replaceAll(".m4a", ".mp3");
                    songName = songName.replaceAll(".webm", ".mp3");
                    songName = songName.replaceAll(".mp4", ".mp3");
                    if (!containsFile(songName)) {
                        System.out.println("FILE IS NOT ON FOLDER. START DOWNLOADING.");
                        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd \"C:\\AntaresMusic\" && youtube-dl --extract-audio --audio-format mp3 -o %(title)s.%(ext)s --restrict-filenames " + url);
                        builder.redirectErrorStream(true);
                        Process p = builder.start();
                        p.waitFor();
                    }
                    audioPlayer.queue(new File("C:\\AntaresMusic\\" + songName));

                } else {

                    for (int i = 1; i < event.getArgs().length; i++) {
                        url += "+" + event.getArgs()[i];
                    }

                    System.out.println("SONG NAME: " + url);

                    ProcessBuilder builder2 = new ProcessBuilder("cmd.exe", "/c", "youtube-dl --get-filename -o %(id)s-%(title)s.%(ext)s --restrict-filenames --default-search ytsearch: " + url);
                    builder2.redirectErrorStream(true);
                    Process p2 = builder2.start();
                    BufferedReader r = new BufferedReader(new InputStreamReader(p2.getInputStream()));
                    songName = r.readLine();
                    System.out.println("SONG NAME2: " + songName);
                    p2.waitFor();

                    event.getMessage().getChannel().sendMessage(event.getMessage().getAuthor() + ". Queued: https://www.youtube.com/watch?v=" + extractVideoId(songName));

                    songName = songName.replaceAll(".m4a", ".mp3");
                    songName = songName.replaceAll(".webm", ".mp3");
                    songName = songName.replaceAll(".mp4", ".mp3");
                    songName = songName.replaceAll(extractVideoId(songName) + "-", "");

                    if (!containsFile(songName)) {
                        System.out.println("FILE NOT FOUND. WILL BEGIN DOWNLOADING.");
                        ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd \"C:\\AntaresMusic\" && youtube-dl --extract-audio --audio-format mp3 -o %(title)s.%(ext)s --restrict-filenames --default-search ytsearch: " + url);
                        builder.redirectErrorStream(true);
                        Process p = builder.start();
                        p.waitFor();
                    }
                    audioPlayer.queue(new File("C:\\AntaresMusic\\" + songName));

                }

            } catch (IOException | UnsupportedAudioFileException | InterruptedException | MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (event.isCommand("queuelocal")) {
            if (event.getArgs() == null) {
                File folder = new File(Bot.musicPath);
                File[] listOfFiles = folder.listFiles();

                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile() && getFileExt(listOfFiles[i].toString()).equals("mp3")) {

                        try {
                            audioPlayer.queue(new File(listOfFiles[i].toString()));
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
                event.getMessage().delete();
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }
            audioPlayer.setPaused(true);

        } else if (event.isCommand("resume")) {

            try {
                event.getMessage().delete();
                audioPlayer.setPaused(false);
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (event.isCommand("skip")) {

            try {
                event.getMessage().delete();
                audioPlayer.skip();
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else if (event.isCommand("loop")) {

            if (playlistLoop == false) {
                audioPlayer.setLoop(true);
                playlistLoop = true;
                try {
                    event.getMessage().getChannel().sendMessage("Playlist is on loop.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                audioPlayer.setLoop(false);
                playlistLoop = false;
                try {
                    event.getMessage().getChannel().sendMessage("Playlist is not on loop.");
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        } else if (event.isCommand("volume")) {

            IChannel channel = event.getMessage().getChannel();
            try {
                event.getMessage().delete();
            } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (event.getArgs() != null) {
                String volume = event.getArgs()[0];
                if (!volume.isEmpty() && volume != null) {
                    Integer value = Integer.parseInt(event.getArgs()[0]);

                    float finalVolume = (value / 100.0f);
                    if (Float.compare(finalVolume, 0) >= 0 && Float.compare(finalVolume, 1) <= 0) {

                        audioPlayer.setVolume(finalVolume);
                    } else {
                        try {
                            channel.sendMessage("Volume values must be between 0 and 100!");
                        } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                            Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    try {
                        channel.sendMessage("You must specify a volume value!" + audioPlayer.getVolume());
                    } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                        Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            } else {
                try {
                    channel.sendMessage("The volume is currently set to " + audioPlayer.getVolume() * 100);
                } catch (MissingPermissionsException | RateLimitException | DiscordException ex) {
                    Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        } else if (event.isCommand("logout")) {

            try {
                event.getMessage().delete();
                audioPlayer.clear();
                audioPlayer.clean();

                client.logout();
                System.exit(0);

            } catch (RateLimitException | DiscordException | MissingPermissionsException ex) {
                Logger.getLogger(CommandListener.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    //AUXILIARY METHODS
    public boolean containsBotRole(List<IRole> roles) {
        for (int i = 0; i < roles.size(); i++) {
            IRole r = roles.get(i);
            if (r.getName().equals(botRole)) {
                return true;
            }
        }
        return false;

    }

    public boolean containsFile(String fileName) {
        File folder = new File(Bot.musicPath);
        File[] listOfFiles = folder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                if (listOfFiles[i].toString().equals(Bot.musicPath + "\\" + fileName)) {
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
            if (c[i] != '-') {
                id += c[i];
            } else {
                break;
            }

        }
        return id;
    }

}

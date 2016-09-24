#Installation Guide
NOTE: Current build only works on Windows OS. 

##First time setup
First of all, we're going to use some external software to use our music functionality (ffmpeg and youtube-dl). 
You must also have the latest Java version installed.

###To install ffmpeg:
1. Go to [FFMPEG](https://ffmpeg.zeranoe.com/builds) and download the static version (either the 32 bit or the 64 bit depending on your pc).
2. Extract it wherever you want. 
3. Go to bin folder and copy file path.
4. Search on windows home: 'view advanced system settings' and click on the first result. Click on environmental variables and under system variables, look for a variable named path. 
If you are using Windows 7:
On variable value, add a ';' on the end and then paste the path you copied earlier. 
5. That's it!

###To install youtube-dl:
1. Go to [YOUTUBE-DL](https://rg3.github.io/youtube-dl/download.html) and download the windows executable file.
2. Place it wherever you want. 
3. Add the file path where the executable is located to your System's 'path' variable. Follow the same instructions listed in step 4 of the ffmpeg installation. 
4. That's it!

Now that we have our software set-up we can proceed to join our bot to the server.

###Create bot and joining server:
1. Go to [Discord Applications](https://discordapp.com/developers/applications/me).
2. Create New Application, name your bot whatever you like.
3. Create bot user.
4. Visit this page: https://discordapp.com/oauth2/authorize?&client_id=CLIENT_ID&scope=bot REPLACE “**CLIENT_ID**” in the url with your bot's client ID provided on the app page (This should be a long number).
5. Select the server you want the bot to join.
6. That's it!

###Setting up the bot
1. Download the bot: https://dl.dropboxusercontent.com/s/m91nadn2ai1m1nb/antaresbot.zip
2. Extract it wherever you want and open init.txt.
3. On token: place the token provided on your Discord app page.
4. On Guild ID: place the server id. For this, on your discord server right click on the top left side of the client (where your server name is located) and copy id (if this option is not available set discord on developer mode).
5. Bot role: Only users with this role will use the most crucial commands (music functionality, etc). Remember to create this role on your discord as well.
6. Bot owner: Paste your ID here. To do this: right click on your Discord profile and copy ID.
6. The bot should already be on your server, give the bot all possible permissions **BEFORE EXECUTING THE BOT**.

##Running the bot
1. Run antaresbot.jar.
2. To stop the bot, use !logout command.


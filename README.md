#Installation Guide
NOTE: Current build only works on Windows OS. 

##First time setup
Before you follow these steps, make sure you have the permissions to add discord users to your server (admin role).
You must also have java installed on your machine (windows only!).

###Create bot and joining server:
1. Go to [Discord Applications](https://discordapp.com/developers/applications/me).
2. Create New Application, name your bot whatever you like.
3. Create bot user.
4. Visit this page: https://discordapp.com/oauth2/authorize?&client_id=CLIENT_ID&scope=bot REPLACE “**CLIENT_ID**” in the url with your bot's client ID provided on the app page (This should be a long number).
5. Select the server you want the bot to join.
6. That's it!

###Setting up the bot
1. Download the bot: https://dl.dropboxusercontent.com/s/qbru6fuysd9h3rm/antares.zip
2. Extract it wherever you want and open init.txt.
3. On token: place the token provided on your Discord app page.
4. On guild: place the server id. For this, on your discord server right click on the top left side of the client (where your server name is located) and copy id (if this option is not available set discord on developer mode).
5. On owner: Paste your ID here. To do this: right click on your Discord profile and copy ID.
6. ON permissions (optional): users that are able to use the bot. if only 1 user, just paste his id. If many users, paste ID, add a ',' and paste the next one, and so on.

##Running the bot
1. Run antaresbot.jar.
2. To stop the bot, use !logout command.

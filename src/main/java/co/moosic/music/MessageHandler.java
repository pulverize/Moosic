package co.moosic.music;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

class MessageHandler extends ListenerAdapter {
private static boolean _deletePermissionNotified = false;
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String message = e.getMessage().getContentRaw();
        if(message.startsWith(Config.command_prefix))
        {
            MessageManager messenger = new MessageManager(e.getChannel());
            String command = message.toLowerCase().substring(Config.command_prefix.length());
            String[] commandWords = command.split("\\s");

            switch(commandWords[0])
            {
                case "np" :
                case "playing":
                case "nowplaying":
                case "status":
                case "track":
                    messenger.SendPlayingTrackInfo();
                    break;
                case "volume":
                    if(commandWords.length > 1) {
                        messenger.SendMessage(
                            MusicPlayer.TrySetVolume(commandWords[1])
                        );
                    }
                    break;
                case "next":
                case "skip":
                    messenger.SendMessage(
                            commandWords.length == 1 ?
                            MusicPlayer.TrySkipSongs(1):
                            MusicPlayer.TrySkipSongs(commandWords[1])
                    );
                    break;
                default:
                    messenger.SendMessage("I don't know the command '" + command + "'. Try again.");
                    break;
            }

            try {
                e.getMessage().delete().complete();
            }catch(InsufficientPermissionException ex){
                if(!_deletePermissionNotified)
                    messenger.SendMessage("I could not delete your command. Please give me permission to do so.");
                _deletePermissionNotified = true;
            }
            catch(Exception ignored){

            }
        }
    }
}

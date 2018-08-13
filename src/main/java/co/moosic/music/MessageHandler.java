package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

class MessageHandler extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String message = e.getMessage().getContentRaw();
        TextChannel textChannel = e.getChannel();
        if(message.startsWith(Config.command_prefix))
        {
            String command = message.toLowerCase().substring(Config.command_prefix.length());
            String[] commandWords = command.split("\\s");

            switch(commandWords[0])
            {
                case "np" :
                case "playing":
                case "nowplaying":
                case "status":
                case "track":
                    AudioTrack playingTrack = MusicPlayer.GetPlayingTrack();
                    if(playingTrack == null)
                        MessageManager.SendMessage(textChannel,"No track appears to be playing.");
                    AudioTrackState trackState = playingTrack.getState();
                    if (trackState == null)
                        MessageManager.SendMessage(textChannel, "Couldn't figure out WHAT is going on with that track. Something went seriously sideways.");
                    else if(trackState != AudioTrackState.PLAYING)
                        MessageManager.SendMessage(textChannel,"There is a track that should be playing but it is inactive, finished, seeking, or loading. Ask me again.");
                    else
                        MessageManager.SendTrackInfo(textChannel, playingTrack);
                    break;
                case "volume":
                    if(commandWords.length > 1) {
                        MusicPlayer.TrySetVolume(commandWords[1]);
                    }
                    break;
                default:
                    MessageManager.SendMessage(textChannel,"I don't know the command '" + command + "'. Try again.");
                    break;
            }

            try {
                e.getMessage().delete().complete();
            }catch(InsufficientPermissionException ex){
                MessageManager.SendMessage(textChannel,"I could not delete your command. Please give me permission to do so.");
            }
            catch(Exception ex){

            }
        }
    }
}

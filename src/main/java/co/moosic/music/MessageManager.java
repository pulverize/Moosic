package co.moosic.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackState;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.awt.*;
import java.util.concurrent.TimeUnit;

class MessageManager {
    private final TextChannel _textChannel;
    public MessageManager(TextChannel channel){
        _textChannel = channel;
    }
    public void SendMessage(String message){
        SendMessage(message,"DJ here");
    }
    private void SendMessage(String message, String title){
        _textChannel.sendMessage(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .addField(title,message,true)
                        .build()
        ).queue();
    }

    public void SendPlayingTrackInfo(){

        AudioTrack playingTrack = MusicPlayer.GetPlayingTrack();
        if(playingTrack == null)
            SendMessage("No track appears to be playing.");
        assert playingTrack != null;
        AudioTrackState trackState = playingTrack.getState();
        if (trackState == null)
            SendMessage( "Couldn't figure out WHAT is going on with that track. Something went seriously sideways.");
        else if(trackState != AudioTrackState.PLAYING)
            SendMessage("There is a track that should be playing but it is inactive, finished, seeking, or loading. Ask me again.");
        else
            SendTrackInfo( playingTrack);
    }

    private void SendTrackInfo(AudioTrack playingTrack){
        _textChannel.sendMessage(
            new EmbedBuilder()
                .setAuthor("Now Playing", playingTrack.getInfo().uri, null)
                .setColor(Color.GREEN)
                .addField("Song Name", playingTrack.getInfo().title, true)
                .addField("Channel", playingTrack.getInfo().author, true)
                .addField("Song Progress", String.format("`%s / %s`", getLength(playingTrack.getPosition()), getLength(playingTrack.getInfo().length)), true)
                .addField("Song State", playingTrack.getState().name(),true)
                .addField("Song Link", "[Youtube Link](" + playingTrack.getInfo().uri + ")", true)
                .setThumbnail(String.format("https://img.youtube.com/vi/%s/hqdefault.jpg", playingTrack.getInfo().identifier))
                .build()
        ).queue();
    }

    private static String getLength(long length) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(length),
                TimeUnit.MILLISECONDS.toSeconds(length) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(length))
        );
    }
}

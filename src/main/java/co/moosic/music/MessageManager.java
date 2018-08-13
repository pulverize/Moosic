package co.moosic.music;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.TextChannel;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

import java.awt.*;
import java.util.concurrent.TimeUnit;

class MessageManager {
    private static TextChannel _lastChannel;

    public static void SendMessage(String message){
        SendMessage(_lastChannel,message);
    }

    public static void SendMessage(TextChannel channel, String message){
        SendMessage(channel,message,"DJ here");
    }
    private static void SendMessage(TextChannel channel, String message, String title){
        _lastChannel = channel;
        channel.sendMessage(
                new EmbedBuilder()
                        .setColor(Color.GREEN)
                        .addField(title,message,true)
                        .build()
        ).queue();
    }

    public static void SendTrackInfo(TextChannel channel, AudioTrack playingTrack){
        _lastChannel = channel;
        channel.sendMessage(
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

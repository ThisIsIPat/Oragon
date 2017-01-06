package util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.rmi.UnexpectedException;

public abstract class VoiceChannel {
    public static net.dv8tion.jda.core.entities.VoiceChannel fetchVoiceChannel(Guild guild, User user) {
        for (net.dv8tion.jda.core.entities.VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            if (voiceChannel.getMembers().contains(user))
                return voiceChannel;
        }
        return null;
    }
}

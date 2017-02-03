package util;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

public abstract class VoiceChannelUtil {
    public static net.dv8tion.jda.core.entities.VoiceChannel fetchVoiceChannel(Guild guild, User user) {
        for (net.dv8tion.jda.core.entities.VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            /*System.out.println(voiceChannel.getName());
            for (Member member : voiceChannel.getMembers())
                System.out.println(member.getUser().getName());
            System.out.println("---");*/
            if (voiceChannel.getMembers().contains(guild.getMember(user)))
                return voiceChannel;
        }
        return null;
    }
}

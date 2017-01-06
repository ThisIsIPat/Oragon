package command;

import core.Init;
import exception.CommandErrorInfoWrapper;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.VoiceChannel;

import java.util.Collections;
import java.util.List;

public class RoomLotteryCommand extends Command {
    
    public RoomLotteryCommand() {
        super("roomlottery", 0, 1);
    }

    @Override
    public boolean onGuildCommand(GuildMessageReceivedEvent event, String[] arguments) {
        int winnerAmount;
        if (arguments.length == 0)
            winnerAmount = 1;
        else
            try {
                winnerAmount = Integer.parseInt(arguments[0]);
            } catch (NumberFormatException e) {
                throw new CommandErrorInfoWrapper("Please enter a number after the command");
            }
        net.dv8tion.jda.core.entities.VoiceChannel prizeChannel = VoiceChannel.fetchVoiceChannel(event.getGuild(), event.getAuthor());
        if (prizeChannel == null)
            throw new CommandErrorInfoWrapper("You need to be in a voice channel so the winners can be dragged to you");

        net.dv8tion.jda.core.entities.VoiceChannel sourceChannel;
        
        // TODO: Add listener to jda object (directly here or better, indirectly (request user entry system)) that listens and checks for possible voice channels
        
        List<Member> prizeNominees = sourceChannel.getMembers();
        Collections.shuffle(prizeNominees, Init.getRand());
        for (int a = 0; a < winnerAmount && a < prizeNominees.size(); a++)
            event.getGuild().getController().moveVoiceMember(prizeNominees.get(a), prizeChannel);
        
        return true;
    }
}

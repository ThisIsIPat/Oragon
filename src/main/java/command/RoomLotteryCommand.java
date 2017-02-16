package command;

import core.Config;
import core.Init;
import exception.CommandErrorInfoWrapper;
import exception.ConfigLoadException;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import util.VoiceChannelUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RoomLotteryCommand extends Command {
    
    public RoomLotteryCommand() {
        super("roomlottery", 0, 2);
    }

    @Override
    protected boolean onGuildCommand(GuildMessageReceivedEvent event, String[] arguments) {
        final int winnerAmount;
        if (arguments.length == 0)
            winnerAmount = 1;
        else if (arguments.length == 1)
            try {
                winnerAmount = Integer.parseInt(arguments[0]);
            } catch (NumberFormatException e) {
                if (arguments[0].equalsIgnoreCase("help")) {
                    printHelp(event.getAuthor());
                    return true;
                }
                else
                    throw new CommandErrorInfoWrapper("Please enter a number after the command.");
            }
        else {
            if (!arguments[0].equalsIgnoreCase("setsource"))
                throw new CommandErrorInfoWrapper("Incorrect Syntax. Please enter \"roomlottery help\" for information about this command.");
            setSourceChannel(event.getGuild(), arguments[1]);
            event.getChannel().sendMessage("Successfully made "+getSourceChannel(event.getGuild()).getName()+" the *roomlottery* source channel.").queue();
            return true;
        }
        VoiceChannel prizeChannel = VoiceChannelUtil.fetchVoiceChannel(event.getGuild(), event.getAuthor());
        if (prizeChannel == null)
            throw new CommandErrorInfoWrapper("You need to be in a voice channel so the winners can be dragged to you!");

        final VoiceChannel sourceChannel = getSourceChannel(event.getGuild());
        
        List<Member> prizeNominees = new ArrayList<>(sourceChannel.getMembers());
        Collections.shuffle(prizeNominees, Init.getRand());
        for (int a = 0; a < winnerAmount && a < prizeNominees.size(); a++) {
            /*System.out.println(prizeNominees.get(0).getUser().getName());
            System.out.println(sourceChannel.getName());
            System.out.println(prizeChannel.getName());*/
            event.getGuild().getController().moveVoiceMember(prizeNominees.get(a), prizeChannel).queue();
        }
        
        return true;
    }

    @Override
    public CommandHelp getHelp() {
        return new CommandHelp("roomlottery { *number* | setsource *channelId* | help }",
                "The roomlottery command randomly selects winners from a source channel that has to be selected before." +
                        "To provide that source channel, right-click on the voice channel you wish to be the source channel, click on" +
                        "\"Copy ID\", and paste it after entering \"roomlottery set \", so the final command looks somewhat like this:\n" +
                        "**\"roomlottery setsource *VOICECHANNEL_ID*\"**\n" +
                        "If you want a certain amount to be moved to your current channel, use:\n" +
                        "**\"roomlottery *NUMBER*\"**\n" +
                        "The default amount used in case you enter no number is 1.",
                null);
    }

    private void setSourceChannel(Guild guild, String id) {
        final VoiceChannel sourceChannel = guild.getVoiceChannelById(id);
        if (sourceChannel == null)
            throw new CommandErrorInfoWrapper("Couldn't find a voice channel with the provided ID.");
        Config.put(guild, "roomlottery_sourceChannel", id);
        try {
            Config.storeConfig();
        } catch (ConfigLoadException e) {
            e.printStackTrace();
        }
    }
    
    private VoiceChannel getSourceChannel(Guild guild) {
        if (Config.contains(guild, "roomlottery_sourceChannel")) {
            final VoiceChannel sourceChannel = guild.getVoiceChannelById(Config.get(guild, "roomlottery_sourceChannel"));
            if (sourceChannel == null) {
                Config.put(guild, "roomlottery_sourceChannel", null);
                throw new CommandErrorInfoWrapper("The source channel that was previously set has been deleted.\n" +
                        "**Enter \"roomlottery help\" for help.**");
            } else
                return sourceChannel;
        }
        throw new CommandErrorInfoWrapper("Please use the command \"roomlottery setsource channelid\" first to set the channel where the winners come from.\n" +
                "**Enter \"roomlottery help\" for more information.**");
    }
    
}

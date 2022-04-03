import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class Commands extends ListenerAdapter {

    public String prefix = "/";

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        //checks is the message is from a bot
        if(event.getAuthor().isBot())
            return;

        //checks if the message starts with the prefix
        if(!event.getMessage().getContentRaw().startsWith(prefix))
            return;

        User author = event.getAuthor();
        Member member = event.getMember();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();

        //splits the contents of the message into an array
        String[] args = message.getContentRaw().split(" ");

        //checks if the message is from a private channel
        if(event.isFromType(ChannelType.PRIVATE)){
            PrivateChannel privateChannel = event.getPrivateChannel();
            if(args[0].equalsIgnoreCase(prefix + "reeport")){

                //checks if the user is blacklisted. Returns if so
                if(BlackList.isBlackList(author.getId())) {
                    privateChannel.sendMessage("BlackListed").queue();
                    return;
                }

                //checks is the user has made 5 reports in the last hour
                if(SpamPrevention.checkTimeReports(author.getId())){
                    SpamPrevention x = SpamPrevention.spamPreventionReport(author.getId());

                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setColor(new Color(102, 214, 238));
                    eb.setAuthor("Report ticket error");
                    eb.setDescription("You have made 5 tickets in the last hour. You may make another ticket in " + x.minutes + " minutes & " + x.seconds + " seconds.");

                    privateChannel.sendMessageEmbeds(eb.build()).queue();
                    return;
                }

                //checks is the report has too few arguments
                if(args.length < 4 ) {
                    EmbedBuilder eb = new EmbedBuilder();

                    eb.setColor(new Color(102, 214, 238));
                    eb.setAuthor("Report ticket error");
                    eb.setDescription("Too few arguments. Make sure to use the format below.\n/report [suspect] [evidence] [reason]");

                    privateChannel.sendMessageEmbeds(eb.build()).queue();
                    return;
                }

                String reason = "";
                for(int i = 3; i < args.length; i++)
                    reason = reason + args[i] + " ";

                System.currentTimeMillis();

                ReportTicket.reportFormat(new ReportTicket(author.getId(), args[1], args[2], System.currentTimeMillis(), reason), channel.getJDA());

            }
            return;
        }

        //if the user does not have ban perms, ignore message
        assert member != null;
        if(!(member.hasPermission(Permission.BAN_MEMBERS))){
            event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> No.").queue(Message -> {Message.delete().queueAfter(5, TimeUnit.SECONDS); message.delete().queueAfter(5, TimeUnit.SECONDS);});
            return;
        }

        if(args[0].equalsIgnoreCase(prefix + "available") || args[0].equalsIgnoreCase(prefix + "active")){
            if(ModCall.checkIfActive(author.getId())){
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Error");
                eb.setDescription("You are already available for mod calls");

                channel.sendMessageEmbeds(eb.build()).queue(message1 -> message1.delete());
                return;
            }

            ModCall.addMod(Storage.modCallMessage, author.getId(), event.getJDA());

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(new Color(102, 214, 238));
            eb.setDescription("You are now available for mod calls.");

            channel.sendMessageEmbeds(eb.build()).queue();
        }

        if(args[0].equalsIgnoreCase(prefix + "unavailable" ) || args[0].equalsIgnoreCase(prefix + "inactive")){
            if(!ModCall.checkIfActive(author.getId())){
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Error");
                eb.setDescription("You aren't available for mod calls");

                channel.sendMessageEmbeds(eb.build()).queue();
                return;
            }

            ModCall.removeMod(Storage.modCallMessage, author.getId(), event.getJDA());

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(new Color(102, 214, 238));
            eb.setDescription("You are no longer available for mod calls.");

            channel.sendMessageEmbeds(eb.build()).queue();
        }

        if(args[0].equalsIgnoreCase(prefix + "searchUser")){
            EmbedBuilder eb;
            eb = ReportTicket.searchUser(args[1]);
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        if(args[0].equalsIgnoreCase(prefix + "searchSuspect")){
            EmbedBuilder eb;
            eb = ReportTicket.searchSuspect(args[1]);
            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        if(args[0].equalsIgnoreCase(prefix + "blacklist")){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(102, 214, 238));

            if(!(args.length < 3)) {
                if(!(BlackList.isBlackList(args[1]))){
                    BlackList.blackListUser(args[1], args[2]);
                    eb.setDescription("The user \"" + args[1] + "\" has been blacklisted");

                    channel.sendMessageEmbeds(eb.build()).queue();
                }else {
                    eb.setTitle("Error");
                    eb.setDescription("The user \"" + args[1] + "\" has already been blacklisted");
                    channel.sendMessageEmbeds(eb.build()).queue();
                }

            }else if (args.length == 2){
                eb.setTitle("Error");
                eb.setDescription("Please enter a reason for the blacklist");
                channel.sendMessageEmbeds(eb.build()).queue();
            }else{
                eb.setTitle("Error");
                eb.setDescription("Please enter a discord ID and reason for the blacklist");
                channel.sendMessageEmbeds(eb.build()).queue();
            }
            return;
        }

        if(args[0].equalsIgnoreCase(prefix + "removeblacklist") || args[0].equalsIgnoreCase(prefix + "blacklistremove")){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(102, 214, 238));

            if(BlackList.isBlackList(args[1])){
                BlackList.removeBlackList(args[1]);

                eb.setDescription("The user \"" + args[1] + "\" has been removed from the blacklist");

                channel.sendMessageEmbeds(eb.build()).queue();
            }else{
                eb.setTitle("Error");
                eb.setDescription("The user \"" + args[1] + "\" is not blacklisted");

                channel.sendMessageEmbeds(eb.build()).queue();
            }
        }

        if(args[0].equalsIgnoreCase(prefix + "help") || args[0].equalsIgnoreCase(prefix + "commands") || args[0].equalsIgnoreCase(prefix + "cmds")){
            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(new Color(102, 214, 238));
            eb.setAuthor("Server Commands");
            eb.addField("/searchuser [discord ID]", "Returns a list of reports made by the discord user", false);
            eb.addField("/searchsuspect [roblox username]","Returns a list of reports for the roblox user",false);
            eb.addField("/blacklist [discord ID] [reason]", "Blacklists a discord user from making tickets", false);
            eb.addField("/removeblacklist [discord ID]", "Removes the blacklist from a discord user", false);
            eb.addField("/active", "Sets you as available for mod calls", false);
            eb.addField("/inactive", "Sets you as unavailable for mod calls", false);
            eb.addField("/buildmodcall", "Builds the message in the mod call channel.\nRequires permission to use.", false);

            channel.sendMessageEmbeds(eb.build()).queue();
            return;
        }

        String ownerID = "395011406426144778";
        if(args[0].equalsIgnoreCase(prefix + "buildmodcall") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            ModCall.buildChannel(author.getJDA());
            return;
        }

        if(args[0].equalsIgnoreCase(prefix + "clearmodcall") && (author.getId().equals(ownerID) || Objects.requireNonNull(event.getGuild().getMember(author)).hasPermission(Permission.ADMINISTRATOR))){
            ModCall.clear(author.getJDA());
            return;
        }

    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        Member member = event.getMember();

        //checks if the user has ban permission (I.E. is a mod or higher) and returns if they don't
        assert member != null;
        if(!(member.hasPermission(Permission.BAN_MEMBERS))){
            System.out.println("No button perms");
            event.deferEdit().queue();
            return;
        }



        if(event.getChannel().getId().equals(Storage.getModCallC())){
            if(event.getComponentId().startsWith("available")){
                event.deferEdit().queue();
                if(!ModCall.checkIfActive(event.getUser().getId()))
                    ModCall.addMod(event.getMessageId(), event.getUser().getId(), event.getJDA());
            }

            if(event.getComponentId().startsWith("unavailable")){
                event.deferEdit().queue();

                if(ModCall.checkIfActive(event.getUser().getId()))
                    ModCall.removeMod(event.getMessageId(), event.getUser().getId(), event.getJDA());

            }
        }

        if (event.getChannel().getId().equals(Storage.getGameTicketC())) {

            if (event.getComponentId().startsWith("acceptReport")) {
                event.deferEdit().queue();
                event.getMessage().delete().queue();


                String[] args = event.getComponentId().split(",");
                String discordUser = ReportTicket.getDiscordID(args[1]);
                String suspect = ReportTicket.getSuspect(args[1]);

                ReportTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                User user = event.getJDA().getUserById(discordUser);

                assert user != null;
                user.openPrivateChannel().queue(channel ->
                        channel.sendMessage("Your report for the user \"**" + suspect + "**\" has been accepted!\nThank you for reporting!").queue());
                return;
            }

            if (event.getComponentId().startsWith("denyReport")) {
                event.deferEdit().queue();
                event.getMessage().delete().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = ReportTicket.getDiscordID(args[1]);
                String suspect = ReportTicket.getSuspect(args[1]);

                ReportTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());

                User user = event.getJDA().getUserById(discordUser);

                assert user != null;
                user.openPrivateChannel().queue(channel ->
                        channel.sendMessage("Your report for the user \"**" + suspect + "**\" has been denied.\nThis could have happened for several reasons. Please review what we need in a report by using **/reportinfo**.").queue());
            }

            if (event.getComponentId().startsWith("customReport")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = ReportTicket.getDiscordID(args[1]);
                String suspect = ReportTicket.getSuspect(args[1]);

                User user = event.getJDA().getUserById(discordUser);

                event.getChannel().sendMessage("Please enter the message you want to send <@" + event.getUser().getId() + ">").queue(Message -> {Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e -> e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e3 -> {
                            System.out.println("true");
                            event.getChannel().sendMessage("The message you are about to send is \"" + e3.getMessage().getContentRaw() + "\"").queue();
                            event.getMessage().delete().queue();
                            ReportTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());
                        },
                        // if the user takes more than 3 minutes, time out
                        3, TimeUnit.SECONDS, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +"> you took too long. Feel free to try again.").queue();});});


                assert user != null;
                user.openPrivateChannel().queue(channel ->
                        channel.sendMessage("Your report for the user \"**" + suspect + "**\" has been denied.\nThis could have happened for several reasons. Please review what we need in a report by using **/reportinfo**.").queue());

            }

            if (event.getComponentId().startsWith("acceptAppeal")) {
                event.deferEdit().queue();
                event.getMessage().delete().queue();


                String[] args = event.getComponentId().split(",");
                String discordUser = AppealTicket.getDiscordID(args[1]);
                String suspect = AppealTicket.getSuspect(args[1]);

                AppealTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                User user = event.getJDA().getUserById(discordUser);

                assert user != null;
                user.openPrivateChannel().queue(channel ->
                        channel.sendMessage("SET").queue());
                return;
            }

            if (event.getComponentId().startsWith("denyAppeal")) {
                event.deferEdit().queue();
                event.getMessage().delete().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = AppealTicket.getDiscordID(args[1]);
                String suspect = AppealTicket.getSuspect(args[1]);

                AppealTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());

                User user = event.getJDA().getUserById(discordUser);

                assert user != null;
                user.openPrivateChannel().queue(channel ->
                        channel.sendMessage("SET").queue());
            }
        }
    }
}

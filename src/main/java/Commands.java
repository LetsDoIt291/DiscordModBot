import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class Commands extends ListenerAdapter {

    public String prefix = "-";

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

            if(args[0].equalsIgnoreCase(prefix + "help")){
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Commands");
                eb.addField("-gamereport", "Starts an in-game report and gives relevant information", false);
                eb.addField("-gamereportinfo", "Gives information on in-game reports", false);
                eb.addField("-appeal", "Starts an in-game appeal and gives relevant information", false);

                channel.sendMessageEmbeds(eb.build()).queue();
                return;
            }

            if(args[0].equalsIgnoreCase(prefix + "gamereportinfo")){
                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Game Report Information");
                eb.addField("Exploit Reports", "> Must be a video. The only exception are images that display the user flying (or any other impossible positions for someone to be in) and also displays their username."
                        + "\n\n> Videos must be uploaded to sites like https://www.youtube.com/ or any other video streaming platform (please avoid using streamable as the videos do expire)."
                        + "\n\n> Images must be uploaded to https://imgur.com/"
                        + "\n\n> The video/image must show:\n> 1. Exploits (the Kill feed or in-game stats are not valid)\n> 2. A username that is readable (a display name is not a username).\n ",false);

                channel.sendMessageEmbeds(eb.build()).queue();
                return;
            }

            return;
        }

        //if the user does not have ban perms, ignore message
        assert member != null;
        if(!(member.hasPermission(Permission.BAN_MEMBERS))){
            event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> No.").queue(Message -> {Message.delete().queueAfter(5, TimeUnit.SECONDS); message.delete().queueAfter(5, TimeUnit.SECONDS);});
            return;
        }

        if(args[0].equalsIgnoreCase(prefix + "searchappealer")){
            ArrayList<EmbedBuilder> appeals = AppealTicket.searchAppealer(args[1]);

            if(!appeals.isEmpty()){
                for(int i = 0; i < appeals.size(); i++){
                    channel.sendMessageEmbeds(appeals.get(i).build()).queue();
                }
            }else{
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Game appeals by " + args[1]);
                eb.setDescription("No appeals found");

                channel.sendMessageEmbeds(eb.build()).queue();
            }

            return;
        }

        if(args[0].equalsIgnoreCase(prefix + "available") || args[0].equalsIgnoreCase(prefix + "active")){
            if(ModCall.checkIfActive(author.getId())){
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Error");
                eb.setDescription("You are already available for mod calls");

                channel.sendMessageEmbeds(eb.build()).queue(Message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
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

            channel.sendMessageEmbeds(eb.build()).queue(Message -> message.delete().queueAfter(5, TimeUnit.SECONDS));
        }

        if(args[0].equalsIgnoreCase(prefix + "searchUser")){
            ArrayList<EmbedBuilder> reports = ReportTicket.searchUser(args[1]);

            if(!reports.isEmpty()){
                for(int i = 0; i < reports.size(); i++){
                    channel.sendMessageEmbeds(reports.get(i).build()).queue();
                }
            }else{
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Game Reports by " + args[1]);
                eb.setDescription("No reports found");

                channel.sendMessageEmbeds(eb.build()).queue();
            }

            return;
        }

        if(args[0].equalsIgnoreCase(prefix + "searchSuspect")){

            ArrayList<EmbedBuilder> reports = ReportTicket.searchSuspect(args[1]);

            if(!reports.isEmpty()){
                for(int i = 0; i < reports.size(); i++){
                    channel.sendMessageEmbeds(reports.get(i).build()).queue();
                }
            }else{
                EmbedBuilder eb = new EmbedBuilder();

                eb.setColor(new Color(102, 214, 238));
                eb.setAuthor("Game Reports for " + args[1]);
                eb.setDescription("No reports found");

                channel.sendMessageEmbeds(eb.build()).queue();
            }

            return;
        }

        if(args[0].equalsIgnoreCase(prefix + "blacklist")){
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(new Color(102, 214, 238));

            if(!(args.length < 3)) {
                if(!(event.getJDA().getUserById(args[1]) == null)){
                    if(!(BlackList.isBlackList(args[1]))){
                        BlackList.blackListUser(args[1], args[2], author.getId(), event.getJDA());
                        eb.setDescription("The user \"" + args[1] + "\" has been blacklisted");

                        channel.sendMessageEmbeds(eb.build()).queue();
                    }else {
                        eb.setTitle("Error");
                        eb.setDescription("The user \"" + args[1] + "\" has already been blacklisted");
                        channel.sendMessageEmbeds(eb.build()).queue();
                    }
                }else{
                    eb.setTitle("Error");
                    eb.setDescription("User does not exist");
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

            if(args.length < 2) return;

            if(BlackList.isBlackList(args[1])){
                BlackList.removeBlackList(args[1], author.getId(), event.getJDA());

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
            eb.addField("-searchuser [discord ID]", "Returns a list of reports made by the discord user", false);
            eb.addField("-searchsuspect [roblox username]","Returns a list of reports for the roblox user",false);
            eb.addField("-searchappealer [discord ID]","Returns a list of appeals for the discord user",false);
            eb.addField("-blacklist [discord ID] [reason]", "Blacklists a discord user from making tickets", false);
            eb.addField("-removeblacklist [discord ID]", "Removes the blacklist from a discord user", false);
            eb.addField("-active", "Sets you as available for mod calls", false);
            eb.addField("-inactive", "Sets you as unavailable for mod calls", false);
            eb.addField("-buildmodcall", "Builds the message in the mod call channel.\nRequires permission to use.", false);
            eb.addField("-clearmodcall", "Clears all users from mod call.\nRequires permission to use.", false);

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

        if(!event.isFromGuild()){
            return;
        }

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
                String discordUser = Storage.reportSort().get(Integer.parseInt(args[1])).userID;
                String suspect = Storage.reportSort().get(Integer.parseInt(args[1])).suspect;

                ReportTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());


                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Game report for the user \"" +suspect + "\" has been accepted!");
                eb.setDescription("Thank you for reporting!");
                eb.setFooter(">> Replying to this message will do nothing <<");

                event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                        .queue();
                return;
            }

            if (event.getComponentId().startsWith("denyReport")) {
                event.deferEdit().queue();
                event.getMessage().delete().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.reportSort().get(Integer.parseInt(args[1])).userID;
                String suspect = Storage.reportSort().get(Integer.parseInt(args[1])).suspect;

                ReportTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Game report for the user \"" +suspect + "\" has been denied.");
                eb.setDescription("> We either concluded the user in question was not exploiting or the evidence provided was insufficient. Please review what we need in a report by using the command -gamereportinfo");
                eb.setFooter(">> Replying to this message will do nothing <<");

                event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                        .queue();
            }

            if (event.getComponentId().startsWith("customReportAccepted")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.reportSort().get(Integer.parseInt(args[1])).userID;
                String suspect = Storage.reportSort().get(Integer.parseInt(args[1])).suspect;

                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the message you want to send. <@" + event.getUser().getId() + ">").queue(Message -> {Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(new Color(102, 214, 238));
                            eb.setTitle("Game report for the user \"" +suspect + "\" has been accepted!");
                            eb.setDescription("You've received the following message from a mod about your report: \"" + e1.getMessage().getContentRaw() + "\"");
                            eb.setFooter(">> Replying to this message will do nothing <<");

                            Message.delete().queue();
                            event.getChannel().sendMessageEmbeds(eb.build()).queue(MessageEmbed -> MessageEmbed.delete().queueAfter(1, TimeUnit.MINUTES));
                            event.getChannel().sendMessage("Is this the message you want to send the user?\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));


                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Message has been sent").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));
                                            ReportTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                                            //sends the message to the reporter
                                            event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                                                    .queue();

                                        }else{
                                            event.getChannel().sendMessage("Message has been discarded").queue(Message3 -> {Message.delete().queueAfter(5, TimeUnit.SECONDS); Message3.delete().queueAfter(5, TimeUnit.SECONDS);});
                                            ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 3 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queue(); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 3 minutes, time out
                        3, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.SECONDS); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
            });}

            if (event.getComponentId().startsWith("userIncorrect")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.reportSort().get(Integer.parseInt(args[1])).userID;
                String suspect = Storage.reportSort().get(Integer.parseInt(args[1])).suspect;

                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the correct username. <@" + event.getUser().getId() + ">").queue(Message -> {Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            Message.delete().queue();
                            event.getChannel().sendMessage("Is this the correct username? \"" + e1.getMessage().getContentRaw() + "\"\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));

                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Username has been changed").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));

                                            Storage.reportChangeUsername(args[1], e1.getMessage().getContentRaw());

                                            ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));
                                        }else{
                                            event.getChannel().sendMessage("Ticket has been returned unchanged").queue(Message3 -> {Message.delete().queueAfter(5, TimeUnit.SECONDS); Message3.delete().queueAfter(5, TimeUnit.SECONDS);});
                                            ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 3 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queue(); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 3 minutes, time out
                        3, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.SECONDS); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
                });}

            if (event.getComponentId().startsWith("customReportDenied")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.reportSort().get(Integer.parseInt(args[1])).userID;
                String suspect = Storage.reportSort().get(Integer.parseInt(args[1])).suspect;

                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the message you want to send. <@" + event.getUser().getId() + ">").queue(Message -> {Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(new Color(102, 214, 238));
                            eb.setTitle("Game report for the user \"" +suspect + "\" has been denied.");
                            eb.setDescription("You've received the following message from a mod about your report: \"" + e1.getMessage().getContentRaw() + "\"");
                            eb.setFooter(">> Replying to this message will do nothing <<");

                            Message.delete().queue();
                            event.getChannel().sendMessageEmbeds(eb.build()).queue(MessageEmbed -> MessageEmbed.delete().queueAfter(1, TimeUnit.MINUTES));
                            event.getChannel().sendMessage("Is this the message you want to send the user?\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));


                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Message has been sent").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));
                                            ReportTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());

                                            //sends the message to the reporter
                                            event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                                                    .queue();
                                        }else{
                                            event.getChannel().sendMessage("Message has been discarded").queue(Message3 -> Message.delete().queueAfter(5, TimeUnit.SECONDS));
                                            ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 1 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {System.out.println("TRUE"); event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 3 minutes, time out
                        3, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 -> Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); ReportTicket.reportFormat(Storage.reportSort().get(Integer.parseInt(args[1])), member.getJDA(),  Integer.parseInt(args[1]));});
            });}

            if (event.getComponentId().startsWith("acceptAppeal")) {
                event.deferEdit().queue();
                event.getMessage().delete().queue();


                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.appealSort().get(Integer.parseInt(args[1])).userID;


                AppealTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Your game appeal has been accepted!");
                eb.setFooter(">> Replying to this message will do nothing <<");

                event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                        .queue();
                return;
            }

            if (event.getComponentId().startsWith("denyAppeal")) {
                event.deferEdit().queue();
                event.getMessage().delete().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.appealSort().get(Integer.parseInt(args[1])).userID;

                AppealTicket.archive(args[1], event.getUser().getId(), 1, event.getJDA());

                EmbedBuilder eb = new EmbedBuilder();
                eb.setColor(new Color(102, 214, 238));
                eb.setTitle("Your game appeal has been denied");
                eb.setDescription("You may make another appeal.");
                eb.setFooter(">> Replying to this message will do nothing <<");

                event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                        .queue();
            }

            if (event.getComponentId().startsWith("customAppealAccepted")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.appealSort().get(Integer.parseInt(args[1])).userID;

                AppealTicket appeal = Storage.appealSort().get(Integer.parseInt(args[1]));

                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the message you want to send. <@" + event.getUser().getId() + ">").queue(Message -> Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(new Color(102, 214, 238));
                            eb.setTitle("Your game appeal has been accepted!");
                            eb.setDescription("You've received the following message from a mod about your appeal: \"" + e1.getMessage().getContentRaw() + "\"");
                            eb.setFooter(">> Replying to this message will do nothing <<");

                            Message.delete().queue();
                            event.getChannel().sendMessageEmbeds(eb.build()).queue(MessageEmbed -> MessageEmbed.delete().queueAfter(1, TimeUnit.MINUTES));
                            event.getChannel().sendMessage("Is this the message you want to send the user?\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));


                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Message has been sent").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));
                                            AppealTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                                            //sends the message to the reporter
                                            event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                                                    .queue();
                                        }else{
                                            event.getChannel().sendMessage("Message has been discarded.\nTicket returned.").queue(Message3 -> Message.delete().queueAfter(15, TimeUnit.SECONDS));
                                            AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 1 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 ->
                                            Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 2 minutes, time out
                        2, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 ->
                                Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));}));
            }

            if (event.getComponentId().startsWith("customAppealDenied")) {
                event.deferEdit().queue();

                String[] args = event.getComponentId().split(",");
                String discordUser = Storage.appealSort().get(Integer.parseInt(args[1])).userID;

                AppealTicket appeal = Storage.appealSort().get(Integer.parseInt(args[1]));

                //deletes the message so no other buttons can be pressed. (if the action is not completed, the message will be remade) P.S. if the bot turns off in the middle of this... RIP
                event.getMessage().delete().queue();

                event.getChannel().sendMessage("Ticket temporarily removed.\nPlease enter the message you want to send. <@" + event.getUser().getId() + ">").queue(Message -> Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                        // make sure it's by the same user, and in the same channel,etc
                        e ->    e.getAuthor().getId().equals(event.getUser().getId())
                                && e.getChannel().equals(event.getChannel())
                                && e.getMessage().getAttachments().isEmpty(),
                        // response
                        e1 -> {
                            EmbedBuilder eb = new EmbedBuilder();
                            eb.setColor(new Color(102, 214, 238));
                            eb.setTitle("Your game appeal has been denied.");
                            eb.setDescription("You've received the following message from a mod about your appeal: \"" + e1.getMessage().getContentRaw() + "\"");
                            eb.setFooter(">> Replying to this message will do nothing <<");

                            Message.delete().queue();
                            event.getChannel().sendMessageEmbeds(eb.build()).queue(MessageEmbed -> MessageEmbed.delete().queueAfter(1, TimeUnit.MINUTES));
                            event.getChannel().sendMessage("Is this the message you want to send the user?\nType \"Yes\" or \"No\"").queue(Message2 -> Message2.delete().queueAfter(1, TimeUnit.MINUTES));


                            Bot.waiter.waitForEvent(MessageReceivedEvent.class,
                                    // make sure it's by the same user, and in the same channel,etc
                                    e ->    {
                                        if(e.getAuthor().getId().equals(event.getUser().getId()) && e.getChannel().equals(event.getChannel()) && e.getMessage().getAttachments().isEmpty()){
                                            if(e.getMessage().getContentRaw().equalsIgnoreCase("no")){
                                                return true;
                                            }else return e.getMessage().getContentRaw().equalsIgnoreCase("yes");
                                        }
                                        return false;
                                    },
                                    // response
                                    e2 -> {
                                        if(e2.getMessage().getContentRaw().equalsIgnoreCase("yes")) {
                                            event.getChannel().sendMessage("Message has been sent").queue(Message3 -> Message3.delete().queueAfter(5, TimeUnit.SECONDS));
                                            event.getMessage().delete().queue();
                                            AppealTicket.archive(args[1], event.getUser().getId(), 0, event.getJDA());

                                            //sends the message to the reporter
                                            event.getJDA().openPrivateChannelById(discordUser).flatMap(channel -> channel.sendMessageEmbeds(eb.build()))
                                                    .queue();
                                        }else{
                                            event.getChannel().sendMessage("Message has been discarded.\nTicket returned.").queue(Message3 -> Message.delete().queueAfter(15, TimeUnit.SECONDS));
                                            AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));
                                        }
                                    },
                                    // if the user takes more than 1 minutes, time out
                                    1, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 ->
                                            Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));});
                        },
                        // if the user takes more than 2 minutes, time out
                        2, TimeUnit.MINUTES, () -> {event.getChannel().sendMessage("Sorry, <@" + event.getUser().getId() +">, you took too long. Feel free to try again.").queue(Message2 ->
                                Message2.delete().queueAfter(1,TimeUnit.MINUTES)); Message.delete().queueAfter(3, TimeUnit.MINUTES); AppealTicket.appealFormat(appeal, member.getJDA(), Integer.parseInt(args[1]));}));
            }
        }
    }
}
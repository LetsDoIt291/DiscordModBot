import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AppealTicket{

    public String userID;
    public String robloxUsername;
    public String banReason;
    public String time;
    public String reasonToUnban;

    public AppealTicket(String userID, String robloxUsername, String banReason, Long time, String reasonToUnban){
        this.userID = userID;
        this.robloxUsername = robloxUsername;
        this.banReason = banReason;
        this.time = String.valueOf(time);
        this.reasonToUnban = reasonToUnban;


        String report = userID + "," + robloxUsername + "," + banReason + "," + time + "," + reasonToUnban;


        //writes the report into a given file. append is true so the previous content is not overridden2
        try {
            FileWriter myWriter = new FileWriter(Storage.appealsFilePath, true);
            myWriter.write(report + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the Appeals file. AppealTicket");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the Appeals file. AppealTicket");
            e.printStackTrace();
        }

    }

    public static EmbedBuilder searchSuspect(String user){
        ArrayList<String> appeals = Storage.appealSort();

        String info = "";

        //Searches for all the appeals of a specific exploiter
        for(int i = 0; i < appeals.size(); i++){
            String[] args = appeals.get(i).split(",");

            if(user.equalsIgnoreCase(args[1])){
                info = info + i + ",";
            }
        }


        //makes everything look pretty
        EmbedBuilder eb = new EmbedBuilder();
        int found = 0;

        for(int i = 0; i < appeals.size(); i++){
            String[] args = appeals.get(i).split(",");
            if(args[1].equalsIgnoreCase(user)){
                found++;
                eb.addField("Appeal ID: " + i, "Roblox username: " + args[1] + "\nDiscord ID: " + args[0] + "\nReason for ban: " + args[2] + "\nReason to unban: " + args[4], false);
            }
        }
        if(found == 0)
            eb.setDescription("No appeals found");

        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Appeals for user " + user);

        return eb;

    }

    public static void appealFormat(AppealTicket r, JDA jda){

        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.gameTicketC);


        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Report ID: " + (Storage.appealSort().size()-1));
        eb.addField("Username", r.robloxUsername, false);
        eb.addField("Reason for ban", r.banReason, false);
        eb.addField("Reason to be unbanned", r.reasonToUnban, false);
        eb.setFooter("Appeal submitted by: " + r.userID);

        assert c != null;
        c.sendMessageEmbeds(eb.build())
                .setActionRow(Button.success("acceptAppeal," + (Storage.appealSort().size()-1), "Accept"), Button.danger("denyAppeal," + (Storage.appealSort().size()-1), "Deny"))
                .queue();
    }

    public static String getDiscordID(String id){
        String[] args = Storage.appealSort().get(Integer.parseInt(id)).split(",");

        return args[0];
    }

    public static String getSuspect(String id){
        String[] args = Storage.appealSort().get(Integer.parseInt(id)).split(",");

        return args[1];
    }

    public static void archive(String aID, String modID, int type, JDA jda){
        ArrayList<String> reports = Storage.appealSort();
        String status;

        if(type == 0)
            status = "accepted";
        else
            status = "denied";

        String[] args = reports.get(Integer.parseInt(aID)).split(",");

        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.archiveC);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Appeal ID: " + aID);
        eb.addField("Username", args[1], false);
        eb.addField("Reason for unban", args[2], false);
        eb.addField("Reason to be unbanned", args[3], false);
        eb.setFooter("Appeal submitted by: " + args[0] + "\nAppeal " + status + " by: " + modID);

        assert c != null;
        c.sendMessageEmbeds(eb.build()).queue();

    }
}

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class ReportTicket {

    public String userID;
    public String suspect;
    public String evidence;
    public String time;
    public String reason;

    public ReportTicket(String userID, String suspect, String evidence, Long time, String reason){
        this.userID = userID;
        this.suspect = suspect;
        this.evidence = evidence;
        this.time = String.valueOf(time);
        this.reason = reason;


        String report = userID + "," + suspect + "," + evidence + "," + time + "," + reason;


        //writes the report into a given file. append is true so the previous content is not overridden2
        try {
            FileWriter myWriter = new FileWriter(Storage.reportsFilePath, true);
            myWriter.write(report + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the reports file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the reports file.");
            e.printStackTrace();
        }

    }

    public static EmbedBuilder searchSuspect(String user){
        ArrayList<String> reports = Storage.reportSort();

        String info = "";

        //Searches for all the reports of a specific exploiter
        for(int i = 0; i < reports.size(); i++){
            String[] args = reports.get(i).split(",");

            if(user.equalsIgnoreCase(args[1])){
                info = info + i + ",";
            }
        }


        //makes everything look pretty
        EmbedBuilder eb = new EmbedBuilder();
        int found = 0;

        for(int i = 0; i < reports.size(); i++){
            String[] args = reports.get(i).split(",");
            if(args[1].equalsIgnoreCase(user)){
                found++;
                eb.addField("Report ID: " + i,"Reporter: " + args[0] + "\nReason: " + args[2] + "\nEvidence: " + args[2], false);
            }
        }
        if(found == 0)
            eb.setDescription("No reports found");

        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Reports for user " + user);

        return eb;

    }

    public static EmbedBuilder searchUser(String userID){
        ArrayList<String> reports = Storage.reportSort();

        String info = "";

        //searches for all the reports from a specific person
        for(int i = 0; i < reports.size(); i++){
            String[] args = reports.get(i).split(",");

            if(userID.equalsIgnoreCase(args[0])){
                info = info + i + ",";
            }
        }


        //makes everything pretty
        EmbedBuilder eb = new EmbedBuilder();
        int found = 0;

        for(int i = 0; i < reports.size(); i++){
            String[] args = reports.get(i).split(",");
            if(args[0].equalsIgnoreCase(userID)){
                found++;
                eb.addField("Report ID: " + i,"Suspect: " + args[1] + "\nReason: " + args[3] + "\nEvidence: " + args[2], false);
            }
        }
        if(found == 0)
            eb.setDescription("No reports found");

        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Reports by " + userID);

        return eb;

    }

    public static void reportFormat(ReportTicket r, JDA jda){

        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.gameTicketC);


        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Report ID: " + (Storage.reportSort().size()-1));
        eb.addField("Suspect", r.suspect, false);
        eb.addField("Reason", r.reason, false);
        eb.addField("Evidence", r.evidence, false);
        eb.setFooter("Report submitted by: " + r.userID);

        assert c != null;
        c.sendMessageEmbeds(eb.build())
                .setActionRow(
                        Button.success("acceptReport," + (Storage.reportSort().size()-1), "Accept"),
                        Button.danger("denyReport," + (Storage.reportSort().size()-1), "Deny"),
                        Button.secondary("customReport," + (Storage.reportSort().size()-1),"Custom Message"))
                .queue();
    }

    public static String getDiscordID(String id){
        String[] args = Storage.reportSort().get(Integer.parseInt(id)).split(",");

        return args[0];
    }

    public static String getSuspect(String id){
        String[] args = Storage.reportSort().get(Integer.parseInt(id)).split(",");

        return args[1];
    }

    public static void archive(String rID, String modID, int type, JDA jda){
        ArrayList<String> reports = Storage.reportSort();
        String status;

        if(type == 0)
            status = "accepted";
        else
            status = "denied";

        String[] args = reports.get(Integer.parseInt(rID)).split(",");

        TextChannel c = Objects.requireNonNull(jda.getGuildById(Storage.server)).getTextChannelById(Storage.archiveC);

        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(new Color(102, 214, 238));
        eb.setAuthor("Report ID: " + rID);
        eb.addField("Suspect", args[1], false);
        eb.addField("Reason", args[4], false);
        eb.addField("Evidence", args[2], false);
        eb.setFooter("Report submitted by: " + args[0] + "\nReport " + status + " by: " + modID);

        assert c != null;
        c.sendMessageEmbeds(eb.build()).queue();

    }
}

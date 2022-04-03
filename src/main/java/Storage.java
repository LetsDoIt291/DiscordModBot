import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    static final String reportsFilePath = "C:\\Users\\Letsd\\Desktop\\Reports.txt";
    static final String infoFilePath = "C:\\Users\\Letsd\\Desktop\\information.txt";
    static final String blackListFilePath = "C:\\Users\\Letsd\\Desktop\\Blacklist.txt";
    static final String availableModsFilePath = "C:\\Users\\Letsd\\Desktop\\Modcall.txt";
    static final String appealsFilePath = "C:\\Users\\Letsd\\Desktop\\Appeals.txt";
    static final String openTicketFilePath = "C:\\Users\\Letsd\\Desktop\\TicketOpen.txt";
    static final String activeAppealsFilePath ="C:\\Users\\Letsd\\Desktop\\ActiveAppeals.txt";

    public static String gameTicketC = readInfoFile(0);
    public static String ticketArchiveC = readInfoFile(1);
    public static String server = readInfoFile(2);
    public static String archiveC = readInfoFile(3);
    public static String modCallC = readInfoFile(4);
    public static String modCallMessage = readInfoFile(5);
    public static String discordTicketC = readInfoFile(6);

    public static void addUserToActiveAppeals(String userID){
        try {
            FileWriter myWriter = new FileWriter(activeAppealsFilePath, true);
            myWriter.write(userID + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the ActiveAppeals file. (addUserToActiveAppeals)");
        }catch (IOException e){
            System.out.println("An error occurred while writing in the ActiveAppeals file. (addUserToActiveAppeals)");
            e.printStackTrace();
        }
    }

    public static void clearActiveAppeals(){
        try {
            FileWriter myWriter = new FileWriter(activeAppealsFilePath);
            myWriter.write("");
            myWriter.close();
            System.out.println("Successfully wrote to the ActiveAppeals file. (clearActiveAppeals)");
        }catch (IOException e){
            System.out.println("An error occurred while writing in the ActiveAppeals file. (clearActiveAppeals)");
            e.printStackTrace();
        }
    }

    public static void removeUserFromActiveAppeals(String userID){
        String idList = "";

        //read the file and looks for the ID
        try {
            File myObj = new File(activeAppealsFilePath);
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                if(!myReader.nextLine().equals(userID))
                    idList = idList + myReader.nextLine() + "\n";
            }
            myReader.close();
            System.out.println("Successfully read the ActiveAppeals file. (removeUserFromActiveAppeals)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the ActiveAppeals file. (removeUserFromActiveAppeals");
            e.printStackTrace();
        }

        System.out.println(idList);

        //rewrites the available mods file without the given ID
        try {
            FileWriter myWriter = new FileWriter(activeAppealsFilePath);
            myWriter.write(idList);
            myWriter.close();
            System.out.println("Successfully wrote to the ActiveAppeals file. (removeUserFromActiveAppeals)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the ActiveAppeals. (removeUserFromActiveAppeals)");
            e.printStackTrace();
        }
    }

    public static boolean checkUserActiveAppeal(String userID){
        //read the file and looks for the ID
        try {
            File myObj = new File(activeAppealsFilePath);
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                if(myReader.nextLine().equals(userID)) {
                    myReader.close();
                    System.out.println("Successfully read the ActiveAppeals file. (checkUserActiveAppeal)");
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the ActiveAppeals file. (checkUserActiveAppeal");
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<String> appealSort(){
        ArrayList<String> appeals = new ArrayList<>();

        //puts all the content of the file into an array. the index position is how report IDs are determined. (used within other methods)
        try {
            File myObj = new File(Storage.appealsFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                appeals.add(myReader.nextLine());
            }
            myReader.close();
            System.out.println("Successfully read and wrote from the appeals file. (appealSort)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the appeals file. (appealSort)");
            e.printStackTrace();
        }

        return appeals;
    }

    public static void addUserToOpenTicket(String userID){
        try {
            FileWriter myWriter = new FileWriter(openTicketFilePath, true);
            myWriter.write(userID + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the openTicket file. (addUserToOpenTicket)");
        }catch (IOException e){
            System.out.println("An error occurred while writing in the openTicket file. (addUserToOpenTicket)");
            e.printStackTrace();
        }
    }

    public static void removeUserFromOpenTicket(String userID){
        String idList = "";

        //read the file and looks for the ID
        try {
            File myObj = new File(openTicketFilePath);
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                if(!myReader.nextLine().equals(userID))
                    idList = idList + myReader.nextLine() + "\n";
            }
            myReader.close();
            System.out.println("Successfully read the openTicket file. (removeUserFromOpenTicket)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the openTicket file. (removeUserFromOpenTicket");
            e.printStackTrace();
        }

        System.out.println(idList);

        //rewrites the available mods file without the given ID
        try {
            FileWriter myWriter = new FileWriter(openTicketFilePath);
            myWriter.write(idList);
            myWriter.close();
            System.out.println("Successfully wrote to the openTicket file. (removeUserFromOpenTicket)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the openTicket. (removeUserFromOpenTicket)");
            e.printStackTrace();
        }
    }

    public static void clearOpenTicket(){
        try {
            FileWriter myWriter = new FileWriter(openTicketFilePath);
            myWriter.write("");
            myWriter.close();
            System.out.println("Successfully wrote to the openTicket file. (clearOpenTicket)");
        }catch (IOException e){
            System.out.println("An error occurred while writing in the openTicket file. (clearOpenTicket)");
            e.printStackTrace();
        }
    }

    public static boolean checkUserOpenTicket(String userID){
        //read the file and looks for the ID
        try {
            File myObj = new File(openTicketFilePath);
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                if(myReader.nextLine().equals(userID)) {
                    myReader.close();
                    System.out.println("Successfully read the openTicket file. (checkUserOpenTicket)");
                    return true;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the openTicket file. (checkUserOpenTicket");
            e.printStackTrace();
        }

        return false;
    }

    public static String getGameTicketC(){
        return gameTicketC;
    }

    public static String getModCallC(){
        return modCallC;
    }

    public static void changeInfoFile(String newID, int index){
        ArrayList<String> info = new ArrayList<>();

        //reads the file and adds every line found to the array list above
        try {
            File myObj = new File(infoFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                info.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading info file. (changeInfoFile)");
            e.printStackTrace();
        }

        info.set(index, newID);

        //rewrites the file with the updated changes
        try {
            FileWriter myWriter = new FileWriter(infoFilePath);
            for(int i = 0; i < info.size(); i++) {
                myWriter.write(info.get(i) + "\n");
            }
            myWriter.close();
            System.out.println("Successfully rewrote the info file. (changeInfoFile)");
        } catch (IOException e) {
            System.out.println("An error occurred while rewriting the info file. (changeInfoFile)");
            e.printStackTrace();
        }

    }

    public static String readInfoFile(int index){
        ArrayList<String> info = new ArrayList<>();

        //reads the file and adds every line found to the array list above
        try {
            File myObj = new File(infoFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                info.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading info file. (readInfoFile)");
            e.printStackTrace();
        }

        return info.get(index);
    }

    public static ArrayList<String> reportSort(){
        ArrayList<String> reports = new ArrayList<>();

        //puts all the content of the file into an array. the index position is how report IDs are determined. (used within other methods)
        try {
            File myObj = new File(Storage.reportsFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                reports.add(myReader.nextLine());
            }
            myReader.close();
            System.out.println("Successfully read and wrote from the reports file. (reportSort)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading file. (reportSort)");
            e.printStackTrace();
        }

        return reports;
    }

    public static void addModToFile(String modID){

        try {
            FileWriter myWriter = new FileWriter(availableModsFilePath, true);
            myWriter.write(modID + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the modcall file. (addModToFile)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing in the modcall file. (addModToFile)");
            e.printStackTrace();
        }
    }

    public static void removeModFromFile(String modID){
        String modList = "";

        //read the file and looks for the ID
        try {
            File myObj = new File(availableModsFilePath);
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                if(!myReader.nextLine().equals(modID))
                    modList = modList + myReader.nextLine() + "\n";
            }
            myReader.close();
            System.out.println("Successfully read the modcall file. (removeModFromFile)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the modcall file. (removeModFromFile");
            e.printStackTrace();
        }

        System.out.println(modList);

        //rewrites the available mods file without the given ID
        try {
            FileWriter myWriter = new FileWriter(availableModsFilePath);
            myWriter.write(modList);
            myWriter.close();
            System.out.println("Successfully wrote to the modcall file. (removeModFromFile)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the modcall. (removeModFromFile)");
            e.printStackTrace();
        }
    }

    public static ArrayList<String> modSort(){
        ArrayList<String> mods = new ArrayList<>();

        //puts all the content of the file into an array.
        try {
            File myObj = new File(availableModsFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                mods.add(myReader.nextLine());
            }
            myReader.close();
            System.out.println("Successfully read and wrote from the modcall file. (modSort)");
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading the modcall file. (modSort)");
            e.printStackTrace();
        }

        //returns the array
        return mods;
    }

    public static void clearModCall(){
        try {
            FileWriter myWriter = new FileWriter(availableModsFilePath);
            myWriter.write("");
            myWriter.close();
            System.out.println("Successfully wrote to the modcall file. (clearModCall)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing in the modcall file. (clearModCall)");
            e.printStackTrace();
        }
    }

    public static void setActiveReportChannel(String cID){
        String contents = "";


        try {
            File myObj = new File(infoFilePath);
            Scanner myReader = new Scanner(myObj);
            while(myReader.hasNextLine()) {
                if (myReader.nextLine().equals(cID))
                    contents = contents + cID + "\n";
                contents = contents + myReader.nextLine();
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading info file. (setActiveReportChannel)");
            e.printStackTrace();
        }

        try {
            FileWriter myWriter = new FileWriter(infoFilePath);
            myWriter.write(contents);
            myWriter.close();
            System.out.println("Successfully wrote to the info file. (setActiveReportChannel)");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to info info. (setActiveReportChannel)");
            e.printStackTrace();
        }
    }
}

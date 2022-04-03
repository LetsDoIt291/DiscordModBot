import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class BlackList {

    public static void blackListUser(String uID, String reason){
        try {
            FileWriter myWriter = new FileWriter(Storage.blackListFilePath, true);
            myWriter.write(uID + "," + reason + "\n");
            myWriter.close();
            System.out.println("Successfully wrote to the blacklist file.");
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the blacklist file.");
            e.printStackTrace();
        }
    }

    public static void removeBlackList(String uID){
        ArrayList<String> blacklist = new ArrayList<>();

        try {
            File myObj = new File(Storage.blackListFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                blacklist.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading info file.");
            e.printStackTrace();
        }

        for(int i = 0; i < blacklist.size(); i++){
            if(blacklist.get(i).startsWith(uID)){
                blacklist.remove(i);
                break;
            }
        }

        try {
            FileWriter myWriter = new FileWriter(Storage.blackListFilePath);
            for (String s : blacklist) {
                myWriter.write(s + "\n");
            }
            myWriter.close();
            System.out.println("Successfully wrote to the blacklist file after rewriting.");
        } catch (IOException e) {
            System.out.println("An error occurred while rewriting to the blacklist file.");
            e.printStackTrace();
        }
    }

    public static boolean isBlackList(String uID){
        Boolean x = false;

        ArrayList<String> blacklist = new ArrayList<>();

        try {
            File myObj = new File(Storage.blackListFilePath);
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                blacklist.add(myReader.nextLine());
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading info file.");
            e.printStackTrace();
        }

        for (String s : blacklist) {
            if (s.startsWith(uID)) {
                x = true;
                break;
            }
        }

        return x;
    }
}

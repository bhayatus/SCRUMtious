package ca.mvp.scrumtious.scrumtious.utils;


public class StringHelper {

    // Shortens description to only show up to 3 lines
    public static String shortenDescription(String description){
        // Shorten description for viewing purposes
        String displayDesc = "";
        if (!description.contains("\n")){
            displayDesc = description;
        }
        else {
            String[] parts = description.split("\n");
            // Only one line break
            if (parts.length == 2) {
                displayDesc = parts[0] + "\n" + parts[1];
            }
            // At least three lines
            else{

                int numberOfNewLines = 0;
                int index = 0;
                int length = description.length();
                while(numberOfNewLines <= 2 && index < length){
                    if (description.charAt(index) == '\n'){
                        numberOfNewLines++;
                        displayDesc += "\n";
                    }
                    else{
                        displayDesc += description.charAt(index);
                    }

                    index++;
                }
            }

        }

        return displayDesc.trim();
    }

}

package com.example.mutual;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * holds the information of ab specific role in the server including the role's abilities and name
 */
public class Role implements Serializable {
    private static final long serialVersionUID = 338245675849926L;
    private String roleName;
    private String values;
    public static final ArrayList<String> abilities = new ArrayList<>(Arrays.asList("create channel", "remove channel", "remove member from server",
            "remove member from channel ", "ban member", "change server name", "see chat history", "pin message", "delete server"));

    /**
     * @param values a string containing nine 0 and 1s to define whether this role has each of nine abilities or not
     * @param name   name of the role
     */
    public Role(String values, String name) {
        this.values = values;
        this.roleName = name;
    }


    /**
     * @return an array list of abilities based on the string of 0 and 1s
     */
    public ArrayList<String> getAvailableAbilities() {
        ArrayList<String> availableAbilities = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if (values.charAt(i) == '1') {
                availableAbilities.add(abilities.get(i));
            }
        }

        return availableAbilities;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getValues() {
        return values;
    }
}

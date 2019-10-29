package iat359.nmatharu.campuscannons.utilities;

import java.util.ArrayList;

public class GameCalcs {

    // Bunch of methods for calculating game values

    // Returns an updated player coin count after a battle based on the enemy's level and the battle result
    public static int updateCoins(int currCoins, int enemyLevel, String battleResult) {

        if(battleResult.equals("VICTORY")) {
            switch (enemyLevel) {
                case 1: return currCoins + 2;
                case 2: return currCoins + 8;
                case 3: return currCoins + 25;
                case 4: return currCoins + 100;
                case 5: return currCoins + 300;
            }
        }

        return currCoins;
    }


    // Returns an updated player XP count after a battle based on the enemy's level and the battle result
    public static int updateXP(int currXP, int enemyLevel, String battleResult) {

        if(battleResult.equals("VICTORY")) {
            switch (enemyLevel) {
                case 1: return currXP + 5;
                case 2: return currXP + 25;
                case 3: return currXP + 100;
                case 4: return currXP + 500;
                case 5: return currXP + 2500;
            }
        }

        if(battleResult.equals("DEFEAT")) {
            return currXP + 1;
        }

        return currXP;
    }


    // Calculates a player's level based on their XP
    public static int calcLevel(int XP) {
        if(XP < 100) {
            return 1;
        } else if (XP < 500) {
            return 2;
        } else if (XP < 2500) {
            return 3;
        } else if (XP < 10000) {
            return 4;
        } else {
            return 5;
        }
    }


    // Calculates a player's health based off their level
    public static int getHealth(int level) {
        return 30*level;
    }


    // Calculates the minimum amount of damage an enemy attack can do based off level
    public static int calcMinDamage(int level) {
        switch (level) {
            case 1: return 3;
            case 2: return 5;
            case 3: return 7;
            case 4: return 10;
            case 5: return 13;
        }
        return 2;
    }


    // Calculates the maximum amount of damage an enemy attack can do based off level
    public static int calcMaxDamage(int level) {
        switch (level) {
            case 1: return 5;
            case 2: return 7;
            case 3: return 9;
            case 4: return 14;
            case 5: return 17;
        }
        return 4;
    }


    // Calculates the number of frames to reload enemy cannons based off level
    public static int calcReloadFrames(int level) {
        return (405 - 45*level)/2;
    }


    // Get's the full name of a weapon based on its ID
    public static String getCannonName(String ID) {
        switch (ID) {
            case "def": return "DEFAULT";
            case "rck": return "ROCK";
            case "bst": return "BLACK STEEL";
            case "yol": return "YE OLDE";
            case "ice": return "ICE CUBE";
            case "spk": return "SPIKY";
            case "shr": return "SHURIKEN";
            case "mlt": return "MOLTEN";
            case "blb": return "BOWLING BALL";
            case "bch": return "BEACH BALL";
            case "acd": return "ACID";
            case "pie": return "CHERRY PIE";
            case "eig": return "8 BALL";
            case "str": return "STAR";
            case "tir": return "A TIRE";
            case "box": return "BOX";
            case "shd": return "SHADOWBALL";
            case "png": return "PENGUIN";
            case "pna": return "PINEAPPLE";
            case "lng": return "LIGHTNING";
            case "fst": return "YOUR OWN FIST";
            default:    return "";
        }
    }

    // Gets the string of the 4 weapon values (mindmg,maxdmg,reloadframes,cost) from the ID
    public static String getCannonVals(String ID) {
        switch (ID) {
            case "def": return "2,6,120,1";
            case "rck": return "4,7,150,5";
            case "bst": return "2,4,75,15";
            case "yol": return "4,9,165,25";
            case "ice": return "4,6,105,40";
            case "spk": return "5,8,135,60";
            case "shr": return "2,5,52,85";
            case "mlt": return "5,10,120,120";
            case "blb": return "1,20,150,150";
            case "bch": return "8,12,120,200";
            case "acd": return "4,12,90,250";
            case "pie": return "15,18,150,350";
            case "eig": return "1,15,60,500";
            case "str": return "6,9,60,750";
            case "tir": return "12,25,120,1000";
            case "box": return "15,30,135,1500";
            case "shd": return "6,10,45,2000";
            case "png": return "9,42,120,2500";
            case "pna": return "8,80,180,3500";
            case "lng": return "7,11,30,5000";
            case "fst": return "1,100,150,9999";
            default:    return "2,6,120,1";
        }
    }

    // Get the full name of the HUB from the ID
    public static String getHubName(String hubID) {
        switch (hubID) {
            case "MEZ": return "MEZZANINE HUB";
            case "THR": return "2600 THEATRE HUB";
            case "LAB": return "359 LAB HUB";
            case "LEC": return "359 LECTURE HUB";
            case "LIB": return "LIBRARY HUB";
            case "VRA": return "VR AREA HUB";
            case "IAT": return "SIAT OFFICES HUB";
            default:    return "HUB";
        }
    }

    // Get the list of items that a hub has for sale from its ID
    public static ArrayList<String> getHubItems(String hubID) {
        ArrayList<String> items = new ArrayList<>();
        switch (hubID) {
            case "MEZ": items.add("rck");
                        items.add("bst");
                        items.add("yol");
                        items.add("ice");
                        items.add("spk");
                        items.add("shr");
                        items.add("mlt");
                        items.add("blb");
                        items.add("bch");
                        break;
            case "LEC": items.add("bst");
                        items.add("yol");
                        items.add("ice");
                        items.add("spk");
                        items.add("shr");
                        items.add("mlt");
                        items.add("blb");
                        items.add("bch");
                        items.add("acd");
                        break;
            case "LAB": items.add("ice");
                        items.add("spk");
                        items.add("shr");
                        items.add("mlt");
                        items.add("blb");
                        items.add("bch");
                        items.add("acd");
                        items.add("pie");
                        items.add("eig");
                        break;
            case "THR": items.add("shr");
                        items.add("mlt");
                        items.add("blb");
                        items.add("bch");
                        items.add("acd");
                        items.add("pie");
                        items.add("eig");
                        items.add("str");
                        items.add("tir");
                        break;
            case "LIB": items.add("blb");
                        items.add("bch");
                        items.add("acd");
                        items.add("pie");
                        items.add("eig");
                        items.add("str");
                        items.add("tir");
                        items.add("box");
                        items.add("shd");
                        break;
            case "VRA": items.add("acd");
                        items.add("pie");
                        items.add("eig");
                        items.add("str");
                        items.add("tir");
                        items.add("box");
                        items.add("shd");
                        items.add("png");
                        items.add("pna");
                        break;
            case "IAT": items.add("eig");
                        items.add("str");
                        items.add("tir");
                        items.add("box");
                        items.add("shd");
                        items.add("png");
                        items.add("pna");
                        items.add("lng");
                        items.add("fst");
                        break;
        }
        return items;
    }

    // Minimum Level of enemies that tend to spawn around a certain hub by ID
    public static int getEnemiesMinLevelByNearestHub(String hubID) {
        switch (hubID) {
            case "MEZ": return 1;
            case "THR": return 2;
            case "LAB": return 2;
            case "LEC": return 1;
            case "LIB": return 3;
            case "VRA": return 3;
            case "IAT": return 4;
        }
        return 1;
    }

    // Maximum Level of enemies that tend to spawn around a certain hub by ID
    public static int getEnemiesMaxLevelByNearestHub(String hubID) {
        switch (hubID) {
            case "MEZ": return 2;
            case "THR": return 4;
            case "LAB": return 3;
            case "LEC": return 3;
            case "LIB": return 4;
            case "VRA": return 5;
            case "IAT": return 5;
        }
        return 2;
    }
}

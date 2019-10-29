package iat359.nmatharu.campuscannons.utilities;

public class RandomVals {

    // Some personal random methods

    // Random float between two vals
    public static float randomFloat(float lowerBound, float upperBound) {
        float range = upperBound - lowerBound;
        return ((float)Math.random())*range + lowerBound;
    }

    // Random negative 1 or positive 1
    public static int randomSignedUnit() {
        if(Math.random() < 0.5) {
            return 1;
        } else {
            return -1;
        }
    }

    // Random int between two bounds (+ 1 on the range because this method is INCLUSIVE for both bounds)
    public static int randomInt(int lowerBound, int upperBound) {
        int range = upperBound - lowerBound + 1;
        return ((int)(Math.random()*range) + lowerBound);
    }

    // Gets a (more or less) random battle result, was using it during testing and am keeping it in case it comes in handy later
    public static String randomBattleResult() {
        double r = Math.random();
        if(r < 0.33) {
            return "VICTORY";
        } else if(r < 0.66) {
            return "DEFEAT";
        } else {
            return "FLEE";
        }
    }
}

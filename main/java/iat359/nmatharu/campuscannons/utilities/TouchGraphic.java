package iat359.nmatharu.campuscannons.utilities;

import processing.core.PApplet;
import processing.core.PVector;

public class TouchGraphic {

    // An object that is essentially an ellipse that grows and fades that is used in WorldMapSketch
    // whenever a player touches the screen, I think it looks nice and adds a sense of feedback

    // Position of graphic on screen, PApplet for it to be drawn to because there is no Processing context in here
    private PVector pos;
    private PApplet p;

    // Countdown for how many frames the graphic lasts for
    private int countdown;

    // Constructor initializes its position and the PApplet
    public TouchGraphic(float mx, float my, PApplet p) {
        pos = new PVector(mx, my);
        this.p = p;
        countdown = 30;
    }

    // Draw the graphic
    public void drawMe() {

        // If countdown is reached, stop drawing
        if(countdown < 0)   return;

        // Draw an ellipse that gets bigger as the countdown decreases and becomes increasingly transparent
        p.ellipseMode(p.CENTER);
        p.noStroke();
        p.fill(255, countdown*3);
        p.ellipse(pos.x, pos.y, 180 - (countdown * 3), 180 - (countdown * 3));

        // Decrement countdown
        countdown--;
    }

    // Should remove the touchGraphic once its countdown is finished
    public boolean shouldRemove() {
        return (countdown < 0);
    }
}

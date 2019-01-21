package ru.racer;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Дмитрий on 19.01.2019.
 */

public class Cockroach {
    private int posX;                       //position by x
    private int posY;                       //position by y

    public Cockroach(int number){
        posY = (number - 1) * 64 + 5 * number;
        posX = 0;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public void move() {
        //Initialize the generator and get a random number in the range
        posX += ThreadLocalRandom.current().nextInt(5, 16);
    }

    public void move(boolean click){
        posX += 30;
    }

    public void setEndPosition(int x){
        posX = x;
    }
}

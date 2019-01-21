package ru.racer;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Дмитрий on 19.01.2019.
 */
public class GameThread implements Runnable
{
    private Thread thread;
    private Cockroach cockroach;
    private boolean finish;
    private boolean checked;

    //constructor
    public GameThread(int number) {
        finish = false;
        checked = false;
        thread = new Thread(this, Integer.toString(number));
        cockroach = new Cockroach(number);
    }
    //start cockroach thread
    public void start(){
        thread.start();
    }
    //move the cockroach after a random amount of time
    @Override
    public void run() {
        do {
            this.cockroach.move();
            if (this.cockroach.getPosX() + 64 >= 850) {
                stop();
            }
            try {
                thread.sleep(ThreadLocalRandom.current().nextInt(50, 200));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } while (!finish);
    }
    //race finish, stop run();
    public void stop(){
        finish = true;
        this.cockroach.setEndPosition(850);
    }

    public void move(){
        this.cockroach.move(true);
    }

    public void Check(){
        checked = true;
    }

    public int getPosX(){
        return cockroach.getPosX();
    }

    public int getPosY(){
        return cockroach.getPosY();
    }

    public boolean isFinish() {
        return finish;
    }

    public boolean isChecked() {
        return checked;
    }

}
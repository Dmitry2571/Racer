package ru.racer;

import java.util.ArrayList;

/**
 * Created by Дмитрий on 19.01.2019. *
 */

public class Application{
    private int tracks;                         //count tracks
    private int countRace;                      //count race
    private String [] res;                      //result of the current race
    private ArrayList<GameThread> threads;      //unsafe thread array
    private ArrayList<String[]> result;         //result of all races
    private GUI gui;
    private int state;                          //game state

    public Application(int n){
        tracks = n;
        result = new ArrayList<>();
        countRace = 0;
        state = 5;
        initThread();
        gui = new GUI(tracks,threads);

    }

    //thread initialization
    private void initThread(){
        threads = new ArrayList<>();
        for(int i = 0; i < tracks; i++) {
            GameThread gameThread = new GameThread(i+1);
            threads.add(gameThread);
        }
    }

    //create new thread in ArrayList
    private  void reInitThread(){
        for(int i = threads.size() - 1; i >= 0; i--)
            threads.get(i).stop();
        try {
            if(!Thread.currentThread().isInterrupted())  Thread.currentThread().sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
        for(int i = 0; i < tracks; i++) {
            GameThread gameThread = new GameThread(i+1);
            threads.set(i,gameThread);
        }
    }

    //string initialization
    private void initString(){
        res = new String[tracks+1];
        res[0] = Integer.toString(countRace);
        for(int i = 0; i < threads.size(); i++) {
            threads.get(i).start();
            res[i+1] = "-";
        }
    }

    //main game function
    public void start(){
        int countRes = 1;       //count winner
        int countStop = 0;
        int leader = 0;
        int max = 0;
        while (true){

            if(threads.size() == 0) break;

            for(int i = 0; i < threads.size(); i++) {
                int x = gui.getPositionClick()[0];
                int y = gui.getPositionClick()[1];
                if (threads.get(i).isFinish()) {
                    countStop++;
                    threads.get(i).stop();
                    if (!threads.get(i).isChecked()) {
                        threads.get(i).Check();
                        if (res[countRes] == "-") res[countRes] = gui.getNameCockroach().get(i);
                        if (countRes + 1 <= tracks) countRes++;
                    }
                }
                if(threads.get(i).getPosX() > max){
                    max = threads.get(i).getPosX();
                    leader = i;
                }
                if (x >= threads.get(i).getPosX()-32 && x < threads.get(i).getPosX() + 40) {
                    if (y >= threads.get(i).getPosY() && y < threads.get(i).getPosY() + 64)
                        threads.get(i).move();
                }
            }
            //sleep
            try {
                if(!Thread.currentThread().isInterrupted())  Thread.currentThread().sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                e.printStackTrace();
            }
            gui.setWinner(gui.getNameCockroach().get(leader));
            gui.Update();
            //change state / finish race
            if(countStop >= threads.size()){
                state = 3;
                if(countRace != result.size()) {
                    result.add(res);
                    System.out.println("Number race: " + res[0]);
                    for (int i = 1; i < res.length; i++)
                        System.out.println("Place " + i + " is " + res[i]);
                    countRes = 1;
                }
                if(state == 4) break;
            }
            manageState();
            countStop = 0;
        }
    }

    private void manageState(){
        state = gui.getState();
        switch (state){
            case 0:

                reInitThread();
                if(countRace != result.size()) result.add(res);
                if(gui.isOpenedTable()) gui.closeTable();
                gui.Update();
                break;
            case 1:
                if(gui.isOpenedTable()) gui.closeTable();
                countRace++;
                initString();
                state = 5;
                break;
            case 2:
                gui.showTable(result);
                break;
            case 3:
                state = gui.getState();
            default:
                    break;
        }
        gui.nullState();
    }
}

package ru.racer;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

/**
 * Created by Дмитрий on 20.01.2019.
 * Icons Cockroach made by https://www.freepik.com/ from https://www.flaticon.com/ is licensed by CC 3.0 BY
 */

public class GUI extends JFrame {
    private final int IMAGE_SIZE = 64;
    private final int WIDTH = IMAGE_SIZE * 15;
    private int tracks;
    private boolean showTable;
    private JPanel panel;
    private ArrayList<JButton> buttons;
    private ArrayList<JTextField> text;
    private ArrayList<GameThread> GT;
    private ArrayList<String> nameCockroach;
    private int[] positionClick;
    private int state;
    private String[] columnNames;
    private String winner;

    public GUI(int tracks, ArrayList<GameThread> GT){
        winner = "Leader: ";
        columnNames = new String[tracks+1];
        columnNames[0] = "Race number";
        for(int i = 1; i < columnNames.length; i++){
            columnNames[i] = i + " place";
        }
        this.tracks = tracks;
        this.GT = GT;
        showTable = false;
        state = 5;
        positionClick = new int[2];
        positionClick[0] = positionClick[1] = 0;
        buttons = new ArrayList<>();
        text = new ArrayList<>();
        nameCockroach = new ArrayList<>();
        initButtons();
        initTextField();
        initPanel();
    }

    //panel initialization
    private void initPanel(){
        //draw elements
        setLayout(new BorderLayout());
        final int _width = WIDTH;
        panel = new JPanel(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int x;
                for(int i = 0; i < text.size(); i++){
                    text.get(i).setLocation(_width - text.get(i).getWidth(),GT.get(i).getPosY() + 37);
                }
                for(int i = 0; i < GT.size(); i++) {
                    g.drawImage(getImage("bg.png", true), 0, GT.get(i).getPosY() - 5,this);
                    g.drawString(nameCockroach.get(i), 10, GT.get(i).getPosY() + 37);
                    g.drawString(winner,
                            (_width - _width/2),
                            (IMAGE_SIZE + 10)*tracks);
                    g.drawImage(getImage("cockroach.gif", false),
                            GT.get(i).getPosX(), GT.get(i).getPosY(), this);
                }
                x = 10;
                for(int i = 0; i < buttons.size(); i++) {
                    buttons.get(i).setLocation(x, (IMAGE_SIZE + 10)*tracks);
                    x += buttons.get(i).getSize().width + 10;
                }
            }
        };

        //add ActionListener for buttons
        for(int i = 0; i < buttons.size(); i++) {
            String name = buttons.get(i).getName();
            buttons.get(i).addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    pressButton(name);
                }
            });
            panel.add(buttons.get(i));
        }

        //add ActionListener for text
        for(int i = 0; i < text.size(); i++) {
            final int x = i;
            text.get(i).addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if(text.get(x).getText().trim().length() > 0) nameCockroach.set(x,text.get(x).getText());
                }
            });
            panel.add(text.get(i));
        }
        panel.setPreferredSize(new Dimension(
                    WIDTH, (IMAGE_SIZE + 10) * tracks + 35));

        //add ActionListener for mouse
        panel.addMouseListener(new MouseAdapter() {
            private int doubleClickDelay = 250;
            private Timer timer;

            {
                ActionListener actionListener = new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        timer.stop();
                    }
                };
                timer = new Timer(doubleClickDelay, actionListener);
                timer.setRepeats(false);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() == 1) {
                    timer.start();
                } else {
                    positionClick[0] = e.getX();
                    positionClick[1] = e.getY();
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setPreferredSize(new Dimension(
                WIDTH+3,(IMAGE_SIZE + 10) * 4 + 40));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane);

        //frame initialization
        pack();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Cockroach race");
        setLocationRelativeTo(null);
        setResizable(false);
        setVisible(true);
    }

    //buttons initialization
    private void initButtons(){
        JButton button = new JButton("New run");
        button.setName("NewRun");
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        buttons.add(button);

        button = new JButton("Start");
        button.setName("Start");
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        buttons.add(button);

        button = new JButton("Result");
        button.setName("Result");
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        buttons.add(button);
    }

    //TextField initialization
    private void initTextField(){
        for(int i = 0; i < tracks; i++) {
            nameCockroach.add(Integer.toString(i+1));
            JTextField newText = new JTextField(5);
            newText.setFont(new Font("Times New Roman", Font.PLAIN, 10));
            text.add(newText);
        }
    }

    //get image by name
    private Image getImage(String name, boolean scale){
        String filename = name.toLowerCase();
        ImageIcon icon = new ImageIcon(getClass().getResource(filename));
        Image image = icon.getImage();
        if (scale) image.getScaledInstance(WIDTH, (IMAGE_SIZE+15) * 4, Image.SCALE_DEFAULT);
        return image;
    }

    //repaint panel
    public void Update(){
        this.panel.repaint();
    }
    public void setWinner(String str){
        winner = "Leader: " + str;
    }
    public int[] getPositionClick(){
        return positionClick;
    }
    public boolean isOpenedTable(){
        return showTable;
    }

    //close table function
    public void closeTable(){
        Component[] componentList = panel.getComponents();
        for(Component c : componentList){
            if(c instanceof JScrollPane)
                panel.remove(c);
        }
        panel.revalidate();
        panel.repaint();
    }

    //open/close result table
    public void showTable(ArrayList<String[]> data){
        if(data.size() > 0) {
            if(!showTable) {
                showTable = true;
                int x = data.size();
                int y = data.get(0).length;
                String[][] strings = new String[x][y];
                for (int i = 0; i < x; i++)
                    strings[i] = data.get(i).clone();
                JTable table = new JTable(strings, columnNames);
                table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                JScrollPane scrollPane = new JScrollPane(table,
                        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

                panel.add(scrollPane);
                pack();
            } else {
                showTable = false;
                closeTable();
            }
        }
    }

    //Action for button
    private void pressButton(String name){
        switch (name) {
            case "NewRun":
                for (int i = 0; i < buttons.size(); i++)
                    if (buttons.get(i).getName() == "Start") {
                        buttons.get(i).setEnabled(true);
                    }
                state = 0;
                break;
            case "Start":
                for (int i = 0; i < buttons.size(); i++)
                    if (buttons.get(i).getName() == "Start") {
                        buttons.get(i).setEnabled(false);
                    }
                state = 1;
                break;
            case "Result":
                state = 2;
                break;
        }
    }

    public int getState(){
        return state;
    }

    public ArrayList<String> getNameCockroach(){
        return nameCockroach;
    }

    public void nullState(){
        state = 5;
    }
}

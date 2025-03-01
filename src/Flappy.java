import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class Flappy extends JPanel implements  ActionListener,KeyListener {
    int boardWidth=360;
    int boardHeight=640;

    //Images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottompipImg;

    //Bird
    int birdX=boardWidth/8;
    int birdY=boardHeight/2;
    int birdWidth=34;
    int birdHeight =24;

    class Bird{
        int x=birdX;
        int y=birdY;
        int width=birdWidth;
        int height=birdHeight;
        Image img;

        Bird(Image img){
            this.img=img;
        }
    }

    //Pipes
    int pipeX=boardWidth;
    int pipeY=0;
    int pipeWidth=64;
    int pipeHeight=512;

    class Pipe{
        int x=pipeX;
        int y=pipeY;
        int width=pipeWidth;
        int height=pipeHeight;
        Image img;
        boolean passed =false;

        Pipe(Image img){
            this.img=img;
        }

    }

    //game logic
    Bird bird;
    int velocityX=-4;//move pipes
    int velocityY =0;//move bird
    int gravity=1;

    ArrayList<Pipe> pipes;
    Random random =new Random();

    Timer gameLoop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score =0;

    public Flappy() {
        setPreferredSize(new Dimension(boardWidth,boardHeight));
        // setBackground(Color.BLUE);
        setFocusable(true);
        addKeyListener(this);


        //load image
        backgroundImg=new ImageIcon(getClass().getResource("./img/flappybirdbg.png")).getImage();
        birdImg=new ImageIcon(getClass().getResource("./img/flappybird.png")).getImage();
        topPipeImg=new ImageIcon(getClass().getResource("./img/toppipe.png")).getImage();
        bottompipImg=new ImageIcon(getClass().getResource("./img/bottompipe.png")).getImage();

        bird =new Bird(birdImg);
        pipes =new ArrayList<Pipe>();


        //place pipis times
        placePipesTimer=new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                placePipes();
            }
        });
        //pipe
        placePipesTimer.start();

        //game timer
        gameLoop=new Timer(1000/60,this);
        gameLoop.start();

    }

    public void placePipes(){

        int randomPipeY =(int )(pipeY -pipeHeight/4- Math.random()*(pipeHeight/2));
        int opeingSpace =boardHeight/4;

        Pipe topPipe=new Pipe(topPipeImg);
        topPipe.y=randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe=new Pipe(bottompipImg);
        bottomPipe.y=topPipe.y + pipeHeight + opeingSpace;
        pipes.add(bottomPipe);

    }

    public  void paintComponent(Graphics g){
        // System.out.println("draw");
        super.paintComponent(g);
        draw(g);
    } 

    // public void draw(Graphics g){
    //     g.drawImage(backgroundImg, 0, 0,boardWidth,boardHeight ,this);
    // }

    public void draw(Graphics g) {
        if (backgroundImg != null) {
            g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, this);
        } else {
            g.setColor(Color.BLUE); // Fallback in case the image isn't loaded
            g.fillRect(0, 0, boardWidth, boardHeight);
        }
        //bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width,bird.height,null);

        //pipes
        for(int i=0;i <pipes.size();i++){
            Pipe pipe =pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width,pipe.height, this);
        }
        //score
        g.setColor(Color.white);
        g.setFont(new Font("Arial",Font.PLAIN,32));
        if(gameOver){
            g.drawString("Game Over: "+ String.valueOf((int) score),10,35 );
        }
        else{
            g.drawString(String.valueOf((int) score),10,35);
        }
    }

    public void move(){
        //bird
        velocityY+=gravity;
        bird.y+=velocityY;
        bird.y=Math.max(bird.y,0);

        //pipes
        for (int i=0;i<pipes.size();i++){
            Pipe pipe =pipes.get(i);
            pipe.x +=velocityX;
             
            if (!pipe.passed && bird.x > pipe.x + pipe.width){
                pipe.passed=true;
                score +=.5;
            }

            if (collision(bird, pipe)){
                gameOver=true;
            }
        }
        if(bird.y> boardHeight){
            gameOver=true;
        }
    }

    public boolean collision (Bird a,Pipe b){
        return a.x <b.x + b.width &&
               a.x + a.width >b.x &&
               a.y < b.y +b.height &&
               a.y + a.height> b.y;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        move();
        repaint();
        if(gameOver){
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }



    @Override
    public void keyTyped(KeyEvent e) { }



    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
        if(e.getKeyCode()==KeyEvent.VK_SPACE){
            velocityY=-9;
            if(gameOver){
                //restart
                bird.y=birdY;
                velocityY=0;
                pipes.clear();
                score=0;
                gameOver=false;
                gameLoop.start();
                placePipesTimer.start();
            }
        }
    }



    @Override
    public void keyReleased(KeyEvent e) {}

    
}

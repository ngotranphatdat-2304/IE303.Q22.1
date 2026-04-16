import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    // Khai bao kich thuoc cua so
    int boardWidth = 360;
    int boardHeight = 640;

    // Khai bao kich thuoc va toc do cua chim
    int birdWidth = 40;
    int birdHeight = 30;
    int birdX = 50; // Vi tri ngang cua chim
    int birdY = boardHeight / 2; // Vi tri doc cua chim


    // Khai bao kich thuoc va khoang cach cua ong
    int pipeWidth = 60;
    int pipeHeight = 400;
    int openingSpace = 150; // Khoang cach giua 2 ong

    // Cac bien luu tru hinh
    Image backgroundImage;
    Image birdImage;
    Image topPipeImage;
    Image bottomPipeImage;


    class Bird {
        int x = birdX; // Vi tri ngang cua chim
        int y = birdY; // Vi tri doc cua chim
        int width = birdWidth;
        int height = birdHeight;
        Image image; // Hinh anh cua chim

        public Bird(Image image) {
            this.image = image;
        }
    }

    class Pipe {
        int x = boardWidth; // Vi tri ngang cua ong
        int y = 0; // Vi tri cua khoang cach giua 2 ong
        int width = pipeWidth;
        int height = pipeHeight;
        Image image; // Hinh anh cua ong
        boolean isTopPipe;
        boolean scored;

        public Pipe(Image image, boolean isTopPipe) {
            this.image = image;
            this.isTopPipe = isTopPipe;
        }
    }

    Bird bird;
    java.util.List<Pipe> pipes; // Danh sach cac ong
    Timer pipeSpawnTimer; // Timer de sinh ra ong moi
    Timer gameLoop;

    int score = 0;
    boolean gameOver = false;

    public FlappyBird() {
        // Thiet lap kich thuoc cho JPanel (vung choi game)
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true); // Cho phep JPanel nhan su kien ban phim
        addKeyListener(this);
        requestFocusInWindow();

        // Load hinh anh nen tu file
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        // Load hinh anh chim tu file
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        // Load hinh anh ong tu file
        topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        
        // Tao doi tuong chim
        bird = new Bird(birdImage);
        
        // Tao doi tuong ong
        pipes = new java.util.ArrayList<>();
        pipeSpawnTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes(); // Sinh ra ong moi moi 2 giay
            }
        });
        pipeSpawnTimer.start();


        // Khoi tao timer de cap nhat game moi 20ms
        gameLoop = new Timer(20, this);
        gameLoop.start();
    }

    // Ham ve cac thanh phan len man hinh
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Ve hinh nen phu kin cua so
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
        // Ve chim tai vi tri cua no
        g.drawImage(bird.image, bird.x, bird.y, bird.width, bird.height, null);
        // Ve ong tai vi tri cua no
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.image, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 30);

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(20, boardHeight / 2 - 90, boardWidth - 40, 160);
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString("GAME OVER", boardWidth / 2 - 95, boardHeight / 2 - 30);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.drawString("Press SPACE or ENTER to restart", 25, boardHeight / 2 + 20);
            g.drawString("Final score: " + score, boardWidth / 2 - 65, boardHeight / 2 + 55);
        }
    }

    public void placePipes() {
        int randomPipeY = (int) (0 - pipeHeight/4 - Math.random()*(pipeHeight/2));
        
        Pipe topPipe = new Pipe(topPipeImage, true);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage, false);
        // Tọa độ y cua ong duoi = (Vi tri ong tren) + (Chieu cao ong) + (Khe ho)
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    public void move() {
        if (gameOver) {
            return;
        }
        bird.y += 3; // Di chuyen xuong

        // Kiem tra va thong bao game over khi cham bien tren/duoi
        if (bird.y <= 0 || bird.y + bird.height >= boardHeight) {
            endGame();
            return;
        }

        // Di chuyen cac ong sang trai
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x -= 5; // Di chuyen sang trai

            // Tang diem khi top pipe di qua chim
            if (pipe.isTopPipe && !pipe.scored && pipe.x + pipe.width < bird.x) {
                score++;
                pipe.scored = true;
            }

            // Kiem tra va thong bao game over khi cham ong
            Rectangle birdRect = new Rectangle(bird.x, bird.y, bird.width, bird.height);
            Rectangle pipeRect = new Rectangle(pipe.x, pipe.y, pipe.width, pipe.height);
            if (birdRect.intersects(pipeRect)) {
                endGame();
                return;
            }

            // Xoa ong khi no di qua man hinh
            if (pipe.x + pipe.width < 0) {
                pipes.remove(i);
                i--; // De tranh loi khi xoa phan tu trong danh sach
            }
        }
    }

    private void endGame() {
        gameOver = true;
        gameLoop.stop();
        pipeSpawnTimer.stop();
    }

    private void resetGame() {
        score = 0;
        gameOver = false;
        bird.x = birdX;
        bird.y = birdY;
        pipes.clear();
        pipeSpawnTimer.start();
        gameLoop.start();
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        move(); // Cap nhat vi tri doi tuong
        repaint(); // Ve lai man hinh sau khi di chuyen
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (gameOver) {
                resetGame();
            } else {
                bird.y -= 50; // Bay len
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        // Tao cua so JFrame
        JFrame frame = new JFrame("Flappy Bird");
        
        // Thiet lap cac thuoc tinh cua so
        //frame.setSize(360, 640);
        frame.setLocationRelativeTo(null); // Canh giua man hinh
        frame.setResizable(false);        // Khong cho phep thay doi kich thuoc cua so
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Them lop game vao cua so
        FlappyBird game = new FlappyBird();
        frame.add(game);
        frame.pack(); // Tu dong dieu chinh cua so vua khit voi JPanel
        
        frame.setVisible(true);
    }
}
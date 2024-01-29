import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Canvas extends JPanel {
    private static final int gridWidth = 160;
    private static final int gridHeight = 80;
    private final int pixelSize = 10;
    static boolean[][] filledPixels = new boolean[gridHeight][gridWidth];

    static int counterX = 0;
    static int counterY = 0;


    public Canvas() {
        setPreferredSize(new Dimension(gridWidth * pixelSize, gridHeight * pixelSize));

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if(e.getX() < gridWidth * pixelSize && e.getY() < gridHeight * pixelSize){
                    int x = e.getX() / pixelSize;
                    int y = e.getY() / pixelSize;
                    filledPixels[y][x] = true;
                    repaint();
                }
            }
        });

        Thread updateThread = new Thread(() -> {
            while (true) {
                boolean[][] newFilledPixels = new boolean[gridHeight][gridWidth];
                for(int i = 0; i < gridHeight; i++){
                    for(int j = 0; j < gridWidth; j++){
                        if(filledPixels[i][j]){
                            int direction = 1;

                            boolean below = true;
                            boolean belowA = true;
                            boolean belowB = true;
                            if(i < gridHeight - 1){
                                Random rand = new Random();
                                if(j == 0){
                                    belowA = filledPixels[i + 1][j + direction];
                                }else if(j == gridWidth - 1){
                                    belowB = filledPixels[i + 1][j - direction];
                                }else{
                                    if (rand.nextDouble() < 0.5) {
                                        direction *= -1;
                                    }
                                    belowA = filledPixels[i + 1][j + direction];
                                    belowB = filledPixels[i + 1][j - direction];
                                }

                                below = filledPixels[i + 1][j];
                            }
                            if(!below){
                                newFilledPixels[i+1][j] = true;
                            }else if(!belowA){
                                newFilledPixels[i+1][j + direction] = true;
                            }else if(!belowB) {
                                newFilledPixels[i + 1][j - direction] = true;
                            }else{
                                newFilledPixels[i][j] = true;
                            }
                        }
                    }
                }
                filledPixels = newFilledPixels;
                repaint();

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        updateThread.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                if (filledPixels[j][i]) {
                    g.setColor(Color.BLUE);
                    g.fillRect(i * pixelSize, j * pixelSize, pixelSize, pixelSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sand Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Canvas());
        frame.pack();
        frame.setVisible(true);
    }
}

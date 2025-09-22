package test.app;
import javax.swing.*;

public class MainFrame extends JFrame {
    private IntroScreen introScreen;
    private SortScreen sortScreen;

    public MainFrame() {
        setTitle("GranSoftTest");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        introScreen = new IntroScreen(this);
        setContentPane(introScreen);
    }

    public void showSortScreen(int count) {
        sortScreen = new SortScreen(this, count);
        setContentPane(sortScreen);
        revalidate();
        repaint();
    }

    public void showIntroScreen() {
        setContentPane(introScreen);
        revalidate();
        repaint();
    }
}

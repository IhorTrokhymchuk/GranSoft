package test.app;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class SortScreen extends JPanel {
    private MainFrame parent;
    private JPanel numbersPanel;
    private JButton sortButton, resetButton;

    private int[] numbers;
    private boolean descending = true;

    public SortScreen(MainFrame parent, int count) {
        this.parent = parent;
        setLayout(new BorderLayout());

        numbersPanel = new JPanel();
        generateNumbers(count);

        sortButton = new JButton("Sort");
        resetButton = new JButton("Reset");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(sortButton);
        bottomPanel.add(resetButton);

        add(new JScrollPane(numbersPanel), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        sortButton.addActionListener(e -> startSorting());
        resetButton.addActionListener(e -> parent.showIntroScreen());
    }

    private void generateNumbers(int count) {
        Random rand = new Random();
        numbers = new int[count];
        boolean hasSmall = false;

        for (int i = 0; i < count; i++) {
            numbers[i] = rand.nextInt(1000) + 1;
            if (numbers[i] <= 30) hasSmall = true;
        }
        if (!hasSmall) numbers[rand.nextInt(count)] = rand.nextInt(30) + 1;

        showNumbers();
    }

    private void showNumbers() {
        numbersPanel.removeAll();

        int rows = Math.min(10, numbers.length);
        int cols = (int) Math.ceil(numbers.length / 10.0);
        numbersPanel.setLayout(new GridLayout(rows, cols, 5, 5));

        JButton[][] btns = new JButton[rows][cols];
        int index = 0;
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                if (index < numbers.length) {
                    btns[row][col] = new JButton(String.valueOf(numbers[index]));
                    int num = numbers[index];
                    btns[row][col].addActionListener(e -> handleNumberClick(num));
                    index++;
                } else {
                    btns[row][col] = new JButton("");
                    btns[row][col].setEnabled(false);
                }
            }
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                numbersPanel.add(btns[r][c]);
            }
        }

        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    private void handleNumberClick(int num) {
        if (num <= 30) {
            generateNumbers(numbers.length);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a value smaller or equal to 30.");
        }
    }

    private void startSorting() {
        new Thread(() -> {
            quickSortAnimated(0, numbers.length - 1);
            descending = !descending;
        }).start();
    }

    private void quickSortAnimated(int low, int high) {
        if (low < high) {
            int pi = partition(low, high);

            SwingUtilities.invokeLater(this::showNumbers);

            try { Thread.sleep(100); } catch (InterruptedException ignored) {}

            quickSortAnimated(low, pi - 1);
            quickSortAnimated(pi + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = numbers[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            boolean condition = descending ? numbers[j] > pivot : numbers[j] < pivot;
            if (condition) {
                i++;
                swap(i, j);
            }
        }
        swap(i + 1, high);
        return i + 1;
    }

    private void swap(int i, int j) {
        int temp = numbers[i];
        numbers[i] = numbers[j];
        numbers[j] = temp;
    }
}
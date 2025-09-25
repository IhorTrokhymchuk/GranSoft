package test;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.Random;

/**
 * GranSoftTest is a small Swing application for generating and sorting numbers.
 *
 * Features:
 * - The user enters the number of values to generate.
 * - Clicking a button with a value ≤ 30 generates a new array of that size.
 * - Supports animated sorting using QuickSort algorithm.
 * - Visual indicators highlight pivot and swapped elements during sorting.
 */
public class GranSoftTest extends JFrame {
    private static final int MAX_ROWS = 10;
    private static final int MAX_NUMBER = 1000;
    private static final int MAX_SMALL_NUMBER = 30;
    private static final int SORT_DELAY_MS = 100;
    private static final int FIELD_COLUMNS = 10;
    private static final int PADDING = 10;
    private static final int GAP = 5;
    private static final int WINDOW_WIDTH = 600;
    private static final int WINDOW_HEIGHT = 400;

    private final Random randomGenerator = new Random();

    private JButton[] numberButtons;
    private JPanel numbersPanel;
    private JTextField inputField;

    private volatile boolean sorting = false;
    private int[] numbers;
    private boolean descending = true;

    /**
     * Constructs the main application window.
     */
    public GranSoftTest() {
        setTitle("GranSoftTest");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setLocationRelativeTo(null);

        initIntroScreen();
        setVisible(true);
    }

    /**
     * Initializes the intro screen where the user inputs the number of values.
     */
    private void initIntroScreen() {
        JPanel introPanel = new JPanel();
        introPanel.setLayout(new GridBagLayout());
        GridBagConstraints gridConstraints = new GridBagConstraints();
        gridConstraints.insets = new Insets(PADDING, PADDING, PADDING, PADDING);
        gridConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridConstraints.gridx = 0;
        gridConstraints.gridy = 0;
        gridConstraints.gridwidth = 2;

        JLabel label = new JLabel("Enter number of values:", SwingConstants.CENTER);
        introPanel.add(label, gridConstraints);

        inputField = new JTextField();
        inputField.setColumns(FIELD_COLUMNS);
        gridConstraints.gridy = 1;
        introPanel.add(inputField, gridConstraints);

        JButton enterButton = new JButton("Enter");
        gridConstraints.gridy = 2;
        gridConstraints.anchor = GridBagConstraints.CENTER;
        introPanel.add(enterButton, gridConstraints);

        enterButton.addActionListener(e -> {
            try {
                int count = Integer.parseInt(inputField.getText());
                if (count <= 0) throw new NumberFormatException();
                initSortScreen(count);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number!");
            }
        });

        setContentPane(introPanel);
        revalidate();
        repaint();
    }

    /**
     * Initializes the screen that displays numbers and sorting controls.
     *
     * @param count Number of values to generate and display.
     */
    private void initSortScreen(int count) {
        JPanel sortPanel = new JPanel(new BorderLayout());

        numbersPanel = new JPanel();
        generateNumbers(count);

        JButton sortButton = new JButton("Sort");
        JButton resetButton = new JButton("Reset");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(sortButton);
        bottomPanel.add(resetButton);

        sortPanel.add(new JScrollPane(numbersPanel), BorderLayout.CENTER);
        sortPanel.add(bottomPanel, BorderLayout.SOUTH);

        sortButton.addActionListener(e -> startSorting());
        resetButton.addActionListener(e -> {
            sorting = false;
            initIntroScreen();
        });

        setContentPane(sortPanel);
        revalidate();
        repaint();
    }

    /**
     * Updates the number buttons on the screen and highlights pivot and swapped elements.
     *
     * @param highlightIndex1 Index of the first element being swapped.
     * @param highlightIndex2 Index of the second element being swapped.
     * @param pivotIndex Index of the pivot element.
     */
    private void showNumbers(int highlightIndex1, int highlightIndex2, int pivotIndex) {
        numbersPanel.removeAll();

        int rowCount = Math.min(MAX_ROWS, numbers.length);
        int columnCount = (int) Math.ceil((double) numbers.length / rowCount);
        numbersPanel.setLayout(new GridLayout(rowCount, columnCount, GAP, GAP));

        for (int row = 0; row < rowCount; row++) {
            for (int col = 0; col < columnCount; col++) {
                int index = col * rowCount + row;
                if (index < numbers.length) {
                    JButton button = numberButtons[index];
                    button.setOpaque(true);
                    button.setText(String.valueOf(numbers[index]));

                    if (index == highlightIndex1 || index == highlightIndex2) {
                        button.setBackground(Color.RED);
                    } else if (index == pivotIndex) {
                        button.setBackground(Color.YELLOW);
                    } else {
                        button.setBackground(null);
                    }

                    numbersPanel.add(button);
                } else {
                    JButton emptyBtn = new JButton("");
                    emptyBtn.setEnabled(false);
                    numbersPanel.add(emptyBtn);
                }
            }
        }

        numbersPanel.revalidate();
        numbersPanel.repaint();
    }

    /**
     * Swaps two elements in the array and updates the display.
     *
     * @param firstNumberIndex Index of the first element to swap.
     * @param secondNumberIndex Index of the second element to swap.
     * @param pivotIndex Index of the pivot element for highlighting.
     */
    private void swap(int firstNumberIndex, int secondNumberIndex, int pivotIndex) {
        int tempValue = numbers[firstNumberIndex];
        numbers[firstNumberIndex] = numbers[secondNumberIndex];
        numbers[secondNumberIndex] = tempValue;


        try {
            SwingUtilities.invokeLater(() -> showNumbers(firstNumberIndex, secondNumberIndex, pivotIndex));
            Thread.sleep(SORT_DELAY_MS);
        } catch (Exception ignored) {}
    }

    /**
     * Recursively performs animated QuickSort on the array.
     *
     * @param low  Starting index.
     * @param high Ending index.
     */
    private void quickSortAnimated(int low, int high) {
        if (!sorting) return;
        if (low < high) {
            int pivotIndex = partition(low, high);

            SwingUtilities.invokeLater(() -> showNumbers(-1, -1, -1));

            quickSortAnimated(low, pivotIndex - 1);
            quickSortAnimated(pivotIndex + 1, high);
        }

    }

    /**
     * Handles click on a number button.
     * If the number is ≤ MAX_SMALL_NUMBER, generates a new array of that size.
     *
     * @param index Index of the clicked button.
     */
    private void handleNumberClick(int index) {
        int clickedValue = numbers[index];
        if (clickedValue <= MAX_SMALL_NUMBER) {
            generateNumbers(clickedValue);
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "Please select a value smaller or equal to " + MAX_SMALL_NUMBER + ".");
        }
    }


    /**
     * Generates a new random array of numbers and updates buttons.
     *
     * @param count Number of elements to generate.
     */
    private void generateNumbers(int count) {
        sorting = false;
        numbers = new int[count];
        numberButtons = new JButton[count];
        boolean hasSmallNumber = false;

        for (int i = 0; i < count; i++) {
            numbers[i] = randomGenerator.nextInt(MAX_NUMBER) + 1;
            if (numbers[i] <= MAX_SMALL_NUMBER) hasSmallNumber = true;

            int index = i;
            JButton button = new JButton(String.valueOf(numbers[i]));
            button.addActionListener(e -> handleNumberClick(index));
            numberButtons[i] = button;
        }

        if (!hasSmallNumber && count > 0) {
            int randomIndex = randomGenerator.nextInt(count);
            numbers[randomIndex] = randomGenerator.nextInt(MAX_SMALL_NUMBER) + 1;
            numberButtons[randomIndex].setText(String.valueOf(numbers[randomIndex]));
        }

        SwingUtilities.invokeLater(() -> showNumbers(-1, -1, -1));
    }

    /**
     * Starts the sorting process in a separate thread.
     */
    private void startSorting() {
        sorting = true;
        new Thread(() -> {
            quickSortAnimated(0, numbers.length - 1);
            descending = !descending;
        }).start();
    }

    /**
     * Partitions the array for QuickSort and highlights the pivot element.
     *
     * @param low  Starting index.
     * @param high Ending index.
     * @return Index of the pivot after partitioning.
     */
    private int partition(int low, int high) {
        int pivot = numbers[high];
        int i = low - 1;

        for (int j = low; j < high; j++) {
            if (!sorting) return i + 1;

            boolean condition = descending ? numbers[j] > pivot : numbers[j] < pivot;
            if (condition) {
                i++;
                swap(i, j, high);
            }
        }
        if (sorting) swap(i + 1, high, high);
        return i + 1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GranSoftTest::new);
    }
}
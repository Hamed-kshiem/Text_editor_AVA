import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.*;
import java.io.*;

public class TextEditor extends JFrame implements ActionListener, KeyListener {
    private Viewer viewer;
    private Text text;
    private JScrollPane scrollPane;
    private JFileChooser fileChooser;
    private String currentFilePath;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem openMenuItem, saveMenuItem, exitMenuItem;
    private JMenu editMenu;
    private JMenuItem cutMenuItem, copyMenuItem, pasteMenuItem, selectAllMenuItem;
    private JMenu searchMenu;
    private JMenuItem searchMenuItem;
    private JToolBar toolBar;
    private JComboBox<String> fontComboBox;
    private JComboBox<Integer> fontSizeComboBox;
    private JComboBox<Color> fontColorComboBox;


    public TextEditor() {
        super("Java Text Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // initialize components
        viewer = new Viewer();
        text = new Text();
        scrollPane = new JScrollPane(viewer);
        fileChooser = new JFileChooser();
        currentFilePath = null;
        menuBar = new JMenuBar();
        fileMenu = new JMenu("File");
        openMenuItem = new JMenuItem("Open");
        saveMenuItem = new JMenuItem("Save");
        exitMenuItem = new JMenuItem("Exit");
        editMenu = new JMenu("Edit");
        cutMenuItem = new JMenuItem("Cut");
        copyMenuItem = new JMenuItem("Copy");
        pasteMenuItem = new JMenuItem("Paste");
        selectAllMenuItem = new JMenuItem("Select All");
        searchMenu = new JMenu("Search");
        searchMenuItem = new JMenuItem("Search");
        toolBar = new JToolBar();
        fontComboBox = new JComboBox<>(new String[]{"Arial", "Times New Roman", "Courier New"});
        fontSizeComboBox = new JComboBox<>(new Integer[]{12, 14, 16, 18, 20, 22, 24});
        fontColorComboBox = new JComboBox<>(new Color[]{Color.BLACK, Color.RED, Color.BLUE, Color.GREEN});

        // add components to frame
        setJMenuBar(menuBar);
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(searchMenu);
        fileMenu.add(openMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(exitMenuItem);
        editMenu.add(cutMenuItem);
        editMenu.add(copyMenuItem);
        editMenu.add(pasteMenuItem);
        editMenu.add(selectAllMenuItem);
        searchMenu.add(searchMenuItem);
        toolBar.add(fontComboBox);
        toolBar.add(fontSizeComboBox);
        toolBar.add(fontColorComboBox);
        add(toolBar, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // set up event listeners
        openMenuItem.addActionListener(this);
        saveMenuItem.addActionListener(this);
        exitMenuItem.addActionListener(this);
        cutMenuItem.addActionListener(this);
        copyMenuItem.addActionListener(this);
        pasteMenuItem.addActionListener(this);
        selectAllMenuItem.addActionListener(this);
        searchMenuItem.addActionListener(this);
        fontComboBox.addActionListener(this);
        fontSizeComboBox.addActionListener(this);
        fontColorComboBox.addActionListener(this);
        viewer.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                viewer.select(e.getX(), e.getY());
            }

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    System.out.println("Double clicked");
                    viewer.selectWord(e.getX(), e.getY());
                }
            }
        });
        viewer.addKeyListener(this);
        // set default font and size
        text.setFont(new Font("Arial", Font.PLAIN, 12));
        viewer.setFont(new Font("Arial", Font.PLAIN, 12));
        fontComboBox.setSelectedItem("Arial");
        fontSizeComboBox.setSelectedItem(12);
        fontColorComboBox.setSelectedItem(Color.BLACK);
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == openMenuItem) {
            int returnVal = fileChooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    reader.close();
                    text.setText(sb.toString());
                    currentFilePath = file.getAbsolutePath();
                    setTitle("Java Text Editor - " + currentFilePath);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else if (source == saveMenuItem) {
            if (currentFilePath == null) {
                int returnVal = fileChooser.showSaveDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    saveFile(file);
                }
            } else {
                saveFile(new File(currentFilePath));
            }
        } else if (source == exitMenuItem) {
            System.exit(0);
        } else if (source == cutMenuItem) {
            text.cut();
        } else if (source == copyMenuItem) {
            text.copy();
        } else if (source == pasteMenuItem) {
            text.paste();
        } else if (source == selectAllMenuItem) {
            text.selectAll();
        } else if (source == searchMenuItem) {
            String searchText = JOptionPane.showInputDialog(this, "Search Text:");
            if (searchText != null && searchText.length() > 0) {
                System.out.println("Searching for: " + searchText);
                int index = viewer.search(searchText);
                if (index >= 0) {
                    JOptionPane.showMessageDialog(this, "Text found at index: " + index, "Search", JOptionPane.INFORMATION_MESSAGE);
                    viewer.select(index, index + searchText.length());
                    viewer.caretColumn = index + searchText.length();
                } else {
                    JOptionPane.showMessageDialog(this, "Text not found: " + searchText, "Search", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } else if (source == fontComboBox) {
            String fontFamily = (String) fontComboBox.getSelectedItem();
            Font font = viewer.getFont().deriveFont(Font.PLAIN);
            font = new Font(fontFamily, font.getStyle(), font.getSize());
            viewer.setFont(font);
            text.setFont(font);
        } else if (source == fontSizeComboBox) {
            int fontSize = (int) fontSizeComboBox.getSelectedItem();
            Font font = viewer.getFont().deriveFont(Font.PLAIN);
            font = new Font(font.getFontName(), font.getStyle(), fontSize);
            viewer.setFont(font);
            text.setFont(font);
        } else if (source == fontColorComboBox) {
            Color newColor = (Color) fontColorComboBox.getSelectedItem();
            Font font = viewer.getFont().deriveFont(Font.PLAIN);
            font = new Font(font.getFontName(), font.getStyle(), font.getSize());
            viewer.setFont(font);
            text.setFont(font);
            viewer.setFontColor(newColor);

        }
    }

    private void saveFile(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(text.getText());
            writer.close();
            currentFilePath = file.getAbsolutePath();
            setTitle("Java Text Editor - " + currentFilePath);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error writing file: " + ex.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        TextEditor editor = new TextEditor();
        editor.setVisible(true);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // add
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        int modifiers = e.getModifiers();

        // handle arrow keys
        if (keyCode == KeyEvent.VK_LEFT) {
            if (modifiers == KeyEvent.CTRL_MASK) {
                text.moveCaretWordBackward();
            } else {
                text.moveCaretBackward();
            }
        } else if (keyCode == KeyEvent.VK_RIGHT) {
            if (modifiers == KeyEvent.CTRL_MASK) {
                text.moveCaretWordForward();
            } else {
                text.moveCaretForward();
            }
        } else if (keyCode == KeyEvent.VK_UP) {
            text.moveCaretUp();
        } else if (keyCode == KeyEvent.VK_DOWN) {
            text.moveCaretDown();
        } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
            text.deleteBackward();
        } else if (keyCode == KeyEvent.VK_DELETE) {
            System.out.println("delete");
            text.deleteForward();
        } else if (keyCode == KeyEvent.VK_ENTER) {
            text.insert("\n", text.getCaretPosition());
        } else if (keyCode == KeyEvent.VK_TAB) {
            text.insert("\t", text.getCaretPosition());
        } else if (keyCode == KeyEvent.VK_A && modifiers == KeyEvent.CTRL_MASK) {
            text.selectAll();
        } else if (keyCode == KeyEvent.VK_X && modifiers == KeyEvent.CTRL_MASK) {
            text.cut();
        } else if (keyCode == KeyEvent.VK_C && modifiers == KeyEvent.CTRL_MASK) {
            text.copy();
        } else if (keyCode == KeyEvent.VK_V && modifiers == KeyEvent.CTRL_MASK) {
            text.paste();
        }

        // handle text input
        if (keyCode != KeyEvent.VK_LEFT && keyCode != KeyEvent.VK_RIGHT && keyCode != KeyEvent.VK_UP && keyCode != KeyEvent.VK_DOWN && !e.isActionKey()) {
            char c = e.getKeyChar();
            if (c != KeyEvent.CHAR_UNDEFINED) {
                text.insert(String.valueOf(c), text.getCaretPosition());
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    class Viewer extends Canvas {
        private int firstLine = 0;
        private int lineHeight;
        private int caretLine = 0;
        private int caretColumn = 0;
        private int selectionStart = -1;
        private int selectionEnd = -1;
        private boolean mousePressed = false;
        private Color fontColor = Color.BLACK;

        public Viewer() {
            setBackground(Color.WHITE);
            setPreferredSize(new Dimension(500, 500));
            Font font = getFont();
            lineHeight = 12; //getFontMetrics(new Font()).getHeight();
        }

        public void paint(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(5, 0, getWidth(), getHeight());

            g.setColor(fontColor);// cha
            FontMetrics fm = getFontMetrics(getFont());
            int y = fm.getAscent();
            int x = 0;
            int lineCount = getLineCount();
            for (int i = firstLine; i < lineCount; i++) {
                String line = getLine(i);
                if (line != null) {
                    g.drawString(line, x, y);
                }
                y += lineHeight;
                if (y > getHeight()) {
                    break;
                }
            }

            // draw caret
            int caretX = getCaretX();
            int caretY = (caretLine - firstLine) * lineHeight;
            g.setColor(Color.BLACK);
            g.drawLine(caretX, caretY, caretX, caretY + lineHeight);

            // draw selection behind text
            if (selectionStart >= 0 && selectionEnd >= 0) {
                int startLine = getLineOfOffset(selectionStart);
                int startColumn = selectionStart - getLineStart(startLine);
                int startX = getColumnX(startLine, startColumn);
                int startY = (startLine - firstLine) * lineHeight;

                int endLine = getLineOfOffset(selectionEnd);
                int endColumn = selectionEnd - getLineStart(endLine);
                int endX = getColumnX(endLine, endColumn);
                int endY = (endLine - firstLine) * lineHeight;

                g.setColor(Color.LIGHT_GRAY);
                if (startLine == endLine) {
                    g.fillRect(startX, startY, endX - startX, lineHeight);
                } else {
                    for (int i = startLine + 1; i < endLine; i++) {
                        g.fillRect(0, (i - firstLine) * lineHeight, getWidth(), lineHeight);
                    }
                    g.fillRect(0, endY, endX, lineHeight);

                }

            }
        }

        private int getColumnX(int startLine, int startColumn) {
            String line = getLine(startLine);
            int x = 0;
            for (int i = 0; i < startColumn; i++) {
                x += getFontMetrics(getFont()).charWidth(line.charAt(i));
            }
            return x;
        }

        private int getLineOfOffset(int selectionStart) {
            int lineCount = getLineCount();
            for (int i = 0; i < lineCount; i++) {
                int start = getLineStart(i);
                int end = start + getLine(i).length();
                if (selectionStart >= start && selectionStart <= end) {
                    return i;
                }
            }
            return -1;
        }

        public void select(int x, int y) {
            selectionStart = x;
            selectionEnd = y;
            repaint();
        }

        public void selectWord(int x, int y) {
            int line = firstLine + y / lineHeight;
            int column = getColumn(line, x);
            int start = getLineStart(line) + column;
            int end = start;
            String lineText = getLineText(line);
            while (start > 0 && Character.isLetterOrDigit(lineText.charAt(start - 1))) {
                start--;
            }
            while (end < lineText.length() && Character.isLetterOrDigit(lineText.charAt(end))) {
                end++;
            }
            setCaretPosition(line, start - getLineStart(line));
            selectionStart = start;
            selectionEnd = end;
            mousePressed = false;
            repaint();
        }

        public int search(String searchText) {
            String text = getText();
            return text.indexOf(searchText);
        }

        private String getText() {
            return text.getText();
        }

        private int getLineCount() {
            return text.getLineCount();
        }

        private String getLine(int index) {
            return text.getLine(index);
        }

        private int getLineStart(int line) {
            return text.getLineStart(line);
        }

        private int getLineEnd(int line) {
            return text.getLineEnd(line);
        }

        private String getLineText(int line) {
            return text.getLineText(line);
        }

        private int getLineOffset(int line) {
            return text.getLineOffset(line);
        }

        private int getLineY(int line) {
            return (line - firstLine) * lineHeight;
        }

        private int getColumn(int line, int x) {
            String lineText = getLineText(line);
            FontMetrics fm = getFontMetrics(getFont());
            int offset = getLineOffset(line);
            int index = 0;
            int width = 0;
            while (index < lineText.length() && width < x - offset) {
                char c = lineText.charAt(index);
                width += fm.charWidth(c);
                index++;
            }
            return index - 1;
        }

        private int getCaretX() {
            String lineText = getLineText(caretLine);
            FontMetrics fm = getFontMetrics(getFont());
            int offset = getLineOffset(caretLine);
            int x = offset + fm.stringWidth(lineText.substring(0, caretColumn));
            return x;
        }

        private void setCaretPosition(int line, int column) {
            caretLine = line;
            caretColumn = column;
        }

        public void update(Graphics g) {
            paint(g);
        }

        public void setFontColor(Color color) {
            this.fontColor = color;
            repaint();
        }
    }

    class Text {
        private StringBuilder sb;

        public Text() {
            sb = new StringBuilder("this is a test");
        }

        public void setText(String text) {
            sb = new StringBuilder(text);
            viewer.repaint();
        }

        public String getText() {
            return sb.toString();
        }

        public void setFont(Font font) {
            viewer.setFont(font);
            viewer.lineHeight = viewer.getFontMetrics(viewer.getFont()).getHeight();
            viewer.repaint();
        }

        public Font getFont() {
            return viewer.getFont();
        }

        public int getLineCount() {
            return sb.toString().split("\n").length;
        }

        public String getLine(int index) {
            String[] lines = sb.toString().split("\n");
            if (index < lines.length) {
                return lines[index];
            } else {
                return null;
            }
        }

        public int getLineStart(int line) {
            String[] lines = sb.toString().split("\n");
            if (line == 0) {
                return 0;
            } else if (line < lines.length) {
                int start = 0;
                for (int i = 0; i < line; i++) {
                    start += lines[i].length() + 1;
                }
                return start;
            } else {
                return -1;
            }
        }

        public int getLineEnd(int line) {
            String[] lines = sb.toString().split("\n");
            if (line < lines.length) {
                return getLineStart(line + 1) - 1;
            } else {
                return -1;
            }
        }

        public String getLineText(int line) {
            String[] lines = sb.toString().split("\n");
            if (line < lines.length) {
                return lines[line];
            } else {
                return null;
            }
        }

        public int getLineOffset(int line) {
            String[] lines = sb.toString().split("\n");
            if (line < lines.length) {
                int caretColumn = viewer.caretColumn;
                return viewer.getFontMetrics(viewer.getFont()).stringWidth(lines[line].substring(0, caretColumn));
            } else {
                return -1;
            }
        }

        public void cut() {
            if (viewer.selectionStart >= 0 && viewer.selectionEnd >= 0) {
                String selection = sb.substring(viewer.selectionStart, viewer.selectionEnd);
                sb.delete(viewer.selectionStart, viewer.selectionEnd);
                viewer.caretLine = viewer.selectionStart / (viewer.getFontMetrics(viewer.getFont()).stringWidth("\n") + 1);
                viewer.caretColumn = viewer.selectionStart - getLineStart(viewer.caretLine);
                viewer.selectionStart = -1;
                viewer.selectionEnd = -1;
                viewer.repaint();
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(selection), null);
            }
        }

        public void copy() {
            if (viewer.selectionStart >= 0 && viewer.selectionEnd >= 0) {
                String selection = sb.substring(viewer.selectionStart, viewer.selectionEnd);
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(selection), null);
            }
        }

        public void paste() {
            try {
                String selection = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
                if (viewer.selectionStart >= 0 && viewer.selectionEnd >= 0) {
                    sb.delete(viewer.selectionStart, viewer.selectionEnd);
                }
                sb.insert(getCaretPosition(), selection);
                setCaretPosition(getCaretPosition() + selection.length());
                viewer.selectionStart = -1;
                viewer.selectionEnd = -1;
                viewer.repaint();
            } catch (UnsupportedFlavorException | IOException ex) {
// ignore
            }
        }

        public void selectAll() {
            viewer.selectionStart = 0;
            viewer.selectionEnd = sb.length();
            viewer.repaint();
        }

        public int getCaretPosition() {
            return viewer.getLineStart(viewer.caretLine) + viewer.caretColumn;
        }

        public void setCaretPosition(int position) {
            int line = 0;
            while (getLineStart(line + 1) <= position) {
                line++;
            }
            int column = position - getLineStart(line);
            viewer.caretLine = line;
            viewer.caretColumn = column;
            viewer.selectionStart = -1;
            viewer.selectionEnd = -1;
            viewer.repaint();
        }

        public void setForeground(Color color) {
            viewer.setForeground(color);
            viewer.repaint();
        }

        public void insert(String input, int caretPosition) {
            sb.insert(caretPosition, input);
            viewer.repaint();
        }

        public void moveCaretWordBackward() {
            int line = viewer.caretLine;
            int column = viewer.caretColumn;
            String lineText = getLineText(line);
            if (column > 0) {
                if (Character.isWhitespace(lineText.charAt(column - 1))) {
                    for (int i = column - 1; i >= 0; i--) {
                        if (!Character.isWhitespace(lineText.charAt(i))) {
                            column = i + 1;
                            break;
                        }
                    }
                } else {
                    for (int i = column - 1; i >= 0; i--) {
                        if (Character.isWhitespace(lineText.charAt(i))) {
                            column = i + 1;
                            break;
                        }
                    }
                }
                viewer.caretColumn = column;
                viewer.repaint();
            }


        }

        public void moveCaretBackward() {
            int line = viewer.caretLine;
            int column = viewer.caretColumn;
            if (column > 0) {
                column--;
                viewer.caretColumn = column;
                viewer.repaint();
            }
        }

        public void moveCaretWordForward() {
            int line = viewer.caretLine;
            int column = viewer.caretColumn;
            String lineText = getLineText(line);
            if (column < lineText.length()) {
                if (Character.isWhitespace(lineText.charAt(column))) {
                    for (int i = column; i < lineText.length(); i++) {
                        if (!Character.isWhitespace(lineText.charAt(i))) {
                            column = i;
                            break;
                        }
                    }
                } else {
                    for (int i = column; i < lineText.length(); i++) {
                        if (Character.isWhitespace(lineText.charAt(i))) {
                            column = i;
                            break;
                        }
                    }
                }
                viewer.caretColumn = column;
                viewer.repaint();
            }
        }

        public void moveCaretForward() {
            int line = viewer.caretLine;
            int column = viewer.caretColumn;
            String lineText = getLineText(line);
            if (column < lineText.length()) {
                column++;
                viewer.caretColumn = column;
                viewer.repaint();
            }
        }

        public void moveCaretUp() {


        }

        public void moveCaretDown() {
        }

        public void deleteBackward() {
            if (viewer.selectionStart >= 0 && viewer.selectionEnd >= 0) {
                sb.delete(viewer.selectionStart, viewer.selectionEnd);
                viewer.caretLine = viewer.selectionStart / (viewer.getFontMetrics(viewer.getFont()).stringWidth("\n") + 1);
                viewer.caretColumn = viewer.selectionStart - getLineStart(viewer.caretLine);
                viewer.selectionStart = -1;
                viewer.selectionEnd = -1;
                viewer.repaint();
            } else {
                if (viewer.caretColumn > 0) {
                    sb.deleteCharAt(getCaretPosition() - 1);
                    setCaretPosition(getCaretPosition() - 1);
                    viewer.repaint();
                }
            }
        }

        public void deleteForward() {
            if (viewer.selectionStart >= 0 && viewer.selectionEnd >= 0) {
                sb.delete(viewer.selectionStart, viewer.selectionEnd);
                viewer.caretLine = viewer.selectionStart / (viewer.getFontMetrics(viewer.getFont()).stringWidth("\n") + 1);
                viewer.caretColumn = viewer.selectionStart - getLineStart(viewer.caretLine);
                viewer.selectionStart = -1;
                viewer.selectionEnd = -1;
                viewer.repaint();
            } else {
                if (viewer.caretColumn < getLineText(viewer.caretLine).length()) {
                    sb.deleteCharAt(getCaretPosition());
                    viewer.repaint();
                }
            }
        }


    }
}



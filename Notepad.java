import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Notepad extends JFrame implements ActionListener {
    private JTextArea textArea;
    private JFileChooser fileChooser;
    private File currentFile;

    public Notepad() {
        super("Einfacher Notizblock");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Textbereich
        textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        // Datei-Dialog
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Textdateien (*.txt)", "txt"));

        // Menüleiste
        JMenuBar menuBar = new JMenuBar();

        // Datei-Menü
        JMenu fileMenu = new JMenu("Datei");
        String[] fileItems = {"Neu", "Öffnen", "Speichern", "Speichern unter", "Beenden"};
        for (String name : fileItems) {
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(this);
            fileMenu.add(item);
            if ("Speichern unter".equals(name)) fileMenu.addSeparator();
        }
        menuBar.add(fileMenu);

        // Bearbeiten-Menü
        JMenu editMenu = new JMenu("Bearbeiten");
        JMenuItem cutItem = new JMenuItem("Ausschneiden");
        JMenuItem copyItem = new JMenuItem("Kopieren");
        JMenuItem pasteItem = new JMenuItem("Einfügen");
        cutItem.addActionListener(e -> textArea.cut());
        copyItem.addActionListener(e -> textArea.copy());
        pasteItem.addActionListener(e -> textArea.paste());
        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        menuBar.add(editMenu);

        setJMenuBar(menuBar);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Neu": newFile(); break;
            case "Öffnen": openFile(); break;
            case "Speichern": saveFile(); break;
            case "Speichern unter": saveFileAs(); break;
            case "Beenden": exitApp(); break;
        }
    }

    private void newFile() {
        textArea.setText("");
        currentFile = null;
        setTitle("Unbenannt - Einfacher Notizblock");
    }

    private void openFile() {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(currentFile))) {
                textArea.read(reader, null);
                setTitle(currentFile.getName() + " - Einfacher Notizblock");
            } catch (IOException ex) {
                showError("Fehler beim Öffnen:", ex);
            }
        }
    }

    private void saveFile() {
        if (currentFile != null) writeFile(currentFile);
        else saveFileAs();
    }

    private void saveFileAs() {
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            if (!currentFile.getName().toLowerCase().endsWith(".txt")) {
                currentFile = new File(currentFile.getParentFile(), currentFile.getName() + ".txt");
            }
            writeFile(currentFile);
        }
    }

    private void writeFile(File file) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            textArea.write(writer);
            setTitle(file.getName() + " - Einfacher Notizblock");
        } catch (IOException ex) {
            showError("Fehler beim Speichern:", ex);
        }
    }

    private void exitApp() {
        if (JOptionPane.showConfirmDialog(this,
                "Möchten Sie wirklich beenden?",
                "Beenden",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private void showError(String message, Exception ex) {
        JOptionPane.showMessageDialog(this,
                message + " " + ex.getMessage(),
                "Fehler",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Notepad().setVisible(true));
    }
}

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class SmartFileCompressor {
    private JFrame frame;
    private JTextField filePathField;
    private JPanel dropPanel;
    private JButton compressButton, decompressButton;
    private JFileChooser fileChooser;

    public SmartFileCompressor() {
        frame = new JFrame("Online File Compressor");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Online File Compressor", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(70, 70, 200));

        JLabel subtitleLabel = new JLabel("Compress Videos, PDFs, Images, and More", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.DARK_GRAY);

        // Drag & Drop Panel
        dropPanel = new JPanel();
        dropPanel.setLayout(new BorderLayout());
        dropPanel.setBorder(BorderFactory.createDashedBorder(new Color(70, 70, 200), 2, 5));
        dropPanel.setBackground(new Color(240, 240, 255));
        dropPanel.setPreferredSize(new Dimension(400, 150));

        JLabel dropLabel = new JLabel("Choose or drop file", SwingConstants.CENTER);
        dropLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        dropLabel.setForeground(Color.GRAY);

        JButton plusButton = new JButton("\u2795"); // Unicode for ➕
        plusButton.setFont(new Font("Arial", Font.BOLD, 40));
        plusButton.setForeground(new Color(70, 70, 200));
        plusButton.setContentAreaFilled(false);
        plusButton.setBorderPainted(false);
        plusButton.setFocusPainted(false);
        plusButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        dropPanel.add(plusButton, BorderLayout.NORTH);
        dropPanel.add(dropLabel, BorderLayout.CENTER);

        fileChooser = new JFileChooser();

        // Open File Chooser on Click
        plusButton.addActionListener(e -> chooseFile());
        dropPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                chooseFile();
            }
        });

        // Drag & Drop Support
        new DropTarget(dropPanel, new DropTargetAdapter() {
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!droppedFiles.isEmpty()) {
                        filePathField.setText(droppedFiles.get(0).getAbsolutePath());
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error reading file.");
                }
            }
        });

        filePathField = new JTextField(30);
        filePathField.setEditable(false);

        compressButton = new JButton("Compress");
        decompressButton = new JButton("Decompress");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(compressButton);
        buttonPanel.add(decompressButton);

        compressButton.addActionListener(e -> compressFile());
        decompressButton.addActionListener(e -> decompressFile());

        // Layout
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(dropPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(filePathField);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(buttonPanel);

        frame.add(titleLabel, BorderLayout.NORTH);
        frame.add(subtitleLabel, BorderLayout.CENTER);
        frame.add(centerPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void chooseFile() {
        int returnValue = fileChooser.showOpenDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            filePathField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void compressFile() {
        String filePath = filePathField.getText();
        if (filePath.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please select a file first.");
            return;
        }

        File file = new File(filePath);

        if (file.getName().matches(".*\\.(mp4|avi|mkv|mov)$")) {
            compressVideo(file);
        } else {
            compressToZip(file);
        }
    }

    private void compressVideo(File inputFile) {
        File outputFile = new File(inputFile.getParent(), "compressed_" + inputFile.getName());

        try {
            String command = "ffmpeg -i \"" + inputFile.getAbsolutePath() +
                             "\" -vcodec libx265 -crf 28 -preset fast \"" + outputFile.getAbsolutePath() + "\"";

            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            JOptionPane.showMessageDialog(frame, "Video compressed: " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame, "Video compression failed: " + e.getMessage());
        }
    }

    private void compressToZip(File file) {
        File zipFile = new File(file.getParent(), "compressed.zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(file)) {

            ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                zos.write(buffer, 0, bytesRead);
            }

            JOptionPane.showMessageDialog(frame, "File compressed: " + zipFile.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Compression failed: " + e.getMessage());
        }
    }

    private void decompressFile() {
        String filePath = filePathField.getText();
        if (filePath.isEmpty() || !filePath.endsWith(".zip")) {
            JOptionPane.showMessageDialog(frame, "Select a valid ZIP file.");
            return;
        }

        File zipFile = new File(filePath);
        File outputDir = zipFile.getParentFile();

        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis)) {

            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                File outputFile = new File(outputDir, entry.getName());
                try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = zis.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
            }
            JOptionPane.showMessageDialog(frame, "File decompressed to: " + outputDir.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Decompression failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SmartFileCompressor::new);
    }
}
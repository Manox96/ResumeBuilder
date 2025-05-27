import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.filechooser.FileNameExtensionFilter;




public class ResumeBuilder extends JFrame {
    private JTabbedPane tabbedPane;
    private Map<String, JComponent> fields;
    private JComboBox<String> templateSelector;
    private JTextArea previewArea;
    private String currentTemplate = "Professional";
    private String profileImagePath = null;
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SECONDARY_COLOR = new Color(52, 73, 94);
    private static final Color ACCENT_COLOR = new Color(231, 76, 60);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    
    public ResumeBuilder() {
        initializeUI();
        setupEventHandlers();
    }
    
    private void initializeUI() {
        setTitle("🚀 Créateur de CV Professionnel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        fields = new HashMap<>();
        
        // Create menu bar
        createMenuBar();
        
        // Create main content
        createMainContent();
        
        // Set window properties
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1000, 600));
        
        // Apply modern look and feel
        try {
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(PRIMARY_COLOR);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // File menu
        JMenu fileMenu = new JMenu("Fichier");
        fileMenu.setForeground(Color.WHITE);
        fileMenu.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        
        JMenuItem newItem = createMenuItem("Nouveau CV", "Créer un nouveau CV");
        JMenuItem saveItem = createMenuItem("Enregistrer", "Enregistrer les données du CV");
        JMenuItem loadItem = createMenuItem("Charger", "Charger un CV sauvegardé");
        JMenuItem exportPdfItem = createMenuItem("Exporter en PDF", "Générer un CV en PDF");
        JMenuItem exitItem = createMenuItem("Quitter", "Quitter l'application");
        
        // Add action listeners to menu items
        newItem.addActionListener(e -> clearAllFields());
        saveItem.addActionListener(e -> saveResumeData());
        loadItem.addActionListener(e -> loadResumeData());
        exportPdfItem.addActionListener(e -> generatePDF());
        exitItem.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, 
                "Êtes-vous sûr de vouloir quitter?", 
                "Quitter l'application", 
                JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
        
        fileMenu.add(newItem);
        fileMenu.addSeparator();
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exportPdfItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        menuBar.add(fileMenu);
    }
    
    private JMenuItem createMenuItem(String text, String tooltip) {
        JMenuItem item = new JMenuItem(text);
        item.setToolTipText(tooltip);
        item.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 11));
        return item;
    }
    
    private void createMainContent() {
        // Create main panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        splitPane.setResizeWeight(0.6);
        
        // Left panel - Input forms
        JPanel leftPanel = createInputPanel();
        splitPane.setLeftComponent(leftPanel);
        
        // Right panel - Preview
        JPanel rightPanel = createPreviewPanel();
        splitPane.setRightComponent(rightPanel);
        
        add(splitPane, BorderLayout.CENTER);
        
        // Bottom panel - Actions
        JPanel bottomPanel = createActionPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Informations du CV",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new java.awt.Font("Arial", java.awt.Font.BOLD, 14),
            PRIMARY_COLOR
        ));
        
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        tabbedPane.setBackground(Color.WHITE);
        
        // Personal Information Tab
        tabbedPane.addTab("👤 Personnel", createPersonalInfoPanel());
        
        // Professional Summary Tab
        tabbedPane.addTab("📝 Résumé", createSummaryPanel());
        
        // Experience Tab
        tabbedPane.addTab("💼 Expérience", createExperiencePanel());
        
        // Education Tab
        tabbedPane.addTab("🎓 Formation", createEducationPanel());
        
        // Skills Tab
        tabbedPane.addTab("⚡ Compétences", createSkillsPanel());
        
        // Projects Tab
        tabbedPane.addTab("🚀 Projets", createProjectsPanel());
        
        // Certifications Tab
        tabbedPane.addTab("🏆 Certifications", createCertificationsPanel());
        
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        int row = 0;
        
        // Add Profile Image Upload Button and helper text inside a rectangle panel
        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS));
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(BorderFactory.createLineBorder(new Color(52, 73, 94), 2, true));
        imagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton uploadImageButton = createStyledButton("Upload Image Here", new Color(52, 73, 94));
        uploadImageButton.addActionListener(e -> uploadProfileImage());
        uploadImageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePanel.add(Box.createVerticalStrut(8));
        imagePanel.add(uploadImageButton);
        
        JLabel uploadHint = new JLabel("Cliquez sur le bouton pour ajouter une photo de profil (JPG, PNG, GIF)");
        uploadHint.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 11));
        uploadHint.setForeground(Color.GRAY);
        uploadHint.setAlignmentX(Component.CENTER_ALIGNMENT);
        imagePanel.add(Box.createVerticalStrut(5));
        imagePanel.add(uploadHint);
        imagePanel.add(Box.createVerticalStrut(8));
        
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(imagePanel, gbc);
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        // Full Name
        addFormField(panel, gbc, row++, "Nom Complet *", "fullName", 30);
        
        // Contact Information
        addFormField(panel, gbc, row++, "Email *", "email", 30);
        addFormField(panel, gbc, row++, "Téléphone", "phone", 20);
        addFormField(panel, gbc, row++, "Adresse", "address", 40);
        addFormField(panel, gbc, row++, "Ville, Code Postal", "location", 30);
        
        // Professional Links
        addFormField(panel, gbc, row++, "URL LinkedIn", "linkedin", 40);
        addFormField(panel, gbc, row++, "URL GitHub", "github", 40);
        addFormField(panel, gbc, row++, "URL Portfolio", "portfolio", 40);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Résumé Professionnel / Objectif");
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        
        JTextArea textArea = new JTextArea(8, 40);
        textArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        textArea.setToolTipText("Écrivez un résumé de 2 à 3 phrases captivantes sur votre parcours professionnel et vos objectifs");
        
        fields.put("summary", textArea);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createExperiencePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Expérience Professionnelle");
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        
        JTextArea textArea = new JTextArea(12, 40);
        textArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        textArea.setToolTipText("Format: Titre du Poste | Entreprise | Dates\n• Réalisation ou responsabilité\n• Une autre réalisation");
        textArea.setText("Ingénieur en Logiciels | Entreprise Tech | 2022-Présent\n• Développé des applications web utilisant Java et Spring Boot\n• Collaboré avec des équipes interfonctionnelles pour livrer des projets\n• Amélioré la performance du système de 30%\n\nStagiaire | Entreprise Précédente | Été 2021\n• Aide à la réalisation d'applications mobiles\n• Participé à des révisions de code et aux tests");
        
        fields.put("experience", textArea);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createEducationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Formation");
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        
        JTextArea textArea = new JTextArea(8, 40);
        textArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        textArea.setToolTipText("Format: Degree | University | Year | GPA (optional)");
        textArea.setText("Bachelor of Science in Computer Science | University Name | 2022 | GPA: 3.8/4.0\nRelevant Coursework: Data Structures, Algorithms, Software Engineering, Database Systems");
        
        fields.put("education", textArea);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSkillsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Technical Skills");
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        
        JTextArea textArea = new JTextArea(10, 40);
        textArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        textArea.setToolTipText("Organize by categories. One skill per line or comma-separated.");
        textArea.setText("Programming Languages: Java, Python, JavaScript, C++\nWeb Technologies: HTML, CSS, React, Node.js, Spring Boot\nDatabases: MySQL, PostgreSQL, MongoDB\nTools & Technologies: Git, Docker, AWS, Jenkins\nSoft Skills: Team Leadership, Problem Solving, Communication");
        
        fields.put("skills", textArea);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createProjectsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Projects");
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        
        JTextArea textArea = new JTextArea(10, 40);
        textArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        textArea.setToolTipText("Format: Project Name | Technologies Used | Date\n• Description of what you built\n• Key features or achievements");
        textArea.setText("E-Commerce Web Application | Java, Spring Boot, React, MySQL | 2023\n• Built a full-stack e-commerce platform with user authentication\n• Implemented shopping cart, payment processing, and order management\n• Deployed on AWS with CI/CD pipeline\n\nTask Management Mobile App | React Native, Firebase | 2022\n• Developed cross-platform mobile app for task management\n• Integrated real-time synchronization and push notifications");
        
        fields.put("projects", textArea);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createCertificationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel label = new JLabel("Certifications & Achievements");
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        
        JTextArea textArea = new JTextArea(8, 40);
        textArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        textArea.setToolTipText("List certifications, awards, or notable achievements");
        textArea.setText("AWS Certified Solutions Architect | Amazon Web Services | 2023\nOracle Certified Java Programmer | Oracle | 2022\nDean's List | University Name | Fall 2021, Spring 2022\nHackathon Winner | Tech Conference 2022 | 1st Place");
        
        fields.put("certifications", textArea);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(Box.createVerticalStrut(10), BorderLayout.CENTER);
        panel.add(scrollPane, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, String fieldName, int columns) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        
        JLabel label = new JLabel(labelText);
        label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JTextField field = new JTextField(columns);
        field.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        
        fields.put(fieldName, field);
        panel.add(field, gbc);
    }
    
    private JPanel createPreviewPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Live Preview",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new java.awt.Font("Arial", java.awt.Font.BOLD, 14),
            PRIMARY_COLOR
        ));
        
        // Preview area
        previewArea = new JTextArea();
        previewArea.setFont(new java.awt.Font("Courier New", java.awt.Font.PLAIN, 10));
        previewArea.setEditable(false);
        previewArea.setBackground(new Color(248, 249, 250));
        previewArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JScrollPane scrollPane = new JScrollPane(previewArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Generate PDF button
        JButton pdfButton = createStyledButton("📄 Generate PDF", ACCENT_COLOR);
        pdfButton.setForeground(Color.BLACK);
        pdfButton.addActionListener(e -> generatePDF());
        
        // Clear All button
        JButton clearButton = createStyledButton("🗑️ Clear All", new Color(149, 165, 166));
        clearButton.setForeground(Color.BLACK);
        clearButton.addActionListener(e -> clearAllFields());
        
        panel.add(pdfButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void setupEventHandlers() {
        // Add document listeners to update preview in real-time
        for (JComponent component : fields.values()) {
            if (component instanceof JTextField) {
                ((JTextField) component).getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
                });
            } else if (component instanceof JTextArea) {
                ((JTextArea) component).getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                    public void insertUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
                    public void removeUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
                    public void changedUpdate(javax.swing.event.DocumentEvent e) { updatePreview(); }
                });
            }
        }
        
        // Initial preview update
        SwingUtilities.invokeLater(this::updatePreview);
    }
    
    private void updatePreview() {
        String preview = generatePreviewText();
        previewArea.setText(preview);
        previewArea.setCaretPosition(0);
    }
    
    private String generatePreviewText() {
        StringBuilder preview = new StringBuilder();

        // Gather fields
        String name = getFieldValue("fullName");
        String email = getFieldValue("email");
        String phone = getFieldValue("phone");
        String address = getFieldValue("address");
        String location = getFieldValue("location");
        String linkedin = getFieldValue("linkedin");
        String github = getFieldValue("github");
        String portfolio = getFieldValue("portfolio");
        String summary = getFieldValue("summary");
        String experience = getFieldValue("experience");
        String education = getFieldValue("education");
        String skills = getFieldValue("skills");
        String projects = getFieldValue("projects");
        String certifications = getFieldValue("certifications");

        // Header box
        String upperName = name.toUpperCase();
        int boxWidth = Math.max(upperName.length() + 10, 56);
        String boxTop = "╔" + "═".repeat(boxWidth - 2) + "╗\n";
        String boxBottom = "╚" + "═".repeat(boxWidth - 2) + "╝\n";
        String nameLine = String.format("║  %-" + (boxWidth - 4) + "s  ║\n", upperName);
        preview.append(boxTop).append(nameLine).append(boxBottom).append("\n\n");

        // Personal Info Section
        preview.append("👤 INFORMATIONS PERSONNELLES\n");
        preview.append("──────────────────────────────────────────────\n");
        if (!email.isEmpty()) preview.append("✉ Email: ").append(email).append("\n");
        if (!phone.isEmpty()) preview.append("☎ Téléphone: ").append(phone).append("\n");
        if (!address.isEmpty()) preview.append("🏠 Adresse: ").append(address).append("\n");
        if (!location.isEmpty()) preview.append("📍 Ville/Code Postal: ").append(location).append("\n");
        if (!linkedin.isEmpty()) preview.append("🔗 LinkedIn: ").append(linkedin).append("\n");
        if (!github.isEmpty()) preview.append("🐙 GitHub: ").append(github).append("\n");
        if (!portfolio.isEmpty()) preview.append("🌐 Portfolio: ").append(portfolio).append("\n");
        preview.append("\n");

        // Summary
        if (!summary.isEmpty()) {
            preview.append("📝 RÉSUMÉ PROFESSIONNEL\n");
            preview.append("──────────────────────────────────────────────\n");
            preview.append(summary).append("\n\n");
        }

        // Experience
        if (!experience.isEmpty()) {
            preview.append("🧑‍💼 EXPÉRIENCE\n");
            preview.append("──────────────────────────────────────────────\n");
            preview.append(experience).append("\n\n");
        }

        // Education
        if (!education.isEmpty()) {
            preview.append("🎓 FORMATION\n");
            preview.append("──────────────────────────────────────────────\n");
            preview.append(education).append("\n\n");
        }

        // Skills
        if (!skills.isEmpty()) {
            preview.append("🛠️ COMPÉTENCES\n");
            preview.append("──────────────────────────────────────────────\n");
            preview.append(skills).append("\n\n");
        }

        // Projects
        if (!projects.isEmpty()) {
            preview.append("🚀 PROJETS\n");
            preview.append("──────────────────────────────────────────────\n");
            preview.append(projects).append("\n\n");
        }

        // Certifications
        if (!certifications.isEmpty()) {
            preview.append("🏆 CERTIFICATIONS & RÉALISATIONS\n");
            preview.append("──────────────────────────────────────────────\n");
            preview.append(certifications).append("\n\n");
        }

        return preview.toString();
    }
    
    private String getFieldValue(String fieldName) {
        JComponent component = fields.get(fieldName);
        if (component instanceof JTextField) {
            return ((JTextField) component).getText().trim();
        } else if (component instanceof JTextArea) {
            return ((JTextArea) component).getText().trim();
        }
        return "";
    }
    
    
    private void generatePDF() {
        String name = getFieldValue("fullName");
        String email = getFieldValue("email");
        String phone = getFieldValue("phone");
        String address = getFieldValue("address");
        String location = getFieldValue("location");
        String linkedin = getFieldValue("linkedin");
        String github = getFieldValue("github");
        String portfolio = getFieldValue("portfolio");
        String summary = getFieldValue("summary");
        String experience = getFieldValue("experience");
        String education = getFieldValue("education");
        String skills = getFieldValue("skills");
        String projects = getFieldValue("projects");
        String certifications = getFieldValue("certifications");

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer votre nom complet avant de générer le PDF.", "Information manquante", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir"));
        fileChooser.setDialogTitle("Enregistrer le CV en PDF");
        fileChooser.setSelectedFile(new File(name.replaceAll("\\s+", "_") + "_CV.pdf"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".pdf")) {
                file = new File(file.getAbsolutePath() + ".pdf");
            }

            try {
                Document document = new Document(PageSize.A4, 40, 40, 40, 40);
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Define fonts
                com.itextpdf.text.Font nameFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.TIMES_ROMAN, 20, com.itextpdf.text.Font.BOLD, new BaseColor(41, 128, 185));
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL, new BaseColor(100, 100, 100));
                com.itextpdf.text.Font sectionHeaderFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.BOLD, BaseColor.BLACK);
                com.itextpdf.text.Font jobTitleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD, BaseColor.BLACK);
                com.itextpdf.text.Font companyFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);
                com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL, new BaseColor(120, 120, 120));
                com.itextpdf.text.Font normalFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);
                com.itextpdf.text.Font contactFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.NORMAL, BaseColor.BLACK);
                com.itextpdf.text.Font skillLevelFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.NORMAL, new BaseColor(150, 150, 150));

                // Main container table (2 columns)
                PdfPTable mainTable = new PdfPTable(2);
                mainTable.setWidthPercentage(100);
                mainTable.setWidths(new float[]{60f, 40f}); // Left column wider
                mainTable.getDefaultCell().setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                mainTable.getDefaultCell().setPadding(0);

                // =================== LEFT COLUMN ===================
                PdfPCell leftColumn = new PdfPCell();
                leftColumn.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                leftColumn.setPaddingRight(15); // Reduced padding
                leftColumn.setVerticalAlignment(Element.ALIGN_TOP);

                // Header with name and title
                Paragraph namePara = new Paragraph(name, nameFont);
                namePara.setSpacingAfter(3); // Reduced spacing
                leftColumn.addElement(namePara);

                if (!summary.isEmpty()) {
                    Paragraph titlePara = new Paragraph(getJobTitleFromSummary(summary), titleFont);
                    titlePara.setSpacingAfter(15); // Reduced spacing
                    leftColumn.addElement(titlePara);
                }

                // Experience Section
                if (!experience.isEmpty()) {
                    addLeftSectionHeader(leftColumn, "EXPERIENCE", sectionHeaderFont);
                    parseAndAddExperience(leftColumn, experience, jobTitleFont, companyFont, dateFont, normalFont);
                }

                // Education Section
                if (!education.isEmpty()) {
                    addLeftSectionHeader(leftColumn, "EDUCATION", sectionHeaderFont);
                    parseAndAddEducation(leftColumn, education, jobTitleFont, companyFont, dateFont, normalFont);
                }

                // Projects Section (if fits)
                if (!projects.isEmpty()) {
                    addLeftSectionHeader(leftColumn, "PROJECTS", sectionHeaderFont);
                    Paragraph projectsPara = new Paragraph(projects, normalFont);
                    projectsPara.setSpacingAfter(10); // Reduced spacing
                    leftColumn.addElement(projectsPara);
                }

                mainTable.addCell(leftColumn);

                // =================== RIGHT COLUMN ===================
                PdfPCell rightColumn = new PdfPCell();
                rightColumn.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                rightColumn.setPaddingLeft(15); // Reduced padding
                rightColumn.setVerticalAlignment(Element.ALIGN_TOP);

                // Add Profile Image if exists
                if (profileImagePath != null) {
                    try {
                        com.itextpdf.text.Image profileImage = com.itextpdf.text.Image.getInstance(profileImagePath);
                        profileImage.scaleToFit(120, 120); // Reduced image size
                        profileImage.setAlignment(Element.ALIGN_CENTER);
                        rightColumn.addElement(profileImage);
                        rightColumn.addElement(new Paragraph("\n")); // Add some spacing
                    } catch (Exception e) {
                        System.err.println("Error adding profile image: " + e.getMessage());
                    }
                }

                // Contact Information
                addRightSectionHeader(rightColumn, "", sectionHeaderFont); // No header, just spacing
                
                if (!phone.isEmpty()) {
                    Paragraph phonePara = new Paragraph(phone, contactFont);
                    phonePara.setSpacingAfter(2); // Reduced spacing
                    rightColumn.addElement(phonePara);
                }
                
                if (!email.isEmpty()) {
                    Paragraph emailPara = new Paragraph(email, contactFont);
                    emailPara.setSpacingAfter(2); // Reduced spacing
                    rightColumn.addElement(emailPara);
                }
                
                if (!portfolio.isEmpty()) {
                    Paragraph portfolioPara = new Paragraph(portfolio, contactFont);
                    portfolioPara.setSpacingAfter(2); // Reduced spacing
                    rightColumn.addElement(portfolioPara);
                }
                
                if (!location.isEmpty()) {
                    Paragraph locationPara = new Paragraph(location, contactFont);
                    locationPara.setSpacingAfter(15); // Reduced spacing
                    rightColumn.addElement(locationPara);
                }

                // Skills Section
                if (!skills.isEmpty()) {
                    addRightSectionHeader(rightColumn, "SKILLS", sectionHeaderFont);
                    parseAndAddSkills(rightColumn, skills, normalFont, skillLevelFont);
                }

                // Languages Section (if LinkedIn contains language info)
                if (!linkedin.isEmpty()) {
                    addRightSectionHeader(rightColumn, "LANGUAGES", sectionHeaderFont);
                    addLanguageSkill(rightColumn, "English", "Native", normalFont, skillLevelFont);
                    addLanguageSkill(rightColumn, "French", "Intermediate", normalFont, skillLevelFont);
                }

                // Certifications Section
                if (!certifications.isEmpty()) {
                    addRightSectionHeader(rightColumn, "CERTIFICATES", sectionHeaderFont);
                    parseAndAddCertifications(rightColumn, certifications, normalFont, dateFont);
                }

                mainTable.addCell(rightColumn);
                document.add(mainTable);

                document.close();
                
                JOptionPane.showMessageDialog(this, 
                    "CV PDF généré avec succès!\nEnregistré sous: " + file.getName(), 
                    "Succès", 
                    JOptionPane.INFORMATION_MESSAGE);
                    
                int choice = JOptionPane.showConfirmDialog(this, 
                    "Voulez-vous ouvrir le fichier PDF?", 
                    "Ouvrir PDF", 
                    JOptionPane.YES_NO_OPTION);
                    
                if (choice == JOptionPane.YES_OPTION) {
                    Desktop.getDesktop().open(file);
                }
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Erreur lors de la génération du PDF: " + e.getMessage(), 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    // Helper method to extract job title from summary
    private String getJobTitleFromSummary(String summary) {
        // Try to extract a job title from the first line of summary
        String[] lines = summary.split("\n");
        if (lines.length > 0 && lines[0].length() < 50) {
            return lines[0];
        }
        return "Professional"; // Default title
    }

    // Helper method to add left column section headers with dotted underline
    private void addLeftSectionHeader(PdfPCell cell, String title, com.itextpdf.text.Font font) throws DocumentException {
        Paragraph header = new Paragraph(title, font);
        header.setSpacingBefore(10); // Reduced spacing
        header.setSpacingAfter(5); // Reduced spacing
        cell.addElement(header);
        
        // Add new line pattern
        Paragraph linePattern = new Paragraph("(___)(___)(___)(___)(___)(___)(___)(___)(___)(___)(___)", 
            new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 7, com.itextpdf.text.Font.NORMAL, new BaseColor(41, 128, 185)));
        linePattern.setSpacingAfter(8); // Reduced spacing
        cell.addElement(linePattern);
    }

    // Helper method to add right column section headers with dotted underline
    private void addRightSectionHeader(PdfPCell cell, String title, com.itextpdf.text.Font font) throws DocumentException {
        if (!title.isEmpty()) {
            Paragraph header = new Paragraph(title, font);
            header.setSpacingBefore(10); // Reduced spacing
            header.setSpacingAfter(5); // Reduced spacing
            cell.addElement(header);
            
            // Add new line pattern
            Paragraph linePattern = new Paragraph("(___)(___)(___)(___)(___)(___)", 
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 7, com.itextpdf.text.Font.NORMAL, new BaseColor(41, 128, 185)));
            linePattern.setSpacingAfter(8); // Reduced spacing
            cell.addElement(linePattern);
        }
    }

    // Parse and add experience entries
    private void parseAndAddExperience(PdfPCell cell, String experience, com.itextpdf.text.Font jobFont, 
                                     com.itextpdf.text.Font companyFont, com.itextpdf.text.Font dateFont, 
                                     com.itextpdf.text.Font normalFont) throws DocumentException {
        
        String[] entries = experience.split("\n\n");
        for (String entry : entries) {
            String[] lines = entry.trim().split("\n");
            if (lines.length > 0) {
                // Job title
                Paragraph jobTitle = new Paragraph(lines[0], jobFont);
                jobTitle.setSpacingAfter(1); // Reduced spacing
                cell.addElement(jobTitle);
                
                // Company and date
                if (lines.length > 1) {
                    String companyLine = lines[1];
                    String[] parts = companyLine.split("\\|");
                    if (parts.length >= 2) {
                        Paragraph company = new Paragraph(parts[0].trim(), companyFont);
                        company.setSpacingAfter(1); // Reduced spacing
                        cell.addElement(company);
                        
                        Paragraph date = new Paragraph(parts[1].trim(), dateFont);
                        date.setSpacingAfter(3); // Reduced spacing
                        cell.addElement(date);
                    } else {
                        Paragraph company = new Paragraph(companyLine, companyFont);
                        company.setSpacingAfter(3); // Reduced spacing
                        cell.addElement(company);
                    }
                }
                
                // Description (bullet points)
                for (int i = 2; i < lines.length; i++) {
                    if (!lines[i].trim().isEmpty()) {
                        Paragraph bullet = new Paragraph("• " + lines[i].trim(), normalFont);
                        bullet.setSpacingAfter(2); // Reduced spacing
                        bullet.setIndentationLeft(8); // Reduced indentation
                        cell.addElement(bullet);
                    }
                }
                
                // Add spacing between entries
                Paragraph spacing = new Paragraph(" ", normalFont);
                spacing.setSpacingAfter(10); // Reduced spacing
                cell.addElement(spacing);
            }
        }
    }

    // Parse and add education entries
    private void parseAndAddEducation(PdfPCell cell, String education, com.itextpdf.text.Font degreeFont, 
                                    com.itextpdf.text.Font schoolFont, com.itextpdf.text.Font dateFont, 
                                    com.itextpdf.text.Font normalFont) throws DocumentException {
        
        String[] entries = education.split("\n\n");
        for (String entry : entries) {
            String[] lines = entry.trim().split("\n");
            if (lines.length > 0) {
                // Degree
                Paragraph degree = new Paragraph(lines[0], degreeFont);
                degree.setSpacingAfter(2);
                cell.addElement(degree);
                
                // School
                if (lines.length > 1) {
                    Paragraph school = new Paragraph(lines[1], schoolFont);
                    school.setSpacingAfter(1);
                    cell.addElement(school);
                }
                
                // Date
                if (lines.length > 2) {
                    Paragraph date = new Paragraph(lines[2], dateFont);
                    date.setSpacingAfter(1);
                    cell.addElement(date);
                }
                
                // Additional info
                if (lines.length > 3) {
                    Paragraph additional = new Paragraph(lines[3], normalFont);
                    additional.setSpacingAfter(15);
                    cell.addElement(additional);
                } else {
                    Paragraph spacing = new Paragraph(" ", normalFont);
                    spacing.setSpacingAfter(15);
                    cell.addElement(spacing);
                }
            }
        }
    }

    // Parse and add skills with levels
    private void parseAndAddSkills(PdfPCell cell, String skills, com.itextpdf.text.Font skillFont, 
                                 com.itextpdf.text.Font levelFont) throws DocumentException {
        
        String[] skillArray = skills.split("[,\n]");
        for (String skill : skillArray) {
            skill = skill.trim();
            if (!skill.isEmpty()) {
                // Create table for skill and level alignment
                PdfPTable skillTable = new PdfPTable(2);
                skillTable.setWidthPercentage(100);
                skillTable.setWidths(new float[]{70f, 30f});
                skillTable.getDefaultCell().setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                skillTable.getDefaultCell().setPadding(1);
                
                PdfPCell skillCell = new PdfPCell(new Phrase(skill, skillFont));
                skillCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                skillCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                
                PdfPCell levelCell = new PdfPCell(new Phrase("Advanced", levelFont));
                levelCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                levelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                
                skillTable.addCell(skillCell);
                skillTable.addCell(levelCell);
                
                skillTable.setSpacingAfter(4);
                cell.addElement(skillTable);
            }
        }
        
        Paragraph spacing = new Paragraph(" ", skillFont);
        spacing.setSpacingAfter(10);
        cell.addElement(spacing);
    }

    // Add language skill
    private void addLanguageSkill(PdfPCell cell, String language, String level, 
                                com.itextpdf.text.Font languageFont, com.itextpdf.text.Font levelFont) 
                                throws DocumentException {
        
        PdfPTable langTable = new PdfPTable(2);
        langTable.setWidthPercentage(100);
        langTable.setWidths(new float[]{70f, 30f});
        langTable.getDefaultCell().setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        langTable.getDefaultCell().setPadding(1);
        
        PdfPCell langCell = new PdfPCell(new Phrase(language, languageFont));
        langCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        langCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        
        PdfPCell levelCell = new PdfPCell(new Phrase(level, levelFont));
        levelCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        levelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        
        langTable.addCell(langCell);
        langTable.addCell(levelCell);
        
        langTable.setSpacingAfter(4);
        cell.addElement(langTable);
    }

    // Parse and add certifications
    private void parseAndAddCertifications(PdfPCell cell, String certifications, 
                                         com.itextpdf.text.Font certFont, com.itextpdf.text.Font dateFont) 
                                         throws DocumentException {
        
        String[] certArray = certifications.split("\n");
        for (String cert : certArray) {
            cert = cert.trim();
            if (!cert.isEmpty()) {
                String[] parts = cert.split("\\|");
                if (parts.length >= 2) {
                    Paragraph certName = new Paragraph(parts[0].trim(), certFont);
                    certName.setSpacingAfter(1);
                    cell.addElement(certName);
                    
                    Paragraph certDate = new Paragraph(parts[1].trim(), dateFont);
                    certDate.setSpacingAfter(8);
                    cell.addElement(certDate);
                } else {
                    Paragraph certName = new Paragraph(cert, certFont);
                    certName.setSpacingAfter(8);
                    cell.addElement(certName);
                }
            }
        }
    }
    
    private void saveResumeData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Resume Data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Resume Data Files", "rbd"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".rbd")) {
                file = new File(file.getAbsolutePath() + ".rbd");
            }
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                for (Map.Entry<String, JComponent> entry : fields.entrySet()) {
                    String value = "";
                    if (entry.getValue() instanceof JTextField) {
                        value = ((JTextField) entry.getValue()).getText();
                    } else if (entry.getValue() instanceof JTextArea) {
                        value = ((JTextArea) entry.getValue()).getText();
                    }
                    writer.println(entry.getKey() + "=" + value.replace("\n", "\\n"));
                }
                writer.println("template=" + currentTemplate);
                if (profileImagePath != null) {
                    writer.println("profileImage=" + profileImagePath);
                }
                
                JOptionPane.showMessageDialog(this, "Resume data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadResumeData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Resume Data");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Resume Data Files", "rbd"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("=")) {
                        String[] parts = line.split("=", 2);
                        String key = parts[0];
                        String value = parts.length > 1 ? parts[1].replace("\\n", "\n") : "";
                        
                        if (key.equals("template")) {
                            templateSelector.setSelectedItem(value);
                            currentTemplate = value;
                        } else if (key.equals("profileImage")) {
                            profileImagePath = value;
                        } else if (fields.containsKey(key)) {
                            JComponent component = fields.get(key);
                            if (component instanceof JTextField) {
                                ((JTextField) component).setText(value);
                            } else if (component instanceof JTextArea) {
                                ((JTextArea) component).setText(value);
                            }
                        }
                    }
                }
                
                updatePreview();
                JOptionPane.showMessageDialog(this, "Resume data loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearAllFields() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear all fields?", 
            "Clear All", 
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            for (JComponent component : fields.values()) {
                if (component instanceof JTextField) {
                    ((JTextField) component).setText("");
                } else if (component instanceof JTextArea) {
                    ((JTextArea) component).setText("");
                }
            }
            profileImagePath = null;
            updatePreview();
        }
    }
    
    private void switchTemplate(String template) {
        templateSelector.setSelectedItem(template);
        currentTemplate = template;
        updatePreview();
    }
    
    private void showAboutDialog() {
        String aboutText = "🚀 Advanced Resume Builder Pro\n\n" +
                          "Version: 2.0\n" +
                          "A powerful, user-friendly resume builder with PDF and HTML export capabilities.\n\n" +
                          "Features:\n" +
                          "• Multiple professional templates\n" +
                          "• Real-time preview\n" +
                          "• PDF export with professional formatting\n" +
                          "• HTML export for web portfolios\n" +
                          "• Save and load resume data\n" +
                          "• Modern, intuitive interface\n\n" +
                          "Built with Java Swing and iText PDF library\n" +
                          "© 2024 Resume Builder Pro";
        
        JOptionPane.showMessageDialog(this, aboutText, "About Resume Builder Pro", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showResumeTips() {
        String tipsText = "📝 Professional Resume Tips\n\n" +
                         "CONTENT TIPS:\n" +
                         "• Keep it concise - 1-2 pages maximum\n" +
                         "• Use action verbs (achieved, developed, managed)\n" +
                         "• Quantify achievements with numbers and percentages\n" +
                         "• Tailor content to the specific job you're applying for\n" +
                         "• Include relevant keywords from the job description\n\n" +
                         "FORMATTING TIPS:\n" +
                         "• Use consistent formatting throughout\n" +
                         "• Choose a clean, professional font\n" +
                         "• Maintain proper white space and margins\n" +
                         "• Use bullet points for easy scanning\n" +
                         "• Ensure contact information is prominent\n\n" +
                         "SECTIONS TO INCLUDE:\n" +
                         "• Professional Summary (2-3 sentences)\n" +
                         "• Work Experience (most recent first)\n" +
                         "• Education and Certifications\n" +
                         "• Technical Skills\n" +
                         "• Relevant Projects\n\n" +
                         "COMMON MISTAKES TO AVOID:\n" +
                         "• Spelling and grammar errors\n" +
                         "• Using personal pronouns (I, me, my)\n" +
                         "• Including irrelevant personal information\n" +
                         "• Using unprofessional email addresses\n" +
                         "• Making it too long or too cluttered";
        
        JTextArea textArea = new JTextArea(tipsText);
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "Professional Resume Tips", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void uploadProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            profileImagePath = selectedFile.getAbsolutePath();
            JOptionPane.showMessageDialog(this, "Profile image uploaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ResumeBuilder().setVisible(true);
        });
    }
}

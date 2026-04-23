import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShopInterface extends JFrame {
    int frameWidth = 1600;
    int frameHeight = 800;
    private List<Shoe> shoes;
    private Map<String, ImageIcon> imageCache = new HashMap<>();
    private JPanel leftFeaturedPanel;
    private JPanel rightGridPanel;
    private JLabel featuredImageLabel;
    private JLabel featuredTitleLabel;
    private JLabel featuredPriceLabel;
    private JLabel featuredBrandLabel;
    private JTextArea featuredDescriptionArea;
    private float alpha = 1f; // Độ trong suốt (từ 0.0 đến 1.0)
    private Timer timer;

    private static class Shoe {
        String imagePath;
        String title;
        String price;
        String brand;
        String description;

        Shoe(String imagePath, String title, String price, String brand, String description) {
            this.imagePath = imagePath;
            this.title = title;
            this.price = price;
            this.brand = brand;
            this.description = description;
        }
    }

    public ShopInterface() {
        setTitle("ShoesMarket - Giao diện sản phẩm");
        setSize(frameWidth, frameHeight);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(40, 40));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        getContentPane().setBackground(Color.WHITE);
        
        shoes = Arrays.asList(createShoes());

        // Panel trái: hiển thị sản phẩm lớn
        JPanel featuredPanel = createFeaturedPanel(shoes.get(0));
        featuredPanel.setPreferredSize(new Dimension(450, 0));
        add(featuredPanel, BorderLayout.WEST);
        rightGridPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        rightGridPanel.setBackground(Color.WHITE);
        for (Shoe shoe : shoes) {
            rightGridPanel.add(createSmallProductPanel(shoe));
        }
        add(rightGridPanel, BorderLayout.CENTER);

        // Preload hình ảnh trong background để tăng tốc độ
        preloadImages();
    }

    // Tạo Panel cho sản phẩm lớn bên trái
    private JPanel createFeaturedPanel(Shoe shoe) {
       this.leftFeaturedPanel = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                // Luôn vẽ nền trắng 100%
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            @Override
            protected void paintChildren(Graphics g) {
                // Chỉ áp dụng trong suốt cho các thành phần con
                Graphics2D g2 = (Graphics2D) g.create();
                
                // Khử răng cưa cho ảnh và chữ trong lúc chuyển động
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                super.paintChildren(g2);
                g2.dispose(); // Hủy bản sao
            }
        };
        leftFeaturedPanel.setOpaque(true);

        leftFeaturedPanel.setBackground(Color.WHITE);

        // Phần ảnh
        featuredImageLabel = new JLabel();
        featuredImageLabel.setIcon(getScaledIcon(shoe.imagePath, 400, 400));
        featuredImageLabel.setHorizontalAlignment(JLabel.CENTER);
        leftFeaturedPanel.add(featuredImageLabel, BorderLayout.NORTH);

        // Phần thông tin
        featuredTitleLabel = new JLabel(shoe.title);
        featuredPriceLabel = new JLabel(shoe.price);
        featuredBrandLabel = new JLabel(shoe.brand);
        featuredDescriptionArea = new JTextArea(shoe.description);

        JPanel infoPanel = new JPanel();
        GridBagLayout gbl = new GridBagLayout();
        infoPanel.setLayout(gbl);
        infoPanel.setOpaque(false);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Thêm đường kẻ ngang
        JSeparator separator = new JSeparator();
        infoPanel.add(separator, gbc);
        
        gbc.gridy++;
        gbc.weighty = 0;
        infoPanel.add(Box.createVerticalStrut(15), gbc);

        featuredTitleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        gbc.gridy++;
        infoPanel.add(featuredTitleLabel, gbc);

        featuredPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        gbc.gridy++;
        infoPanel.add(featuredPriceLabel, gbc);

        featuredBrandLabel.setForeground(Color.GRAY);
        gbc.gridy++;
        infoPanel.add(featuredBrandLabel, gbc);
        
        gbc.gridy++;
        infoPanel.add(Box.createVerticalStrut(10), gbc);

        featuredDescriptionArea.setWrapStyleWord(true);
        featuredDescriptionArea.setLineWrap(true);
        featuredDescriptionArea.setEditable(false);
        featuredDescriptionArea.setOpaque(false);
        featuredDescriptionArea.setForeground(Color.LIGHT_GRAY);
        gbc.gridy++;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        infoPanel.add(featuredDescriptionArea, gbc);

        leftFeaturedPanel.add(infoPanel, BorderLayout.CENTER);
        return leftFeaturedPanel;
    }

    // Tạo Panel cho từng ô nhỏ trong lưới
    private JPanel createSmallProductPanel(Shoe shoe) {
        RoundedPanel pnlProduct = new RoundedPanel(30, new Color(242, 242, 242));
        pnlProduct.setLayout(new BorderLayout(10, 10));
        pnlProduct.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15)); // Padding
        pnlProduct.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Phần tên + mô tả
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setOpaque(false);
        
        JLabel lblTitle = new JLabel(shoe.title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        JLabel lblDesc = new JLabel(shoe.description);
        lblDesc.setForeground(Color.GRAY);
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        
        headerPanel.add(lblTitle);
        headerPanel.add(lblDesc);

        // Phần ảnh
        JLabel lblImage = new JLabel();
        lblImage.setIcon(getScaledIcon(shoe.imagePath, 180, 180)); 
        lblImage.setHorizontalAlignment(JLabel.CENTER);

        // Phần giá + thương hiệu
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        
        JLabel lblBrand = new JLabel(shoe.brand);
        lblBrand.setForeground(Color.DARK_GRAY);
        
        JLabel lblPrice = new JLabel(shoe.price);
        lblPrice.setFont(new Font("SansSerif", Font.BOLD, 20));

        footerPanel.add(lblBrand, BorderLayout.WEST);
        footerPanel.add(lblPrice, BorderLayout.EAST);

        pnlProduct.add(headerPanel, BorderLayout.NORTH);
        pnlProduct.add(lblImage, BorderLayout.CENTER);
        pnlProduct.add(footerPanel, BorderLayout.SOUTH);

        addShoeSelectionListener(pnlProduct, shoe);

        return pnlProduct;
    }

    private void addShoeSelectionListener(Component component, Shoe shoe) {
        component.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        component.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                updateFeaturedPanel(shoe);
                if (component instanceof RoundedPanel) {
                    ((RoundedPanel) component).setBorderVisible(true); //
                }

                // Tắt viền cho các ô khác
                for (Component comp : rightGridPanel.getComponents()) {
                    if (comp instanceof RoundedPanel && comp != component) {
                        ((RoundedPanel) comp).setBorderVisible(false);
                        comp.repaint();
                    }
                }
                component.repaint();
            }
        });
    }

    private void preloadImages() {
        SwingUtilities.invokeLater(() -> {
            for (Shoe shoe : shoes) {
                getScaledIcon(shoe.imagePath, 180, 180); // Small images
                getScaledIcon(shoe.imagePath, 400, 400); // Large images
            }
        });
    }

    private void updateFeaturedPanel(Shoe shoe) {
        if (timer != null && timer.isRunning()) {
        timer.stop();
        }

        alpha = 0f;

        featuredImageLabel.setIcon(getScaledIcon(shoe.imagePath, 400, 400));
        featuredTitleLabel.setText(shoe.title);
        featuredPriceLabel.setText(shoe.price);
        featuredBrandLabel.setText(shoe.brand);
        featuredDescriptionArea.setText(shoe.description);

        timer = new Timer(20, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.08f;
                if (alpha >= 1f) {
                    alpha = 1f;
                    timer.stop();
                }
                leftFeaturedPanel.repaint(0, 0, leftFeaturedPanel.getWidth(), leftFeaturedPanel.getHeight());

            }
        });
        timer.start();
    }

    private Shoe[] createShoes() {
        return new Shoe[] {
            new Shoe("images/img1.png", "4DFWD PULSE SHOES", "$160.00", "Adidas", "This product is excluded from all promotional discounts and offers."),
            new Shoe("images/img2.png", "FORUM MID SHOES", "$100.00", "Adidas", "This product is excluded from all promotional discounts and offers."),
            new Shoe("images/img3.png", "SUPERNOVA SHOES", "$150.00", "Adidas", "NMD City Stock 2."),
            new Shoe("images/img4.png", "Adidas", "$160.00", "Adidas", "NMD City Stock 2."),
            new Shoe("images/img5.png", "Adidas", "$120.00", "Adidas", "NMD City Stock 2."),
            new Shoe("images/img6.png", "4DFWD PULSE SHOES", "$160.00", "Adidas", "This product is excluded from all promotional discounts and offers."),
            new Shoe("images/img1.png", "4DFWD PULSE SHOES", "$160.00", "Adidas", "This product is excluded from all promotional discounts and offers."),
            new Shoe("images/img2.png", "FORUM MID SHOES", "$100.00", "Adidas", "This product is excluded from all promotional discounts and offers."),
        };
    }

    public class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;
        private boolean showBorder = false; // Biến bật/tắt viền
        private Color borderColor = Color.BLUE;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false); // Để không hiển thị nền vuông mặc định
        }

        public void setBorderVisible(boolean visible) {
            this.showBorder = visible;
            repaint(); // Vẽ lại để cập nhật giao diện
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Vẽ nền bo góc
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);

            // Vẽ viền nếu được bật
            if (showBorder) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(2)); // Độ dày viền
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, cornerRadius, cornerRadius);
            }
        }
    }

    // Tải và thu phóng hình ảnh
    public ImageIcon getScaledIcon(String path, int width, int height) {
        String key = path + "_" + width + "_" + height;
        if (imageCache.containsKey(key)) {
            return imageCache.get(key);
        }
        
        ImageIcon imageIcon = new ImageIcon(path); 
        // Thu phóng ảnh
        Image image = imageIcon.getImage(); 
        Image newimg = image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH); 
        ImageIcon scaledIcon = new ImageIcon(newimg);
        
        // Cache hình ảnh
        imageCache.put(key, scaledIcon);
        return scaledIcon;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShopInterface().setVisible(true));
    }
}
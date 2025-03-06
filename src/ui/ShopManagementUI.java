package ui;

import repository.ShopRepository;
import repository.ShopPhoneRepository;
import repository.PhoneRepository;
import domain.Shop;
import domain.ShopPhone;
import domain.Phone;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ShopManagementUI extends JFrame {
    private JTextField ownerIdField;
    private JButton searchButton;
    private JList<String> shopList;
    private DefaultListModel<String> listModel;
    private ShopRepository shopRepository;
    private ShopPhoneRepository shopPhoneRepository;
    private PhoneRepository phoneRepository;

    public ShopManagementUI() {
        setTitle("가맹점 관리 시스템");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        shopRepository = new ShopRepository();
        shopPhoneRepository = new ShopPhoneRepository();
        phoneRepository = new PhoneRepository();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        getContentPane().add(panel);

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout());
        ownerIdField = new JTextField(15);
        searchButton = new JButton("검색");

        searchPanel.add(new JLabel("점주 ID:"));
        searchPanel.add(ownerIdField);
        searchPanel.add(searchButton);

        panel.add(searchPanel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        shopList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(shopList);
        panel.add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ownerIdText = ownerIdField.getText();
                try {
                    int ownerId = Integer.parseInt(ownerIdText);
                    List<Shop> shops = shopRepository.findByOwnerId(ownerId);
                    listModel.clear();
                    for (Shop shop : shops) {
                        listModel.addElement(shop.getShopName() + " (ID: " + shop.getShopId() + ")");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(ShopManagementUI.this, "유효한 점주 ID를 입력해주세요.");
                }
            }
        });

        shopList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedShop = shopList.getSelectedValue();
                    if (selectedShop != null) {
                        int shopId = Integer.parseInt(selectedShop.split("ID: ")[1].replace(")", ""));
                        showPhonesOfShop(shopId);
                    }
                }
            }
        });
    }

    // 선택된 가맹점에서 판매 중인 휴대폰을 보여주는 화면
    private void showPhonesOfShop(int shopId) {
        List<ShopPhone> shopPhones = shopPhoneRepository.findPhonesByShopId(shopId);

        // 테이블에 표시할 데이터 준비
        String[] columnNames = {"모델","브랜드", "가격", "재고"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (ShopPhone shopPhone : shopPhones) {
            Phone phone = phoneRepository.findById(shopPhone.getPhoneId());
            if (phone != null) {
                Object[] row = {phone.getModelName(),phone.getBrand(), phone.getPrice(), shopPhone.getStock()};
                tableModel.addRow(row);
            }
        }

        // 테이블 생성
        JTable phoneTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(phoneTable);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // 버튼 추가 (나중에 판매, 수정, 삭제 버튼 추가 예정)
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("판매");
        JButton editButton = new JButton("수정");
        JButton deleteButton = new JButton("삭제");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 창 설정
        JFrame phoneFrame = new JFrame("가맹점 판매 중인 휴대폰");
        phoneFrame.setSize(600, 400);
        phoneFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        phoneFrame.setLocationRelativeTo(null);
        phoneFrame.getContentPane().add(panel);
        phoneFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ShopManagementUI().setVisible(true);
            }
        });
    }
}

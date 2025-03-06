package ui;

import domain.*;
import repository.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
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
    private SaleRepository saleRepository;
    private CustomerRepository customerRepository;

    public ShopManagementUI() {
        setTitle("가맹점 관리 시스템");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        shopRepository = new ShopRepository();
        shopPhoneRepository = new ShopPhoneRepository();
        phoneRepository = new PhoneRepository();
        saleRepository = new SaleRepository();
        customerRepository = new CustomerRepository();

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
        JButton viewSalesButton = new JButton("판매 내역 보기");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(viewSalesButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 창 설정
        JFrame phoneFrame = new JFrame("가맹점 판매 중인 휴대폰");
        phoneFrame.setSize(600, 400);
        phoneFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        phoneFrame.setLocationRelativeTo(null);
        phoneFrame.getContentPane().add(panel);
        phoneFrame.setVisible(true);


        // 버튼 이벤트 처리
        viewSalesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 판매 내역 보기 기능 호출
                showSalesHistory(shopId); // shopId는 선택된 가맹점 ID
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = phoneTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(phoneFrame, "판매할 휴대폰을 선택하세요.");
                    return;
                }

                // 선택된 행에서 휴대폰 정보 가져오기
                String modelName = tableModel.getValueAt(selectedRow, 0).toString();
                int price = Integer.parseInt(tableModel.getValueAt(selectedRow, 2).toString());
                int stock = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());

                // 판매 수량 입력
                String quantityStr = JOptionPane.showInputDialog(phoneFrame, "판매할 수량을 입력하세요:");
                if (quantityStr == null || quantityStr.trim().isEmpty()) return;

                try {
                    int quantity = Integer.parseInt(quantityStr);
                    if (quantity <= 0 || quantity > stock) {
                        JOptionPane.showMessageDialog(phoneFrame, "유효한 수량을 입력하세요. (남은 재고: " + stock + ")");
                        return;
                    }

                    // 고객 ID 입력 받기
                    // 고객 정보 입력 받기
                    String customerName = JOptionPane.showInputDialog(phoneFrame, "고객 이름을 입력하세요:");
                    if (customerName == null || customerName.trim().isEmpty()) return;

                    String customerPhone = JOptionPane.showInputDialog(phoneFrame, "고객 전화번호를 입력하세요:");
                    if (customerPhone == null || customerPhone.trim().isEmpty()) return;

                    Customer existingCustomer = customerRepository.findCustomerByNameAndPhone(customerName, customerPhone);
                    if (existingCustomer == null) {
                        // 새로운 고객 생성
                        existingCustomer = new Customer(customerPhone, customerName);
                        customerRepository.save(existingCustomer);

                        customerRepository.findCustomerByNameAndPhone(customerName, customerPhone);
                        // 고객 저장 후, 다시 고객을 조회하여 저장되었는지 확인
                        existingCustomer = customerRepository.findCustomerByNameAndPhone(customerName, customerPhone);
                        if (existingCustomer == null) {
                            JOptionPane.showMessageDialog(phoneFrame, "고객 정보 저장 실패");
                            return;
                        }
                    }
                    Phone phone = phoneRepository.findByModelName(modelName);
                    if (phone == null) {
                        JOptionPane.showMessageDialog(phoneFrame, "휴대폰 정보를 찾을 수 없습니다.");
                        return;
                    }

                    LocalDateTime saleDate = LocalDateTime.now();
                    // 총 가격 계산
                    int totalPrice = price * quantity;

                    // 판매 기록 저장
                    Sale sale = new Sale(existingCustomer.getCustomerId(), phone.getPhoneId(), quantity, saleDate, shopId, totalPrice);
                    saleRepository.saveSale(sale);
                    shopPhoneRepository.decreaseStock(shopId, phone.getPhoneId(), quantity);
                    JOptionPane.showMessageDialog(phoneFrame, "판매 완료");

                    // UI 갱신 (재고 감소 반영)
                    tableModel.setValueAt(stock - quantity, selectedRow, 3);

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(phoneFrame, "올바른 숫자를 입력하세요.");
                }
            }
        });
    }

    private void showSalesHistory(int shopId) {
        // 판매 내역 가져오기
        List<Sale> sales = saleRepository.findSalesByShopId(shopId);

        // 테이블에 표시할 데이터 준비
        String[] columnNames = {"고객 이름", "모델", "수량", "총 가격", "판매일"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (Sale sale : sales) {
            // 판매 내역에 포함된 고객 정보와 전화번호를 찾음
            Customer customer = customerRepository.findById(sale.getCustomerId());
            Phone phone = phoneRepository.findById(sale.getPhoneId());

            if (customer != null && phone != null) {
                Object[] row = {
                        customer.getName(),
                        phone.getModelName(),
                        sale.getQuantity(),
                        sale.getTotalPrice(),
                        sale.getSaleDate().toString()
                };
                tableModel.addRow(row);
            }
        }

        // 판매 내역 테이블 생성
        JTable salesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(salesTable);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // 창 설정
        JFrame salesFrame = new JFrame("판매 내역");
        salesFrame.setSize(600, 400);
        salesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        salesFrame.setLocationRelativeTo(null);
        salesFrame.getContentPane().add(panel);
        salesFrame.setVisible(true);
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

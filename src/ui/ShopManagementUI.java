package ui;

import domain.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import repository.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class ShopManagementUI extends JFrame {
    private final JTextField ownerIdField;
    private final JList<String> shopList;
    private final DefaultListModel<String> listModel;

    private final ShopRepository shopRepository;
    private final ShopPhoneRepository shopPhoneRepository;
    private final PhoneRepository phoneRepository;
    private final SaleRepository saleRepository;
    private final CustomerRepository customerRepository;

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
        JButton searchButton = new JButton("검색");

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new ShopManagementUI().setVisible(true);
            }
        });
    }

    // 선택된 가맹점에서 판매 중인 휴대폰을 보여주는 화면
    private void showPhonesOfShop(int shopId) {
        List<ShopPhone> shopPhones = shopPhoneRepository.findPhonesByShopId(shopId);

        // 검색 필드를 추가합니다.
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");

        searchPanel.add(new JLabel("모델명 또는 브랜드:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 테이블에 표시할 데이터 준비
        String[] columnNames = {"모델", "브랜드", "가격", "재고"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        for (ShopPhone shopPhone : shopPhones) {
            Phone phone = phoneRepository.findById(shopPhone.getPhoneId());
            if (phone != null) {
                Object[] row = {phone.getModelName(), phone.getBrand(), phone.getPrice(), shopPhone.getStock()};
                tableModel.addRow(row);
            }
        }


        // 버튼 이벤트 처리
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText().toLowerCase();

                // 기존 테이블 데이터를 지운 후, 검색 조건에 맞는 휴대폰만 다시 추가
                tableModel.setRowCount(0);
                for (ShopPhone shopPhone : shopPhones) {
                    Phone phone = phoneRepository.findById(shopPhone.getPhoneId());
                    if (phone != null && (phone.getModelName().toLowerCase().contains(searchText) ||
                            phone.getBrand().toLowerCase().contains(searchText))) {
                        Object[] row = {phone.getModelName(), phone.getBrand(), phone.getPrice(), shopPhone.getStock()};
                        tableModel.addRow(row);
                    }
                }
            }
        });

        // 테이블 생성
        JTable phoneTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(phoneTable);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(searchPanel, BorderLayout.NORTH); // 검색 패널 추가
        panel.add(scrollPane, BorderLayout.CENTER);

        // 버튼 추가 (나중에 판매, 수정, 삭제 버튼 추가 예정)
        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("판매");
        JButton editButton = new JButton("재고 수정");
        JButton viewStatisticsButton = new JButton("판매 통계 보기");
        JButton viewSalesButton = new JButton("판매 내역 보기");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(viewStatisticsButton);
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
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = phoneTable.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(phoneFrame, "수정할 휴대폰을 선택하세요.");
                    return;
                }

                // 선택된 행에서 휴대폰 정보 가져오기
                String modelName = tableModel.getValueAt(selectedRow, 0).toString();
                int phoneId = phoneRepository.findByModelName(modelName).getPhoneId(); // phoneId 가져오기
                int stock = Integer.parseInt(tableModel.getValueAt(selectedRow, 3).toString());

                // 수정할 재고 입력 받기
                String newStockStr = JOptionPane.showInputDialog(phoneFrame, "수정할 재고를 입력하세요:", stock);

                if (newStockStr == null) return;

                try {
                    int newStock = Integer.parseInt(newStockStr);

                    // 재고 업데이트
                    shopPhoneRepository.updateStock(shopId, phoneId, newStock);  // phoneId를 사용하여 재고만 업데이트
                    tableModel.setValueAt(newStock, selectedRow, 3); // 테이블에서 재고 업데이트

                    JOptionPane.showMessageDialog(phoneFrame, "수정 완료");

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(phoneFrame, "올바른 숫자를 입력하세요.");
                }
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
        // 판매 통계 보기 버튼 클릭 이벤트 처리
        viewStatisticsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 판매 통계 보기 기능 호출
                showYearlySalesStatistics(shopId); // shopId는 선택된 가맹점 ID
            }
        });
    }

    // 선택된 가맹점에서 판매 통계 버튼 클릭 시 호출되는 메서드
    private void showYearlySalesStatistics(int shopId) {
        List<Sale> sales = saleRepository.findSalesByShopId(shopId);

        // 판매된 연도의 목록을 추출
        Set<Integer> yearsWithSales = new HashSet<>();
        for (Sale sale : sales) {
            int year = sale.getSaleDate().getYear();
            yearsWithSales.add(year);
        }

        // 연도별로 토글 버튼 생성
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        for (Integer year : yearsWithSales) {
            JButton yearButton = new JButton(year + "년 매출 통계");
            yearButton.addActionListener(e -> showYearSalesChart(shopId, year));
            buttonPanel.add(yearButton);
        }

        JScrollPane scrollPane = new JScrollPane(buttonPanel);
        scrollPane.setPreferredSize(new Dimension(200, 400));

        // 차트 패널
        JPanel chartPanel = new JPanel();
        chartPanel.setPreferredSize(new Dimension(600, 400));

        // 메인 레이아웃
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane, chartPanel);
        splitPane.setDividerLocation(200);

        // 전체 프레임 설정
        JFrame statisticsFrame = new JFrame("판매 통계");
        statisticsFrame.setLayout(new BorderLayout());
        statisticsFrame.add(splitPane, BorderLayout.CENTER);
        statisticsFrame.setSize(900, 600);
        statisticsFrame.setLocationRelativeTo(null); // 화면 중앙에 배치
        statisticsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        statisticsFrame.setVisible(true);
    }

    // 선택된 연도에 대한 매출 통계 차트를 월별로 보여주는 메서드
    private void showYearSalesChart(int shopId, int year) {
        List<Sale> sales = saleRepository.findSalesByShopIdAndYear(shopId, year);

        // 월별 판매 금액과 판매량 초기화
        int[] monthlySalesAmount = new int[12]; // 각 월의 판매 금액
        int[] monthlyQuantity = new int[12];    // 각 월의 판매량

        for (Sale sale : sales) {
            int month = sale.getSaleDate().getMonthValue() - 1; // 월은 0부터 시작하므로 1을 빼기
            monthlySalesAmount[month] += sale.getTotalPrice();
            monthlyQuantity[month] += sale.getQuantity();
        }

        // JFreeChart의 데이터셋 준비
        DefaultCategoryDataset salesAmountDataset = new DefaultCategoryDataset();  // 판매 금액용 데이터셋
        DefaultCategoryDataset quantityDataset = new DefaultCategoryDataset();    // 판매량용 데이터셋

        // 각 월에 대한 판매 금액과 판매량 데이터 추가
        for (int month = 0; month < 12; month++) {
            salesAmountDataset.addValue(monthlySalesAmount[month], "판매 금액", (month + 1) + "월");
            quantityDataset.addValue(monthlyQuantity[month], "판매량", (month + 1) + "월");
        }

        // 판매 금액 차트 생성
        JFreeChart salesChart = ChartFactory.createBarChart(
                year + " 년 판매 금액", // 차트 제목
                "월",                 // x축 레이블
                "금액",               // y축 레이블
                salesAmountDataset,   // 판매 금액 데이터셋
                PlotOrientation.VERTICAL, // 그래프 방향
                true,                 // 범례 표시
                true,                 // 툴팁 표시
                false                 // URL 링크 표시
        );

        // 판매량 차트 생성
        JFreeChart quantityChart = ChartFactory.createBarChart(
                year + " 년 판매량", // 차트 제목
                "월",              // x축 레이블
                "판매량",          // y축 레이블
                quantityDataset,   // 판매량 데이터셋
                PlotOrientation.VERTICAL, // 그래프 방향
                true,              // 범례 표시
                true,              // 툴팁 표시
                false              // URL 링크 표시
        );

        // 판매 금액 차트에 한글 폰트 설정
        Font font = new Font("맑은 고딕", Font.PLAIN, 12);
        salesChart.getTitle().setFont(font);
        salesChart.getCategoryPlot().getDomainAxis().setLabelFont(font);
        salesChart.getCategoryPlot().getRangeAxis().setLabelFont(font);
        salesChart.getCategoryPlot().getDomainAxis().setTickLabelFont(font);
        salesChart.getCategoryPlot().getRangeAxis().setTickLabelFont(font);
        salesChart.getLegend().setItemFont(font);

        // 판매량 차트에 한글 폰트 설정
        quantityChart.getTitle().setFont(font);
        quantityChart.getCategoryPlot().getDomainAxis().setLabelFont(font);
        quantityChart.getCategoryPlot().getRangeAxis().setLabelFont(font);
        quantityChart.getCategoryPlot().getDomainAxis().setTickLabelFont(font);
        quantityChart.getCategoryPlot().getRangeAxis().setTickLabelFont(font);
        quantityChart.getLegend().setItemFont(font);  // 판매량 차트 legend 폰트
        // 판매량 그래프의 y축을 정수로 설정
        CategoryPlot quantityPlot = quantityChart.getCategoryPlot();
        NumberAxis quantityAxis = (NumberAxis) quantityPlot.getRangeAxis();
        quantityAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); // 정수로 설정

        // 차트 패널 생성 및 추가
        ChartPanel salesChartPanel = new ChartPanel(salesChart);
        ChartPanel quantityChartPanel = new ChartPanel(quantityChart);

        // 차트 패널을 새 창에 표시
        JFrame chartFrame = new JFrame(year + " 년 판매 통계");
        chartFrame.setLayout(new GridLayout(2, 1));
        chartFrame.setSize(600, 800);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setLocationRelativeTo(null);
        chartFrame.getContentPane().add(salesChartPanel, BorderLayout.NORTH);
        chartFrame.getContentPane().add(quantityChartPanel, BorderLayout.SOUTH);
        chartFrame.setVisible(true);
    }

    private void showSalesHistory(int shopId) {
        // 판매 내역 가져오기
        List<Sale> sales = saleRepository.findSalesByShopId(shopId);

        // 검색 필드를 추가합니다.
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");

        searchPanel.add(new JLabel("고객 이름 또는 모델 명:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // 테이블에 표시할 데이터 준비
        String[] columnNames = {"고객 이름", "모델", "수량", "총 가격", "판매일"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // 버튼 이벤트 처리
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText().toLowerCase();

                // 기존 테이블 데이터를 지운 후, 검색 조건에 맞는 판매 내역만 다시 추가
                tableModel.setRowCount(0);
                for (Sale sale : sales) {
                    Customer customer = customerRepository.findById(sale.getCustomerId());
                    Phone phone = phoneRepository.findById(sale.getPhoneId());
                    if (customer != null && phone != null &&
                            (customer.getName().toLowerCase().contains(searchText) ||
                                    phone.getModelName().toLowerCase().contains(searchText))) {
                        Object[] row = {customer.getName(), phone.getModelName(), sale.getQuantity(), sale.getTotalPrice(), sale.getSaleDate()};
                        tableModel.addRow(row);
                    }
                }
            }
        });

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
        panel.add(searchPanel, BorderLayout.NORTH); // 검색 패널 추가
        panel.add(scrollPane, BorderLayout.CENTER);


        // 창 설정
        JFrame salesFrame = new JFrame("판매 내역");
        salesFrame.setSize(600, 400);
        salesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        salesFrame.setLocationRelativeTo(null);
        salesFrame.getContentPane().add(panel);
        salesFrame.setVisible(true);
    }
}

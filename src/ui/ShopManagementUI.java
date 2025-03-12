package ui;

import com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme;
import domain.*;
import domain.dto.SaleDTO;
import domain.dto.ShopPhoneDTO;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import repository.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


public class ShopManagementUI extends JFrame {
    private final JList<String> shopList;
    private final DefaultListModel<String> listModel;

    private final ShopRepository shopRepository = new ShopRepository();
    private final ShopPhoneRepository shopPhoneRepository = new ShopPhoneRepository();
    private final PhoneRepository phoneRepository = new PhoneRepository();
    private final SaleRepository saleRepository = new SaleRepository();
    private final CustomerRepository customerRepository = new CustomerRepository();
    private final OwnerRepository ownerRepository = new OwnerRepository();
    private final CommonCodeRepository commonCodeRepository = new CommonCodeRepository();

    private UUID ownerId; // 점주 UUID 저장
    private JLabel ownerNameLabel;  // 추가된 JLabel

    public ShopManagementUI() {
        setTitle("휴대폰 판매 관리 시스템");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        getContentPane().add(panel);

        listModel = new DefaultListModel<>();
        shopList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(shopList);
        panel.add(scrollPane, BorderLayout.CENTER);

        // 가맹점 목록 위에 표시될 텍스트 라벨 추가
        ownerNameLabel = new JLabel(); // 초기에는 이름이 없으므로 빈 JLabel로 설정
        ownerNameLabel.setHorizontalAlignment(SwingConstants.CENTER); // 가운데 정렬
        panel.add(ownerNameLabel, BorderLayout.NORTH);

        shopList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    String selectedShop = shopList.getSelectedValue();
                    if (selectedShop != null) {
                        String[] split = selectedShop.split("\\(ID: ");
                        int shopId = Integer.parseInt(split[1].replace(")", ""));
                        showPhonesOfShop(shopId, split[0]);
                    }
                }
            }
        });
        // UUID 입력 후 가맹점 목록 자동 조회
        requestOwnerId();

        setVisible(true);
    }

    public static void main(String[] args) {
        FlatDarkFlatIJTheme.setup();

        SwingUtilities.invokeLater(() ->
                new ShopManagementUI().setVisible(true));
    }

    private void requestOwnerId() {
        while (true) {
            String ownerIdText = JOptionPane.showInputDialog(this, "점주 UUID를 입력하세요:", "로그인", JOptionPane.QUESTION_MESSAGE);
            if (ownerIdText == null) System.exit(0); // 사용자가 취소하면 프로그램 종료

            try {
                ownerId = UUID.fromString(ownerIdText); // UUID 유효성 검사
                Owner owner = ownerRepository.findByUUID(ownerId);
                if (owner != null) {
                    ownerNameLabel.setText(owner.getName() + " 님이 운영중인 가맹점 목록"); // 로그인한 점주의 이름과 메시지 표시
                    searchShops(owner); // 가맹점 목록 자동 검색
                    return;
                }
                JOptionPane.showMessageDialog(this, "등록되지 않은 사용자입니다", "등록되지 않은 사용자", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "유효한 UUID를 입력해주세요.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchShops(Owner owner) {
        try {
            int ownerId = owner.getOwnerId();
            List<Shop> shops = shopRepository.findByOwnerId(ownerId);
            listModel.clear();
            for (Shop shop : shops) {
                listModel.addElement(shop.getShopName() + " (ID: " + shop.getShopId() + ")");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(ShopManagementUI.this, "에러발생");
        }
    }

    // 선택된 가맹점에서 판매 중인 휴대폰을 보여주는 화면
    private void showPhonesOfShop(int shopId, String shopName) {
        List<ShopPhone> shopPhones = shopPhoneRepository.findPhonesByShopId(shopId);

        // 검색 필드를 추가합니다.
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("검색");

        JComboBox<String> brandComboBox = new JComboBox<>();
        // 🔹 브랜드 목록 불러오기 (Common Code 활용)
        List<String> brands = commonCodeRepository.findBrandNames();
        for (String brand : brands) {
            brandComboBox.addItem(brand);
        }
        searchPanel.add(new JLabel("모델명 또는 브랜드:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("브랜드:"));
        searchPanel.add(brandComboBox); // 브랜드 콤보박스 추가

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


        // 검색 버튼 이벤트 처리
        searchButton.addActionListener(e -> searchPhoneOfShop(shopId, searchField, tableModel));

        // 테이블 생성
        JTable phoneTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(phoneTable);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(searchPanel, BorderLayout.NORTH); // 검색 패널 추가
        panel.add(scrollPane, BorderLayout.CENTER);

        // 버튼 추가 (나중에 판매, 수정, 삭제 버튼 추가 예정)
        JPanel buttonPanel = new JPanel();
        JButton sellButton = new JButton("판매");
        JButton editButton = new JButton("재고 수정");
        JButton viewStatisticsButton = new JButton("판매 통계 보기");
        JButton viewSalesButton = new JButton("판매 내역 보기");
        JButton deleteButton = new JButton("삭제");

        buttonPanel.add(sellButton);
        buttonPanel.add(editButton);
        buttonPanel.add(viewStatisticsButton);
        buttonPanel.add(viewSalesButton);
        buttonPanel.add(deleteButton);  // 삭제 버튼 추가

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // 창 설정
        JFrame phoneFrame = new JFrame(shopName + "에서 판매 중인 휴대폰");
        phoneFrame.setSize(600, 400);
        phoneFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        phoneFrame.setLocationRelativeTo(null);
        phoneFrame.getContentPane().add(panel);
        phoneFrame.setVisible(true);


        // 판매 통계 버튼 이벤트 처리
        viewSalesButton.addActionListener(e -> showSalesHistory(shopId));
        // 수정 버튼 이벤트 처리
        editButton.addActionListener(e -> editPhoneStock(shopId, phoneTable, phoneFrame, tableModel));
        // 판매 버튼 이벤트 처리
        sellButton.addActionListener( e -> sellPhoneToCustomer(shopId, phoneTable, phoneFrame, tableModel));
        // 판매 통계 보기 버튼 클릭 이벤트 처리
        viewStatisticsButton.addActionListener(e -> showYearlySalesStatistics(shopId));
        deleteButton.addActionListener(e -> deletePhoneFromShop(shopId, phoneTable, tableModel));
    }

    private void searchPhoneOfShop(int shopId, JTextField searchField, DefaultTableModel tableModel) {
        String searchText = searchField.getText().toLowerCase();

        List<ShopPhoneDTO> filtered = shopPhoneRepository.findByShopIdAndSearchText(shopId, searchText);
        tableModel.setRowCount(0);
        for (ShopPhoneDTO shopPhone : filtered) {
            Object[] row = {shopPhone.getModelName(), shopPhone.getBrand(), shopPhone.getPrice(), shopPhone.getStock()};
            tableModel.addRow(row);
        }
    }

    private void sellPhoneToCustomer(int shopId, JTable phoneTable, JFrame phoneFrame, DefaultTableModel tableModel) {
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
                int response = JOptionPane.showConfirmDialog(
                        phoneFrame,
                        "고객 정보가 없습니다. 새로 추가하시겠습니까?",
                        "고객 추가 확인",
                        JOptionPane.YES_NO_OPTION
                );

                if (response == JOptionPane.YES_OPTION) {
                    // 새로운 고객 생성 및 저장
                    existingCustomer = new Customer(customerPhone, customerName);
                    customerRepository.save(existingCustomer);

                    // 저장 확인 (다시 조회)
                    existingCustomer = customerRepository.findCustomerByNameAndPhone(customerName, customerPhone);
                    if (existingCustomer == null) {
                        JOptionPane.showMessageDialog(phoneFrame, "고객 정보 저장 실패");
                        return;
                    }
                } else {
                    JOptionPane.showMessageDialog(phoneFrame, "판매를 취소합니다.");
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

    private void editPhoneStock(int shopId, JTable phoneTable, JFrame phoneFrame, DefaultTableModel tableModel) {
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

    private void deletePhoneFromShop(int shopId, JTable phoneTable, DefaultTableModel tableModel) {
        int selectedRow = phoneTable.getSelectedRow();
        if (selectedRow != -1) {
            String modelName = (String) tableModel.getValueAt(selectedRow, 0);
            int phoneId = phoneRepository.findByModelName(modelName).getPhoneId();

            // 삭제 확인 메시지 표시
            int confirm = JOptionPane.showConfirmDialog(
                    null,
                    "정말로 [" + modelName + "] 모델을 삭제하시겠습니까?",
                    "삭제 확인",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    shopPhoneRepository.deleteByShopIdAndPhoneId(shopId, phoneId);  // 삭제 작업
                    tableModel.removeRow(selectedRow);  // 테이블에서 삭제된 행을 제거
                    JOptionPane.showMessageDialog(null, "휴대폰이 성공적으로 삭제되었습니다.");
                } catch (RuntimeException e) {
                    JOptionPane.showMessageDialog(null, "삭제에 실패했습니다: " + e.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "삭제할 휴대폰을 선택해주세요.");
        }
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
        int totalSalesAmount = 0; // 총 판매 금액
        int totalQuantity = 0;    // 총 판매량

        for (Sale sale : sales) {
            int month = sale.getSaleDate().getMonthValue() - 1; // 월은 0부터 시작하므로 1을 빼기
            monthlySalesAmount[month] += sale.getTotalPrice();
            monthlyQuantity[month] += sale.getQuantity();
            totalSalesAmount += sale.getTotalPrice(); // 총 판매 금액 합산
            totalQuantity += sale.getQuantity();     // 총 판매량 합산
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
                "",                 // x축 레이블
                "금액",               // y축 레이블
                salesAmountDataset,   // 판매 금액 데이터셋
                PlotOrientation.VERTICAL, // 그래프 방향
                true,                 // 범례 표시
                true,                 // 툴팁 표시
                false                 // URL 링크 표시
        );

        CategoryPlot salesPlot = salesChart.getCategoryPlot();
        BarRenderer salesRenderer = (BarRenderer) salesPlot.getRenderer();
        salesRenderer.setDefaultItemLabelsVisible(true); // 항목 레이블 표시
        salesRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator()); // 레이블 생성기 설정

        // 판매량 차트 생성
        JFreeChart quantityChart = ChartFactory.createBarChart(
                year + " 년 판매량", // 차트 제목
                "",              // x축 레이블
                "판매량",          // y축 레이블
                quantityDataset,   // 판매량 데이터셋
                PlotOrientation.VERTICAL, // 그래프 방향
                true,              // 범례 표시
                true,              // 툴팁 표시
                false              // URL 링크 표시
        );
        // 판매량 차트의 렌더러 설정
        CategoryPlot quantityPlot1 = quantityChart.getCategoryPlot();
        BarRenderer quantityRenderer = (BarRenderer) quantityPlot1.getRenderer();
        quantityRenderer.setDefaultItemLabelsVisible(true); // 항목 레이블 표시
        quantityRenderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator()); // 레이블 생성기 설정

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

        // 총 판매량과 총 판매 금액 레이블 생성
        JLabel totalSalesLabel = new JLabel("총 판매 금액: " + totalSalesAmount + " 원");
        JLabel totalQuantityLabel = new JLabel("총 판매량: " + totalQuantity + " 개");

        // 차트 패널 생성 및 추가
        ChartPanel salesChartPanel = new ChartPanel(salesChart);
        ChartPanel quantityChartPanel = new ChartPanel(quantityChart);

//        // 차트 패널을 새 창에 표시
//        JFrame chartFrame = new JFrame(year + " 년 판매 통계");
//        chartFrame.setLayout(new GridLayout(3, 1));
//        chartFrame.setSize(600, 740);
//        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        chartFrame.setLocationRelativeTo(null);
//        chartFrame.getContentPane().add(salesChartPanel, BorderLayout.NORTH);
//        chartFrame.getContentPane().add(quantityChartPanel, BorderLayout.CENTER);
//        chartFrame.setVisible(true);

        // 총 판매량, 총 판매 금액 레이블을 아래에 추가
        JPanel totalPanel = new JPanel();
        totalPanel.setLayout(new BoxLayout(totalPanel, BoxLayout.Y_AXIS));
        totalPanel.add(totalSalesLabel);
        totalPanel.add(totalQuantityLabel);
        // totalPanel의 크기 제한
        totalPanel.setPreferredSize(new Dimension(600, 40)); // 가로 600, 세로 50으로 설정

//        chartFrame.getContentPane().add(totalPanel, BorderLayout.SOUTH);

        // 차트 패널 크기 조정 (차트 영역을 더 크게 할 수 있도록)
        salesChartPanel.setPreferredSize(new Dimension(600, 350));  // 판매 금액 차트 크기
        quantityChartPanel.setPreferredSize(new Dimension(600, 350));  // 판매량 차트 크기

        JFrame chartFrame = new JFrame(year + " 년 판매 통계");
        chartFrame.setLayout(new BorderLayout());
        chartFrame.setSize(600, 800);
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setLocationRelativeTo(null);

// 차트 패널과 레이블 패널 추가
        chartFrame.getContentPane().add(salesChartPanel, BorderLayout.NORTH);
        chartFrame.getContentPane().add(quantityChartPanel, BorderLayout.CENTER);
        chartFrame.getContentPane().add(totalPanel, BorderLayout.SOUTH); // 레이블은 아래에 추가

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
        String[] columnNames = {"고객 이름","연락처", "모델", "수량", "총 가격", "판매일"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

        // 버튼 이벤트 처리
        searchButton.addActionListener(e -> searchSellHistory(shopId, searchField, tableModel));

        for (Sale sale : sales) {
            // 판매 내역에 포함된 고객 정보와 전화번호를 찾음
            Customer customer = customerRepository.findById(sale.getCustomerId());
            Phone phone = phoneRepository.findById(sale.getPhoneId());

            if (customer != null && phone != null) {
                Object[] row = {
                        customer.getName(),
                        customer.getPhoneNumber(),
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

    private void searchSellHistory(int shopId, JTextField searchField, DefaultTableModel tableModel) {
        String searchText = searchField.getText().toLowerCase();

        List<SaleDTO> filteredSales = saleRepository.findSalesByShopIdAndSearchText(shopId, searchText);
        // 기존 테이블 데이터를 지운 후, 검색 조건에 맞는 판매 내역만 다시 추가
        tableModel.setRowCount(0);
        for (SaleDTO sale : filteredSales) {
            Object[] row = {sale.getCustomerName(),sale.getCustomerPhoneNumber() ,sale.getModelName(), sale.getQuantity(), sale.getTotalPrice(), sale.getSaleDate()};
            tableModel.addRow(row);
        }
    }
}

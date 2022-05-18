package ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import dao.KhachHangDAO;
import entity.KhachHang;
import net.java.balloontip.BalloonTip;

public class QuanLyKhachHangPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ColoredButton btnSearch;
	private DefaultTableModel model;
	private CustomTable table;
	private ColoredButton btnThem;
	private ColoredButton btnSua;
	private ColoredButton btnXoa;
	private ColoredButton btnQuayLai;

	private JTextField txtMa;
	private JDateChooser dateNgaySinh;
	private final Dimension dimension = new Dimension(100, 25);
	private JTextField txtHoTen;
	private JTextField txtDiaChi;
	private JComboBox<String> cbbGT;
	private JTextField txtSdt;
	private JTextField txtEmail;

	private ColoredButton btnXoaTrang;
	private KhachHangDAO khachHangDAO;
	private JPanel jPanel1;
	private JPanel jPanel2;
	private JTextField txtHoTen2;
	private JTextField txtSdt2;
	private JTextField txtDiaChi2;
	private JTextField txtEmail2;

	private ColoredButton btnXoaTrang2;
	private JTabbedPane tabbedPane;
	private MainFrame mainFrame;
	private JLabel lbPage;
	private ColoredButton btnHome;
	private ColoredButton btnEnd;
	private ColoredButton btnBefore;
	private ColoredButton btnNext;
	private int currentIndex = 0;
	private List<KhachHang> dsKH;
	private BalloonTip ballTen;
	private BalloonTip ballNS;
	private BalloonTip ballDC;
	private BalloonTip ballEmail;
	private BalloonTip ballSDT;

	public QuanLyKhachHangPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		setOpaque(true);
		setLayout(new BorderLayout());
		setBackground(Color.white);
		setLookAndFeel();

		addControls();
		addEvents();
		getAllComponents(this).forEach(item -> {
			item.addKeyListener(new KeyAdapter() {
				private boolean isCtrlPressed = false;

				@Override
				public void keyPressed(KeyEvent e) {
					if(isCtrlPressed) {
						//Nhấn phím N khi đang giữ phím Ctrl
						if(e.getKeyCode() == KeyEvent.VK_N)
							btnThem.doClick();
						else if(e.getKeyCode() == KeyEvent.VK_X)
							btnXoa.doClick();
						else if(e.getKeyCode() == KeyEvent.VK_LEFT)
							btnQuayLai.doClick();
						else if(e.getKeyCode() == KeyEvent.VK_E) {
							btnXoaTrang.doClick();
							btnXoaTrang2.doClick();
						}

						else if(e.getKeyCode() == KeyEvent.VK_F)
							btnSearch.doClick();

					} else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
						isCtrlPressed = true;
					else 
						isCtrlPressed = false;
				}

				@Override
				public void keyReleased(KeyEvent e) {
					isCtrlPressed = false;
				}
			});
		});
		khachHangDAO = new KhachHangDAO();

		dsKH = new ArrayList<KhachHang>();

		loadData();
	}
	public Component getDefaultFocusComponent() {
		return txtHoTen2;
	}

	private void taiDuLieuLenBang(List<KhachHang> dsKH, int minIndex) {
		if(minIndex >= dsKH.size() || minIndex < 0)
			return;

		lbPage.setText("Trang " + (minIndex / 20 + 1) + " trong " + ((dsKH.size() - 1) / 20 + 1) + " trang.");

		model.setRowCount(0);
		model.getDataVector().removeAllElements();
		model.fireTableDataChanged();
		NumberFormat nf = NumberFormat.getInstance(Locale.US);
		nf.setMinimumIntegerDigits(2);
		nf.setMaximumFractionDigits(2);

		for(int i = minIndex; i < minIndex + 20; i++) {
			if(i >= dsKH.size())
				break;
			KhachHang khachHang = dsKH.get(i);
			SwingUtilities.invokeLater(() -> {
				LocalDate ns = khachHang.getNgaySinh();
				String ngaySinh = null;
				if(ns != null) 
					ngaySinh = ns.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				
				model.addRow(new Object[] { khachHang.getMaKhachHang(), khachHang.getTenKhachHang(), ngaySinh,
						khachHang.getDiaChi(), khachHang.isGioiTinh() ? "Nam" : "Nữ",
								khachHang.getEmail() == null ? "" : khachHang.getEmail(), khachHang.getSoDienThoai() });
			});
		}

		currentIndex = minIndex;

	}

	private void loadData() {
		dsKH = khachHangDAO.layDSKhachHangGanDay();
		taiDuLieuLenBang(dsKH, 0);

	}
	public List<Component> getAllComponents(Container c) {
		Component[] comps = c.getComponents();
		List<Component> compList = new ArrayList<Component>();
		for (Component comp : comps) {
			compList.add(comp);
			if (comp instanceof Container)
				compList.addAll(getAllComponents((Container) comp));
		}
		return compList;
	}

	private void addEvents() {
		this.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(evt.getPropertyName().equals("ancestor")) {
					tabbedPane.setSelectedIndex(0);
				}
			}
		});
		
		txtHoTen2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				timKhachHang();
			}
		});
		
		txtDiaChi2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				timKhachHang();
			}
		});
		
		txtEmail2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				timKhachHang();
			}
		});
		
		txtSdt2.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				timKhachHang();
			}
		});
		
		txtHoTen.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (txtHoTen.getText().trim().isEmpty()) {
					ballTen.setTextContents("Tên khách hàng không được rỗng");
					ballTen.setVisible(true);
				} 
				else if (!txtHoTen.getText().trim().matches("[\\p{Lu}[A-Z]][\\p{L}[a-z]]*(\\s+[\\p{Lu}[A-Z]][\\p{L}[a-z]]*)*")) {
					ballTen.setTextContents(" + Họ và tên khách hàng phải bắt đầu chữ cái in hoa \n"
							+ " + Không chứa các ký tự đặc biệt và số");
					ballTen.setVisible(true);
				} else 
					ballTen.setVisible(false);
			}
		});
		
		dateNgaySinh.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				ballNS.setVisible(false);
				Date date = dateNgaySinh.getDate();
				if(date != null) {
					LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					if (!ngayHopLe(LocalDate.now(), localDate)) {
						ballNS.setTextContents("Ngày sinh phải trước ngày hiện tại");
						ballNS.setVisible(true);
					}
				}
			}
		});
		
		txtDiaChi.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				ballDC.setVisible(false);
				if (txtDiaChi.getText().trim().isEmpty()) {
					ballDC.setTextContents("Địa chỉ không được rỗng");
					ballDC.setVisible(true);
				}
			}
		});
		
		txtSdt.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				ballSDT.setVisible(false);
				if (!txtSdt.getText().trim().matches("[0]\\d{9}")) {
					ballSDT.setTextContents("Số điện thoại gồm 10 chữ số, bắt đầu là 0");
					ballSDT.setVisible(true);
				}
			}
		});
		
		txtEmail.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				ballEmail.setVisible(false);
				if(!txtEmail.getText().trim().isEmpty()) {
					if (!txtEmail.getText().trim().matches("^[a-z][a-z0-9\\.]{7,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
						ballEmail.setTextContents(
								" + Email phải bắt đầu bằng 1 ký tự \n" + "+ Chỉ chứa ký tự a-z, 0-9 và ký tự dấu chấm (.) \n"
										+ "+ Độ dài tối thiểu là 8, độ dài tối đa là 32 \n"
										+ " +Tên miền có thể là tên miền cấp 1 hoặc cấp 2");
						ballEmail.setVisible(true);
					}
				}
			}
		});
		
		btnHome.addActionListener(e -> {
			if(model.getRowCount() > 0) {
				taiDuLieuLenBang(dsKH, 0);
			}
		});

		btnEnd.addActionListener(e -> {
			if(model.getRowCount() > 0) {
				taiDuLieuLenBang(dsKH, dsKH.size() - dsKH.size() % 20);
			}
		});

		btnBefore.addActionListener(e -> {
			if(model.getRowCount() > 0) {
				taiDuLieuLenBang(dsKH, currentIndex  - 20);
			}
		});

		btnNext.addActionListener(e -> {
			if(model.getRowCount() > 0) {
				taiDuLieuLenBang(dsKH, currentIndex + 20);
			}
		});

		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				// pane 1

				int row = table.getSelectedRow();
				if (row == -1)
					return;
				txtMa.setText(table.getValueAt(row, 0).toString());
				txtHoTen.setText(table.getValueAt(row, 1).toString());
				try {
					if(table.getValueAt(row, 2) != null) {
						Date date = new SimpleDateFormat("dd-MM-yyyy").parse(table.getValueAt(row, 2).toString());
						dateNgaySinh.setDate(date);
					}
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				txtDiaChi.setText(table.getValueAt(row, 3).toString());
				cbbGT.setSelectedItem(table.getValueAt(row, 4).toString().equals("Nam") ? "Nam" : "Nữ");
				try {
					txtEmail.setText(table.getValueAt(row, 5).toString());
				} catch (Exception ee) {
					txtEmail.setText("");
				}
				txtSdt.setText(table.getValueAt(row, 6).toString());


			}
		});

		// xoa rong
		btnXoaTrang.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				txtMa.setText("");
				txtHoTen.setText("");
				txtHoTen.requestFocus();
				txtDiaChi.setText("");
				txtEmail.setText("");
				txtSdt.setText("");

				dateNgaySinh.setCalendar(null);
				if (table.getSelectedRow() != -1)
					table.clearSelection();

			}
		});
		btnXoaTrang2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				txtHoTen2.setText("");
				txtDiaChi2.setText("");
				txtEmail2.setText("");
				txtSdt2.setText("");

			}
		});
		btnThem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {

				if (validData()) {
					LocalDate ngaySinh = null;
					Date date = dateNgaySinh.getDate();
					if(date != null) {
						SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
						String str_date = format1.format(date);

						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
						// convert String to LocalDate
						ngaySinh = LocalDate.parse(str_date, formatter);
					}
					String ten = txtHoTen.getText().trim();
					String diachi = txtDiaChi.getText().trim();
					String sdt = txtSdt.getText().trim();
					String email = txtEmail.getText().trim();
					boolean gt = cbbGT.getSelectedItem().toString().equals("Nam") ? true : false;
					KhachHang khachHang = new KhachHang(ten, ngaySinh, diachi, gt, email, sdt);

					if (khachHangDAO.addKhachHang(khachHang)) {
						UIConstant.showInfo(QuanLyKhachHangPanel.this, "Thêm thành công");
						dsKH.add(khachHang);
						xoaRong();
					} else
						UIConstant.showInfo(QuanLyKhachHangPanel.this, "Thêm thất bại");
				}

			}

		});

		btnXoa.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				if (row == -1) {
					UIConstant.showInfo(QuanLyKhachHangPanel.this, "Bạn chưa chọn dòng để xóa");
					return;
				} else {
					int click = JOptionPane.showConfirmDialog(QuanLyKhachHangPanel.this,
							"Bạn có chắc chăn muốn xóa không ?", "Cảnh báo", JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE, new ImageIcon("Images//warning.png"));
					if (click == JOptionPane.YES_OPTION) {
						if (khachHangDAO.xoaKhachHang(txtMa.getText().toString().trim())) {
							UIConstant.showInfo(QuanLyKhachHangPanel.this, "Xóa thành công");
							model.removeRow(row);
							dsKH.remove(row + currentIndex);
							xoaRong();
						} else
							UIConstant.showInfo(QuanLyKhachHangPanel.this, "Xóa thất bại");
					} else
						return;
				}
			}
		});

		btnSua.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(tabbedPane.getSelectedIndex() == 0) {
					tabbedPane.setSelectedIndex(1);
				}
				else {
					int row = table.getSelectedRow();
					if (row == -1) {
						UIConstant.showWarning(QuanLyKhachHangPanel.this, "Bạn chưa chọn khách hàng để cập nhật");
					} else {

						if (validData()) {
							LocalDate ngaySinh = null;
							Date date = dateNgaySinh.getDate();
							if(date != null) {
								SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy");
								String str_date = format1.format(date);

								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
								// convert String to LocalDate
								ngaySinh = LocalDate.parse(str_date, formatter);
							}
							
							String ma = txtMa.getText().trim();
							String ten = txtHoTen.getText().trim();
							String diachi = txtDiaChi.getText().trim();
							String sdt = txtSdt.getText().trim();
							boolean gt = cbbGT.getSelectedItem().toString().equals("Nam") ? true : false;
							String email;
							try {
								email = txtEmail.getText().trim();
							} catch (Exception ex) {
								email = "";
							}

							KhachHang khachHang = new KhachHang(ten, ngaySinh, diachi, gt, email, sdt);
							khachHang.setMaKhachHang(ma);

							// fomart ngay sinh
							LocalDate local = khachHang.getNgaySinh();
							DateTimeFormatter fTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
							String fString = local.format(fTimeFormatter);
							if (khachHangDAO.capNhatKhachHang(khachHang)) {								
								model.setValueAt(khachHang.getMaKhachHang(), row, 0);
								model.setValueAt(khachHang.getTenKhachHang(), row, 1);
								model.setValueAt(fString, row, 2);
								model.setValueAt(khachHang.getDiaChi(), row, 3);
								model.setValueAt(khachHang.isGioiTinh() ? "Nam" : "Nữ", row, 4);
								model.setValueAt(khachHang.getEmail(), row, 5);
								model.setValueAt(khachHang.getSoDienThoai(), row, 6);

								dsKH.set(row + currentIndex, khachHang);

								UIConstant.showInfo(QuanLyKhachHangPanel.this, "Cập nhật thành công");
								txtHoTen.requestFocus();
							}

						} else {
							UIConstant.showWarning(QuanLyKhachHangPanel.this, "Cập nhật không thành công");
							return;
						}
					}
				}

			}
		});
		btnSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				timKhachHang();

			}
		});
		btnQuayLai.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				mainFrame.changeCenter(mainFrame.getTrangChuPanel());
			}
		});
	}

	private void timKhachHang() {
		String ten = txtHoTen2.getText().trim();

		String diachi = txtDiaChi2.getText().trim();
		String email = txtEmail2.getText().trim();

		String sdt = txtSdt2.getText().trim();

		dsKH = khachHangDAO.timKhachHang(ten, diachi, email, sdt);
		if (dsKH.size() != 0) {
			taiDuLieuLenBang(dsKH, 0);

			txtHoTen2.requestFocus();
		} else {
			txtHoTen2.requestFocus();
			UIConstant.showInfo(QuanLyKhachHangPanel.this, "Không tìm thấy khách hàng nào !");
		}
	}
	private void xoaRong() {
		txtHoTen2.setText("");
		txtDiaChi2.setText("");
		txtEmail2.setText("");
		txtSdt2.setText("");
	}

	private boolean ngayHopLe(LocalDate ngayHienTai, LocalDate ngaySinh) {
		if (ngaySinh.isBefore(ngayHienTai))
			return true;

		return false;
	}

	private boolean validData() {
		String diaChi = txtDiaChi.getText().trim();
		String email = txtEmail.getText().trim();
		String sdt = txtSdt.getText().trim();
		String ten = txtHoTen.getText().trim();
		Date date = dateNgaySinh.getDate();

		// tên
		if (ten.isEmpty()) {
			ballTen.setTextContents("Tên khách hàng không được rỗng");
			ballTen.setVisible(true);
			return false;
		} 
		else if (!ten.matches("[\\p{Lu}[A-Z]][\\p{L}[a-z]]*(\\s+[\\p{Lu}[A-Z]][\\p{L}[a-z]]*)*")) {
			ballTen.setTextContents(" + Họ và tên khách hàng phải bắt đầu chữ cái in hoa \n"
					+ " + Không chứa các ký tự đặc biệt và số");
			ballTen.setVisible(true);
			return false;
		} else 
			ballTen.setVisible(false);

		// ngày
		if(date != null) {
			LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			if (!ngayHopLe(LocalDate.now(), localDate)) {
				ballNS.setTextContents("Ngày sinh phải trước ngày hiện tại");
				ballNS.setVisible(true);
				return false;
			}
		}
		ballNS.setVisible(false);

		// số điện thoại
		if (!sdt.matches("[0]\\d{9}")) {
			ballSDT.setTextContents("Số điện thoại gồm 10 chữ số, bắt đầu là 0");
			ballSDT.setVisible(true);
			return false;
		}
		ballSDT.setVisible(false);

		// đia chỉ
		if (diaChi.isEmpty()) {
			ballDC.setTextContents("Địa chỉ không được rỗng");
			ballDC.setVisible(true);
			return false;
		}
		ballDC.setVisible(false);

		// email
		if(!email.isEmpty()) {
			if (!email.matches("^[a-z][a-z0-9\\.]{7,32}@[a-z0-9]{2,}(\\.[a-z0-9]{2,4}){1,2}$")) {
				ballEmail.setTextContents(
						" + Email phải bắt đầu bằng 1 ký tự \n" + "+ Chỉ chứa ký tự a-z, 0-9 và ký tự dấu chấm (.) \n"
								+ "+ Độ dài tối thiểu là 8, độ dài tối đa là 32 \n"
								+ " +Tên miền có thể là tên miền cấp 1 hoặc cấp 2");
				ballEmail.setVisible(true);
				return false;
			}
		}
		ballEmail.setVisible(false);


		return true;
	}

	private void addControls() {
		jPanel1 = new JPanel(new BorderLayout());
		jPanel1.setOpaque(false);
		jPanel2 = new JPanel(new BorderLayout());
		jPanel2.setOpaque(false);

		addNorth();
		addCenter();
		addSouth();
		addNorth2();
		tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Tìm kiếm", jPanel2);
		tabbedPane.addTab("Cập nhật khách hàng", jPanel1);

		tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
		this.add(tabbedPane, BorderLayout.NORTH);

	}

	private void addSouth() {
		Box boxSouth = Box.createVerticalBox();
		boxSouth.add(Box.createVerticalStrut(20));
		JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 0));

		btnXoa = new ColoredButton("Xóa khách hàng", new ImageIcon("Images//delete.png"));
		btnXoa.setBackground(UIConstant.DANGER_COLOR);
		btnXoa.setFont(UIConstant.NORMAL_FONT);
		btnSua = new ColoredButton("Cập nhật thông tin", new ImageIcon("Images//modify.png"));
		btnSua.setBackground(UIConstant.WARNING_COLOR);

		btnQuayLai = new ColoredButton("Quay lại", new ImageIcon("Images//back.png"));
		btnQuayLai.setFont(UIConstant.NORMAL_FONT);
		//		pnlSouth.add(btnThem);

		pnlSouth.add(btnXoa);
		pnlSouth.add(btnSua);
		pnlSouth.add(btnQuayLai);
		pnlSouth.setOpaque(false);
		boxSouth.add(pnlSouth);
		this.add(boxSouth, BorderLayout.SOUTH);
	}

	private void addCenter() {

		Box boxCenter = Box.createVerticalBox();

		JPanel pnlDanhSach = new JPanel(new BorderLayout());
		//		JLabel lblDanhSach = new JLabel("Danh sách kết quả");
		//		pnlDanhSach.add(lblDanhSach);
		//		boxCenter.add(pnlDanhSach);

		String[] columns = new String[] { "Mã", "Tên", "Ngày sinh", "Địa chỉ", "Giới tính", "Email", "Số điện thoại" };

		model = new DefaultTableModel(columns, 0);
		table = new CustomTable(model);

		table.setAutoCreateRowSorter(true);

		JScrollPane pane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pane.getViewport().setBackground(Color.WHITE);
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Danh sách kết quả", pane);

		lbPage = new JLabel("Trang 1 trong 1 trang."); lbPage.setFont(UIConstant.NORMAL_FONT);

		btnHome = new ColoredButton(new ImageIcon("Images/double_left.png")); btnHome.setBackground(UIConstant.LINE_COLOR);
		btnEnd = new ColoredButton(new ImageIcon("Images/double_right.png")); btnEnd.setBackground(UIConstant.LINE_COLOR);
		btnBefore = new ColoredButton(new ImageIcon("Images/left.png")); btnBefore.setBackground(UIConstant.LINE_COLOR);
		btnNext = new ColoredButton(new ImageIcon("Images/right.png")); btnNext.setBackground(UIConstant.LINE_COLOR);

		btnHome.setToolTipText("Trang đầu");
		btnEnd.setToolTipText("Trang cuối");
		btnBefore.setToolTipText("Trang trước");
		btnNext.setToolTipText("Trang kế tiếp");

		Box boxPage = Box.createHorizontalBox();
		boxPage.add(Box.createHorizontalStrut(5));
		boxPage.add(btnHome); boxPage.add(Box.createHorizontalStrut(5));
		boxPage.add(btnBefore); boxPage.add(Box.createHorizontalStrut(5));
		boxPage.add(btnNext); boxPage.add(Box.createHorizontalStrut(5));
		boxPage.add(btnEnd); boxPage.add(Box.createHorizontalStrut(5));
		boxPage.add(Box.createHorizontalGlue());
		boxPage.add(lbPage); boxPage.add(Box.createHorizontalStrut(5));

		boxCenter.add(tabbedPane);
		boxCenter.add(Box.createVerticalStrut(5));
		boxCenter.add(boxPage);
		pnlDanhSach.add(boxCenter, BorderLayout.CENTER);
		pnlDanhSach.setOpaque(false);

		this.add(pnlDanhSach, BorderLayout.CENTER);
	}

	private void addNorth2() {
		JPanel pnlCenter = new JPanel(new BorderLayout());
		Box boxMain = Box.createVerticalBox();
		Box boxLine2 = Box.createHorizontalBox();

		JLabel lblTen = new JLabel("Họ và tên:");
		lblTen.setFont(UIConstant.NORMAL_FONT);
		txtHoTen2 = new JTextField();
		txtHoTen2.setFont(UIConstant.NORMAL_FONT);
		lblTen.setPreferredSize(dimension);

		JLabel lblSdt = new JLabel("Số điện thoại:");
		lblSdt.setFont(UIConstant.NORMAL_FONT);
		txtSdt2 = new JTextField();
		txtSdt2.setFont(UIConstant.NORMAL_FONT);
		lblSdt.setPreferredSize(dimension);

		boxLine2.add(lblTen);
		boxLine2.add(txtHoTen2);
		boxLine2.add(Box.createHorizontalStrut(20));
		boxLine2.add(lblSdt);
		boxLine2.add(txtSdt2);

		// line 3
		Box boxLine3 = Box.createHorizontalBox();

		JLabel lblDiaChi = new JLabel("Địa chỉ:");
		lblDiaChi.setFont(UIConstant.NORMAL_FONT);
		txtDiaChi2 = new JTextField();
		txtDiaChi2.setFont(UIConstant.NORMAL_FONT);
		lblDiaChi.setPreferredSize(lblTen.getMaximumSize());
		lblDiaChi.setPreferredSize(dimension);

		JLabel lblEmail = new JLabel("Email:");
		txtEmail2 = new JTextField();
		lblEmail.setFont(UIConstant.NORMAL_FONT);
		txtEmail2.setFont(UIConstant.NORMAL_FONT);
		lblEmail.setPreferredSize(lblSdt.getMaximumSize());
		lblEmail.setPreferredSize(dimension);

		boxLine3.add(lblDiaChi);
		boxLine3.add(txtDiaChi2);
		boxLine3.add(Box.createHorizontalStrut(20));
		boxLine3.add(lblEmail);
		boxLine3.add(txtEmail2);
		// buton

		JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));
		btnSearch = new ColoredButton("Tìm kiếm", new ImageIcon("Images//search.png"));
		btnSearch.setFont(UIConstant.NORMAL_FONT);

		btnXoaTrang2 = new ColoredButton("Xóa rỗng", new ImageIcon("Images//empty.png"));
		btnXoaTrang2.setBackground(UIConstant.DANGER_COLOR);
		btnXoaTrang2.setFont(UIConstant.NORMAL_FONT);
		pnlButton.add(btnSearch);

		pnlButton.add(btnXoaTrang2);
		pnlButton.setOpaque(false);

		boxMain.add(Box.createVerticalStrut(15));
		boxMain.add(boxLine2);
		boxMain.add(Box.createVerticalStrut(15));
		boxMain.add(boxLine3);

		pnlCenter.add(boxMain, BorderLayout.CENTER);
		pnlCenter.add(pnlButton, BorderLayout.SOUTH);
		pnlCenter.setOpaque(false);
		jPanel2.add(pnlCenter, BorderLayout.NORTH);

	}

	private void addNorth() {
		JPanel pnlCenter = new JPanel(new BorderLayout());

		JLabel lblMa = new JLabel("Mã khách hàng:");
		lblMa.setPreferredSize(dimension);
		lblMa.setFont(UIConstant.NORMAL_FONT);

		txtMa = new JTextField();
		txtMa.setEnabled(false);
		txtMa.setFont(UIConstant.NORMAL_FONT);
		txtMa.setPreferredSize(new Dimension(300, 20));

		JLabel lblTen = new JLabel("Họ và tên:");
		lblTen.setPreferredSize(dimension);
		lblTen.setFont(UIConstant.NORMAL_FONT);

		txtHoTen = new JTextField();
		txtHoTen.setFont(UIConstant.NORMAL_FONT);

		JLabel lblNgaySinh = new JLabel("Ngày sinh:");
		lblNgaySinh.setPreferredSize(dimension);
		lblNgaySinh.setFont(UIConstant.NORMAL_FONT);

		dateNgaySinh = new JDateChooser();
		dateNgaySinh.setDateFormatString("dd-MM-yyyy");
		dateNgaySinh.setFont(UIConstant.NORMAL_FONT);
		dateNgaySinh.setPreferredSize(new Dimension(180, 20));

		JLabel lblSdt = new JLabel("Số điện thoại:");
		lblSdt.setFont(UIConstant.NORMAL_FONT);
		lblSdt.setPreferredSize(dimension);

		txtSdt = new JTextField();
		txtSdt.setFont(UIConstant.NORMAL_FONT);
		txtSdt.setFont(UIConstant.NORMAL_FONT);

		JLabel lblGioiTinh = new JLabel("Giới tính:");
		lblGioiTinh.setFont(UIConstant.NORMAL_FONT);
		lblGioiTinh.setPreferredSize(dimension);

		cbbGT = new JComboBox<String>();
		cbbGT.setFont(UIConstant.NORMAL_FONT);
		cbbGT.addItem("Nam");
		cbbGT.addItem("Nữ");

		JLabel lblDiaChi = new JLabel("Địa chỉ:");
		lblDiaChi.setPreferredSize(dimension);
		lblDiaChi.setFont(UIConstant.NORMAL_FONT);

		txtDiaChi = new JTextField();
		txtDiaChi.setFont(UIConstant.NORMAL_FONT);
		txtDiaChi.setPreferredSize(new Dimension(300, 20));

		JLabel lblEmail = new JLabel("Email:");
		lblEmail.setPreferredSize(dimension);
		lblEmail.setFont(UIConstant.NORMAL_FONT);

		txtEmail = new JTextField();
		txtEmail.setFont(UIConstant.NORMAL_FONT);
		txtEmail.setPreferredSize(new Dimension(180, 20));

		lblSdt.setFont(UIConstant.NORMAL_FONT);
		lblSdt.setPreferredSize(dimension);

		ballTen = new BalloonTip(txtHoTen, "Tên thuốc không được rỗng!"); ballTen.setVisible(false); ballTen.setCloseButton(null);
		ballNS = new BalloonTip(dateNgaySinh, "Quy cách đóng gói thuốc không được rỗng!"); ballNS.setVisible(false); ballNS.setCloseButton(null);
		ballDC = new BalloonTip(txtDiaChi, "Đơn vị tính không được rỗng"); ballDC.setVisible(false); ballDC.setCloseButton(null);
		ballEmail = new BalloonTip(txtEmail, "Đơn giá phải là số và lớn hơn 0"); ballEmail.setVisible(false); ballEmail.setCloseButton(null);
		ballSDT = new BalloonTip(txtSdt, "Nước sản xuất không được rỗng"); ballSDT.setVisible(false); ballSDT.setCloseButton(null);


		Box boxInput = Box.createHorizontalBox();
		Box boxLeft = Box.createVerticalBox();
		Box boxRight = Box.createVerticalBox();
		boxInput.add(Box.createHorizontalStrut(5));
		boxInput.add(boxLeft);
		boxInput.add(Box.createHorizontalStrut(5));
		boxInput.add(boxRight);
		boxInput.add(Box.createHorizontalStrut(5));

		Box boxMa = Box.createHorizontalBox();
		boxMa.add(lblMa);
		boxMa.add(txtMa);

		Box boxTen = Box.createHorizontalBox();
		boxTen.add(lblTen);
		boxTen.add(txtHoTen);

		Box boxNgaySinh = Box.createHorizontalBox();
		boxNgaySinh.add(lblNgaySinh);
		boxNgaySinh.add(dateNgaySinh);

		Box boxGioiTinh = Box.createHorizontalBox();
		boxGioiTinh.add(lblGioiTinh);
		boxGioiTinh.add(cbbGT);

		Box boxDiaChi = Box.createHorizontalBox();
		boxDiaChi.add(lblDiaChi);
		boxDiaChi.add(txtDiaChi);

		Box boxEmail = Box.createHorizontalBox();
		boxEmail.add(lblEmail);
		boxEmail.add(txtEmail);

		Box boxSDT = Box.createHorizontalBox();
		boxSDT.add(lblSdt);
		boxSDT.add(txtSdt);
		boxSDT.add(Box.createHorizontalStrut(5));
		boxSDT.add(boxGioiTinh);

		boxLeft.add(Box.createVerticalStrut(5));
		boxLeft.add(boxMa);
		boxLeft.add(Box.createVerticalStrut(5));
		boxLeft.add(boxTen);
		boxLeft.add(Box.createVerticalStrut(5));
		boxLeft.add(boxDiaChi);
		boxLeft.add(Box.createVerticalStrut(5));

		boxRight.add(Box.createVerticalStrut(5));
		boxRight.add(boxNgaySinh);
		boxRight.add(Box.createVerticalStrut(5));
		boxRight.add(boxSDT);
		boxRight.add(Box.createVerticalStrut(5));
		boxRight.add(boxEmail);
		boxRight.add(Box.createVerticalStrut(5));

		// button

		JPanel pnlButton = new JPanel(new FlowLayout(FlowLayout.CENTER, 100, 10));
		btnThem = new ColoredButton("Thêm mới", new ImageIcon("Images//add.png"));
		btnThem.setBackground(UIConstant.PRIMARY_COLOR);
		btnThem.setFont(UIConstant.NORMAL_FONT);



		btnXoaTrang = new ColoredButton("Xóa rỗng", new ImageIcon("Images//empty.png"));
		btnXoaTrang.setBackground(UIConstant.DANGER_COLOR);
		btnXoaTrang.setFont(UIConstant.NORMAL_FONT);
		pnlButton.add(btnThem);

		pnlButton.add(btnXoaTrang);
		pnlButton.setOpaque(false);

		pnlCenter.add(boxInput, BorderLayout.CENTER);
		pnlCenter.add(pnlButton, BorderLayout.SOUTH);
		pnlCenter.setOpaque(false);
		jPanel1.add(pnlCenter, BorderLayout.NORTH);

	}

	private void setLookAndFeel() {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if (info.getName().equals("Windows")) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
					break;

				} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
						| UnsupportedLookAndFeelException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
	public void themKhachHangMoi() {
		tabbedPane.setSelectedIndex(1);
		this.setVisible(true);
	}

}

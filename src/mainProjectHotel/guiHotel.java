/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package mainProjectHotel;

import com.mongodb.client.MongoCollection;
import com.toedter.calendar.JDateChooser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import org.bson.Document;
import org.bson.types.Decimal128;
import packageHotel.Database;
import packageHotel.Room;
import packageHotel.Hotel;
import packageHotel.Pemesanan;
import packageHotel.Tamu;

/**
 *
 * @author github.com/Reza1290/gui-hotel-reservation
 */
public class guiHotel extends javax.swing.JFrame {

    private final Hotel hotel; // Sekali Define Tidak dapat diubah! sekali assign
    private List<String> filterType; // awalnya kosong ["Twin"]
    private List<Tamu> listTamu;
    private List<Boolean> bookType;
    private Database databaseHotel;

    /**
     * Creates new form guiHotel
     */
    public guiHotel() {
        this.hotel = new Hotel("Hoteru", "Jl. Kenangan Bersamamu");
        this.filterType = new LinkedList<String>();
        this.bookType = new LinkedList<Boolean>();
        this.listTamu = new Vector<Tamu>();
        this.databaseHotel = new Database("mongodb+srv://reza:reza1290@cluster0.snqx5vy.mongodb.net/?retryWrites=true&w=majority", "db_hotel_java");
        
        this.bookType.add(false);

        this.initComponents();
        this.initDisplay();
        this.generateData();

    }

    /**
     * Inisialisasi Awal Tampilan GUI
     */
    private void initDisplay() {
        labelSearch.setEnabled(false);

        namaLabel.setText("HOTEL " + this.hotel.getNamaHotel());
        jalanLabel.setText(this.hotel.getAlamat());
        setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        listRoomPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
        innerRoomPanel.setLayout(new GridLayout(3, 5, 20, 20));

    }

    /**
     * Mengisi data awal berupa kamar-kamar dalam hotel dan daftar tamu. Secara
     * otomatis menghasilkan dan menambahkan kamar-kamar ke dalam hotel, serta
     * menciptakan daftar tamu dan menambahkannya ke daftar tamu hotel.
     */
    private void generateData() {
        if (this.hotel.getDaftarKamar() != null) {

            this.hotel.clearKamar();
            this.hotel.clearBooked();

        }

        if (!this.listTamu.isEmpty()) {
            this.listTamu.clear();
        }


        LinkedList<Document> kamar = this.databaseHotel.getAllData("kamar");

        for (int i = 0; i < kamar.size(); i++) {

            Document ruang = (Document) kamar.get(i);

            Room room = new Room((int) ruang.get("nomorKamar"), ruang.get("tipeKamar").toString(), Double.parseDouble(ruang.get("harga").toString()));
            room.setStatus((boolean) ruang.get("status"));
            this.hotel.tambahKamar(room);

        }
        List<Room> dataKamar = this.hotel.getDaftarKamar();

        this.postKamar.setModel(new DefaultComboBoxModel<Room>(dataKamar.toArray(new Room[0])));
        this.deleteSelectKamar.setModel(new DefaultComboBoxModel<Room>(dataKamar.toArray(new Room[0])));

        LinkedList<Document> user = this.databaseHotel.getAllData("tamu");
        List<Tamu> tamuList = new ArrayList<>();
        for (int i = 0; i < user.size(); i++) {
            Document tamu = (Document) user.get(i);
            Tamu guest = new Tamu(tamu.get("nama").toString(), tamu.get("alamat").toString(), tamu.get("noTelepon").toString(), tamu.get("email").toString());
            tamuList.add(guest);

        }
        this.listTamu.addAll(tamuList);

        this.postUser.setModel(new DefaultComboBoxModel<Tamu>(tamuList.toArray(new Tamu[0])));
        this.deleteSelectUser.setModel(new DefaultComboBoxModel<Tamu>(tamuList.toArray(new Tamu[0])));

        LinkedList<Document> pemesanan = this.databaseHotel.getAllData("pemesanan");

        for (int i = 0; i < pemesanan.size(); i++) {
            Document book = (Document) pemesanan.get(i);
            Document booker = (Document) book.get("tamu");
            Document rooms = (Document) book.get("kamar");

            Tamu pemesan = new Tamu(booker.get("nama").toString(), booker.get("alamat").toString(), booker.get("noTelepon").toString(), booker.get("email").toString());
            Room booked = new Room((int) rooms.get("nomorKamar"), rooms.get("tipeKamar").toString(), Double.parseDouble(rooms.get("harga").toString()));

            Pemesanan booking = new Pemesanan(pemesan, LocalDate.parse(book.get("tanggalIN").toString()), LocalDate.parse(book.get("tanggalOUT").toString()), booked);

            this.hotel.pesanKamar(booking);
        }

        LinkedList<Document> log = this.databaseHotel.getAllData("log");

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Timestamp");
        tableModel.addColumn("Name");
        tableModel.addColumn("Room Number");
        tableModel.addColumn("Check-in Date");
        tableModel.addColumn("Check-out Date");
        
        for (Document document : log) {

            Object timestamp = document.get("timestamp");
            Document tamu = (Document) document.get("tamu");
            Object name = tamu.get("nama");
            Document ruangan = (Document) document.get("kamar");
            Object nomorKamar = ruangan.get("nomorKamar");
            Object tanggalIN = document.get("tanggalIN");
            Object tanggalOUT = document.get("tanggalOUT");

            Object[] row = {timestamp, name, nomorKamar, tanggalIN, tanggalOUT};
            tableModel.addRow(row);
        }

        this.logTable.setModel(tableModel);

        this.deleteList();
        this.renderComponents();
    }

    /**
     * Metode ini digunakan untuk menghasilkan secara acak jenis kamar dari
     * daftar jenis kamar yang tersedia.
     *
     * @return Jenis kamar acak yang diambil dari daftar jenis kamar yang telah
     * ditentukan.
     */
    private String getRandomRoomType() {
        String[] roomTypes = {"King", "Twin", "Deluxe", "Single", "Queen", "Double", "Suite", "Standard"};
        int randomIndex = new Random().nextInt(roomTypes.length);
        return roomTypes[randomIndex];
    }

    private void updateLog() {

        MongoCollection<Document> collection = this.databaseHotel.connectDatabase().getCollection("pemesanan");
        Document doc = collection.find().sort(new Document("_id", -1)).first();
        doc.put("timestamp", new java.util.Date());
        this.databaseHotel.postData("log", doc);
    }

    /**
     * Ini adalah Fungsi untuk meRender Komponen ( Looping Component ) ->dynamic
     * Component
     *
     * @param hotel
     */
    private void renderComponents() {

        for (int i = 0; i < this.hotel.getDaftarKamar().size(); i++) {
            if ((this.filterType.contains(this.hotel.getDaftarKamar().get(i).getTipeKamar())) && (this.bookType.contains(this.hotel.getDaftarKamar().get(i).isStatus()))) {
                innerRoomPanel.add(new roomPanel(this.hotel.getDaftarKamar().get(i), hotel));
            }
        }
        revalidate();
        repaint();
    }

    /**
     * Fungsi untuk membersihkan rendered List Function to delete rendered List
     *
     * (REFRESH)
     */
    public void deleteList() {
        innerRoomPanel.removeAll();
        revalidate();
        repaint();
    }

    /**
     * Class Custom Panel For Container Render Wadah untuk tempat Rendering
     *
     * Inheritence from JPanel
     */
    private class roomPanel extends JPanel {

        public roomPanel(Room room, Hotel hotel) {
            super();
            setPreferredSize(new Dimension(200, 200));
            setBorder(BorderFactory.createLineBorder(Color.black, 2, true));
            setLayout(new GridLayout(4, 1));

            JButton setStatus = new JButton();

            JLabel noKamar = new JLabel("<html><h1 >No." + room.getNomorKamar() + "</h1></html>", JLabel.CENTER);
            JLabel statusKamar = new JLabel("", JLabel.CENTER);
            JLabel hargaKamar = new JLabel("<html>Price : $ " + room.getHarga()
                    + "/night <br>Tipe : " + room.getTipeKamar() + "</html>", JLabel.CENTER);

            if (room.isStatus()) {
                statusKamar.setText("Booked");
                setStatus.setText("UnBook");

                setStatus.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boolean res = hotel.removeBookedRoom(room.getNomorKamar());

                        if (res) {
                            if (databaseHotel.deleteData("pemesanan", "kamar.nomorKamar", String.valueOf(room.getNomorKamar()), false)) {
                                Document data = new Document().append("status", false);
                                databaseHotel.editData("kamar", data, "nomorKamar", String.valueOf(room.getNomorKamar()), false);
                                JOptionPane.showMessageDialog(rootPane, "Berhasil!");
                            }
                            statusKamar.setText("Ready");
                            setStatus.setText("Book");
                        } else {
                            JOptionPane.showMessageDialog(rootPane, "Gagal!");
                        }

                        generateData();
                    }
                });

            } else {
                statusKamar.setText("Ready");
                setStatus.setText("Book");
                setStatus.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JTextField firstName = new JTextField();
                        JTextField noTelepon = new JTextField();
                        JTextField email = new JTextField();
                        JDateChooser dateCheckIn = new JDateChooser();
                        JDateChooser dateCheckOut = new JDateChooser();
                        dateCheckIn.setMinSelectableDate(new Date());
                        dateCheckOut.setMinSelectableDate(new Date());

                        DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
                        JComboBox comboBox = new JComboBox((Vector) listTamu);

                        /**
                         * Membuat Selected Item Pertama ke Item Dengan Index- 0
                         */
                        comboBox.setSelectedIndex(0);

                        Tamu firstTamu = (Tamu) comboBox.getSelectedItem();
                        firstName.setEnabled(false);
                        firstName.setText(firstTamu.getNama());
                        noTelepon.setEnabled(false);
                        noTelepon.setText(firstTamu.getNoTelepon());
                        email.setEnabled(false);
                        email.setText(firstTamu.getEmail());

                        comboBox.addActionListener((event) -> {
                            Tamu selectedTamu = (Tamu) comboBox.getSelectedItem();

                            firstName.setText(selectedTamu.getNama());
                            noTelepon.setText(selectedTamu.getNoTelepon());
                            email.setText(selectedTamu.getEmail());
                        });

                        final JComponent[] inputs = new JComponent[]{
                            new JLabel("Select Tamu"),
                            comboBox,
                            new JLabel("Name"),
                            firstName,
                            new JLabel("No. Telp"),
                            noTelepon,
                            new JLabel("Email"),
                            email,
                            new JLabel("Tanggal Check In : "),
                            dateCheckIn,
                            new JLabel("Tanggal Check Out : "),
                            dateCheckOut,};

                        int result = JOptionPane.showConfirmDialog(null, inputs, "Pesan Kamar", JOptionPane.PLAIN_MESSAGE);

                        if (result == JOptionPane.OK_OPTION) {
                            System.out.println("You entered "
                                    + firstName.getText() + ", "
                                    + noTelepon.getText() + ", "
                                    + email.getText() + ", "
                                    + formatDate.format(dateCheckIn.getDate())
                                    + formatDate.format(dateCheckOut.getDate()));

                            int index = comboBox.getSelectedIndex();
                            Tamu detailTamu = listTamu.get(index);

                            boolean res = hotel.pesanKamar(detailTamu, room, LocalDate.parse(formatDate.format(dateCheckIn.getDate())), LocalDate.parse(formatDate.format(dateCheckOut.getDate())));

                            if (res == true) {

                                Document tamu = new Document()
                                        .append("nama", detailTamu.getNama())
                                        .append("alamat", detailTamu.getAlamat())
                                        .append("noTelepon", detailTamu.getNoTelepon())
                                        .append("email", detailTamu.getEmail());

                                Document kamar = new Document()
                                        .append("nomorKamar", room.getNomorKamar())
                                        .append("tipeKamar", room.getTipeKamar())
                                        .append("status", room.isStatus())
                                        .append("harga", room.getHarga());

                                Document data = new Document()
                                        .append("tamu", tamu)
                                        .append("tanggalIN", (LocalDate.parse(formatDate.format(dateCheckIn.getDate())).toString()))
                                        .append("tanggalOUT", (LocalDate.parse(formatDate.format(dateCheckOut.getDate())).toString()))
                                        .append("kamar", kamar);

                                databaseHotel.postData("pemesanan", data);

                                kamar.replace("harga", new Decimal128(new BigDecimal(room.getHarga())));

                                databaseHotel.editData("kamar", kamar, "nomorKamar", Integer.toString(room.getNomorKamar()), false);

                                JOptionPane.showMessageDialog(rootPane, "Berhasil");

                                updateLog();
                            } else {
                                JOptionPane.showMessageDialog(rootPane, "Gagal Booking!");
                            }
                        } else {
                            System.out.println("User canceled / closed the dialog, result = " + result);
                            JOptionPane.showMessageDialog(null, "Cancelled");

                        }
                        generateData();

                    }
                });
            }

            add(noKamar);
            add(statusKamar);
            add(hargaKamar);
            add(setStatus);

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PostDialog = new javax.swing.JDialog();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        postHarga = new javax.swing.JTextField();
        postNomorKamar = new javax.swing.JTextField();
        insertKamar = new javax.swing.JButton();
        postTipeKamar = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        postStatus = new javax.swing.JComboBox<>();
        editKamar = new javax.swing.JButton();
        postKamar = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        postAlamat = new javax.swing.JTextField();
        postNama = new javax.swing.JTextField();
        insertUser = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        editUser = new javax.swing.JButton();
        postUser = new javax.swing.JComboBox<>();
        postEmail = new javax.swing.JTextField();
        postTelepon = new javax.swing.JTextField();
        DeleteDialog = new javax.swing.JDialog();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        deleteHarga = new javax.swing.JTextField();
        deleteKamar = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        deleteSelectKamar = new javax.swing.JComboBox<>();
        deleteTipeKamar = new javax.swing.JTextField();
        deleteStatus = new javax.swing.JTextField();
        deleteNokamar = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        deleteAlamat = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        deleteUser = new javax.swing.JButton();
        deleteSelectUser = new javax.swing.JComboBox<>();
        deleteEmail = new javax.swing.JTextField();
        deleteNoTelepon = new javax.swing.JTextField();
        deleteNama = new javax.swing.JTextField();
        logPane = new javax.swing.JFrame();
        LOG = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        logTable = new javax.swing.JTable();
        listRoomPanel = new javax.swing.JPanel();
        innerRoomPanel = new javax.swing.JPanel();
        sidePanel = new javax.swing.JPanel();
        namaLabel = new javax.swing.JLabel();
        jalanLabel = new javax.swing.JLabel();
        filterPanel = new javax.swing.JPanel();
        typeTwin = new javax.swing.JCheckBox();
        typeKing = new javax.swing.JCheckBox();
        typeQueen = new javax.swing.JCheckBox();
        typeDouble = new javax.swing.JCheckBox();
        typeSingle = new javax.swing.JCheckBox();
        labelSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        filterBookPanel = new javax.swing.JPanel();
        filterBooked = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        PostDialog.setSize(getPreferredSize());
        PostDialog.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                PostDialogComponentShown(evt);
            }
        });

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Input/Edit Data KAMAR");

        jLabel2.setText("Nomor Kamar   :");

        jLabel3.setText("Tipe Kamar       :");

        postHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postHargaActionPerformed(evt);
            }
        });

        insertKamar.setText("Insert");
        insertKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertKamarActionPerformed(evt);
            }
        });

        postTipeKamar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "King", "Queen", "Single", "Double" }));

        jLabel4.setText("Harga                :");

        jLabel5.setText("Status                :");

        postStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Available", "Booked" }));

        editKamar.setText("Edit");
        editKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editKamarActionPerformed(evt);
            }
        });

        postKamar.setToolTipText("");
        postKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postKamarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(postStatus, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(postTipeKamar, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(postHarga, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(insertKamar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(editKamar))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(postNomorKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(postKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(postNomorKamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(postKamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(postTipeKamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(postStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(postHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(editKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(insertKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(146, 146, 146))
        );

        jTabbedPane1.addTab("Kamar", jPanel1);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("Input/ Edit User");

        jLabel8.setText("Nama                :");

        jLabel9.setText("No Telepon       :");

        postAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postAlamatActionPerformed(evt);
            }
        });

        insertUser.setText("Insert");
        insertUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertUserActionPerformed(evt);
            }
        });

        jLabel10.setText("Alamat              :");

        jLabel11.setText("Email                 :");

        editUser.setText("Edit");
        editUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editUserActionPerformed(evt);
            }
        });

        postUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postUserActionPerformed(evt);
            }
        });

        postEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postEmailActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(postEmail))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(postAlamat, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(postTelepon)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(insertUser)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(editUser))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(postNama, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(postUser, 0, 147, Short.MAX_VALUE)))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(postNama, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(postUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(postTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(postEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(postAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(insertUser, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editUser, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(148, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("User", jPanel2);

        javax.swing.GroupLayout PostDialogLayout = new javax.swing.GroupLayout(PostDialog.getContentPane());
        PostDialog.getContentPane().setLayout(PostDialogLayout);
        PostDialogLayout.setHorizontalGroup(
            PostDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PostDialogLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PostDialogLayout.setVerticalGroup(
            PostDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PostDialogLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        DeleteDialog.setPreferredSize(new java.awt.Dimension(586, 350));
        DeleteDialog.setSize(getPreferredSize());
        DeleteDialog.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                DeleteDialogComponentShown(evt);
            }
        });

        jPanel3.setPreferredSize(new java.awt.Dimension(586, 350));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Delete Kamar");

        jLabel12.setText("Nomor Kamar   :");

        jLabel13.setText("Tipe Kamar       :");

        deleteHarga.setEnabled(false);
        deleteHarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteHargaActionPerformed(evt);
            }
        });

        deleteKamar.setText("Delete");
        deleteKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteKamarActionPerformed(evt);
            }
        });

        jLabel14.setText("Harga                :");

        jLabel15.setText("Status                :");

        deleteSelectKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSelectKamarActionPerformed(evt);
            }
        });

        deleteTipeKamar.setEnabled(false);

        deleteStatus.setEnabled(false);

        deleteNokamar.setEnabled(false);
        deleteNokamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteNokamarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteStatus))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(deleteSelectKamar, 0, 280, Short.MAX_VALUE)
                            .addComponent(deleteHarga, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(deleteTipeKamar)
                            .addComponent(deleteKamar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(deleteNokamar))))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(deleteSelectKamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteNokamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteTipeKamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(deleteStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteHarga, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 19, Short.MAX_VALUE)
                .addComponent(deleteKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(48, 48, 48))
        );

        jTabbedPane2.addTab("Kamar", jPanel3);

        jPanel4.setPreferredSize(new java.awt.Dimension(586, 350));

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("Delete User");

        jLabel17.setText("Nama                :");

        jLabel18.setText("No Telepon       :");

        deleteAlamat.setEnabled(false);
        deleteAlamat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAlamatActionPerformed(evt);
            }
        });

        jLabel19.setText("Alamat              :");

        jLabel20.setText("Email                 :");

        deleteUser.setText("Delete");
        deleteUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteUserActionPerformed(evt);
            }
        });

        deleteSelectUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSelectUserActionPerformed(evt);
            }
        });

        deleteEmail.setEnabled(false);
        deleteEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEmailActionPerformed(evt);
            }
        });

        deleteNoTelepon.setEnabled(false);

        deleteNama.setEnabled(false);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deleteEmail))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(deleteSelectUser, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(deleteAlamat, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(deleteNoTelepon)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addGap(0, 171, Short.MAX_VALUE)
                                .addComponent(deleteUser, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(deleteNama))))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(deleteSelectUser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteNama, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(deleteNoTelepon, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(deleteEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deleteAlamat, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel19))
                .addGap(18, 18, 18)
                .addComponent(deleteUser, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(48, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("User", jPanel4);

        javax.swing.GroupLayout DeleteDialogLayout = new javax.swing.GroupLayout(DeleteDialog.getContentPane());
        DeleteDialog.getContentPane().setLayout(DeleteDialogLayout);
        DeleteDialogLayout.setHorizontalGroup(
            DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DeleteDialogLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        DeleteDialogLayout.setVerticalGroup(
            DeleteDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        logTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Tanggal", "Nomor Kamar", "Nama User", "Check In", "Check Out"
            }
        ));
        jScrollPane1.setViewportView(logTable);
        if (logTable.getColumnModel().getColumnCount() > 0) {
            logTable.getColumnModel().getColumn(0).setHeaderValue("Tanggal");
            logTable.getColumnModel().getColumn(1).setHeaderValue("Nomor Kamar");
            logTable.getColumnModel().getColumn(2).setHeaderValue("Nama User");
            logTable.getColumnModel().getColumn(3).setHeaderValue("Check In");
            logTable.getColumnModel().getColumn(4).setHeaderValue("Check Out");
        }

        javax.swing.GroupLayout LOGLayout = new javax.swing.GroupLayout(LOG);
        LOG.setLayout(LOGLayout);
        LOGLayout.setHorizontalGroup(
            LOGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LOGLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 694, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        LOGLayout.setVerticalGroup(
            LOGLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(LOGLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout logPaneLayout = new javax.swing.GroupLayout(logPane.getContentPane());
        logPane.getContentPane().setLayout(logPaneLayout);
        logPaneLayout.setHorizontalGroup(
            logPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
            .addGroup(logPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(logPaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(LOG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        logPaneLayout.setVerticalGroup(
            logPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
            .addGroup(logPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(logPaneLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(LOG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        listRoomPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        listRoomPanel.setAutoscrolls(true);

        javax.swing.GroupLayout innerRoomPanelLayout = new javax.swing.GroupLayout(innerRoomPanel);
        innerRoomPanel.setLayout(innerRoomPanelLayout);
        innerRoomPanelLayout.setHorizontalGroup(
            innerRoomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        innerRoomPanelLayout.setVerticalGroup(
            innerRoomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout listRoomPanelLayout = new javax.swing.GroupLayout(listRoomPanel);
        listRoomPanel.setLayout(listRoomPanelLayout);
        listRoomPanelLayout.setHorizontalGroup(
            listRoomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(innerRoomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        listRoomPanelLayout.setVerticalGroup(
            listRoomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(innerRoomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        namaLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        namaLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        namaLabel.setText("HOTEL");
        namaLabel.setToolTipText("NAMA HOTEL");

        jalanLabel.setFont(new java.awt.Font("Arial", 2, 12)); // NOI18N
        jalanLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        filterPanel.setBackground(new java.awt.Color(255, 255, 255));
        filterPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        typeTwin.setText("Twin");
        typeTwin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeTwinActionPerformed(evt);
            }
        });

        typeKing.setText("King");
        typeKing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeKingActionPerformed(evt);
            }
        });

        typeQueen.setText("Queen");
        typeQueen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeQueenActionPerformed(evt);
            }
        });

        typeDouble.setText("Double");
        typeDouble.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeDoubleActionPerformed(evt);
            }
        });

        typeSingle.setText("Single");
        typeSingle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeSingleActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout filterPanelLayout = new javax.swing.GroupLayout(filterPanel);
        filterPanel.setLayout(filterPanelLayout);
        filterPanelLayout.setHorizontalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(typeTwin, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeKing, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeQueen, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeDouble, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(typeSingle, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        filterPanelLayout.setVerticalGroup(
            filterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(typeTwin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeKing)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeQueen)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeDouble)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typeSingle)
                .addContainerGap(9, Short.MAX_VALUE))
        );

        labelSearch.setText("Search..");
        labelSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                labelSearchActionPerformed(evt);
            }
        });

        btnSearch.setText("OK");
        btnSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchActionPerformed(evt);
            }
        });

        filterBookPanel.setBackground(new java.awt.Color(255, 255, 255));
        filterBookPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        filterBooked.setText("Booked");
        filterBooked.setToolTipText("");
        filterBooked.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterBookedActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout filterBookPanelLayout = new javax.swing.GroupLayout(filterBookPanel);
        filterBookPanel.setLayout(filterBookPanelLayout);
        filterBookPanelLayout.setHorizontalGroup(
            filterBookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterBookPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filterBooked, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        filterBookPanelLayout.setVerticalGroup(
            filterBookPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(filterBookPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(filterBooked)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton1.setText("Refresh Data");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("+ Data");
        jButton2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jButton2ComponentShown(evt);
            }
        });
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("- Data");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Histroy Log");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout sidePanelLayout = new javax.swing.GroupLayout(sidePanel);
        sidePanel.setLayout(sidePanelLayout);
        sidePanelLayout.setHorizontalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(namaLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jalanLabel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filterBookPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(sidePanelLayout.createSequentialGroup()
                        .addComponent(labelSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, sidePanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        sidePanelLayout.setVerticalGroup(
            sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sidePanelLayout.createSequentialGroup()
                .addComponent(namaLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jalanLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(labelSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 27, Short.MAX_VALUE)
                    .addComponent(btnSearch, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(filterBookPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(sidePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(211, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(sidePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(listRoomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(listRoomPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sidePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void labelSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_labelSearchActionPerformed
        // TODO add your handling code here:


    }//GEN-LAST:event_labelSearchActionPerformed

    private void typeTwinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeTwinActionPerformed
        if (typeTwin.isSelected()) {
            this.filterType.add("Twin");
            deleteList();
            renderComponents();
        } else {
            this.filterType.remove("Twin");
            deleteList();
            renderComponents();
        }
    }//GEN-LAST:event_typeTwinActionPerformed

    private void typeKingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeKingActionPerformed
        if (typeKing.isSelected()) {
            this.filterType.add("King");
            deleteList();
            renderComponents();
        } else {
            this.filterType.remove("King");
            deleteList();
            renderComponents();
        }
    }//GEN-LAST:event_typeKingActionPerformed

    private void typeQueenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeQueenActionPerformed
        if (typeQueen.isSelected()) {
            this.filterType.add("Queen");
            deleteList();
            renderComponents();
        } else {
            this.filterType.remove("Queen");
            deleteList();
            renderComponents();
        }
    }//GEN-LAST:event_typeQueenActionPerformed

    private void typeDoubleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeDoubleActionPerformed
        if (typeDouble.isSelected()) {
            this.filterType.add("Double");
            deleteList();
            renderComponents();
        } else {
            this.filterType.remove("Double");
            deleteList();
            renderComponents();
        }
    }//GEN-LAST:event_typeDoubleActionPerformed

    private void typeSingleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeSingleActionPerformed
        if (typeSingle.isSelected()) {
            this.filterType.add("Single");
            deleteList();
            renderComponents();
        } else {
            this.filterType.remove("Single");
            deleteList();
            renderComponents();
        }
    }//GEN-LAST:event_typeSingleActionPerformed

    private void btnSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSearchActionPerformed

    private void filterBookedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterBookedActionPerformed
        // TODO add your handling code here:
        if (filterBooked.isSelected()) {
            this.bookType.add(true);
            deleteList();
            renderComponents();
        } else {
            this.bookType.remove(true);
            deleteList();
            renderComponents();
        }
    }//GEN-LAST:event_filterBookedActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        this.generateData();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        PostDialog.setVisible(true);
        PostDialog.setSize(new Dimension(480, 400));
        PostDialog.setMaximumSize(new Dimension(480, 400));

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        DeleteDialog.setVisible(true);
        DeleteDialog.setSize(new Dimension(480, 400));
        DeleteDialog.setMaximumSize(new Dimension(480, 400));
    }//GEN-LAST:event_jButton3ActionPerformed

    private void insertKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertKamarActionPerformed
        // TODO add your handling code here:

        boolean status;

        status = this.postStatus.getSelectedItem() != "Available";

        Document data = new Document()
                .append("nomorKamar", Integer.parseInt(this.postNomorKamar.getText()))
                .append("tipeKamar", this.postTipeKamar.getSelectedItem().toString())
                .append("status", status)
                .append("harga", new Decimal128(new BigDecimal(Double.parseDouble(this.postHarga.getText()))));

        if (this.databaseHotel.postData("kamar", data)) {
            generateData();
            JOptionPane.showMessageDialog(rootPane, "Berhasil!");
            this.PostDialog.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Gagal!");
        }


    }//GEN-LAST:event_insertKamarActionPerformed

    private void PostDialogComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_PostDialogComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_PostDialogComponentShown

    private void jButton2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jButton2ComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton2ComponentShown

    private void postHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_postHargaActionPerformed

    private void postAlamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postAlamatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_postAlamatActionPerformed

    private void insertUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertUserActionPerformed
        // TODO add your handling code here:

        Document data = new Document()
                .append("nama", this.postNama.getText())
                .append("alamat", this.postAlamat.getText())
                .append("noTelepon", this.postTelepon.getText())
                .append("email", this.postEmail.getText());

        if (this.databaseHotel.postData("tamu", data)) {

            generateData();
            JOptionPane.showMessageDialog(rootPane, "Berhasil!");
            this.PostDialog.setVisible(false);
        } else {

            JOptionPane.showMessageDialog(rootPane, "Gagal!");
        }

    }//GEN-LAST:event_insertUserActionPerformed

    private void postEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_postEmailActionPerformed

    private void deleteHargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteHargaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteHargaActionPerformed

    private void deleteKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteKamarActionPerformed
        // TODO add your handling code here:

        Room now = (Room) this.deleteSelectKamar.getSelectedItem();

        if (this.databaseHotel.deleteData("kamar", "nomorKamar", String.valueOf(now.getNomorKamar()), false)) {
            generateData();
            JOptionPane.showMessageDialog(rootPane, "Berhasil!");
            this.DeleteDialog.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Gagal!");

        }

    }//GEN-LAST:event_deleteKamarActionPerformed

    private void deleteAlamatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAlamatActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteAlamatActionPerformed

    private void deleteEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteEmailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteEmailActionPerformed

    private void DeleteDialogComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_DeleteDialogComponentShown
        // TODO add your handling code here:
    }//GEN-LAST:event_DeleteDialogComponentShown

    private void deleteUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteUserActionPerformed
        // TODO add your handling code here:

        Tamu now = (Tamu) this.deleteSelectUser.getSelectedItem();

        if (this.databaseHotel.deleteData("kamar", "nomorKamar", now.getEmail(), true)) {
            generateData();
            JOptionPane.showMessageDialog(rootPane, "Berhasil!");
            this.DeleteDialog.setVisible(false);
        } else {
            JOptionPane.showMessageDialog(rootPane, "Gagal!");

        }
    }//GEN-LAST:event_deleteUserActionPerformed

    private void postKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postKamarActionPerformed
        // TODO add your handling code here:
        int s;

        Room now = (Room) this.postKamar.getSelectedItem();

        this.postNomorKamar.setText(String.valueOf(now.getNomorKamar()));
        this.postTipeKamar.setSelectedItem(now.getTipeKamar());
        if (now.isStatus() == true) {
            s = 1;
        } else {
            s = 0;
        }
        this.postStatus.setSelectedIndex(s);
        this.postHarga.setText(String.valueOf(now.getHarga()));

    }//GEN-LAST:event_postKamarActionPerformed

    private void editKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editKamarActionPerformed
        // TODO add your handling code here:
        boolean status;

        Room now = (Room) this.postKamar.getSelectedItem();

        status = this.postStatus.getSelectedItem() != "Available";

        Document data = new Document()
                .append("nomorKamar", Integer.parseInt(this.postNomorKamar.getText()))
                .append("tipeKamar", this.postTipeKamar.getSelectedItem().toString())
                .append("status", status)
                .append("harga", new Decimal128(new BigDecimal(Double.parseDouble(this.postHarga.getText()))));

        if (this.databaseHotel.editData("kamar", data, "nomorKamar", String.valueOf(now.getNomorKamar()), false)) {

            generateData();
            JOptionPane.showMessageDialog(rootPane, "Berhasil!");
            this.PostDialog.setVisible(false);

        } else {
            JOptionPane.showMessageDialog(rootPane, "Gagal!");
        }


    }//GEN-LAST:event_editKamarActionPerformed

    private void editUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editUserActionPerformed
        // TODO add your handling code here:

        Tamu now = (Tamu) this.postUser.getSelectedItem();

        Document data = new Document()
                .append("nama", this.postNama.getText())
                .append("alamat", this.postAlamat.getText())
                .append("noTelepon", this.postTelepon.getText())
                .append("email", this.postEmail.getText());

        if (this.databaseHotel.editData("tamu", data, "email", now.getEmail(), true)) {

            generateData();
            JOptionPane.showMessageDialog(rootPane, "Berhasil!");
            this.PostDialog.setVisible(false);
        } else {

            JOptionPane.showMessageDialog(rootPane, "Gagal!");
        }
    }//GEN-LAST:event_editUserActionPerformed

    private void postUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postUserActionPerformed
        // TODO add your handling code here:

        Tamu now = (Tamu) this.postUser.getSelectedItem();

        this.postNama.setText(now.getNama());
        this.postAlamat.setText(now.getAlamat());
        this.postEmail.setText(now.getEmail());
        this.postTelepon.setText(now.getNoTelepon());

    }//GEN-LAST:event_postUserActionPerformed

    private void deleteSelectKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectKamarActionPerformed
        // TODO add your handling code here:
        Room now = (Room) this.deleteSelectKamar.getSelectedItem();

        this.deleteNokamar.setText(String.valueOf(now.getNomorKamar()));
        this.deleteTipeKamar.setText(now.getTipeKamar());
        this.deleteStatus.setText(now.isStatus() == true ? "Booked" : "vailable");
        this.deleteHarga.setText(String.valueOf(now.getHarga()));

    }//GEN-LAST:event_deleteSelectKamarActionPerformed

    private void deleteNokamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteNokamarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_deleteNokamarActionPerformed

    private void deleteSelectUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectUserActionPerformed
        // TODO add your handling code here:
        Tamu now = (Tamu) this.deleteSelectUser.getSelectedItem();

        this.deleteNama.setText(now.getNama());
        this.deleteAlamat.setText(now.getAlamat());
        this.deleteEmail.setText(now.getEmail());
        this.deleteNoTelepon.setText(now.getNoTelepon());
    }//GEN-LAST:event_deleteSelectUserActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        this.logPane.setVisible(true);
        this.logPane.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }//GEN-LAST:event_jButton4ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(guiHotel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(guiHotel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(guiHotel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(guiHotel.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new guiHotel().setVisible(true);

            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog DeleteDialog;
    private javax.swing.JPanel LOG;
    private javax.swing.JDialog PostDialog;
    private javax.swing.JButton btnSearch;
    private javax.swing.JTextField deleteAlamat;
    private javax.swing.JTextField deleteEmail;
    private javax.swing.JTextField deleteHarga;
    private javax.swing.JButton deleteKamar;
    private javax.swing.JTextField deleteNama;
    private javax.swing.JTextField deleteNoTelepon;
    private javax.swing.JTextField deleteNokamar;
    private javax.swing.JComboBox<Room> deleteSelectKamar;
    private javax.swing.JComboBox<Tamu> deleteSelectUser;
    private javax.swing.JTextField deleteStatus;
    private javax.swing.JTextField deleteTipeKamar;
    private javax.swing.JButton deleteUser;
    private javax.swing.JButton editKamar;
    private javax.swing.JButton editUser;
    private javax.swing.JPanel filterBookPanel;
    private javax.swing.JCheckBox filterBooked;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JPanel innerRoomPanel;
    private javax.swing.JButton insertKamar;
    private javax.swing.JButton insertUser;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JLabel jalanLabel;
    private javax.swing.JTextField labelSearch;
    private javax.swing.JPanel listRoomPanel;
    private javax.swing.JFrame logPane;
    private javax.swing.JTable logTable;
    private javax.swing.JLabel namaLabel;
    private javax.swing.JTextField postAlamat;
    private javax.swing.JTextField postEmail;
    private javax.swing.JTextField postHarga;
    private javax.swing.JComboBox<Room> postKamar;
    private javax.swing.JTextField postNama;
    private javax.swing.JTextField postNomorKamar;
    private javax.swing.JComboBox<String> postStatus;
    private javax.swing.JTextField postTelepon;
    private javax.swing.JComboBox<String> postTipeKamar;
    private javax.swing.JComboBox<Tamu> postUser;
    private javax.swing.JPanel sidePanel;
    private javax.swing.JCheckBox typeDouble;
    private javax.swing.JCheckBox typeKing;
    private javax.swing.JCheckBox typeQueen;
    private javax.swing.JCheckBox typeSingle;
    private javax.swing.JCheckBox typeTwin;
    // End of variables declaration//GEN-END:variables
}

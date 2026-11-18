import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

public class SistemRumahSakit implements Serializable {
    private static final long serialVersionUID = 1L;
    private String namaRumahSakit;
    private ArrayList<Pasien> daftarPasien = new ArrayList<>();
    private ArrayList<LayananMedis> daftarLayanan = new ArrayList<>();
    private ArrayList<Transaksi> daftarTransaksi = new ArrayList<>();

    public SistemRumahSakit(String namaRumahSakit) {
        this.namaRumahSakit = namaRumahSakit;
    }

    public void tambahPasien(String id, String nama) {
        daftarPasien.add(new Pasien(id, nama));
    }

    public void tambahLayananMedis(String id, String nama, int kuota) {
        daftarLayanan.add(new LayananMedis(id, nama, kuota));
    }

    private Pasien cariPasien(String id) throws TransaksiTidakValidException {
        for (Pasien p : daftarPasien) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        throw new TransaksiTidakValidException("Pasien dengan ID " + id + " tidak ditemukan");
    }

    private LayananMedis cariLayanan(String kode) throws TransaksiTidakValidException {
        for (LayananMedis l : daftarLayanan) {
            if (l.getId().equals(kode)) {
                return l;
            }
        }
        throw new TransaksiTidakValidException("Layanan dengan kode " + kode + " tidak ditemukan");
    }

    private String ambilNilai(String bagian, String kunci) throws TransaksiTidakValidException {
        String[] pecah = bagian.split(">");
        if (pecah.length != 2 || !pecah[0].trim().equals(kunci)) {
            throw new TransaksiTidakValidException("Bagian '" + bagian + "' tidak sesuai format " + kunci + ">nilai");
        }
        return pecah[1].trim();
    }

    private void prosesBaris(String baris) throws TransaksiTidakValidException {
        String[] bagian = baris.trim().split("\\.");
        if (bagian.length != 3) {
            throw new TransaksiTidakValidException("Format baris salah: " + baris);
        }

        String idPasien = ambilNilai(bagian[0], "ID");
        String kodeLayanan = ambilNilai(bagian[1], "KD");
        String teksJumlah = ambilNilai(bagian[2], "JML");

        int jumlah;
        try {
            jumlah = Integer.parseInt(teksJumlah);
        } catch (NumberFormatException e) {
            throw new TransaksiTidakValidException("Jumlah bukan angka: " + teksJumlah);
        }
        if (jumlah <= 0) {
            throw new TransaksiTidakValidException("Jumlah harus lebih dari 0");
        }

        Pasien pasien = cariPasien(idPasien);
        LayananMedis layanan = cariLayanan(kodeLayanan);
        layanan.kurangiKuota(jumlah);
        daftarTransaksi.add(new Transaksi(pasien, layanan, jumlah));
    }

    public void jalankanTransaksiDariFile(String namaFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(namaFile))) {
            String baris;
            int nomor = 0;
            while ((baris = br.readLine()) != null) {
                nomor++;
                if (baris.trim().isEmpty()) {
                    continue;
                }
                try {
                    prosesBaris(baris);
                    System.out.println("[OK] Baris " + nomor + ": " + baris);
                } catch (TransaksiTidakValidException e) {
                    System.out.println("[GAGAL] Baris " + nomor + ": " + e.getMessage());
                }
            }
        }
    }

    public void simpanLaporanAkhir() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("memory.ser"))) {
            oos.writeObject(this);
            System.out.println("Status sistem disimpan ke memory.ser");
        } catch (IOException e) {
            System.out.println("Gagal menyimpan memory.ser: " + e.getMessage());
        }

        int totalQty = 0;
        for (Transaksi t : daftarTransaksi) {
            totalQty += t.getJumlah();
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("laporan.txt"))) {
            pw.println("Laporan Kesimpulan Total: " + totalQty);
            System.out.println("Laporan disimpan ke laporan.txt (Total QTY = " + totalQty + ")");
        } catch (IOException e) {
            System.out.println("Gagal menulis laporan.txt: " + e.getMessage());
        }
    }
}

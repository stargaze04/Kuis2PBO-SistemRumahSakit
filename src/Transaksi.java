import java.io.Serializable;

public class Transaksi implements Serializable {
    private static final long serialVersionUID = 1L;
    private Pasien pasien;
    private LayananMedis layanan;
    private int jumlah;

    public Transaksi(Pasien pasien, LayananMedis layanan, int jumlah) {
        this.pasien = pasien;
        this.layanan = layanan;
        this.jumlah = jumlah;
    }

    public Pasien getPasien() {
        return pasien;
    }

    public LayananMedis getLayanan() {
        return layanan;
    }

    public int getJumlah() {
        return jumlah;
    }
}

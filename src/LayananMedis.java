public class LayananMedis extends Entitas {
    private static final long serialVersionUID = 1L;
    private int kuota;

    public LayananMedis(String id, String nama, int kuota) {
        super(id, nama);
        this.kuota = kuota;
    }

    public int getKuota() {
        return kuota;
    }

    public void kurangiKuota(int jumlah) throws TransaksiTidakValidException {
        if (jumlah > kuota) {
            throw new TransaksiTidakValidException("Kuota " + getNama() + " tidak cukup (sisa " + kuota + ", diminta " + jumlah + ")");
        }
        kuota -= jumlah;
    }

    @Override
    public String getInfo() {
        return "Layanan " + getId() + " - " + getNama() + " (kuota " + kuota + ")";
    }
}

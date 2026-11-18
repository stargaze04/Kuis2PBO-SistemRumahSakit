public class Pasien extends Entitas {
    private static final long serialVersionUID = 1L;

    public Pasien(String id, String nama) {
        super(id, nama);
    }

    @Override
    public String getInfo() {
        return "Pasien " + getId() + " - " + getNama();
    }
}

package domain.entity;

import java.util.List;

/**
 * Entity inti yang merepresentasikan satu kegiatan pada jadwal.
 * Berada di layer domain, bebas dari urusan tampilan maupun penyimpanan.
 */
public class Activity {
    /** Urutan hari dalam seminggu, dipakai untuk validasi dan pengurutan. */
    public static final List<String> DAYS =
            List.of("senin", "selasa", "rabu", "kamis", "jumat", "sabtu", "minggu");

    /** ID unik kegiatan, tidak boleh diubah setelah dibuat. */
    private final int id;

    /** Judul kegiatan. */
    private String title;

    /** Hari pelaksanaan kegiatan. */
    private String day;

    /** Waktu pelaksanaan dengan format HH:mm. */
    private String time;

    public Activity(int id, String title, String day, String time) {
        this.id = id;
        this.title = title;
        this.day = day;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDay() {
        return day;
    }

    public String getTime() {
        return time;
    }

    /** Mengubah judul kegiatan. */
    public void changeTitle(String title) {
        this.title = title;
    }

    /** Mengubah hari kegiatan. */
    public void changeDay(String day) {
        this.day = day;
    }

    /** Mengubah waktu kegiatan. */
    public void changeTime(String time) {
        this.time = time;
    }

    /** Nomor urut hari dalam seminggu (Senin = 0); hari tak dikenal diletakkan paling akhir. */
    public int getDayOrder() {
        int index = DAYS.indexOf(day.toLowerCase());
        return index < 0 ? Integer.MAX_VALUE : index;
    }
}

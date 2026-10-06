package usecase;

import domain.entity.Activity;
import domain.entity.SortOption;
import domain.exception.EntityNotFoundException;
import domain.exception.ValidationException;
import domain.repository.IActivityRepository;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Use case yang menangani logika bisnis jadwal kegiatan.
 * Tidak melakukan I/O, hanya memproses data dan melempar checked exception domain.
 */
public class ActivityUseCase {
    /** Format waktu 24 jam: HH:mm. */
    private static final Pattern TIME_PATTERN = Pattern.compile("([01]\\d|2[0-3]):[0-5]\\d");

    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final IActivityRepository activityRepository;

    public ActivityUseCase(IActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** Mengambil semua kegiatan. */
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /** Menambahkan kegiatan baru setelah validasi. */
    public Activity addActivity(String title, String day, String time) throws ValidationException {
        validateTitle(title);
        validateDay(day);
        validateTime(time);
        return activityRepository.save(title.trim(), day.trim(), time.trim());
    }

    /**
     * Mengubah judul, hari, dan/atau waktu kegiatan.
     * Parameter {@code null} berarti field tersebut tidak diubah.
     */
    public void updateActivity(int id, String title, String day, String time)
            throws EntityNotFoundException, ValidationException {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Kegiatan", id));

        // Validasi semua field lebih dulu agar perubahan bersifat atomik.
        if (title != null) {
            validateTitle(title);
        }
        if (day != null) {
            validateDay(day);
        }
        if (time != null) {
            validateTime(time);
        }

        if (title != null) {
            activity.changeTitle(title.trim());
        }
        if (day != null) {
            activity.changeDay(day.trim());
        }
        if (time != null) {
            activity.changeTime(time.trim());
        }
        activityRepository.update(activity);
    }

    /** Menghapus kegiatan berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeActivity(int id) throws EntityNotFoundException {
        if (!activityRepository.deleteById(id)) {
            throw new EntityNotFoundException("Kegiatan", id);
        }
    }

    /** Mencari kegiatan yang judulnya mengandung kata kunci (case-insensitive). */
    public List<Activity> searchActivities(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return activityRepository.findBy(a -> a.getTitle().toLowerCase().contains(lowerKeyword));
    }

    /** Mengurutkan kegiatan sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Activity> sortActivities(SortOption option) {
        return activityRepository.findAll().stream()
                .sorted(option.comparator())
                .toList();
    }

    private void validateTitle(String title) throws ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Judul kegiatan tidak boleh kosong!");
        }
    }

    private void validateDay(String day) throws ValidationException {
        if (day == null || !Activity.DAYS.contains(day.trim().toLowerCase())) {
            throw new ValidationException("Hari tidak valid (Senin - Minggu)!");
        }
    }

    private void validateTime(String time) throws ValidationException {
        if (time == null || !TIME_PATTERN.matcher(time.trim()).matches()) {
            throw new ValidationException("Waktu tidak valid (gunakan format HH:mm)!");
        }
    }
}

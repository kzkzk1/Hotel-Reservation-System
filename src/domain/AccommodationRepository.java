package domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 宿泊のデータを管理するリポジトリ。
 * メモリ上にリストを保持し、ファイル(data/accommodations.txt)と同期する。
 * ファイル形式: 部屋番号,チェックイン日時,チェックアウト日時
 *   （チェックアウト日時が空なら未精算）
 * 客室は部屋番号で保存し、読み込み時にRoomRepositoryから復元する。
 */
public class AccommodationRepository {

    private static final String FILE_PATH = "data/accommodations.txt";

    private List<Accommodation> accommodations = new ArrayList<>();
    private RoomRepository roomRepository;

    public AccommodationRepository(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
        load();
    }

    private void load() {
        accommodations.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                // -1をつけると、末尾の空要素も残す（チェックアウト日時が空のとき用）
                String[] parts = line.split(",", -1);
                String roomNumber = parts[0];
                String checkInStr = parts[1];
                String checkOutStr = parts[2];

                // 部屋番号から客室オブジェクトを引く
                Room room = roomRepository.findByRoomNumber(roomNumber);

                // Accommodationは中でLocalDateTime.now()を使うが、
                // 読み込み時は保存された日時で復元したいので、専用に復元する
                Accommodation acc = new Accommodation(room);
                // チェックイン日時を保存値で上書き（復元）
                acc.restoreCheckInDateTime(LocalDateTime.parse(checkInStr));
                // チェックアウト日時があれば復元
                if (!checkOutStr.isEmpty()) {
                    acc.restoreCheckOutDateTime(LocalDateTime.parse(checkOutStr));
                }
                accommodations.add(acc);
            }
        } catch (IOException e) {
            System.out.println("accommodations.txt が読み込めませんでした（初回なら正常）");
        }
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Accommodation acc : accommodations) {
                String checkOutStr = (acc.getCheckOutDateTime() == null)
                        ? "" : acc.getCheckOutDateTime().toString();
                writer.write(acc.getRoom().getRoomNumber() + ","
                        + acc.getCheckInDateTime().toString() + ","
                        + checkOutStr);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("accommodations.txt への書き込みに失敗しました");
        }
    }

    public void add(Accommodation accommodation) {
        accommodations.add(accommodation);
        save();
    }

    public List<Accommodation> findAll() {
        return accommodations;
    }

    // 部屋番号から、まだ精算していない宿泊を探す（チェックアウト用）
    public Accommodation findActiveByRoomNumber(String roomNumber) {
        for (Accommodation acc : accommodations) {
            if (acc.getRoom().getRoomNumber().equals(roomNumber) && !acc.isPaid()) {
                return acc;
            }
        }
        return null;
    }
}
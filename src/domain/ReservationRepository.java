package domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * 予約のデータを管理するリポジトリ。
 * メモリ上にリストを保持し、ファイル(data/reservations.txt)と同期する。
 * ファイル形式: 予約番号,宿泊日,種別名
 * 客室タイプは種別名で保存し、読み込み時にRoomTypeRepositoryから復元する。
 */
public class ReservationRepository {

    private static final String FILE_PATH = "data/reservations.txt";

    private List<Reservation> reservations = new ArrayList<>();
    private RoomTypeRepository roomTypeRepository;

    public ReservationRepository(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
        load();
    }

    private void load() {
        reservations.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                int reservationNumber = Integer.parseInt(parts[0]);
                LocalDate accommodationDate = LocalDate.parse(parts[1]);  // "2026-04-01"形式
                String typeName = parts[2];

                RoomType roomType = roomTypeRepository.findByTypeName(typeName);

                reservations.add(new Reservation(reservationNumber, accommodationDate, roomType));
            }
        } catch (IOException e) {
            System.out.println("reservations.txt が読み込めませんでした（初回なら正常）");
        }
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Reservation r : reservations) {
                writer.write(r.getReservationNumber() + ","
                        + r.getAccommodationDate() + ","
                        + r.getRoomType().getTypeName());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("reservations.txt への書き込みに失敗しました");
        }
    }

    // 予約をリストに追加する（新規予約のとき）
    public void add(Reservation reservation) {
        reservations.add(reservation);
        save();  // 追加したらすぐファイルに反映
    }

    public List<Reservation> findAll() {
        return reservations;
    }

    // 予約番号から予約を探す
    public Reservation findByNumber(int reservationNumber) {
        for (Reservation r : reservations) {
            if (r.getReservationNumber() == reservationNumber) {
                return r;
            }
        }
        return null;
    }

    // 次の予約番号を発行する（今ある予約番号の最大+1、無ければ1）
    public int nextReservationNumber() {
        int max = 0;
        for (Reservation r : reservations) {
            if (r.getReservationNumber() > max) {
                max = r.getReservationNumber();
            }
        }
        return max + 1;
    }

    // 予約を削除する（キャンセル用）
    public void remove(Reservation reservation) {
        reservations.remove(reservation);
        save();  // 削除したらファイルに反映
    }
}
package domain;

import java.time.LocalDateTime;

/**
 * 宿泊を表すエンティティ。
 * チェックインで生成され、チェックアウトで精算される。
 */
public class Accommodation {

    private LocalDateTime checkInDateTime;    // チェックイン日時
    private LocalDateTime checkOutDateTime;   // チェックアウト日時（nullなら未精算）
    private Room room;                        // 参照：どの客室の宿泊か

    public Accommodation(Room room) {
        this.room = room;
        this.checkInDateTime = LocalDateTime.now();  // 現在日時でチェックイン
        this.checkOutDateTime = null;
    }

    public LocalDateTime getCheckInDateTime() {
        return checkInDateTime;
    }

    public LocalDateTime getCheckOutDateTime() {
        return checkOutDateTime;
    }

    public Room getRoom() {
        return room;
    }

    // 精算済みにする（チェックアウト日時に現在日時を入れる）
    public void markAsPaid() {
        this.checkOutDateTime = LocalDateTime.now();
    }

    // 精算済みか（チェックアウト日時が入っていれば精算済み）
    public boolean isPaid() {
        return this.checkOutDateTime != null;
    }
}
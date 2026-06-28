package domain;

import java.time.LocalDateTime;

/**
 * 宿泊を表すエンティティ。
 * チェックインで生成され、チェックアウトで精算される。
 * チェックアウト日時が入っているかどうかで精算済みかを判断する。
 */
public class Accommodation {

    private LocalDateTime checkInDateTime;    // チェックイン日時
    private LocalDateTime checkOutDateTime;   // チェックアウト日時（nullなら未精算）
    private int charge;                       // 料金
    private Room room;                        // 参照：どの客室の宿泊か

    public Accommodation(Room room, int charge) {
        this.room = room;
        this.charge = charge;
        this.checkInDateTime = LocalDateTime.now();  // 現在日時でチェックイン
        this.checkOutDateTime = null;                // まだチェックアウトしていない
    }

    public LocalDateTime getCheckInDateTime() {
        return checkInDateTime;
    }

    public LocalDateTime getCheckOutDateTime() {
        return checkOutDateTime;
    }

    public int getCharge() {
        return charge;
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
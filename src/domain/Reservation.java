package domain;

import java.time.LocalDate;

/**
 * 予約を表すエンティティ。
 * 予約番号で識別し、宿泊日とどの客室タイプを予約したかを持つ。
 */
public class Reservation {

    private int reservationNumber;   // 予約番号
    private LocalDate stayDate;      // 宿泊日
    private RoomType roomType;       // 参照：どの客室タイプを予約したか

    public Reservation(int reservationNumber, LocalDate stayDate, RoomType roomType) {
        this.reservationNumber = reservationNumber;
        this.stayDate = stayDate;
        this.roomType = roomType;
    }

    public int getReservationNumber() {
        return reservationNumber;
    }

    public LocalDate getStayDate() {
        return stayDate;
    }

    public RoomType getRoomType() {
        return roomType;
    }
}
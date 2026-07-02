package domain;

import java.time.LocalDate;

/**
 * 予約を表すエンティティ。
 */
public class Reservation {

    private int reservationNumber;        // 予約番号
    private LocalDate accommodationDate;  // 宿泊日
    private RoomType roomType;            // 参照：予約した客室タイプ

    public Reservation(int reservationNumber, LocalDate accommodationDate, RoomType roomType) {
        this.reservationNumber = reservationNumber;
        this.accommodationDate = accommodationDate;
        this.roomType = roomType;
    }

    public int getReservationNumber() {
        return reservationNumber;
    }

    public LocalDate getAccommodationDate() {
        return accommodationDate;
    }

    public RoomType getRoomType() {
        return roomType;
    }
}
package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import domain.RepositoryFactory;
import domain.RoomType;
import domain.Reservation;

/**
 * 予約UCの処理（担当：大和）。
 * makeReservation() の中に、予約の処理を実装する。
 */
public class ReservationControl {

    public void makeReservation(BufferedReader reader) throws IOException {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        // TODO（大和）: 予約UCを実装する（Issue #17参照）
        System.out.println("[予約] 未実装");
    }
}
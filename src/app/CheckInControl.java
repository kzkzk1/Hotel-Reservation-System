package app;

import java.io.BufferedReader;
import java.io.IOException;
import domain.RepositoryFactory;
import domain.Reservation;
import domain.RoomType;
import domain.Room;
import domain.Accommodation;

/**
 * チェックインUCの処理（担当：佑典）。
 * checkIn() の中に、チェックインの処理を実装する。
 */
public class CheckInControl {

    public void checkIn(BufferedReader reader) throws IOException {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        // TODO（佑典）: チェックインUCを実装する（Issue #18参照）
        System.out.println("[チェックイン] 実装");
    }
}
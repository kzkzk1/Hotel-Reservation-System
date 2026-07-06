package app;

import java.io.BufferedReader;
import java.io.IOException;
import domain.RepositoryFactory;
import domain.Reservation;
import domain.RoomType;

/**
 * 予約キャンセルUCの処理（担当：岳淑）。
 * cancel() の中に、キャンセルの処理を実装する。
 */
public class CancelControl {

    public void cancel(BufferedReader reader) throws IOException {
        RepositoryFactory factory = RepositoryFactory.getInstance();
        // TODO（岳淑）: キャンセルUCを実装する
        System.out.println("[キャンセル] 未実装");
    }
}
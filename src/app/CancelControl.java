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

        try {
            // 1. 予約番号を入力してもらう
            System.out.print("キャンセルする予約番号を入力してください: ");
            String input = reader.readLine();
            int number = Integer.parseInt(input);

            // 2. 予約を探す
            Reservation r = factory.getReservationRepository().findByNumber(number);
            if (r == null) {
                System.out.println("警告: 無効な予約番号です。");
                return;
            }

            // 3. 予約の客室タイプを取得
            RoomType type = r.getRoomType();

            // 5. 予約を削除する
            factory.getReservationRepository().remove(r);

            // 6. 完了メッセージ
            System.out.println("予約番号 " + number + " の予約をキャンセルしました。");

        } catch (NumberFormatException e) {
            System.out.println("警告: 予約番号は正しい数値で入力してください。");
        }
    }
}

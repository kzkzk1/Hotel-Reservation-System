package app;

import java.io.BufferedReader;
import java.io.IOException;
import domain.RepositoryFactory;
import domain.Accommodation;
import domain.Room;

/**
 * チェックアウトUCの処理（担当：宏大）。
 * checkOut() の中に、チェックアウトの処理を実装する。
 */
public class CheckOutControl {

    public void checkOut(BufferedReader reader) throws IOException {
        RepositoryFactory factory = RepositoryFactory.getInstance();

        try {
            // 1. 部屋番号を入力してもらう
            System.out.print("チェックアウトする部屋番号を入力してください: ");
            String roomNumber = reader.readLine();

            // 2. その部屋の「未精算（チェックアウト前）」の宿泊記録を探す
            // （AccommodationRepository に用意されている専用メソッドを使います）
            Accommodation acc = factory.getAccommodationRepository().findActiveByRoomNumber(roomNumber);
            if (acc == null) {
                System.out.println("警告: 入力された部屋番号の宿泊記録が存在しません、または既にチェックアウト済みです。");
                return;
            }

            // 3. 料金を取得して表示
            // （大和さんのコード同様、Room -> RoomType と辿って料金を取得します）
            int charge = acc.getRoom().getRoomType().getCharge();
            System.out.println("ご請求金額は " + charge + " 円です。");

            // 4. 精算の確認（受付係が支払いを受け取ったか）
            System.out.print("精算を完了しますか？ (Y/N): ");
            String confirm = reader.readLine();
            if (!confirm.equalsIgnoreCase("Y")) {
                System.out.println("チェックアウト処理を中断しました。");
                return;
            }

            // 5. 客室を空室状態に戻す
            Room room = acc.getRoom();
            // 佑典さんのコードが引数なしの setRoomToOccupied() だったので、対になる空室メソッドを呼びます
            room.setRoomToVacant(); 
            
            // 6. 精算記録を更新（Accommodationを精算済みにする）

            acc.markAsPaid(); 

            // 7. メモリ上の変更をテキストファイル (.txt) に同期して保存する
            factory.getRoomRepository().save();
            factory.getAccommodationRepository().save();

            // 8. 完了メッセージ
            System.out.println("【完了】部屋番号 " + roomNumber + " のチェックアウトが完了しました。");

        } catch (Exception e) {
            // 予期せぬエラーが起きた場合（ファイル書き込み失敗など）の安全策
            System.out.println("エラーが発生しました: " + e.getMessage());
        }
    }
}
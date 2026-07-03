package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import domain.RepositoryFactory;
import domain.RoomType;
import domain.Reservation;

public class ReservationControl {

    public void makeReservation(BufferedReader reader) throws IOException {
        RepositoryFactory factory = RepositoryFactory.getInstance();

        // 1. 宿泊日を入力してもらう
        System.out.println("宿泊日を入力（例 2026-04-01）");
        String dateStr = reader.readLine();
        LocalDate date = LocalDate.parse(dateStr);

        // 2. 空きのある客室タイプを表示
        System.out.println("--- 空きのある客室タイプ ---");
        for (RoomType rt : factory.getRoomTypeRepository().findAll()) {
            if (rt.getAvailableRoomCount() > 0) { // 空き部屋数が0より大きいものだけを表示
                System.out.println("種別名: " + rt.getTypeName() + 
                                   " / 料金: " + rt.getCharge() + 
                                   " / 空き数: " + rt.getAvailableRoomCount());
            }
        }
        System.out.println("----------------------------");

        // 3. 客室タイプを選んでもらう（種別名を入力）
        System.out.println("希望する客室タイプの種別名を入力してください:");
        String typeName = reader.readLine();
        RoomType type = factory.getRoomTypeRepository().findByTypeName(typeName);

        // 4. type が null または空き0なら「予約できません」と表示して return
        if (type == null || type.getAvailableRoomCount() <= 0) { // 空き部屋数の確認[cite: 3]
            System.out.println("予約できません");
            return;
        }

        // 5. 予約番号を発行
        int number = factory.getReservationRepository().nextReservationNumber(); // 次の予約番号を発行（最大値+1）

        // 6. 予約を作る（new Reservation を使用）
        Reservation r = new Reservation(number, date, type); // 予約番号、宿泊日、客室タイプを指定してインスタンス化

        // 7. リポジトリに追加
        factory.getReservationRepository().add(r); // メモリに追加され、自動的に reservations.txt に保存される[cite: 2]

        // 8. 空き部屋数を1減らす
        type.decrementAvailableRoomCount(); // 該当客室タイプの空きを1減らす[cite: 3]
        factory.getRoomTypeRepository().save();

        // 9. 予約番号を表示
        System.out.println("予約完了。予約番号：" + number);
    }
}
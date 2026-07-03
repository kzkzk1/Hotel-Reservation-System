package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException; // 追加
import domain.RepositoryFactory;
import domain.RoomType;
import domain.Reservation;

public class ReservationControl {

    public void makeReservation(BufferedReader reader) throws IOException {
        RepositoryFactory factory = RepositoryFactory.getInstance();

        // 1. 宿泊日を入力してもらう
        System.out.println("宿泊日を入力（例 2026-04-01）");
        String dateStr = reader.readLine();
        
        LocalDate date;
        try {
            // 入力された文字列を日付に変換
            date = LocalDate.parse(dateStr);
        } catch (DateTimeParseException e) {
            // 形式が間違っていた場合は警告を出して処理を終了
            System.out.println("日付の形式が正しくありません。予約処理を中断します。");
            return;
        }

        // 2. 空きのある客室タイプを表示
        System.out.println("--- 空きのある客室タイプ ---");
        boolean hasAvailableRoom = false; // 空室があるかどうかのフラグ

        for (RoomType rt : factory.getRoomTypeRepository().findAll()) {
            if (rt.getAvailableRoomCount() > 0) { 
                System.out.println("種別名: " + rt.getTypeName() + 
                                   " / 料金: " + rt.getCharge() + 
                                   " / 空き数: " + rt.getAvailableRoomCount());
                hasAvailableRoom = true; // 1つでも空きがあれば true にする
            }
        }
        System.out.println("----------------------------");

        // 空室が1つもなかった場合の処理
        if (!hasAvailableRoom) {
            System.out.println("空室がありません");
            return; // 以降の入力処理を行わず終了
        }

        // 3. 客室タイプを選んでもらう（種別名を入力）
        System.out.println("希望する客室タイプの種別名を入力してください:");
        String typeName = reader.readLine();
        RoomType type = factory.getRoomTypeRepository().findByTypeName(typeName);

        // 4. type が null または空き0なら「予約できません」と表示して return
        if (type == null || type.getAvailableRoomCount() <= 0) { 
            System.out.println("予約できません");
            return;
        }

        // 5. 予約番号を発行
        int number = factory.getReservationRepository().nextReservationNumber(); 

        // 6. 予約を作る
        Reservation r = new Reservation(number, date, type); 

        // 7. リポジトリに追加
        factory.getReservationRepository().add(r); 

        // 8. 空き部屋数を1減らす
        type.decrementAvailableRoomCount(); 
        factory.getRoomTypeRepository().save();

        // 9. 予約番号を表示
        System.out.println("予約完了。予約番号：" + number);
    }
}
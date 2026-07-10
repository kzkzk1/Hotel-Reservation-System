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
        boolean hasAvailableRoom = false;

        for (RoomType rt : factory.getRoomTypeRepository().findAll()) {
            String typeName = rt.getTypeName();
            
            // 総部屋数と、指定された日(date)の予約数を取得
            int totalRooms = factory.getRoomRepository().countTotalRoomsByType(typeName);
            int reservedCount = factory.getReservationRepository().countReservationsByTypeAndDate(typeName, date);
            
            // 引き算をして空室を算出
            int availableCount = totalRooms - reservedCount;

            if (availableCount > 0) { 
                System.out.println("種別名: " + typeName + 
                                " / 料金: " + rt.getCharge() + 
                                " / 空き数: " + availableCount);
                hasAvailableRoom = true;
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
        if (type == null) { 
            System.out.println("予約できません");
            return;
        }

        // リポジトリから総部屋数と現在の予約数を取得し、その日の空室数を計算する
        int totalRooms = factory.getRoomRepository().countTotalRoomsByType(type.getTypeName());
        int reservedCount = factory.getReservationRepository().countReservationsByTypeAndDate(type.getTypeName(), date);
        int availableCount = totalRooms - reservedCount;

        // 計算した空室数が0以下なら予約不可にする
        if (availableCount <= 0) { 
            System.out.println("予約できません（満室です）");
            return;
        }

        // 5. 予約番号を発行
        int number = factory.getReservationRepository().nextReservationNumber(); 

        // 6. 予約を作る
        Reservation r = new Reservation(number, date, type); 

        // 7. リポジトリに追加
        factory.getReservationRepository().add(r); 

        // 9. 予約番号を表示
        System.out.println("予約完了。予約番号：" + number);
    }
}
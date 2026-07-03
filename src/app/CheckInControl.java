package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List; //もともと作ってもらってたベースに追加
import domain.RepositoryFactory;
import domain.Reservation;
import domain.RoomType;
import domain.Room;
import domain.Accommodation;

public class CheckInControl {

    public void checkIn(BufferedReader reader) throws IOException {
        RepositoryFactory factory = RepositoryFactory.getInstance();

        try {
            // 1. 予約番号を入力してもらう
            System.out.print("予約番号を入力してください: ");
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
            String typeName = type.getTypeName();

            // 4. そのタイプの空き部屋を全部表示する（一覧表示）
            List<Room> availableRooms = factory.getRoomRepository().findAllAvailableByType(typeName);
            if (availableRooms.isEmpty()) {
                System.out.println("警告: " + typeName + "タイプの空室がありません。");
                return;
            }

            System.out.println("【割り当て可能な空室一覧（" + typeName + "）】");
            for (Room room : availableRooms) {
                System.out.println("- 部屋番号: " + room.getRoomNumber());
            }

            // 5. 受付係に部屋番号を入力してもらう（表示された中から選ぶ）
            System.out.print("割り当てる部屋番号を入力してください: ");
            String roomNumberInput = reader.readLine();

            // 6. 選ばれた部屋を探す。その部屋が本当に空きか、正しい種別か確認
            Room selectedRoom = factory.getRoomRepository().findByRoomNumber(roomNumberInput);
            
            if (selectedRoom == null) {
                System.out.println("警告: 入力された部屋番号が存在しません。");
                return;
            }
            if (selectedRoom.isOccupied()) {
                System.out.println("警告: 指定された部屋は既に使用中です。");
                return;
            }
            if (!selectedRoom.getRoomType().getTypeName().equals(typeName)) {
                System.out.println("警告: 指定された部屋は予約された客室タイプと一致しません。");
                return;
            }

            // 7. 部屋を使用中にする
            selectedRoom.setRoomToOccupied();
            // Roomの状態変更を rooms.txt に保存する
            factory.getRoomRepository().save();

            // 8. 宿泊を作る
            Accommodation acc = new Accommodation(selectedRoom);
            // まずRepositoryのリストに追加し、その後ファイルに保存(同期)する
            factory.getAccommodationRepository().add(acc); 
            factory.getAccommodationRepository().save(); 

            // 9. 部屋番号を表示
            System.out.println("チェックインが完了しました。部屋番号: " + selectedRoom.getRoomNumber() + " を割り当てました。");

        } catch (NumberFormatException e) {
            System.out.println("警告: 予約番号は正しい数値で入力してください。");
        }
    }
}
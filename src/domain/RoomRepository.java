package domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 客室のデータを管理するリポジトリ。
 * メモリ上にリストを保持し、ファイル(data/rooms.txt)と同期する。
 * ファイル形式: 部屋番号,種別名,使用中か
 * 客室タイプは種別名で保存し、読み込み時にRoomTypeRepositoryから復元する。
 */
public class RoomRepository {

    private static final String FILE_PATH = "data/rooms.txt";

    private List<Room> rooms = new ArrayList<>();
    private RoomTypeRepository roomTypeRepository;

    // 客室タイプを引くために、RoomTypeRepositoryを受け取る
    public RoomRepository(RoomTypeRepository roomTypeRepository) {
        this.roomTypeRepository = roomTypeRepository;
        load();
    }

    private void load() {
        rooms.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                String roomNumber = parts[0];
                String typeName = parts[1];
                boolean occupied = Boolean.parseBoolean(parts[2]);

                // 種別名から客室タイプオブジェクトを引く
                RoomType roomType = roomTypeRepository.findByTypeName(typeName);

                Room room = new Room(roomNumber, roomType);
                if (occupied) {
                    room.setRoomToOccupied();  // 使用中なら状態を復元
                }
                rooms.add(room);
            }
        } catch (IOException e) {
            System.out.println("rooms.txt が読み込めませんでした（初回なら正常）");
        }
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Room room : rooms) {
                writer.write(room.getRoomNumber() + ","
                        + room.getRoomType().getTypeName() + ","
                        + room.isOccupied());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("rooms.txt への書き込みに失敗しました");
        }
    }

    public List<Room> findAll() {
        return rooms;
    }

    // 部屋番号から客室を探す
    public Room findByRoomNumber(String roomNumber) {
        for (Room room : rooms) {
            if (room.getRoomNumber().equals(roomNumber)) {
                return room;
            }
        }
        return null;
    }

    // 指定した客室タイプの、空いている客室を全部返す（一覧表示用）
    public List<Room> findAllAvailableByType(String typeName) {
        List<Room> result = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getRoomType().getTypeName().equals(typeName) && !room.isOccupied()) {
                result.add(room);
            }
        }
        return result;
    }

    public int countTotalRoomsByType(String typeName) {
        int count = 0;
        for (Room room : rooms) {
            if (room.getRoomType().getTypeName().equals(typeName)) {
                count++;
            }
        }
        return count;
    }
}
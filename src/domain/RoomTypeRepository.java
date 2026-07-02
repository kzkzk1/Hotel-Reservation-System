package domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 客室タイプのデータを管理するリポジトリ。
 * メモリ上にリストを保持し、ファイル(data/roomtypes.txt)と同期する。
 * ファイル形式: 種別名,料金,定員,空き部屋数
 */
public class RoomTypeRepository {

    private static final String FILE_PATH = "data/roomtypes.txt";

    private List<RoomType> roomTypes = new ArrayList<>();

    // 作られたときにファイルから読み込む
    public RoomTypeRepository() {
        load();
    }

    // ファイルから客室タイプを読み込んでリストに復元する
    private void load() {
        roomTypes.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] parts = line.split(",");
                String typeName = parts[0];
                int charge = Integer.parseInt(parts[1]);
                int capacity = Integer.parseInt(parts[2]);
                int availableRoomCount = Integer.parseInt(parts[3]);
                roomTypes.add(new RoomType(typeName, charge, capacity, availableRoomCount));
            }
        } catch (IOException e) {
            System.out.println("roomtypes.txt が読み込めませんでした（初回なら正常）");
        }
    }

    // リストをファイルに書き出す（変更があったら呼ぶ）
    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (RoomType rt : roomTypes) {
                writer.write(rt.getTypeName() + "," + rt.getCharge() + ","
                        + rt.getCapacity() + "," + rt.getAvailableRoomCount());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("roomtypes.txt への書き込みに失敗しました");
        }
    }

    // 全ての客室タイプを返す
    public List<RoomType> findAll() {
        return roomTypes;
    }

    // 種別名から客室タイプを探す（見つからなければ null）
    public RoomType findByTypeName(String typeName) {
        for (RoomType rt : roomTypes) {
            if (rt.getTypeName().equals(typeName)) {
                return rt;
            }
        }
        return null;
    }
}
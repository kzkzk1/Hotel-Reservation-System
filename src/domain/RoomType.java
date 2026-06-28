package domain;

/**
 * 客室タイプ（シングル・ダブル・スイートなど）を表すエンティティ。
 * 種別ごとに料金・定員・空き部屋数を持つ。
 */
public class RoomType {

    private String typeName;          // 種別名（例: "シングル"）
    private int price;                // 一泊あたりの料金
    private int capacity;             // 定員
    private int availableRoomCount;   // 空き部屋数

    public RoomType(String typeName, int price, int capacity, int availableRoomCount) {
        this.typeName = typeName;
        this.price = price;
        this.capacity = capacity;
        this.availableRoomCount = availableRoomCount;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getPrice() {
        return price;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableRoomCount() {
        return availableRoomCount;
    }

    // 空き部屋数を1減らす（予約のとき）
    public void decrementAvailableRoomCount() {
        this.availableRoomCount--;
    }

    // 空き部屋数を1増やす（キャンセルのとき）
    public void incrementAvailableRoomCount() {
        this.availableRoomCount++;
    }
}
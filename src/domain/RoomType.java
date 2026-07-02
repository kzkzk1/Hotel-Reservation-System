package domain;

/**
 * 客室タイプ（シングル・ダブル・スイートなど）を表すエンティティ。
 */
public class RoomType {

    private String typeName;          // 種別名
    private int charge;               // 一泊あたりの料金
    private int capacity;             // 定員
    private int availableRoomCount;   // 空き部屋数

    public RoomType(String typeName, int charge, int capacity, int availableRoomCount) {
        this.typeName = typeName;
        this.charge = charge;
        this.capacity = capacity;
        this.availableRoomCount = availableRoomCount;
    }

    public String getTypeName() {
        return typeName;
    }

    public int getCharge() {
        return charge;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getAvailableRoomCount() {
        return availableRoomCount;
    }

    public void decrementAvailableRoomCount() {
        this.availableRoomCount--;
    }

    public void incrementAvailableRoomCount() {
        this.availableRoomCount++;
    }
}
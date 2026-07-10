package domain;

/**
 * 客室タイプ（シングル・ダブル・スイートなど）を表すエンティティ。
 */
public class RoomType {

    private String typeName;          // 種別名
    private int charge;               // 一泊あたりの料金
    private int capacity;             // 定員

    public RoomType(String typeName, int charge, int capacity) {
        this.typeName = typeName;
        this.charge = charge;
        this.capacity = capacity;
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
}
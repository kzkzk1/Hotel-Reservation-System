package domain;

/**
 * 客室（個別の部屋）を表すエンティティ。
 */
public class Room {

    private String roomNumber;   // 部屋番号
    private boolean occupied;    // 使用中か
    private RoomType roomType;   // 参照：この部屋の客室タイプ

    public Room(String roomNumber, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.occupied = false;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public boolean isOccupied() {
        return occupied;
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomToOccupied() {
        this.occupied = true;
    }

    public void setRoomToVacant() {
        this.occupied = false;
    }
}
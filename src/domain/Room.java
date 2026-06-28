package domain;

/**
 * 客室（個別の部屋）を表すエンティティ。
 * 部屋番号で識別し、どの客室タイプかを参照として持つ。
 */
public class Room {

    private String roomNumber;   // 部屋番号（例: "101"）
    private boolean occupied;    // 使用中か（true=使用中, false=空室）
    private RoomType roomType;   // 参照：この部屋の客室タイプ

    public Room(String roomNumber, RoomType roomType) {
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.occupied = false;   // 新しい部屋は最初は空室
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

    // 部屋を使用中にする（チェックイン）
    public void setRoomToOccupied() {
        this.occupied = true;
    }

    // 部屋を空室にする（チェックアウト）
    public void setRoomToVacant() {
        this.occupied = false;
    }
}
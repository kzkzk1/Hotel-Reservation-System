import domain.RepositoryFactory;
import domain.RoomType;
import domain.Room;

public class Test {
    public static void main(String[] args) {
        // ファクトリから、各リポジトリを取得
        RepositoryFactory factory = RepositoryFactory.getInstance();

        System.out.println("=== 客室タイプ ===");
        for (RoomType rt : factory.getRoomTypeRepository().findAll()) {
            System.out.println(rt.getTypeName() + " / 料金:" + rt.getCharge()
                + " / 空き:" + rt.getAvailableRoomCount());
        }

        System.out.println("=== 客室 ===");
        for (Room room : factory.getRoomRepository().findAll()) {
            System.out.println(room.getRoomNumber() + " / "
                + room.getRoomType().getTypeName() + " / 使用中:" + room.isOccupied());
        }

        System.out.println("=== ダブルの空き部屋 ===");
        Room available = factory.getRoomRepository().findAvailableByType("ダブル");
        System.out.println(available != null ? available.getRoomNumber() : "空きなし");

        System.out.println("=== 次の予約番号 ===");
        System.out.println(factory.getReservationRepository().nextReservationNumber());
    }
}
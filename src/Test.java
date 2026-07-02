import domain.RoomTypeRepository;
import domain.RoomType;
import domain.RoomRepository;
import domain.Room;

public class Test {
    public static void main(String[] args) {
        // リポジトリを作る（このとき、コンストラクタでファイルを読み込む）
        RoomTypeRepository repo = new RoomTypeRepository();

        // 全部の客室タイプを表示してみる
        System.out.println("=== 読み込んだ客室タイプ ===");
        for (RoomType rt : repo.findAll()) {
            System.out.println(
                rt.getTypeName() + " / 料金:" + rt.getCharge()
                + " / 定員:" + rt.getCapacity()
                + " / 空き:" + rt.getAvailableRoomCount()
            );
        }

        // 種別名で探すテスト
        System.out.println("=== 種別名で探す（ダブル）===");
        RoomType found = repo.findByTypeName("ダブル");
        if (found != null) {
            System.out.println("見つかった: " + found.getTypeName() + " 料金:" + found.getCharge());
        } else {
            System.out.println("見つからなかった");
        }

        // --- RoomRepository のテスト ---
        System.out.println("\n=== 読み込んだ客室 ===");
        RoomRepository roomRepo = new RoomRepository(repo);  // repoはさっきのRoomTypeRepository
        for (Room room : roomRepo.findAll()) {
            System.out.println(
                room.getRoomNumber() + " / " + room.getRoomType().getTypeName()
                + " / 使用中:" + room.isOccupied()
            );
        }

        System.out.println("=== ダブルの空き部屋を探す ===");
        Room available = roomRepo.findAvailableByType("ダブル");
        if (available != null) {
            System.out.println("空き部屋: " + available.getRoomNumber());
        } else {
            System.out.println("空きなし");
        }
    }
}
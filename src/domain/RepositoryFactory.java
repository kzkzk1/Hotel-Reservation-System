package domain;

/**
 * 全リポジトリを1箇所で生成・管理するファクトリ。
 * Singleton（インスタンスは1つだけ）。
 * 各UCは、このファクトリ経由でリポジトリを取得することで、
 * 同じデータ（同じリスト）を共有する。
 */
public class RepositoryFactory {

    // 唯一のインスタンス
    private static RepositoryFactory instance = new RepositoryFactory();

    private RoomTypeRepository roomTypeRepository;
    private RoomRepository roomRepository;
    private ReservationRepository reservationRepository;
    private AccommodationRepository accommodationRepository;

    // privateコンストラクタ（外から new できない＝1つだけになる）
    private RepositoryFactory() {
        // 依存順に生成する
        // RoomType → Room, Reservation → Accommodation
        roomTypeRepository = new RoomTypeRepository();
        roomRepository = new RoomRepository(roomTypeRepository);
        reservationRepository = new ReservationRepository(roomTypeRepository);
        accommodationRepository = new AccommodationRepository(roomRepository);
    }

    // 唯一のインスタンスを返す
    public static RepositoryFactory getInstance() {
        return instance;
    }

    public RoomTypeRepository getRoomTypeRepository() {
        return roomTypeRepository;
    }

    public RoomRepository getRoomRepository() {
        return roomRepository;
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    public AccommodationRepository getAccommodationRepository() {
        return accommodationRepository;
    }
}
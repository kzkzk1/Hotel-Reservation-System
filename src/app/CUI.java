package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * ホテル予約システムのCUI（コンソール画面）。
 * メニューを表示し、選択に応じて各ユースケースを呼び分ける。
 * 各UCの中身は、担当者が実装する（ここは骨格）。
 */
public class CUI {

    private BufferedReader reader;

    public CUI() {
        reader = new BufferedReader(new InputStreamReader(System.in));
    }

    // メインループ：メニューを出し続け、選択を処理する
    public void execute() throws IOException {
        while (true) {
            System.out.println("");
            System.out.println("=== ホテル予約システム ===");
            System.out.println("1: 予約");
            System.out.println("2: チェックイン");
            System.out.println("3: チェックアウト");
            System.out.println("0: 終了");
            System.out.print("> ");

            String input = reader.readLine();
            int menu;
            try {
                menu = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("数字を入力してください");
                continue;
            }

            if (menu == 0) {
                System.out.println("システムを終了します");
                break;
            }

            switch (menu) {
                case 1:
                    reserveRoom();
                    break;
                case 2:
                    checkIn();
                    break;
                case 3:
                    checkOut();
                    break;
                default:
                    System.out.println("無効な選択です");
                    break;
            }
        }
        reader.close();
    }

    // --- 各ユースケース（中身は担当者が実装する） ---

    // 予約UC（アクター：利用者 / 実装担当：大和）
    private void reserveRoom() throws IOException {
        System.out.println("[予約] ここに予約処理を実装します（未実装）");
    }

    // チェックインUC（アクター：受付係 / 実装担当：佑典）
    private void checkIn() throws IOException {
        System.out.println("[チェックイン] ここにチェックイン処理を実装します（未実装）");
    }

    // チェックアウトUC（アクター：受付係 / 実装担当：宏大）
    private void checkOut() throws IOException {
        System.out.println("[チェックアウト] ここにチェックアウト処理を実装します（未実装）");
    }

    // プログラムの入口
    public static void main(String[] args) throws IOException {
        CUI cui = new CUI();
        cui.execute();
    }
}
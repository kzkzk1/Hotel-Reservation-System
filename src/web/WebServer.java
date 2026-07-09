package web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import domain.RepositoryFactory;
import domain.RoomType;
import domain.Room;
import domain.Reservation;
import domain.Accommodation;

/**
 * ホテル予約システムのWebサーバー（全UC対応）。
 * トップページにメニュー、各UCを別URLに分けている。
 */
public class WebServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", new TopHandler());
        server.createContext("/reserve", new ReserveHandler());
        server.createContext("/checkin", new CheckInHandler());
        server.createContext("/checkout", new CheckOutHandler());
        server.createContext("/cancel", new CancelHandler());
        server.createContext("/", new TopHandler());
        server.createContext("/guest", new GuestHandler());
        server.createContext("/staff", new StaffHandler());
        server.createContext("/reserve", new ReserveHandler());
        server.createContext("/checkin", new CheckInHandler());
        server.createContext("/checkout", new CheckOutHandler());
        server.createContext("/cancel", new CancelHandler());

        server.start();
        System.out.println("サーバー起動: http://localhost:8080");
    }

    // ===== トップページ（利用者/スタッフの選択）=====
    static class TopHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String html = page("ようこそ",
                  "<ul class='menu'>"
                + "<li><a href='/guest'>ご利用のお客様</a></li>"
                + "<li><a href='/staff'>スタッフの方</a></li>"
                + "</ul>");
            sendHtml(exchange, html);
        }
    }

    // ===== 利用者メニュー（予約・キャンセル）=====
    static class GuestHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String html = page("ご利用のお客様",
                  "<ul class='menu'>"
                + "<li><a href='/reserve'>お部屋のご予約</a></li>"
                + "<li><a href='/cancel'>ご予約のキャンセル</a></li>"
                + "</ul>");
            sendHtml(exchange, html);
        }
    }

    // ===== スタッフメニュー（チェックイン・チェックアウト）=====
    static class StaffHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String html = page("スタッフ用メニュー",
                  "<ul class='menu'>"
                + "<li><a href='/checkin'>チェックイン</a></li>"
                + "<li><a href='/checkout'>チェックアウト</a></li>"
                + "</ul>");
            sendHtml(exchange, html);
        }
    }

    // ===== 予約 =====
    static class ReserveHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            RepositoryFactory factory = RepositoryFactory.getInstance();
            String message = "";

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                Map<String, String> p = parseParams(readRequestBody(exchange));
                try {
                    LocalDate date = LocalDate.parse(p.get("date"));
                    RoomType type = factory.getRoomTypeRepository().findByTypeName(p.get("type"));
                    if (type == null || type.getAvailableRoomCount() <= 0) {
                        message = msg("予約できません（空きがありません）", true);
                    } else {
                        int number = factory.getReservationRepository().nextReservationNumber();
                        factory.getReservationRepository().add(new Reservation(number, date, type));
                        type.decrementAvailableRoomCount();
                        factory.getRoomTypeRepository().save();
                        message = msg("予約完了。予約番号：" + number, false);
                    }
                } catch (Exception e) {
                    message = msg("入力エラー：" + e.getMessage(), true);
                }
            }

            StringBuilder options = new StringBuilder();
            for (RoomType rt : factory.getRoomTypeRepository().findAll()) {
                if (rt.getAvailableRoomCount() > 0) {
                    options.append("<option value='").append(rt.getTypeName()).append("'>")
                           .append(rt.getTypeName()).append("（").append(rt.getCharge())
                           .append("円 / 空き").append(rt.getAvailableRoomCount()).append("）</option>");
                }
            }

            String html = page("部屋を予約する", message
                + "<form method='post'>"
                + "<p>宿泊日：<input type='date' name='date' required></p>"
                + "<p>客室タイプ：<select name='type'>" + options + "</select></p>"
                + "<p><button type='submit'>予約する</button></p>"
                + "</form>" + backLink());
            sendHtml(exchange, html);
        }
    }

    // ===== チェックイン =====
    static class CheckInHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            RepositoryFactory factory = RepositoryFactory.getInstance();
            String message = "";
            String roomOptions = "";  // 部屋選択のドロップダウン（予約番号確定後に出す）
            String hiddenNumber = "";

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                Map<String, String> p = parseParams(readRequestBody(exchange));
                String action = p.get("action");

                try {
                    if ("search".equals(action)) {
                        // 予約番号から空き部屋一覧を出す
                        int number = Integer.parseInt(p.get("number"));
                        Reservation r = factory.getReservationRepository().findByNumber(number);
                        if (r == null) {
                            message = msg("無効な予約番号です", true);
                        } else {
                            String typeName = r.getRoomType().getTypeName();
                            List<Room> rooms = factory.getRoomRepository().findAllAvailableByType(typeName);
                            if (rooms.isEmpty()) {
                                message = msg(typeName + "の空室がありません", true);
                            } else {
                                StringBuilder sb = new StringBuilder();
                                for (Room room : rooms) {
                                    sb.append("<option value='").append(room.getRoomNumber()).append("'>")
                                      .append(room.getRoomNumber()).append("</option>");
                                }
                                roomOptions = sb.toString();
                                hiddenNumber = String.valueOf(number);
                                message = msg("予約番号 " + number + "（" + typeName + "）：部屋を選んでください", false);
                            }
                        }
                    } else if ("assign".equals(action)) {
                        // 選ばれた部屋にチェックイン
                        String roomNumber = p.get("room");
                        Room room = factory.getRoomRepository().findByRoomNumber(roomNumber);
                        if (room == null || room.isOccupied()) {
                            message = msg("その部屋は割り当てできません", true);
                        } else {
                            room.setRoomToOccupied();
                            factory.getRoomRepository().save();
                            factory.getAccommodationRepository().add(new Accommodation(room));
                            message = msg("チェックイン完了。部屋番号：" + roomNumber, false);
                        }
                    }
                } catch (Exception e) {
                    message = msg("入力エラー：" + e.getMessage(), true);
                }
            }

            String body = message;
            // 予約番号を入力するフォーム
            body += "<form method='post'>"
                  + "<input type='hidden' name='action' value='search'>"
                  + "<p>予約番号：<input type='number' name='number' required> "
                  + "<button type='submit'>予約を確認</button></p>"
                  + "</form>";
            // 部屋選択フォーム（一覧が出たときだけ表示）
            if (!roomOptions.isEmpty()) {
                body += "<form method='post'>"
                      + "<input type='hidden' name='action' value='assign'>"
                      + "<p>部屋番号：<select name='room'>" + roomOptions + "</select> "
                      + "<button type='submit'>チェックイン</button></p>"
                      + "</form>";
            }
            body += backLink();
            sendHtml(exchange, page("チェックインする", body));
        }
    }

    // ===== チェックアウト =====
    static class CheckOutHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            RepositoryFactory factory = RepositoryFactory.getInstance();
            String message = "";

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                Map<String, String> p = parseParams(readRequestBody(exchange));
                try {
                    String roomNumber = p.get("room");
                    Accommodation acc = factory.getAccommodationRepository().findActiveByRoomNumber(roomNumber);
                    if (acc == null) {
                        message = msg("その部屋の宿泊記録がありません", true);
                    } else {
                        int charge = acc.getRoom().getRoomType().getCharge();
                        acc.markAsPaid();
                        factory.getAccommodationRepository().save();
                        acc.getRoom().setRoomToVacant();
                        factory.getRoomRepository().save();
                        message = msg("チェックアウト完了。ご請求金額：" + charge + "円", false);
                    }
                } catch (Exception e) {
                    message = msg("入力エラー：" + e.getMessage(), true);
                }
            }

            String html = page("チェックアウトする", message
                + "<form method='post'>"
                + "<p>部屋番号：<input type='text' name='room' required> "
                + "<button type='submit'>チェックアウト</button></p>"
                + "</form>" + backLink());
            sendHtml(exchange, html);
        }
    }

    // ===== キャンセル =====
    static class CancelHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            RepositoryFactory factory = RepositoryFactory.getInstance();
            String message = "";

            if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
                Map<String, String> p = parseParams(readRequestBody(exchange));
                try {
                    int number = Integer.parseInt(p.get("number"));
                    Reservation r = factory.getReservationRepository().findByNumber(number);
                    if (r == null) {
                        message = msg("無効な予約番号です", true);
                    } else {
                        r.getRoomType().incrementAvailableRoomCount();
                        factory.getRoomTypeRepository().save();
                        factory.getReservationRepository().remove(r);
                        message = msg("予約番号 " + number + " をキャンセルしました", false);
                    }
                } catch (Exception e) {
                    message = msg("入力エラー：" + e.getMessage(), true);
                }
            }

            String html = page("予約をキャンセルする", message
                + "<form method='post'>"
                + "<p>予約番号：<input type='number' name='number' required> "
                + "<button type='submit'>キャンセル</button></p>"
                + "</form>" + backLink());
            sendHtml(exchange, html);
        }
    }

    // ===== 共通部品 =====

    // ページの外枠（高級ホテルのデザイン）
    private static String page(String title, String body) {
        return "<html><head><meta charset='UTF-8'><title>" + title + " | Hotel Washizaki</title>"
             + "<style>"
             + "* { margin:0; padding:0; box-sizing:border-box; }"
             + "body { font-family:'Georgia','Times New Roman',serif; background:#f5f2ec; color:#2b2b2b; line-height:1.7; }"
             + "header { background:#1a2a3a; padding:28px 0; text-align:center; border-bottom:3px solid #c9a86a; }"
             + "header .name { color:#c9a86a; font-size:30px; letter-spacing:8px; font-weight:normal; }"
             + "header .sub { color:#a8b5c2; font-size:12px; letter-spacing:4px; margin-top:6px; }"
             + ".container { max-width:600px; margin:50px auto; padding:0 24px; }"
             + "h1 { font-size:26px; color:#1a2a3a; font-weight:normal; letter-spacing:2px; margin-bottom:8px; }"
             + ".rule { width:50px; height:2px; background:#c9a86a; margin:16px 0 32px; }"
             + "h2 { font-size:18px; color:#1a2a3a; font-weight:normal; margin:24px 0 16px; }"
             + "p { margin:14px 0; }"
             + "input, select { font-family:inherit; font-size:15px; padding:10px 12px; border:1px solid #ccc; background:#fff; width:100%; margin-top:4px; }"
             + "label { font-size:14px; color:#555; letter-spacing:1px; }"
             + "button { font-family:inherit; font-size:15px; letter-spacing:2px; background:#1a2a3a; color:#c9a86a; border:1px solid #c9a86a; padding:12px 32px; cursor:pointer; margin-top:10px; transition:0.3s; }"
             + "button:hover { background:#c9a86a; color:#1a2a3a; }"
             + "a { color:#1a2a3a; }"
             + ".menu { list-style:none; }"
             + ".menu li { margin:0; border-bottom:1px solid #ddd; }"
             + ".menu li:first-child { border-top:1px solid #ddd; }"
             + ".menu a { display:block; padding:18px 8px; text-decoration:none; color:#1a2a3a; font-size:17px; letter-spacing:2px; transition:0.2s; }"
             + ".menu a:hover { background:#eee7da; padding-left:18px; }"
             + ".back { display:inline-block; margin-top:32px; font-size:13px; letter-spacing:1px; text-decoration:none; }"
             + ".card { background:#fff; border:1px solid #e0d9cc; padding:16px 20px; margin:10px 0; }"
             + "</style></head>"
             + "<body>"
             + "<header><div class='name'>HOTEL WASHIZAKI</div>"
             + "<div class='sub'>LUXURY &amp; COMFORT</div></header>"
             + "<div class='container'>"
             + "<h1>" + title + "</h1><div class='rule'></div>"
             + body
             + "</div></body></html>";
    }

    // 結果メッセージ
    private static String msg(String text, boolean error) {
        String color = error ? "#a03030" : "#2a6a3a";
        String bg = error ? "#f5e5e5" : "#e5f0e5";
        return "<div style='background:" + bg + "; color:" + color
             + "; padding:14px 18px; margin-bottom:20px; border-left:3px solid " + color + ";'>"
             + text + "</div>";
    }

    // トップへ戻るリンク
    private static String backLink() {
        return "<a class='back' href='/'>← メニューに戻る</a>";
    }

    private static void sendHtml(HttpExchange exchange, String html) throws IOException {
        byte[] bytes = html.getBytes("UTF-8");
        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String readRequestBody(HttpExchange exchange) throws IOException {
        java.io.InputStream is = exchange.getRequestBody();
        java.io.ByteArrayOutputStream buffer = new java.io.ByteArrayOutputStream();
        int b;
        while ((b = is.read()) != -1) buffer.write(b);
        return buffer.toString("UTF-8");
    }

    private static Map<String, String> parseParams(String query) throws IOException {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.isEmpty()) return params;
        for (String pair : query.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(URLDecoder.decode(kv[0], "UTF-8"), URLDecoder.decode(kv[1], "UTF-8"));
            }
        }
        return params;
    }
}
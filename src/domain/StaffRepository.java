package domain;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * スタッフ（受付係）の社員番号を管理するリポジトリ。
 * data/staff.txt から社員番号のリストを読み込む。
 */
public class StaffRepository {

    private static final String FILE_PATH = "data/staff.txt";

    private List<String> employeeNumbers = new ArrayList<>();

    public StaffRepository() {
        load();
    }

    private void load() {
        employeeNumbers.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    employeeNumbers.add(line.trim());
                }
            }
        } catch (IOException e) {
            System.out.println("staff.txt が読み込めませんでした");
        }
    }

    // 入力された社員番号が登録されているか確認する
    public boolean isValid(String employeeNumber) {
        return employeeNumbers.contains(employeeNumber);
    }
}
package cn.henry.study.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * description:
 *
 * @author Hlingoes
 * @date 2020/5/22 23:45
 */
public class PracticeTest {

    @Test
    public void testList() {
        int size = 50;
        List<String> list = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            list.add("test_" + i);
        }
        String insertSql = "insert into test (id) values ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if ((i + 1) % 5 == 0 || (i + 1) == size) {
                sb.append("('" + list.get(i) + "')");
                System.out.println(insertSql + sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append("('" + list.get(i) + "'),");
            }
        }
    }

}

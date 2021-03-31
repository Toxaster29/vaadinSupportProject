package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.order.OnlineOrder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class OnlineOrderDaoImpl implements OnlineOrderDao {

    private static String subsContextUrl = "jdbc:postgresql://localhost:5432/subscontext";
    private static String user = "postgres";
    private static String passwd = "123";

    @Override
    public List<OnlineOrder> getAllOnlineOrders() {
        List<OnlineOrder> orders = new ArrayList<>();
        String sql = "select order_id , hid, created_date from orders where state = 2 and shop_state = 0 ";
        try (Connection con = DriverManager.getConnection(subsContextUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                orders.add(new OnlineOrder(rs.getInt(1), rs.getString(2), rs.getTimestamp(3).toLocalDateTime()));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return orders;
    }
}

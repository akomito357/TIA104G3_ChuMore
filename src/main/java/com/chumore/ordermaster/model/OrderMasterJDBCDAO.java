package com.chumore.ordermaster.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OrderMasterJDBCDAO implements OrderMasterDAO_interface{
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/chumore?serverTimezone=Asia/Taipei";
	String userid = "root";
	String password = "123456";
	
	private static final String INSERT_STMT = "INSERT INTO order_master ("
			+ "	order_table_id,"
			+ "	rest_id,"
			+ "	member_id,"
			+ "	order_status,"
			+ "	subtotal_price,"
			+ "	total_price,"
			+ "	served_datetime,"
			+ "	point_earned,"
			+ "	point_used,"
			+ "	checkout_datetime) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	private static final String DELETE_STMT = "DELETE FROM order_master where order_id = ?";
	private static final String UPDATE_STMT = "UPDATE order_master SET " 
			+ "member_id = ?, "
			+ "order_status = ?, " 
			+ "subtotal_price = ?, "
			+ "total_price = ?, "
			+ "point_earned = ?, "
			+ "point_used = ?, "
			+ "checkout_datetime = ? "
			+ "WHERE order_id = ?";
	private static final String GET_ALL_STMT = "select * from order_master";
	private static final String GET_ONE_STMT = "select * from order_master where order_id = ?";
	
	public OrderMasterVO getById(Integer orderId) {
		OrderMasterVO order = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName(driver);
			
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(GET_ONE_STMT);
			pstmt.setInt(1, orderId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				order = new OrderMasterVO();
				order.setOrderId(rs.getInt("order_id"));
				order.setOrderTableId(rs.getInt("order_table_id"));
				order.setRestId(rs.getInt("rest_id"));
				order.setMemberId(rs.getInt("member_id"));
				order.setOrderStatus(rs.getInt("order_status"));
				order.setSubtotalPrice(rs.getDouble("subtotal_price"));
				order.setTotalPrice(rs.getDouble("total_price"));
				order.setServedDatetime(rs.getTimestamp("served_datetime"));
				order.setPointEarned(rs.getInt("point_earned"));
				order.setPointUsed(rs.getInt("point_used"));
				order.setCheckoutDatetime(rs.getTimestamp("checkout_datetime"));
			}
			
//			System.out.println(order);
			
		} catch (SQLException e){
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				con.close();
			}catch(SQLException e){
				e.printStackTrace();
			}
			
		}
		
		return order;
	}
	
	public List<OrderMasterVO> getAll(){
		List<OrderMasterVO> list = new ArrayList<>();
		OrderMasterVO order = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				order = new OrderMasterVO();
				order.setOrderId(rs.getInt("order_id"));
				order.setOrderTableId(rs.getInt("order_table_id"));
				order.setRestId(rs.getInt("rest_id"));
				order.setMemberId(rs.getInt("member_id"));
				order.setOrderStatus(rs.getInt("order_status"));
				order.setSubtotalPrice(rs.getDouble("subtotal_price"));
				order.setTotalPrice(rs.getDouble("total_price"));
				order.setServedDatetime(rs.getTimestamp("served_datetime"));
				order.setPointEarned(rs.getInt("point_earned"));
				order.setPointUsed(rs.getInt("point_used"));
				order.setCheckoutDatetime(rs.getTimestamp("checkout_datetime"));
				list.add(order);
			}
			
//			for (OrderMasterVO orderItem : list) {
//				System.out.println(orderItem);
//			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch(ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	public void insert(OrderMasterVO orderMasterVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(INSERT_STMT);
			
			pstmt.setInt(1, orderMasterVO.getOrderTableId());
			pstmt.setInt(2, orderMasterVO.getRestId());
			pstmt.setInt(3, orderMasterVO.getMemberId());
			pstmt.setInt(4,  orderMasterVO.getOrderStatus());
			pstmt.setDouble(5, orderMasterVO.getSubtotalPrice());
			pstmt.setDouble(6, orderMasterVO.getTotalPrice());
			pstmt.setTimestamp(7, orderMasterVO.getServedDatetime());
			pstmt.setInt(8, orderMasterVO.getPointEarned());
			pstmt.setInt(9, orderMasterVO.getPointUsed());
			pstmt.setTimestamp(10, orderMasterVO.getCheckoutDatetime());
			
			pstmt.executeUpdate();
			
//			System.out.println("successfully insert: " + orderMasterVO);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				con.close();				
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void update(OrderMasterVO orderMasterVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(UPDATE_STMT);
			
			pstmt.setInt(1, orderMasterVO.getMemberId());
			pstmt.setInt(2,  orderMasterVO.getOrderStatus());
			pstmt.setDouble(3, orderMasterVO.getSubtotalPrice());
			pstmt.setDouble(4, orderMasterVO.getTotalPrice());
			pstmt.setInt(5, orderMasterVO.getPointEarned());
			pstmt.setInt(6, orderMasterVO.getPointUsed());
			pstmt.setTimestamp(7, orderMasterVO.getCheckoutDatetime());
			pstmt.setInt(8, orderMasterVO.getOrderId());
			
			pstmt.executeUpdate();
			
//			System.out.println("successfully update: " + orderMasterVO);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				con.close();				
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}	
	}	

	public void delete(Integer orderId) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, userid, password);
			pstmt = con.prepareStatement(DELETE_STMT);
			pstmt.setInt(1, orderId);
			
			pstmt.executeUpdate();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				pstmt.close();				
			} catch (SQLException e) {
				e.printStackTrace();
			}
			
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

}

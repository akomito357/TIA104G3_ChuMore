package com.chumore.cuisinetype.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CuisineTypeJDBCDAO implements CuisineTypeDAO{
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/chumore?serverTimezone=Asia/Taipei";
	String user = "root";
	String password = "123456";
	
//	private String 
	
	private static final String GET_ALL_STMT = "SELECT * FROM cuisine_type ORDER BY cuisine_type_id";
	private static final String GET_ByID_STMT = "SELECT * FROM cuisine_type WHERE cuisine_type_id = ?";
	private static final String GET_By_NAME_STMT = "SELECT * FROM cuisine_type WHERE cuisine_descr LIKE ?";
	private static final String INSERT_STMT = "INSERT INTO cuisine_type(cuisine_descr) VALUES (?)";
	private static final String UPDATE_STMT = "UPDATE cuisine_type SET cuisine_descr = ? WHERE cuisine_type_id = ?";
	
	public List<CuisineTypeVO> getAll() {
		CuisineTypeVO cuisineTypeVO = null;
		List<CuisineTypeVO> list = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				cuisineTypeVO = new CuisineTypeVO();
				cuisineTypeVO.setCuisineTypeId(rs.getInt("cuisine_type_id"));
				cuisineTypeVO.setCuisineDescr(rs.getString("cuisine_descr"));
				list.add(cuisineTypeVO);
			}
			
			for (CuisineTypeVO typeVO : list) {
				System.out.println(typeVO);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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

	public CuisineTypeVO getById(Integer cuisineTypeId) {
		CuisineTypeVO cuisineTypeVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(GET_ByID_STMT);
			pstmt.setInt(1, cuisineTypeId);
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				cuisineTypeVO = new CuisineTypeVO();
				cuisineTypeVO.setCuisineTypeId(rs.getInt("cuisine_type_id"));
				cuisineTypeVO.setCuisineDescr(rs.getString("cuisine_descr"));
			}
			
			System.out.println(cuisineTypeVO);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
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
		
		return cuisineTypeVO;
	}

	public List<CuisineTypeVO> getByName(String cuisineDescr) {
		CuisineTypeVO cuisineTypeVO = null;
		List<CuisineTypeVO> list = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(GET_By_NAME_STMT);
			pstmt.setString(1, "%" + cuisineDescr + "%");
			
			rs = pstmt.executeQuery();
			
			while (rs.next()) {
				cuisineTypeVO = new CuisineTypeVO();
				cuisineTypeVO.setCuisineTypeId(rs.getInt("cuisine_type_id"));
				cuisineTypeVO.setCuisineDescr(rs.getString("cuisine_descr"));
				list.add(cuisineTypeVO);
			}
			
//			System.out.println("1:" + list);
			
			for (CuisineTypeVO typeVO : list) {
				System.out.println(typeVO);
			}
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException ne) {
			System.out.println("查無結果！");
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
				System.out.println("查無結果因此無法關閉ResultSet");
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

	public int insert(CuisineTypeVO cuisineTypeVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(INSERT_STMT);
			pstmt.setString(1, cuisineTypeVO.getCuisineDescr());
			
			pstmt.executeUpdate();
			return 1;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
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

	public int update(CuisineTypeVO cuisineTypeVO) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(UPDATE_STMT);
			pstmt.setString(1, cuisineTypeVO.getCuisineDescr());
			pstmt.setInt(2, cuisineTypeVO.getCuisineTypeId());
			
			pstmt.executeUpdate();
			return 1;
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return -1;
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
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

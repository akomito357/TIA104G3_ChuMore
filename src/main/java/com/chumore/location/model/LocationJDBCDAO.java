package com.chumore.location.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LocationJDBCDAO implements LocationDAO{
	
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/chumore?serverTimezone=Asia/Taipei";
	String user = "root";
	String password = "123456";
	
	private static final String GET_ALL_STMT = "SELECT * FROM location ORDER BY locationId";
	private static final String GET_ByID_STMT = "SELECT * FROM location WHERE locationId = ?";
	private static final String GET_ByCity_STMT = "SELECT * FROM location WHERE city LIKE ?";
	private static final String GET_Dist_ByCity_STMT = "SELECT dist FROM location WHERE city LIKE ?";
	
	
	
	public List<LocationVO> getAll() {
		LocationVO locationVO = null;
		List<LocationVO> list = new ArrayList<>();
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(GET_ALL_STMT);
			pstmt.executeQuery();
			
			while (rs.next()) {
				locationVO = new LocationVO();
				locationVO.setLocationId(rs.getInt("location_id"));
				locationVO.setCity(rs.getString("city"));
				locationVO.setDistrict(rs.getString("district"));
				list.add(locationVO);
			}
			
			for (LocationVO location : list) {
				System.out.println(location);
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
	

	public LocationVO getById(Integer locationId) {
		LocationVO locationVO = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(GET_ByID_STMT);
			pstmt.setInt(1, locationId);
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				locationVO = new LocationVO();
				locationVO.setLocationId(rs.getInt("location_id"));
				locationVO.setCity(rs.getString("city"));
				locationVO.setDistrict(rs.getString("district"));
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
		
		return locationVO;
	}

	public List<LocationVO> getByCity(String city) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		LocationVO locationVO = null;
		List<LocationVO> list = new ArrayList<>();
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(GET_ByCity_STMT);
			pstmt.setString(1, "%" + city + "%");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				locationVO = new LocationVO();
				locationVO.setLocationId(rs.getInt("locationId"));
				locationVO.setCity(rs.getString("city"));
				locationVO.setDistrict(rs.getString("district"));
				list.add(locationVO);
			}
			
			for (LocationVO location : list) {
				System.out.println(location);
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

	public List<LocationVO> getDistByCity(String city) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		LocationVO locationVO = null;
		List<LocationVO> list = new ArrayList<>();
		
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			pstmt = con.prepareStatement(GET_Dist_ByCity_STMT);
			pstmt.setString(0, "%" + city + "%");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				locationVO = new LocationVO();
				locationVO.setLocationId(rs.getInt("locationId"));
				locationVO.setCity(rs.getString("city"));
				locationVO.setDistrict(rs.getString("district"));
				list.add(locationVO);
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

}

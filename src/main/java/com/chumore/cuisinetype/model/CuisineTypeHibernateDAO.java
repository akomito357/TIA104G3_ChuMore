package com.chumore.cuisinetype.model;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.chumore.util.HibernateUtil;

public class CuisineTypeHibernateDAO implements CuisineTypeDAO{
	
	private SessionFactory sessionFactory;
	
	public CuisineTypeHibernateDAO() {
		sessionFactory = HibernateUtil.getSessionFactory();
	}
	
	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}
	
	@Override
	public List<CuisineTypeVO> getAll() {
		Session session = getSession();
		List<CuisineTypeVO> list = null;
		try {
			session.beginTransaction();
			list = session.createQuery("from CuisineTypeVO", CuisineTypeVO.class).list();
			session.getTransaction().commit();
		} catch(Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		}finally {
			HibernateUtil.shutdown();
		}
		return list;
		
	}

	@Override
	public CuisineTypeVO getById(Integer cuisineTypeId) {
		Session session = getSession();
		CuisineTypeVO type = null;
		
		try {
			session.beginTransaction();
			type = session.get(CuisineTypeVO.class, cuisineTypeId);
			session.getTransaction().commit();
		}catch(Exception e){
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			HibernateUtil.shutdown();
		}
		
		return type;		
	}

	@Override
	public List<CuisineTypeVO> getByName(String cuisineDescr) {
		Session session = getSession();
		List<CuisineTypeVO> list = null;
		try {			
			session.beginTransaction();
			list = session.createQuery("from CuisineTypeVO where cuisineDescr like :cuisineDescr", CuisineTypeVO.class)
				.setParameter("cuisineDescr", "%" + cuisineDescr + "%")
				.list();
			session.getTransaction().commit();
		}catch(Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
		} finally {
			HibernateUtil.shutdown();
		}
		
		return list;
	}

	@Override
	public int insert(CuisineTypeVO cuisineTypeVO) {
		Session session = getSession();
		
		try {
			session.beginTransaction();
			int pk = (Integer)session.save(cuisineTypeVO);
			session.getTransaction().commit();
			return pk;
			// 得到回傳的主鍵值
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			return -1;
		}finally {
			HibernateUtil.shutdown();
		}
	}

	@Override
	public int update(CuisineTypeVO cuisineTypeVO) {
		Session session = getSession();
		try {
			session.beginTransaction();
			session.update(cuisineTypeVO);
			session.getTransaction().commit();
			return 1;
		} catch (Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			return -1;
		} finally {
			HibernateUtil.shutdown();
		}
	}
	
	public int delete(Integer cuisineTypeId) {
		Session session = getSession();
		try {
			session.beginTransaction();
			CuisineTypeVO type = session.get(CuisineTypeVO.class, cuisineTypeId);
			
			if (type != null) {
				session.delete(type);
				session.getTransaction().commit();
			}
			return 1;
			
		} catch(Exception e) {
			e.printStackTrace();
			session.getTransaction().rollback();
			return -1;
		} finally {
			HibernateUtil.shutdown();
		}
	}

}

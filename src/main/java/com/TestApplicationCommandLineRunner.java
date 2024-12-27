package com;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.chumore.cuisinetype.model.CuisineTypeRepository;
import com.chumore.favrest.model.FavRestRepository;
import com.chumore.location.model.LocationRepository;
import com.chumore.ordermaster.model.OrderMasterRepository;
import com.chumore.rest.compositequery.RestCompositeQuery;
import com.chumore.rest.model.RestRepository;
import com.chumore.rest.model.RestVO;

@SpringBootApplication
public class TestApplicationCommandLineRunner implements CommandLineRunner{

	@Autowired
	RestRepository restRepository;
	
	@Autowired
	LocationRepository locationRepos;
	
	@Autowired
	CuisineTypeRepository cuisineTypeRepos;

	@Autowired
	FavRestRepository favRepos;
	
	@Autowired
	OrderMasterRepository orderRepos;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public static void main(String[] args) {
		SpringApplication.run(TestApplicationCommandLineRunner.class);
	}
	
	public void run(String...args) throws Exception{
//		Optional<RestVO> optional = restRepository.findById(2001);
//		RestVO rest = optional.get();
//		System.out.println(rest);
		
//		Optional<OrderMasterVO> optional2 = orderRepos.findById(1);
//		OrderMasterVO type = optional2.get();
//		System.out.println(type);
		
//		CuisineTypeVO type2 = new CuisineTypeVO();
//		type2.setCuisineTypeId(11);
//		type2.setCuisineDescr("手搖");
//		cuisineTypeRepos.save(type2);
		

//		System.out.println(location.getDistrict());
		
//		Map<String, String[]> map = new TreeMap<>();
//		map.put("cuisineTypeId", new String[] {"4"});
//		
//		List<RestVO> list = RestCompositeQuery.getAllC(map, sessionFactory.openSession());
//		for(RestVO rest : list) {
//			System.out.println(rest);
//		}
		
		
	}
	
}

package com.chumore.usepoints.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsePointsServiceImpl implements UsePointsService{

	@Autowired
	private UsePointsRepository usePointsRepository;
	
	@Override
	public UsePointsVO addUsePoints(UsePointsVO usePoints) {
		return usePointsRepository.save(usePoints);
	}

	@Override
	public UsePointsVO updateUsePoints(UsePointsVO usePoints) {
		return usePointsRepository.save(usePoints);
	}

}

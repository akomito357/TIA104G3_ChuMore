package com.chumore.usepoints.model;

import org.springframework.stereotype.Service;

@Service
public interface UsePointsService {

	UsePointsVO addUsePoints(UsePointsVO usePoints);
	UsePointsVO updateUsePoints(UsePointsVO usePoints);
	
}

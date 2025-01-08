package com.chumore.discpts.model;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chumore.discpts.dto.DiscPtsSummaryDTO;
import java.util.List;


@Service
public class DiscPtsService {
    
    @Autowired
    private DiscPtsRepository discPtsRepository;
    
    public List<DiscPtsSummaryDTO> getPointsSummary(Integer memberId) {
        return discPtsRepository.findPointsSummaryByMember(memberId);
    }
}
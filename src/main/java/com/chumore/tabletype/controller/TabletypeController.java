package com.chumore.tabletype.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.chumore.tabletype.model.TableTypeService;
import com.chumore.tabletype.model.TableTypeVO;

@CrossOrigin
@Controller
@RequestMapping("tableType")
public class TabletypeController {

	@Autowired
	TableTypeService tableTypeSvc;

	@GetMapping("getTableType")
	public ResponseEntity<?> getTableTypeByRestId(@RequestParam Integer restId) {
		List<TableTypeVO> list = tableTypeSvc.getAllTableTypeById(restId);
		return ResponseEntity.ok(list);
	}

}

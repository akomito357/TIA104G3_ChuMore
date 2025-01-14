package com.chumore.ordermaster.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.chumore.discpts.model.DiscPtsService;
import com.chumore.discpts.model.DiscPtsVO;
import com.chumore.exception.DataMismatchException;
import com.chumore.exception.PointsOverUsedException;
import com.chumore.exception.ResourceNotFoundException;
import com.chumore.member.model.MemberService;
import com.chumore.member.model.MemberVO;
import com.chumore.orderitem.dto.OrderItemForOrderDto;
import com.chumore.orderitem.model.OrderItemVO;
import com.chumore.orderitem.model.OrderItem_Service;
import com.chumore.orderlineitem.dto.OrderLineItemForOrderDto;
import com.chumore.orderlineitem.model.OrderLineItemVO;
import com.chumore.orderlineitem.model.OrderLineItem_Service;
import com.chumore.ordermaster.compositequery.OrderMasterCompositeQuery;
import com.chumore.ordermaster.dto.RestDiningDto;
import com.chumore.product.model.ProductVO;
import com.chumore.product.model.Product_Service;
import com.chumore.review.model.ReviewVO;
import com.chumore.usepoints.model.UsePointsService;
import com.chumore.usepoints.model.UsePointsVO;

@Transactional
@Service("OrderMasterService")
public class OrderMasterServiceImpl implements OrderMasterService {
	
	private BigDecimal earnRatio = new BigDecimal(100); // 消費X元得1點
	private BigDecimal discountRatio = new BigDecimal(10); // 1點抵X元

	@Autowired
	OrderMasterRepository repository;
	
	@Autowired
	OrderItem_Service orderItemSvc;
	
	@Autowired
	OrderLineItem_Service orderLineItemSvc;
	
	@Autowired
	Product_Service productSvc;
	
	@Autowired
	MemberService memberSvc;
	
	@Autowired
	DiscPtsService discPtsSvc;

	@Autowired
	UsePointsService usePtSvc;
	
	@Autowired
	SessionFactory factory;

	@Override
	@Transactional(readOnly = true)
	public OrderMasterVO getOneById(Integer orderId) {
		Optional<OrderMasterVO> optional = repository.findById(orderId);
		OrderMasterVO order = optional.orElse(null);
		
		if (order == null) {
			throw new ResourceNotFoundException("No order found for orderId: " + orderId);
		}
		
		return order;
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderMasterVO> getAllOrder() {
		return repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderMasterVO> getAllOrder(Map<String, String[]> map) {
		return OrderMasterCompositeQuery.getAllC(map, factory.openSession());
	}

	@Override
	public OrderMasterVO addOrderMaster(OrderMasterVO orderMaster) {
		return repository.save(orderMaster);
	}

	@Override
	public OrderMasterVO updateOrderMaster(OrderMasterVO orderMaster) {
		return repository.save(orderMaster);
	}

	@Override
	public Integer deleteOrderMasterById(Integer orderId) {
		if (repository.existsById(orderId)) {
			repository.deleteByOrderId(orderId);
			return 1;
		} else {
			return -1;
		}
	}

	@Transactional(readOnly = true)
	public Set<UsePointsVO> getUsePointsByOrderId(Integer orderId) {
		return getOneById(orderId).getUsePoints();
	}

//	public Set<OrderItemVO> getOrderItemByOrderId(Integer orderId){
//		return getOneById(orderId).getOrderItems();
//	}

	@Transactional(readOnly = true)
	public Set<ReviewVO> getReviewByOrderId(Integer orderId) {
		return getOneById(orderId).getReviews();
	}

	@Override
	@Transactional(readOnly = true)
	public List<OrderMasterVO> getByMemberId(Integer memberId) {
		return repository.getByMemberId(memberId);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<OrderMasterVO> findByMemberId(Integer memberId, Pageable pageable) {
		return repository.findByMemberId(memberId, pageable);
	}
	
	@Transactional(readOnly = true)
	public Page<RestDiningDto>findOrderByRestId(Integer restId, Pageable pageable){
		return repository.findOrderByRestId(restId, pageable);
	}
	
	
	public void submitOrder(OrderItemForOrderDto item, HttpSession session) {
		String memo = item.getMemo();
//		System.out.println(memo);
		
		// 建立新點餐
		OrderItemVO orderItem = new OrderItemVO();
		orderItem.setOrderId((Integer)session.getAttribute("orderId"));
		orderItem.setMemo(memo);
		orderItem.setCreatedDatetime(new Timestamp(System.currentTimeMillis()));
		orderItem.setUpdatedDatetime(new Timestamp(System.currentTimeMillis()));
		orderItem = orderItemSvc.addOrUpdateOrderItem(orderItem);
		
		// 獲取點餐細項並將資料放入
		Integer orderItemId = orderItem.getOrderItemId();
		List<OrderLineItemForOrderDto> orders = item.getOrder();
		BigDecimal thisSubTotalPrice = new BigDecimal("0");
		
		for (OrderLineItemForOrderDto lineDto : orders) {
			Integer productId = lineDto.getProductId();
			Integer count = lineDto.getCount();
			BigDecimal priceForOne = lineDto.getOrigPriceForOne();
			ProductVO product = productSvc.getProductById(productId);
			OrderMasterVO orderMaster = getOneById((Integer)session.getAttribute("orderId"));
			
			// 如果送來餐點資料的ID與餐廳ID和價格不符，拋出exception
			if (!product.getRestId().equals(orderMaster.getRestId())) {
				throw new DataMismatchException("Rest Id mismatch: in product which id = " + productId.toString() + "; The received Rest Id is " + product.getRestId().toString());
			} else if(product.getProductPrice().compareTo(priceForOne) != 0) { // 不同時
				throw new DataMismatchException("Product price mismatch: in product which id = " + productId.toString());
			}

			OrderLineItemVO orderLineItem = new OrderLineItemVO();
			orderLineItem.setOrderItemId(orderItemId);
			orderLineItem.setProduct(product);			
			orderLineItem.setQuantity(count);
			orderLineItem.setPrice(priceForOne);
			
//			priceForOne.multiply(BigDecimal.valueOf(count));
			thisSubTotalPrice = thisSubTotalPrice.add(priceForOne.multiply(BigDecimal.valueOf(count)));
			
			System.out.println(orderLineItem);
			orderLineItemSvc.addOrderLineItem(orderLineItem);
		}
		
		// 更新orderMaster的小計
		OrderMasterVO order = getOneById((Integer)session.getAttribute("orderId"));
		BigDecimal subtotalPrice = order.getSubtotalPrice();
		subtotalPrice = subtotalPrice.add(thisSubTotalPrice);
		order.setSubtotalPrice(subtotalPrice);
		updateOrderMaster(order);
		
		
	}

	public Page<Map<String, Object>> findOrderByRestId(Integer restId,LocalDateTime startDatetime, LocalDateTime endDatetime, Integer orderTableId, String memberName, Pageable pageable){
		return repository.findOrderByRestId(restId, startDatetime, endDatetime, orderTableId ,memberName, pageable);
	}
	
	@Override
	public BigDecimal calcDiscount(Integer usePoints) {
		BigDecimal discounts = discountRatio.multiply(BigDecimal.valueOf(usePoints));
		return discounts;
	}
	
	@Override
	@Transactional(readOnly = true)
	public BigDecimal calcTotalPrice(Integer orderId, BigDecimal discounts) {
		OrderMasterVO orderMaster = getOneById(orderId);
		BigDecimal subtotalPrice = orderMaster.getSubtotalPrice();
		BigDecimal totalPrice = subtotalPrice.subtract(discounts);
		
		if (totalPrice.compareTo(BigDecimal.ZERO) == -1) {
			// totalPrice < 0
			throw new PointsOverUsedException("discount(" + discounts + ") exceed total price(" + totalPrice + ")");
		}
		
		return totalPrice;
	}
	
	public Integer calcEarnPoints(BigDecimal totalPrice) {
		Integer earnedPoints = null;
		try {
			System.out.println(totalPrice);
			System.out.println(totalPrice.divide(earnRatio, 0, RoundingMode.DOWN).intValue());
			earnedPoints = totalPrice.divide(earnRatio, RoundingMode.DOWN).intValue();
		} catch (ArithmeticException ae) {
			System.out.println("ArithmeticException!");
			ae.printStackTrace();
		}
		
		return earnedPoints;
	}
	
	
	@Transactional
	public OrderMasterVO checkout(Integer memberId, Integer orderId, Integer pointUsed) {
		BigDecimal discounts = calcDiscount(pointUsed);
		BigDecimal totalPrice = calcTotalPrice(orderId, discounts);
		Integer earnedPoints = calcEarnPoints(totalPrice);
		MemberVO member = memberSvc.getOneMember(memberId).orElse(null);
		
		OrderMasterVO orderMaster = getOneById(orderId);
		orderMaster.setMember(member);
		orderMaster.setPointUsed(pointUsed);
		orderMaster.setTotalPrice(totalPrice);
		orderMaster.setOrderStatus(1); // 已結帳
		orderMaster.setPointEarned(earnedPoints);
		orderMaster.setCheckoutDatetime(LocalDateTime.now());
		
		System.out.println(orderMaster);

		// 處理扣除與新增點數
		if (member != null) {
			discPtsSvc.deductPoints(memberId, orderMaster, pointUsed);
			discPtsSvc.gainPoints(memberId, orderMaster, pointUsed, earnedPoints);
		}
		
//		List<DiscPtsVO> availPointList= discPtsSvc.findAvailablePointsListByMemberAndRest(
//				memberId, orderMaster.getRestId());
//		Integer remainPtsToCalc = pointUsed;
//		
//		for (DiscPtsVO discPts : availPointList) {
////			System.out.println("pts: " + discPts);
//			Integer thisPtsQty = discPts.getDiscPtsQty();
//			
//			UsePointsVO usePoints = new UsePointsVO();
//			usePoints.setDiscPts(discPts);
//			usePoints.setOrderMaster(orderMaster);
//			
//			if (remainPtsToCalc - thisPtsQty >= 0) {
//				discPts.setDiscPtsQty(0); // 這筆被扣完了
//				usePoints.setUsedPointsQuantity(thisPtsQty);
//				remainPtsToCalc = remainPtsToCalc - thisPtsQty;
//				
//			} else if (remainPtsToCalc - thisPtsQty < 0) {
//				discPts.setDiscPtsQty(thisPtsQty - remainPtsToCalc);
//				usePoints.setUsedPointsQuantity(remainPtsToCalc);
//				remainPtsToCalc = 0;
//			}
//			
//			discPtsSvc.update(discPts);
//			usePtSvc.addUsePoints(usePoints);
//			System.out.println(discPts);
//			System.out.println(usePoints);
//		}
		
		// 處理新增點數
		// 先看DB中有沒有符合的到期日與會員ID、餐廳ID的pts
		
//		LocalDate today = LocalDate.now();
//		int currentMonth = today.getMonth().getValue();
//		int currentYear = today.getYear();
//		LocalDate expectExpirDate = null;
//		
//		System.out.println("today: " + today);
//		System.out.println("currentYear: " + currentYear);
//		System.out.println("currentMonth: " + currentMonth);
//		
//		if (currentMonth <= 6) {
//			expectExpirDate = LocalDate.of(currentYear, 12, 31);
//		} else if (currentMonth > 6) {
//			expectExpirDate = LocalDate.of(currentYear + 1, 6, 30);
//		}
//		
//		System.out.println("expectExpirDate: " + expectExpirDate);
//		
//		List<DiscPtsVO> recentPoints = discPtsSvc.findRecentPointsByMemberAndRest(memberId, orderMaster.getRestId(), expectExpirDate);
//		if (recentPoints.size() != 0) {
//			DiscPtsVO firstRecentPoints = recentPoints.get(0);
//			Integer firstRecentPointsQty = firstRecentPoints.getDiscPtsQty();
//			firstRecentPoints.setDiscPtsQty(firstRecentPointsQty + earnedPoints);
//			discPtsSvc.update(firstRecentPoints);
//		} else {
//			DiscPtsVO discPts = new DiscPtsVO();
//			discPts.setMember(member);
//			discPts.setRest(orderMaster.getRest());
//			discPts.setDiscPtsQty(earnedPoints);
//			discPts.setExpDate(expectExpirDate);
//			discPtsSvc.add(discPts);
//		}
//		
		
		
//		throw new RuntimeException("break");
		return orderMaster;
	}

}

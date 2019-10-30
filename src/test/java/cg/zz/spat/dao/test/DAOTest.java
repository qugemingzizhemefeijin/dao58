package cg.zz.spat.dao.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cg.zz.spat.dao.test.entity.Order;
import cg.zz.spat.dao.test.utils.H2DatabaseTestCase;

public class DAOTest extends H2DatabaseTestCase {
	
	protected static final Logger LOGGER = LoggerFactory.getLogger(DAOTest.class);
	
	@Test
	@DisplayName("测试ID查询表数据")
	public void getTest() throws Exception {
		long id = 300;
		
		Order order = daoHelpers.sql.get(Order.class, id);
		assertNotNull(order);
		assertEquals(order.getId().longValue(), id);
		LOGGER.info("order = " + order);
	}

	@Test
	@DisplayName("测试批量ID查询表数据")
	public void getListByIdsTest() throws Exception {
		List<Order> list = daoHelpers.sql.getListByIDS(Order.class, new Long[] {300L , 302L});
		assertNotNull(list);
		assertTrue(list.size() > 0);
		
		LOGGER.info("orderList size = " + list.size());
		LOGGER.info("orderList = " + list);
	}

	@Test
	@DisplayName("测试自定义列表查询表数据")
	public void getListByCustomTest() throws Exception {
		List<Order> list = daoHelpers.sql.getListByCustom(Order.class, "id,name", "id<10", "price desc");
		assertNotNull(list);
		assertTrue(list.size() > 0);
		
		LOGGER.info("orderList size = " + list.size());
		LOGGER.info("orderList = " + list);
	}
	
	@Test
	@DisplayName("测试分页查询表数据")
	public void getListByPageTest() throws Exception {
		List<Order> list = daoHelpers.sql.getListByPage(Order.class, "id>100", "id,price,name", 1, 10, "id asc");
		

		assertNotNull(list);
		assertTrue(list.size() > 0);
		
		LOGGER.info("orderList size = " + list.size());
		LOGGER.info("orderList = " + list);
	}

	@Test
	@DisplayName("测试插入表数据")
	public void insertTest() throws Exception {
		String name = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		
		Order order = new Order();
		order.setCreate_time(new Date());
		order.setCreator("chengang");
		order.setName(name);
		order.setPrice("199.00");
		order.setStatus(1);
		
		Long id = (Long)daoHelpers.sql.insert(order);
		assertNotNull(id);
		assertTrue(id.longValue() > 0);
		
		Order newOrder = daoHelpers.sql.get(Order.class, id);
		LOGGER.info("order = " + newOrder);
		
		assertNotNull(newOrder);
		assertEquals(name, newOrder.getName());
	}

	@Test
	@DisplayName("测试修改表数据")
	public void updateTest() throws Exception {
		String name = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		long id = 300;
		
		Order order = daoHelpers.sql.get(Order.class, id);
		assertNotNull(order);
		
		order.setName(name);
		daoHelpers.sql.updateEntity(order);
		
		Order newOrder = daoHelpers.sql.get(Order.class, id);
		assertNotNull(newOrder);
		
		assertEquals(order.getName(), newOrder.getName());
	}

	@Test
	@DisplayName("测试通过ID和updateStatement修改表数据")
	public void updateByIDTest() throws Exception {
		long id = 408;
		
		Order order = daoHelpers.sql.get(Order.class, id);
		assertNotNull(order);
		
		String name = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		String price = "123.00";
		
		assertNotEquals(name, order.getName());
		assertNotEquals(price, order.getPrice());
		
		String updateStatement = "price='"+price+"',name='"+name+"'";
		
		daoHelpers.sql.updateByID(Order.class, updateStatement, id);
		
		Order newOrder = daoHelpers.sql.get(Order.class, id);
		assertNotNull(newOrder);
		
		assertEquals(name, newOrder.getName());
		assertEquals(price, newOrder.getPrice());
	}

	@Test
	@DisplayName("测试通过ID删除表数据")
	public void deleteByIDTest() throws Exception {
		long id = 1;
		
		Order order = daoHelpers.sql.get(Order.class, id);
		assertNotNull(order);
		
		daoHelpers.sql.deleteByID(Order.class, id);
		
		Order newOrder = daoHelpers.sql.get(Order.class, id);
		assertNull(newOrder);
	}

	@Test
	@DisplayName("测试通过ID数组删除表数据")
	public void deleteByIDSTest() throws Exception {
		Long[] ids = new Long[] {100L , 101L};
		
		for(Long id : ids) {
			Order order = daoHelpers.sql.get(Order.class, id);
			assertNotNull(order);
		}
		
		daoHelpers.sql.deleteByIDS(Order.class, ids);
		
		for(Long id : ids) {
			Order order = daoHelpers.sql.get(Order.class, id);
			assertNull(order);
		}
	}

	@Test
	@DisplayName("测试通过任意条件删除表数据")
	public void deleteByCustomTest() throws Exception {
		int count = daoHelpers.sql.getCount(Order.class, "id<100");
		assertTrue(count > 0);
		
		daoHelpers.sql.deleteByCustom(Order.class, "id<100");
		
		count = daoHelpers.sql.getCount(Order.class, "id<100");
		assertFalse(count > 0);
	}

	@Test
	@DisplayName("测试查询表数据数量")
	public void getCountTest() throws Exception {
		int count = daoHelpers.sql.getCount(Order.class, "id>100");
		assertTrue(count > 0);
		
		LOGGER.info("count = " +count);
	}

	@Test
	@DisplayName("测试事务提交")
	public void transactionCommitTest() throws Exception {
		long id = 200;
		Date currDate = new Date();
		
		daoHelpers.beginTransaction();
		
		Order order = daoHelpers.sql.get(Order.class, id);
		assertNotNull(order);
		
		order.setCreate_time(currDate);
		daoHelpers.sql.updateEntity(order);
		
		daoHelpers.commitTransaction();
		
		Order newOrder = daoHelpers.sql.get(Order.class, id);
		assertNotNull(newOrder);
		assertEquals(currDate, newOrder.getCreate_time());
	}

	@Test
	@DisplayName("测试事务回滚")
	public void transactionRollbackTest() throws Exception {
		long id = 200;
		
		daoHelpers.beginTransaction();
		
		Order order = daoHelpers.sql.get(Order.class, id);
		assertNotNull(order);
		Date oldDate = order.getCreate_time();
		
		order.setCreate_time(new Date());
		daoHelpers.sql.updateEntity(order);
		
		daoHelpers.rollbackTransaction();
		
		Order newOrder = daoHelpers.sql.get(Order.class, id);
		assertNotNull(newOrder);
		assertEquals(oldDate, newOrder.getCreate_time());
		
		LOGGER.info("oldDate = {} , newDate = {}" , oldDate , newOrder.getCreate_time());
	}

}
